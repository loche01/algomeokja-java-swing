package DB;

import java.sql.*;
import java.util.*;
import model.FoodBean;
import model.MealLogBean;

public class MealLogDAO {
	private DBConnectionMgr pool; // DB 연결 관리 객체

    public MealLogDAO() {
        try {
            pool = DBConnectionMgr.getInstance(); // 🔹 예외 처리 추가
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("DBConnectionMgr 인스턴스 생성 실패");
        }
    }

    // 식사 로그 추가 (담은 목록의 음식들을 meal_log 테이블에 INSERT)
    public boolean insertMealLogs(int mealCode, Vector<FoodBean> favoriteItems) {
        if (mealCode <= 0 || favoriteItems == null || favoriteItems.isEmpty()) {
            System.err.println("식단 상세 정보를 저장할 수 없는 입력입니다.");
            return false;
        }

        Connection conn = null;
        boolean originalAutoCommit = true;
        boolean transactionStarted = false;
        
        try {
            conn = pool.getConnection();
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            transactionStarted = true;

            insertMealLogs(conn, mealCode, favoriteItems);
            conn.commit();
            return true;
        } catch (Exception e) {
            rollback(conn, transactionStarted, e);
            System.err.println("식단 상세 정보 저장 중 오류가 발생했습니다.");
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            pool.freeConnection(conn);
        }
    }

    // 외부 트랜잭션에서 같은 Connection으로 모든 식단 상세를 저장
    public void insertMealLogs(Connection conn, int mealCode, Vector<FoodBean> favoriteItems)
            throws SQLException {
        if (conn == null || mealCode <= 0 || favoriteItems == null || favoriteItems.isEmpty()) {
            throw new SQLException("식단 상세 정보를 저장할 수 없는 입력입니다.");
        }

        String sql = "INSERT INTO meal_log " +
                     "(meal_code, food_code, weight_g, meal_kcal, meal_carb, meal_protein, meal_fat) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (FoodBean food : favoriteItems) {
                if (food == null || food.getFoodCode() <= 0) {
                    throw new SQLException("유효하지 않은 음식 정보입니다.");
                }

                pstmt.setInt(1, mealCode);
                pstmt.setInt(2, food.getFoodCode());
                pstmt.setDouble(3, food.getWeight());
                pstmt.setDouble(4, food.getFoodKcal());
                pstmt.setDouble(5, food.getCarb());
                pstmt.setDouble(6, food.getProtein());
                pstmt.setDouble(7, food.getFat());

                if (pstmt.executeUpdate() <= 0) {
                    throw new SQLException("식단 상세 정보 저장에 실패했습니다.");
                }
            }
        }
    }

    private void rollback(Connection conn, boolean transactionStarted, Exception originalError) {
        if (conn == null || !transactionStarted) {
            return;
        }

        try {
            conn.rollback();
        } catch (SQLException rollbackError) {
            originalError.addSuppressed(rollbackError);
        }
    }
    
    // 특정 식사의 음식 목록 조회
    public Vector<MealLogBean> getMealLogsByMealCode(int meal_code) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Vector<MealLogBean> mealLogs = new Vector<>();
        
        try {
            conn = pool.getConnection();
            String sql = "SELECT ml.*, f.food_name "
                      + "FROM meal_log ml "
                      + "JOIN food f ON ml.food_code = f.food_code "
                      + "WHERE ml.meal_code = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, meal_code);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                MealLogBean log = new MealLogBean();
                log.setMeal_log_code(rs.getInt("meal_log_code"));
                log.setMeal_code(rs.getInt("meal_code"));
                log.setFood_code(rs.getInt("food_code"));
                log.setWeight_g(rs.getDouble("weight_g"));
                log.setMeal_kcal(rs.getDouble("meal_kcal"));
                log.setMeal_carb(rs.getDouble("meal_carb"));
                log.setMeal_protein(rs.getDouble("meal_protein"));
                log.setMeal_fat(rs.getDouble("meal_fat"));
                log.setFood_name(rs.getString("food_name"));
                
                mealLogs.add(log);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(conn, pstmt, rs);
        }
        
        return mealLogs;
    }

    // 오늘 섭취한 영양소 정보를 가져오는 메서드
    public Map<String, Double> getTodayNutrition(String userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<String, Double> nutritionMap = new HashMap<>();
        
        // 초기값 설정
        nutritionMap.put("calories", 0.0);
        nutritionMap.put("carbs", 0.0);
        nutritionMap.put("protein", 0.0);
        nutritionMap.put("fat", 0.0);
        
        try {
            conn = pool.getConnection();
            
            String sql = "SELECT SUM(ml.meal_kcal) as total_calories, " +
                         "SUM(ml.meal_carb) as total_carbs, " +
                         "SUM(ml.meal_protein) as total_protein, " +
                         "SUM(ml.meal_fat) as total_fat " +
                         "FROM meal m " +
                         "JOIN meal_log ml ON m.meal_code = ml.meal_code " +
                         "WHERE m.user_id = ? AND DATE(m.meal_date) = CURDATE()";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                nutritionMap.put("calories", rs.getDouble("total_calories"));
                nutritionMap.put("carbs", rs.getDouble("total_carbs"));
                nutritionMap.put("protein", rs.getDouble("total_protein"));
                nutritionMap.put("fat", rs.getDouble("total_fat"));
            }
            
        } catch (Exception e) {
            System.out.println("❌ 영양소 정보 조회 중 오류: " + e.getMessage());
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
        
        return nutritionMap;
    }
}
