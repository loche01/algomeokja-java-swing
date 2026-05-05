package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExerciseLogDAO {
    private DBConnectionMgr pool;
    
    public ExerciseLogDAO() {
        try {
            pool = DBConnectionMgr.getInstance();
        } catch (Exception e) {
            System.err.println("❌ DBConnectionMgr 초기화 오류 발생!");
            e.printStackTrace();
        }
    }
    
    public boolean saveExerciseLog(int exerciseCode, String userId, 
            int exerciseLogRuntime, double weightInput, double exerciseKcal) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isSaved = false;

        try {
            conn = pool.getConnection();
            
            // 먼저, 최대 exercise_log_code 값을 가져옵니다
            String maxQuery = "SELECT COALESCE(MAX(exercise_log_code), 1000) + 1 AS next_code FROM exercise_log";
            pstmt = conn.prepareStatement(maxQuery);
            rs = pstmt.executeQuery();
            
            int nextCode = 1001; // 레코드가 없는 경우 기본값
            if (rs.next()) {
                nextCode = rs.getInt("next_code");
            }
            
            // 이전 PreparedStatement 닫기
            pstmt.close();
            
            // 이제 다음 코드로 삽입합니다 - 'user01' 대신 userId 파라미터 사용
            String sql = "INSERT INTO exercise_log (exercise_log_code, exercise_code, user_id, " +
                    "exercise_log_runtime, weight_input, exercise_kcal) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, nextCode);
            pstmt.setInt(2, exerciseCode);
            pstmt.setString(3, userId);  // 'user01' 대신 파라미터로 전달된 userId 사용
            pstmt.setInt(4, exerciseLogRuntime);
            pstmt.setDouble(5, weightInput);
            pstmt.setDouble(6, exerciseKcal);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                isSaved = true;
                System.out.println("✅ 운동 로그 저장 완료! 사용자 ID: " + userId + ", 운동 코드: " + exerciseCode);
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL 실행 중 오류 발생! 운동 로그 저장: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ 알 수 없는 오류 발생!");
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
        return isSaved;
    }}