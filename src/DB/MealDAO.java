package DB;

import java.sql.*;
import java.time.LocalTime;
import model.MealBean;

public class MealDAO {
    private DBConnectionMgr pool;
    
    public MealDAO() {
        try {
            pool = DBConnectionMgr.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // 새로운 식사 기록 추가하고 생성된 meal_code 반환
    public int insertMeal(String userId, String mealType) {
        Connection conn = null;
        int newMealCode = -1;

        try {
            conn = pool.getConnection();
            newMealCode = insertMeal(conn, userId, mealType);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(conn);
        }

        return newMealCode;
    }

    // 외부 트랜잭션에서 같은 Connection으로 식사 헤더를 저장
    public int insertMeal(Connection conn, String userId, String mealType) throws SQLException {
        if (conn == null) {
            throw new SQLException("식단 저장용 DB 연결이 없습니다.");
        }

        String sql = "INSERT INTO meal (user_id, meal_date, meal_time, meal_type) " +
                     "VALUES (?, CURDATE(), CURTIME(), ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, mealType);

            if (pstmt.executeUpdate() <= 0) {
                return -1;
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }
    
    // 특정 날짜의 식사 기록 조회
    public MealBean getMealByDate(String user_id, Date meal_date) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        MealBean meal = null;
        
        try {
            conn = pool.getConnection();
            String sql = "SELECT * FROM meal WHERE user_id=? AND meal_date=?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user_id);
            pstmt.setDate(2, meal_date);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                meal = new MealBean();
                meal.setMeal_code(rs.getInt("meal_code"));
                meal.setUser_id(rs.getString("user_id"));
                meal.setMeal_date(rs.getDate("meal_date"));
                meal.setMeal_time(rs.getTime("meal_time"));
                meal.setMeal_image_path(rs.getString("meal_image_path"));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(conn, pstmt, rs);
        }
        
        return meal;
    }

    // 특정 날짜와 시간대의 총 칼로리 조회 - 성능 최적화
    public double getTotalCaloriesByTimeSlot(String userId, String timeSlot) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        double totalCalories = 0;

        try {
            conn = pool.getConnection();
            
            // 인덱스를 활용한 최적화된 쿼리
            String sql = "SELECT SUM(ml.meal_kcal) as total_calories " +
                         "FROM meal m " +
                         "JOIN meal_log ml ON m.meal_code = ml.meal_code " +
                         "WHERE m.user_id = ? AND m.meal_type = ? " +
                         "AND DATE(m.meal_date) = CURDATE()";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, timeSlot);
            
            // 디버그 로그 제거하여 성능 향상
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                totalCalories = rs.getDouble("total_calories");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 리소스 정리 최적화
            pool.freeConnection(conn, pstmt, rs);
        }
        
        return totalCalories;
    }

    // 현재 시간대 확인 메서드
    public String getCurrentTimeSlot() {
        LocalTime now = LocalTime.now();
        
        if (now.isAfter(LocalTime.of(6, 0)) && now.isBefore(LocalTime.of(10, 0))) {
            return "아침";
        } else if (now.isAfter(LocalTime.of(11, 0)) && now.isBefore(LocalTime.of(14, 0))) {
            return "점심";
        } else if (now.isAfter(LocalTime.of(18, 0)) && now.isBefore(LocalTime.of(20, 0))) {
            return "저녁";
        } else {
            return "간식";
        }
    }

    // meal_type 업데이트 메서드 추가
    public boolean updateMealType(int mealCode, String mealType) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;
        
        try {
            conn = pool.getConnection();
            String sql = "UPDATE meal SET meal_type = ? WHERE meal_code = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, mealType);
            pstmt.setInt(2, mealCode);
            
            success = pstmt.executeUpdate() > 0;
            
        } catch (Exception e) {
            System.err.println("❌ meal_type 업데이트 중 오류: " + e.getMessage());
        } finally {
            pool.freeConnection(conn, pstmt);
        }
        
        return success;
    }
}
