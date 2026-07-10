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
            System.err.println("❌ 유효하지 않은 입력값: mealCode=" + mealCode + 
                             ", favoriteItems=" + (favoriteItems == null ? "null" : favoriteItems.size()));
            return false;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = true;
        
        try {
            conn = pool.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작
            
            String sql = "INSERT INTO meal_log " +
                        "(meal_code, food_code, weight_g, meal_kcal, meal_carb, meal_protein, meal_fat) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            
            System.out.println("🔹 식단 상세 정보 저장 시작... (meal_code: " + mealCode + ")");
            
            for (FoodBean food : favoriteItems) {
                try {
                    if (food.getFoodCode() <= 0) {
                        throw new SQLException("유효하지 않은 food_code: " + food.getFoodCode());
                    }

                    System.out.println("\n✅ 저장 중인 음식: " + food.getFoodName());
                    System.out.println("  - 음식 코드: " + food.getFoodCode());
                    System.out.println("  - 설정 그램: " + food.getWeight() + "g");
                    
                    // 이미 FoodInfoPanel에서 계산된 값을 그대로 사용
                    double finalKcal = food.getFoodKcal();
                    double finalCarb = food.getCarb();
                    double finalProtein = food.getProtein();
                    double finalFat = food.getFat();
                    
                    System.out.println("  - 저장할 칼로리: " + finalKcal + "kcal");
                    
                    // meal_log 테이블에 삽입
                    pstmt.setInt(1, mealCode);
                    pstmt.setInt(2, food.getFoodCode());
                    pstmt.setDouble(3, food.getWeight());
                    pstmt.setDouble(4, finalKcal);
                    pstmt.setDouble(5, finalCarb);
                    pstmt.setDouble(6, finalProtein);
                    pstmt.setDouble(7, finalFat);
                    
                    int result = pstmt.executeUpdate();
                    if (result <= 0) {
                        System.out.println("❌ 음식 저장 실패: " + food.getFoodName());
                        success = false;
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("❌ 음식 저장 중 오류: " + food.getFoodName() 
                                   + " (코드: " + food.getFoodCode() + ")");
                    e.printStackTrace();
                    success = false;
                    break;
                }
            }
            
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
            
        } catch (Exception e) {
            success = false;
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            pool.freeConnection(conn, pstmt);
        }
        
        return success;
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
