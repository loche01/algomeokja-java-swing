package DB;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import model.UserGoal;

public class GoalDAO {
    private DBConnectionMgr pool;

    public GoalDAO() {
        try {
            pool = DBConnectionMgr.getInstance();
        } catch (Exception e) {  
            System.err.println("❌ DBConnectionMgr 초기화 오류 발생! GoalDAO 생성 실패");
            e.printStackTrace();
        }
    }

    // 🔹 `user_id` 존재 여부 확인 메서드
    public boolean isUserExists(String userId) {
        String query = "SELECT COUNT(*) FROM user WHERE user_id = ?";
        try (Connection conn = pool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL 실행 중 오류 발생! (isUserExists) userId: " + userId);
            e.printStackTrace();
        } catch (Exception e) {  
            System.err.println("❌ 알 수 없는 오류 발생! userId: " + userId);
            e.printStackTrace();
        }
        return false;
    }

    // 🔹 목표 저장 또는 업데이트 (유저 존재 여부 확인 추가)
    public boolean saveOrUpdateGoal(UserGoal goal) {
        if (!isUserExists(goal.getUserId())) {
            System.err.println("❌ 오류: user_id가 user 테이블에 존재하지 않음. 저장 불가!");
            return false;
        }

        System.out.println("✅ 목표 저장 시작: userId=" + goal.getUserId() + 
                          ", 시작 체중=" + goal.getStartWeight() + 
                          ", 목표 체중=" + goal.getTargetWeight() + 
                          ", 기간=" + goal.getTargetDuration() + "일");

        String checkQuery = "SELECT COUNT(*) FROM target WHERE user_id = ?";
        String insertQuery = "INSERT INTO target (user_id, start_weight, target_weight, target_duration) VALUES (?, ?, ?, ?)";
        String updateQuery = "UPDATE target SET start_weight=?, target_weight=?, target_duration=? WHERE user_id=?";

        try (Connection conn = pool.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

            System.out.println("✅ DB 연결 성공, 기존 데이터 확인 중...");
            checkStmt.setString(1, goal.getUserId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                boolean exists = rs.next() && rs.getInt(1) > 0;
                System.out.println("✅ 기존 데이터 확인 결과: " + (exists ? "있음" : "없음"));

                if (exists) { 
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        System.out.println("✅ 기존 데이터 업데이트 시작...");
                        updateStmt.setBigDecimal(1, goal.getStartWeight());
                        updateStmt.setBigDecimal(2, goal.getTargetWeight());
                        updateStmt.setInt(3, goal.getTargetDuration());
                        updateStmt.setString(4, goal.getUserId());

                        int rowsUpdated = updateStmt.executeUpdate();
                        if (rowsUpdated > 0) {
                            System.out.println("✅ 목표 데이터 수정 완료! userId: " + goal.getUserId());
                            return true;
                        } else {
                            System.err.println("❌ 목표 데이터 수정 실패! userId: " + goal.getUserId());
                        }
                    }
                } else { 
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        System.out.println("✅ 새 데이터 삽입 시작...");
                        insertStmt.setString(1, goal.getUserId());
                        insertStmt.setBigDecimal(2, goal.getStartWeight());
                        insertStmt.setBigDecimal(3, goal.getTargetWeight());
                        insertStmt.setInt(4, goal.getTargetDuration());

                        int rowsInserted = insertStmt.executeUpdate();
                        if (rowsInserted > 0) {
                            System.out.println("✅ 목표 데이터 저장 완료! userId: " + goal.getUserId());
                            return true;
                        } else {
                            System.err.println("❌ 목표 데이터 저장 실패! userId: " + goal.getUserId());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL 실행 중 오류 발생! userId: " + goal.getUserId());
            System.err.println("❌ SQL 오류 메시지: " + e.getMessage());
            System.err.println("❌ SQL 상태: " + e.getSQLState());
            System.err.println("❌ 오류 코드: " + e.getErrorCode());
            e.printStackTrace();
        } catch (Exception e) {  
            System.err.println("❌ 알 수 없는 오류 발생! userId: " + goal.getUserId());
            System.err.println("❌ 오류 메시지: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // 🔹 user_id로 최신 목표 체중 가져오기
    public UserGoal getUserGoal(String userId) {
        System.out.println("✅ 목표 데이터 조회 시작: userId=" + userId);
        
        String sql = "SELECT * FROM target WHERE user_id = ?";
        try (Connection conn = pool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            System.out.println("✅ DB 연결 성공, 목표 데이터 조회 중...");
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("✅ 목표 데이터 조회 성공!");
                    
                    // 결과 데이터 확인
                    BigDecimal startWeight = rs.getBigDecimal("start_weight");
                    BigDecimal targetWeight = rs.getBigDecimal("target_weight");
                    int targetDuration = rs.getInt("target_duration");
                    
                    System.out.println("✅ 조회된 데이터: 시작 체중=" + startWeight + 
                                      ", 목표 체중=" + targetWeight + 
                                      ", 기간=" + targetDuration + "일");
                    
                    return new UserGoal(
                        userId,
                        startWeight,
                        targetWeight,
                        targetDuration
                    );
                } else {
                    System.out.println("❌ 목표 데이터 없음! userId: " + userId);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ 목표 체중 정보 불러오기 실패! userId: " + userId);
            System.err.println("❌ SQL 오류 메시지: " + e.getMessage());
            System.err.println("❌ SQL 상태: " + e.getSQLState());
            System.err.println("❌ 오류 코드: " + e.getErrorCode());
            e.printStackTrace();
        } catch (Exception e) {  
            System.err.println("❌ 알 수 없는 오류 발생! userId: " + userId);
            System.err.println("❌ 오류 메시지: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // 🔹 식단 기록을 통한 체중 변화 계산
    public Map<String, Object> calculateWeightChangeFromMealLogs(String userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("weightChange", 0.0);
        result.put("progressPercent", 0.0);
        
        // 목표 정보 가져오기
        UserGoal goal = getUserGoal(userId);
        if (goal == null) {
            return result;
        }
        
        // 목표 시작일부터 현재까지의 기간 계산 (일수)
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(goal.getTargetDuration());
        long daysPassed = ChronoUnit.DAYS.between(startDate, today);
        
        // 목표 기간 대비 경과 기간 비율
        double timeProgressRatio = (double) daysPassed / goal.getTargetDuration();
        if (timeProgressRatio > 1.0) timeProgressRatio = 1.0; // 100%를 넘지 않도록
        
        // 식단 기록을 통한 칼로리 섭취량 계산
        double totalCaloriesConsumed = calculateTotalCaloriesConsumed(userId, startDate);
        
        // 기초 대사량 계산 (간단한 추정)
        double bmr = 1800; // 평균적인 기초 대사량 (실제로는 성별, 나이, 체중 등에 따라 계산)
        double totalCaloriesBurned = bmr * daysPassed;
        
        // 칼로리 차이 계산
        double calorieDeficit = totalCaloriesBurned - totalCaloriesConsumed;
        
        // 체중 변화 추정 (7700kcal = 1kg)
        double estimatedWeightChange = calorieDeficit / 7700.0;
        
        // 목표 체중 변화
        double targetWeightChange = goal.getTargetWeight().doubleValue() - goal.getStartWeight().doubleValue();
        
        // 진행률 계산
        double progressPercent = 0.0;
        if (targetWeightChange != 0) {
            progressPercent = (estimatedWeightChange / targetWeightChange) * 100.0;
            if (progressPercent > 100.0) progressPercent = 100.0; // 100%를 넘지 않도록
            if (progressPercent < 0.0) progressPercent = 0.0; // 0% 미만이 되지 않도록
        }
        
        result.put("weightChange", estimatedWeightChange);
        result.put("progressPercent", progressPercent);
        result.put("timeProgressRatio", timeProgressRatio * 100.0);
        
        return result;
    }
    
    // 특정 기간 동안의 총 칼로리 섭취량 계산
    private double calculateTotalCaloriesConsumed(String userId, LocalDate startDate) {
        String sql = "SELECT SUM(ml.meal_kcal) as total_calories " +
                     "FROM meal m " +
                     "JOIN meal_log ml ON m.meal_code = ml.meal_code " +
                     "WHERE m.user_id = ? AND m.meal_date >= ?";
        
        try (Connection conn = pool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            pstmt.setDate(2, java.sql.Date.valueOf(startDate));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_calories");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ 총 칼로리 계산 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0.0;
    }
    
    // 사용자의 최신 체중 정보 가져오기
    public BigDecimal getLatestWeight(String userId) {
        String sql = "SELECT weight FROM bodyinfo WHERE user_id = ? ORDER BY bodyinfo_code DESC LIMIT 1";
        
        try (Connection conn = pool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new BigDecimal(rs.getFloat("weight"));
                }
            }
        } catch (Exception e) {
            System.err.println("❌ 최신 체중 정보 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
}
