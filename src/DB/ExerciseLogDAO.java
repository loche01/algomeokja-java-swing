package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return saveExerciseLog(exerciseCode, null, userId, exerciseLogRuntime, weightInput, exerciseKcal);
    }

    public boolean saveExerciseLog(int exerciseCode, String exerciseName, String userId,
            int exerciseLogRuntime, double weightInput, double exerciseKcal) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean isSaved = false;

        try {
            conn = pool.getConnection();

            String sql = "INSERT INTO exercise_log (user_id, exercise_code, exercise_name, exercise_calories) " +
                    "VALUES (?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setInt(2, exerciseCode);
            pstmt.setString(3, exerciseName);
            pstmt.setInt(4, (int) Math.round(exerciseKcal));

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
    }

    public List<Map<String, Object>> getTodayExerciseLogs(String userId) {
        List<Map<String, Object>> logs = new ArrayList<>();
        if (userId == null || userId.trim().isEmpty()) {
            return logs;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();

            String sql = "SELECT exercise_name, exercise_calories, exercise_date " +
                    "FROM exercise_log " +
                    "WHERE user_id = ? AND DATE(exercise_date) = CURDATE() " +
                    "ORDER BY exercise_date DESC, exercise_log_id DESC";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> log = new HashMap<>();
                log.put("exercise_name", rs.getString("exercise_name"));
                log.put("exercise_calories", rs.getInt("exercise_calories"));
                log.put("exercise_date", rs.getTimestamp("exercise_date"));
                logs.add(log);
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL 실행 중 오류 발생! 오늘 운동 로그 조회: " + e.getMessage());
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
        return logs;
    }
}
