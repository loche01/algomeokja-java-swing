package DB;

import java.sql.*;
import java.util.Vector;
import model.FoodBean;

public class FoodDAO {

    private DBConnectionMgr pool; // DB 연결 관리 객체

    public FoodDAO() {
        try {
            pool = DBConnectionMgr.getInstance(); // 🔹 예외 처리 추가
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("DBConnectionMgr 인스턴스 생성 실패");
        }
    }

    public Vector<FoodBean> getAllFoods() {
        Vector<FoodBean> foodList = new Vector<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        System.out.println("🔹 FoodDAO: 데이터 가져오는 중...");
        
        try {
            conn = pool.getConnection();
            String sql = "SELECT * FROM food";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                FoodBean food = new FoodBean();
                food.setFoodCode(rs.getInt("food_code"));
                food.setFoodName(rs.getString("food_name"));
                food.setFoodKcal(rs.getDouble("food_kcal"));
                food.setCarb(rs.getDouble("carb"));
                food.setProtein(rs.getDouble("protein"));
                food.setFat(rs.getDouble("fat"));

                foodList.add(food);
                //System.out.println("✅ 가져온 데이터: " + food.getFoodName() + " (" + food.getFoodKcal() + " kcal)");
            }
            //System.out.println("🔹 총 " + foodList.size() + " 개의 데이터가 로드되었습니다.");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(conn, pstmt, rs);
        }

        return foodList;
    }

    public Vector<FoodBean> getFoodListBySearch(String searchKeyword) {
        Vector<FoodBean> foodList = new Vector<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        System.out.println("🔹 FoodDAO: 검색 수행 (" + searchKeyword + ")");

        try {
            conn = pool.getConnection();
            String sql = "SELECT * FROM food WHERE food_name LIKE ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + searchKeyword + "%"); // 🔹 부분 일치 검색
            rs = pstmt.executeQuery();

            while (rs.next()) {
                FoodBean food = new FoodBean();
                food.setFoodCode(rs.getInt("food_code"));
                food.setFoodName(rs.getString("food_name"));
                food.setFoodKcal(rs.getDouble("food_kcal"));
                food.setCarb(rs.getDouble("carb"));
                food.setProtein(rs.getDouble("protein"));
                food.setFat(rs.getDouble("fat"));
                
                foodList.add(food);
                //System.out.println("✅ 검색 결과 추가: " + food.getFoodName() + " (" + food.getFoodKcal() + " kcal)");
            }

            System.out.println("🔹 총 " + foodList.size() + "개의 검색 결과");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(conn, pstmt, rs);
        }

        return foodList;
    }

    public FoodBean getFoodByName(String foodName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = pool.getConnection();
            String sql = "SELECT * FROM food WHERE food_name = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, foodName);
            rs = pstmt.executeQuery();
            
            if(rs.next()) {
                FoodBean bean = new FoodBean();
                bean.setFoodCode(rs.getInt("food_code"));
                bean.setFoodName(rs.getString("food_name"));
                bean.setFoodKcal(rs.getDouble("food_kcal"));
                bean.setCarb(rs.getDouble("carb"));
                bean.setProtein(rs.getDouble("protein"));
                bean.setFat(rs.getDouble("fat"));
                
                System.out.println("✅ 음식 정보 조회: " + bean.getFoodName() 
                               + " (코드: " + bean.getFoodCode() + ")");
                return bean;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(conn, pstmt, rs);
        }
        return null;
    }
}
