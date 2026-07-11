package DB;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarDAO {
    private DBConnectionMgr pool;

    public CalendarDAO() {
        try {
            pool = DBConnectionMgr.getInstance();
        } catch (Exception e) {
            System.err.println("캘린더 DB 연결 관리자 초기화 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> getExerciseLogsByDate(String userId, LocalDate date) {
        List<Map<String, Object>> logs = new ArrayList<>();
        if (!isValidRequest(userId, date)) {
            return logs;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();
            String sql = "SELECT exercise_name, exercise_calories, exercise_date "
                    + "FROM exercise_log "
                    + "WHERE user_id = ? AND DATE(exercise_date) = ? "
                    + "ORDER BY exercise_date DESC, exercise_log_id DESC";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setDate(2, Date.valueOf(date));
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> log = new HashMap<>();
                log.put("exercise_name", rs.getString("exercise_name"));
                log.put("exercise_calories", rs.getInt("exercise_calories"));
                log.put("exercise_date", rs.getTimestamp("exercise_date"));
                logs.add(log);
            }
        } catch (Exception e) {
            System.err.println("선택 날짜 운동 기록 조회 중 오류가 발생했습니다.");
            e.printStackTrace();
        } finally {
            pool.freeConnection(conn, pstmt, rs);
        }

        return logs;
    }

    public List<Map<String, Object>> getMealLogsByDate(String userId, LocalDate date) {
        List<Map<String, Object>> logs = new ArrayList<>();
        if (!isValidRequest(userId, date)) {
            return logs;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();
            String sql = "SELECT m.meal_code, m.meal_type, m.meal_time, "
                    + "COALESCE(SUM(ml.meal_kcal), 0) AS total_calories, "
                    + "GROUP_CONCAT(f.food_name ORDER BY ml.meal_log_code SEPARATOR ', ') AS food_names "
                    + "FROM meal m "
                    + "LEFT JOIN meal_log ml ON m.meal_code = ml.meal_code "
                    + "LEFT JOIN food f ON ml.food_code = f.food_code "
                    + "WHERE m.user_id = ? AND m.meal_date = ? "
                    + "GROUP BY m.meal_code, m.meal_type, m.meal_time "
                    + "ORDER BY m.meal_time DESC, m.meal_code DESC";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setDate(2, Date.valueOf(date));
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> log = new HashMap<>();
                log.put("meal_code", rs.getInt("meal_code"));
                log.put("meal_type", rs.getString("meal_type"));
                log.put("meal_time", rs.getTime("meal_time"));
                log.put("total_calories", rs.getDouble("total_calories"));
                log.put("food_names", rs.getString("food_names"));
                logs.add(log);
            }
        } catch (Exception e) {
            System.err.println("선택 날짜 식단 기록 조회 중 오류가 발생했습니다.");
            e.printStackTrace();
        } finally {
            pool.freeConnection(conn, pstmt, rs);
        }

        return logs;
    }

    private boolean isValidRequest(String userId, LocalDate date) {
        return pool != null && userId != null && !userId.trim().isEmpty() && date != null;
    }
}
