package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.UserBean; 
import ui_n_utils.UserSessionManager;


public class LoginDAO {

    private DBConnectionMgr pool;

    public LoginDAO() {
        try {
            pool = DBConnectionMgr.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 // 🔹 로그인 검증 메서드 (관리자 & 일반 사용자 구분)
    public boolean checkLogin(String userId, String userPwd) {
        Connection conn = null;
        boolean loginSuccess = false;

        try {
            conn = pool.getConnection();

            // 1️⃣ 관리자(admin) 테이블에서 조회
            String adminQuery = "SELECT * FROM admin WHERE admin_id = ? AND admin_pwd = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(adminQuery)) {
                pstmt.setString(1, userId);
                pstmt.setString(2, userPwd);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        loginSuccess = true;
                        UserSessionManager.getInstance().setAdminSession(userId);
                        System.out.println("✅ 관리자 로그인 성공! ID: " + userId);
                        return loginSuccess;
                    }
                }
            }

            // 2️⃣ 일반 사용자(user) 테이블에서 조회
            String userQuery = "SELECT * FROM user WHERE user_id = ? AND user_pwd = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(userQuery)) {
                pstmt.setString(1, userId);
                pstmt.setString(2, userPwd);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        loginSuccess = true;

                        // ✅ 새로운 로그인 유저 정보 저장
                        UserBean user = new UserBean();
                        user.setUser_id(rs.getString("user_id"));
                        user.setUser_name(rs.getString("user_name"));
                        user.setUser_email(rs.getString("user_email"));
                        user.setUser_phone(rs.getString("user_phone"));

                        // ✅ 세션에 저장
                        UserSessionManager.getInstance().setCurrentUser(user);

                        System.out.println("✅ 로그인 성공! 세션 저장된 유저: " + user.getUser_id());

                        // 🔹 세션 유지 확인
                        UserBean sessionUser = UserSessionManager.getInstance().getCurrentUser();
                        if (sessionUser == null || sessionUser.getUser_id() == null) {
                            System.out.println("❌ [UserSessionManager] 로그인 직후에도 세션 정보가 없습니다!");
                        } else {
                            System.out.println("✅ [UserSessionManager] 로그인 후 세션 유지 확인: " + sessionUser.getUser_id());
                        }
                    } else {
                        System.out.println("❌ 로그인 실패! 아이디 또는 비밀번호가 일치하지 않습니다.");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(conn);
        }
        return loginSuccess;
    }

    // ✅ 회원 정보 업데이트
    public boolean updateUserInfo(UserBean user) {
        UserBean sessionUser = UserSessionManager.getInstance().getCurrentUser();
        if (sessionUser == null || sessionUser.getUser_id() == null) {
            System.out.println("❌ [UserSessionManager] 패널 전환 후에도 로그인 정보가 없습니다!");
            return false;
        }

        System.out.println("✅ [UserSessionManager] 패널 전환 후에도 로그인 정보 유지: " + sessionUser.getUser_id());

        Connection conn = null;
        boolean success = false;

        try {
            conn = pool.getConnection();
            String sql = "UPDATE user SET user_name = ?, user_email = ?, user_phone = ?, user_pwd = ? WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, user.getUser_name());
                pstmt.setString(2, user.getUser_email());
                pstmt.setString(3, user.getUser_phone());
                pstmt.setString(4, user.getUser_pwd());
                pstmt.setString(5, user.getUser_id());

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    success = true;
                    System.out.println("✅ 회원 정보 수정 완료! userId: " + user.getUser_id());

                    // ✅ 최신 데이터 가져와서 세션에 반영
                    UserBean updatedUser = getUserById(user.getUser_id());
                    if (updatedUser != null) {
                        UserSessionManager.getInstance().setCurrentUser(updatedUser);
                        System.out.println("✅ 세션 정보 갱신 완료! userId: " + updatedUser.getUser_id());
                    } else {
                        System.out.println("❌ 회원 정보 수정은 성공했지만 세션 갱신 실패!");
                    }
                } else {
                    System.out.println("❌ 회원 정보 업데이트 실패! userId: " + user.getUser_id());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(conn);
        }
        return success;
    }

    // ✅ 회원 정보 조회 (보안 강화: 비밀번호 제외)
    public UserBean getUserById(String userId) {
        Connection conn = null;
        UserBean user = null;

        try {
            conn = pool.getConnection();
            String sql = "SELECT * FROM user WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        user = new UserBean();
                        user.setUser_id(rs.getString("user_id"));
                        user.setUser_name(rs.getString("user_name"));
                        user.setUser_email(rs.getString("user_email"));
                        user.setUser_phone(rs.getString("user_phone"));
                        // 🔹 비밀번호는 보안상 저장하지 않음
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(conn);
        }
        return user;
    }

    // 관리자 정보 조회 메서드 추가
    public UserBean getAdminInfo(String adminId, String adminPwd) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        UserBean admin = null;

        try {
            conn = pool.getConnection();
            String sql = "SELECT * FROM admin WHERE admin_id = ? AND admin_pwd = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, adminId);
            pstmt.setString(2, adminPwd);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // 관리자 로그인 성공 시, UserBean 객체 생성 및 정보 담기
                admin = new UserBean();
                admin.setUser_id(rs.getString("admin_id"));
                admin.setUser_pwd(rs.getString("admin_pwd"));
                admin.setUser_name("관리자"); // 관리자 이름 설정
                admin.setUser_role("ADMIN"); // 관리자 역할 설정
                
                System.out.println("✅ 관리자 정보 조회 성공: " + admin.getUser_id());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(conn, pstmt, rs);
        }
        return admin; // 없으면 null
    }

    // 사용자 또는 관리자 정보 조회 (통합 메서드)
    public UserBean getUserInfo(String userId, String userPwd) {
        // 먼저 관리자 테이블에서 조회
        UserBean admin = getAdminInfo(userId, userPwd);
        if (admin != null) {
            return admin; // 관리자 정보 반환
        }
        
        // 관리자가 아니면 일반 사용자 테이블에서 조회
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        UserBean user = null;

        try {
            conn = pool.getConnection();
            String sql = "SELECT * FROM user WHERE user_id = ? AND user_pwd = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, userPwd);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // 로그인 성공 시, UserBean 객체 생성 및 정보 담기
                user = new UserBean();
                user.setUser_id(rs.getString("user_id"));
                user.setUser_pwd(rs.getString("user_pwd"));
                user.setUser_name(rs.getString("user_name"));
                user.setUser_phone(rs.getString("user_phone"));
                user.setUser_email(rs.getString("user_email"));
                user.setUser_createdtime(rs.getTimestamp("user_createdtime"));
                user.setUser_role("USER"); // 일반 사용자 역할 설정
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(conn, pstmt, rs);
        }
        return user; // 없으면 null
    }

}
