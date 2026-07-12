package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BodyInfoDAO {

    private DBConnectionMgr pool;

    public BodyInfoDAO() {
        try {
            pool = DBConnectionMgr.getInstance();
        } catch (Exception e) {
            System.err.println("❌ DBConnectionMgr 초기화 오류 발생!");
            e.printStackTrace();
        }
    }

    // 🔹 `user_id` 존재 여부 확인 (외래 키 오류 방지)
    private boolean userExists(String userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean exists = false;

        try {
            conn = pool.getConnection(); // ✅ try-catch로 감싸서 예외 처리
            String sql = "SELECT COUNT(*) FROM user WHERE user_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                exists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {  // ✅ 예외 처리 추가
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) pool.freeConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return exists;
    }

    // 🔹 신체 정보 저장 (created_at 없이)
    public boolean saveBodyInfo(String userId, float height, float weight, float fatRate, 
                                float fatMass, float muscleMass) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean isSaved = false;

        try {
            // ✅ `user_id` 존재 여부 확인
            if (!userExists(userId)) {
                System.err.println("❌ 신체 정보를 저장할 사용자를 찾을 수 없습니다.");
                return false;
            }

            // ✅ `bodyinfo` 테이블에 데이터 삽입
            conn = pool.getConnection();
            String sql = "INSERT INTO bodyinfo (user_id, height, weight, fat_rate, fat_mass, muscle_mass) VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setFloat(2, height);
            pstmt.setFloat(3, weight);
            pstmt.setFloat(4, fatRate);
            pstmt.setFloat(5, fatMass);
            pstmt.setFloat(6, muscleMass);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                isSaved = true;
            }

  

        } catch (SQLException e) {
            System.err.println("❌ 신체 정보 저장 중 SQL 오류가 발생했습니다.");
            e.printStackTrace();
        } catch (Exception e) {  // ✅ 예외 처리 추가
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) pool.freeConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return isSaved;
    }

    // 🔹 특정 사용자의 최신 신체 정보 조회
    public ResultSet getLatestBodyInfo(String userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();
            String sql = "SELECT * FROM bodyinfo WHERE user_id = ? ORDER BY bodyinfo_code DESC LIMIT 1";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("❌ SQL 실행 중 오류 발생!");
            e.printStackTrace();
        } catch (Exception e) {  // ✅ 예외 처리 추가
            e.printStackTrace();
        }
        return rs;
    }

}
