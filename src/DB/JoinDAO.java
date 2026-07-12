package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import model.UserBean;
import security.PasswordHasher;
import ui_n_utils.UserSessionManager;

public class JoinDAO {
    
    private DBConnectionMgr pool; // DB 연결 관리 객체

    public JoinDAO() {
        try {
            pool = DBConnectionMgr.getInstance(); // 🔹 예외 처리 추가
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("DBConnectionMgr 인스턴스 생성 실패");
        }
    }

    // 🔹 회원가입 정보 DB에 저장
    public boolean joinUserWithRawPassword(UserBean user, char[] rawPassword) {
        Connection con = null;
        PreparedStatement pstmt = null;

        String encodedPassword;
        try {
            encodedPassword = PasswordHasher.hash(rawPassword);
        } catch (RuntimeException e) {
            System.err.println("회원가입 비밀번호를 안전하게 처리하지 못했습니다.");
            return false;
        }

        String sql = "INSERT INTO user (user_id, user_pwd, user_name, user_phone, user_email, user_createdtime, user_birthdate, user_gender) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            con = pool.getConnection(); // 🔹 예외 발생 가능 코드 → try-catch 추가
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, user.getUser_id());
            pstmt.setString(2, encodedPassword);
            pstmt.setString(3, user.getUser_name());
            pstmt.setString(4, user.getUser_phone());
            pstmt.setString(5, user.getUser_email());
            pstmt.setTimestamp(6, new Timestamp(System.currentTimeMillis())); // 현재 시간 저장
            pstmt.setString(7, user.getUser_birthdate()); // 생년월일 저장
            pstmt.setString(8, user.getUser_gender()); // 성별 저장

            int result = pstmt.executeUpdate();
            
            if (result > 0) { // ✅ 회원가입 성공 시
                UserSessionManager.getInstance().setCurrentUser(user); // ✅ 세션에 저장
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("회원가입 데이터 저장 중 오류 발생!");
            return false;
        } catch (Exception e) { // 🔹 DB 연결 오류 예외 처리
            e.printStackTrace();
            System.out.println("DB 연결 중 오류 발생!");
            return false;
        } finally {
            pool.freeConnection(con, pstmt);
        }
    } //--registerUser
    
    // 🔹 ID 중복 확인 기능
    public boolean isUserIdExists(String userId) throws Exception {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        
        String sql = "SELECT COUNT(*) FROM user WHERE user_id = ?";

        try {
            con = pool.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true; // ID가 이미 존재함
            }
            return false; // 사용 가능한 ID

        } catch (SQLException e) {
            e.printStackTrace();
            return true; // 예외 발생 시 중복된 것으로 간주
        } finally {
            pool.freeConnection(con, pstmt, rs);
        }
    } // --isUserIdExists
    

 // 이메일 중복 확인
    public boolean isEmailExists(String email) throws Exception {
        String sql = "SELECT COUNT(*) FROM user WHERE user_email = ?"; // ✅ SQL 수정 (user_id → user_email)

        try (
            Connection con = pool.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql)
        ) {
            pstmt.setString(1, email); //  파라미터 설정

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; //  중복 여부 반환
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("이메일 중복 검사 중 오류 발생"); //  예외 발생 시 throw
        }
        return false; // 기본적으로 중복되지 않음
    }
    
    // 전화번호 중복 확인
    public boolean isPhoneExists(String phone) throws Exception{
    	 String sql = "SELECT COUNT(*) FROM user WHERE user_phone = ?"; 
try (
        Connection con = pool.getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)
    ) {
        pstmt.setString(1, phone); //  파라미터 설정

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0; //  중복 여부 반환
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw new Exception("전화번호 중복 검사 중 오류 발생"); //  예외 발생 시 throw
    }

    return false; // 기본적으로 중복되지 않음
    }
}
