package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.UserBean;
import security.PasswordHasher;


public class LoginDAO {

    private DBConnectionMgr pool;

    public LoginDAO() {
        try {
            pool = DBConnectionMgr.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 사용자 또는 관리자 정보 조회 (관리자 인증 실패 시 일반 사용자 확인)
    public UserBean getUserInfo(String userId, char[] rawPassword) {
        UserBean admin = getAdminInfo(userId, rawPassword);
        if (admin != null) {
            return admin;
        }

        return getRegularUserInfo(userId, rawPassword);
    }

    private UserBean getAdminInfo(String adminId, char[] rawPassword) {
        Connection conn = null;

        try {
            conn = pool.getConnection();
            String sql = "SELECT admin_id, admin_pwd FROM admin WHERE admin_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, adminId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }

                    String storedPassword = rs.getString("admin_pwd");
                    if (!matchesStoredPassword(rawPassword, storedPassword)) {
                        return null;
                    }

                    upgradeLegacyPassword("admin", "admin_id", "admin_pwd",
                            adminId, rawPassword, storedPassword);

                    UserBean admin = new UserBean();
                    admin.setUser_id(rs.getString("admin_id"));
                    admin.setUser_name("관리자");
                    admin.setUser_role("ADMIN");
                    return admin;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(conn);
        }
        return null;
    }

    private UserBean getRegularUserInfo(String userId, char[] rawPassword) {
        Connection conn = null;

        try {
            conn = pool.getConnection();
            String sql = "SELECT user_id, user_pwd, user_name, user_phone, user_email, user_createdtime "
                    + "FROM user WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }

                    String storedPassword = rs.getString("user_pwd");
                    if (!matchesStoredPassword(rawPassword, storedPassword)) {
                        return null;
                    }

                    upgradeLegacyPassword("user", "user_id", "user_pwd",
                            userId, rawPassword, storedPassword);

                    UserBean user = new UserBean();
                    user.setUser_id(rs.getString("user_id"));
                    user.setUser_name(rs.getString("user_name"));
                    user.setUser_phone(rs.getString("user_phone"));
                    user.setUser_email(rs.getString("user_email"));
                    user.setUser_createdtime(rs.getTimestamp("user_createdtime"));
                    user.setUser_role("USER");
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(conn);
        }
        return null;
    }

    private boolean matchesStoredPassword(char[] rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        if (PasswordHasher.hasRecognizedPrefix(storedPassword)) {
            return PasswordHasher.verify(rawPassword, storedPassword);
        }

        int difference = rawPassword.length ^ storedPassword.length();
        int length = Math.max(rawPassword.length, storedPassword.length());
        for (int i = 0; i < length; i++) {
            char rawCharacter = i < rawPassword.length ? rawPassword[i] : 0;
            char storedCharacter = i < storedPassword.length() ? storedPassword.charAt(i) : 0;
            difference |= rawCharacter ^ storedCharacter;
        }
        return difference == 0;
    }

    private void upgradeLegacyPassword(String tableName, String idColumn, String passwordColumn,
            String accountId, char[] rawPassword, String storedPassword) {
        boolean legacyPassword = !PasswordHasher.hasRecognizedPrefix(storedPassword);
        boolean outdatedHash = PasswordHasher.isEncoded(storedPassword)
                && PasswordHasher.needsUpgrade(storedPassword);
        if (!legacyPassword && !outdatedHash) {
            return;
        }

        String encodedPassword;
        try {
            encodedPassword = PasswordHasher.hash(rawPassword);
        } catch (RuntimeException e) {
            System.err.println("비밀번호 해시 점진 전환을 준비하지 못했습니다. 다음 로그인에서 다시 시도합니다.");
            return;
        }

        Connection conn = null;
        try {
            conn = pool.getConnection();
            String sql = "UPDATE " + tableName + " SET " + passwordColumn + " = ? WHERE "
                    + idColumn + " = ? AND " + passwordColumn + " = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, encodedPassword);
                pstmt.setString(2, accountId);
                pstmt.setString(3, storedPassword);
                if (pstmt.executeUpdate() != 1) {
                    System.err.println("비밀번호 해시 점진 전환을 완료하지 못했습니다. 다음 로그인에서 다시 시도합니다.");
                }
            }
        } catch (Exception e) {
            System.err.println("비밀번호 해시 점진 전환 중 오류가 발생했습니다. 다음 로그인에서 다시 시도합니다.");
        } finally {
            pool.freeConnection(conn);
        }
    }
}
