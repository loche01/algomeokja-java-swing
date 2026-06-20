package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
        boolean isSaved = false;

        try {
            conn = pool.getConnection();

            String sql = "INSERT INTO exercise_log (user_id, exercise_code, exercise_calories) " +
                    "VALUES (?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setInt(2, exerciseCode);
            pstmt.setInt(3, (int) Math.round(exerciseKcal));

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
                if (pstmt != null) pstmt.close();
                if (conn != null) pool.freeConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return isSaved;
    }}
