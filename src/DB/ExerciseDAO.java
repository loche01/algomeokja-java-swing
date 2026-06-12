package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.ExerciseBean;

public class ExerciseDAO {

    private DBConnectionMgr pool;

    public ExerciseDAO() {
        try {
            pool = DBConnectionMgr.getInstance();
        } catch (Exception e) {
            System.err.println("❌ DBConnectionMgr 초기화 오류 발생!");
            e.printStackTrace();
        }
    }

    // 🔹 운동 정보 저장 메서드
    public boolean saveExercise(String exerciseName, String exerciseCategory,
                               String exerciseType, float exerciseMET) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean isSaved = false;

        try {
            conn = pool.getConnection();
            String sql = "INSERT INTO exercise (exercise_name, exercise_category, exercise_type, exercise_MET) "
                       + "VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, exerciseName);
            pstmt.setString(2, exerciseCategory);
            pstmt.setString(3, exerciseType);
            pstmt.setFloat(4, exerciseMET);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                isSaved = true;
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL 실행 중 오류 발생! 운동명: " + exerciseName);
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

    // 🔹 모든 운동 정보 조회 메서드
    public List<ExerciseBean> getAllExercises() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<ExerciseBean> exerciseList = new ArrayList<>();

        try {
            conn = pool.getConnection();
            String sql = "SELECT exercise_code, exercise_name, exercise_category, exercise_type, exercise_MET FROM exercise";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ExerciseBean exercise = new ExerciseBean();
                exercise.setExerciseCode(rs.getInt("exercise_code"));
                exercise.setExerciseName(rs.getString("exercise_name"));
                exercise.setExerciseCategory(rs.getString("exercise_category"));
                exercise.setExerciseType(rs.getString("exercise_type"));
                exercise.setExerciseMET(rs.getFloat("exercise_MET"));
                exerciseList.add(exercise);
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL 실행 중 오류 발생!");
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
        return exerciseList;
    }

    // 🔹 특정 카테고리 운동 정보 조회 메서드
    public List<ExerciseBean> getExercisesByCategory(String category) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<ExerciseBean> exerciseList = new ArrayList<>();

        try {
            conn = pool.getConnection();
            String sql = "SELECT exercise_code, exercise_name, exercise_category, exercise_type, exercise_MET "
                       + "FROM exercise WHERE exercise_category = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, category);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ExerciseBean exercise = new ExerciseBean();
                exercise.setExerciseCode(rs.getInt("exercise_code"));
                exercise.setExerciseName(rs.getString("exercise_name"));
                exercise.setExerciseCategory(rs.getString("exercise_category"));
                exercise.setExerciseType(rs.getString("exercise_type"));
                exercise.setExerciseMET(rs.getFloat("exercise_MET"));
                exerciseList.add(exercise);
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL 실행 중 오류 발생! 카테고리: " + category);
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
        return exerciseList;
    }

    // 🔹 운동명으로 검색하는 메서드
    public List<ExerciseBean> searchExercisesByName(String keyword) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<ExerciseBean> exerciseList = new ArrayList<>();

        try {
            conn = pool.getConnection();
            String sql = "SELECT exercise_code, exercise_name, exercise_category, exercise_type, exercise_MET "
                       + "FROM exercise WHERE exercise_name LIKE ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                ExerciseBean exercise = new ExerciseBean();
                exercise.setExerciseCode(rs.getInt("exercise_code"));
                exercise.setExerciseName(rs.getString("exercise_name"));
                exercise.setExerciseCategory(rs.getString("exercise_category"));
                exercise.setExerciseType(rs.getString("exercise_type"));
                exercise.setExerciseMET(rs.getFloat("exercise_MET"));
                exerciseList.add(exercise);
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL 실행 중 오류 발생! 키워드: " + keyword);
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
        return exerciseList;
    }

    // 🔹 특정 운동 정보 조회 메서드 (운동 코드로)
    public ExerciseBean getExerciseByCode(int exerciseCode) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ExerciseBean exercise = new ExerciseBean(); // 빈 객체 생성하여 반환 (null 방지)

        try {
            conn = pool.getConnection();
            String sql = "SELECT exercise_name, exercise_category, exercise_type, exercise_MET "
                       + "FROM exercise WHERE exercise_code = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, exerciseCode);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                exercise.setExerciseCode(exerciseCode);
                exercise.setExerciseName(rs.getString("exercise_name"));
                exercise.setExerciseCategory(rs.getString("exercise_category"));
                exercise.setExerciseType(rs.getString("exercise_type"));
                exercise.setExerciseMET(rs.getFloat("exercise_MET"));
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL 실행 중 오류 발생! 운동 코드: " + exerciseCode);
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
        return exercise;
    }

    // 🔹 운동 정보 수정 메서드
    public boolean updateExercise(int exerciseCode, String exerciseName,
                                 String exerciseCategory, String exerciseType, float exerciseMET) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean isUpdated = false;

        try {
            conn = pool.getConnection();
            String sql = "UPDATE exercise SET exercise_name = ?, exercise_category = ?, "
                       + "exercise_type = ?, exercise_MET = ? WHERE exercise_code = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, exerciseName);
            pstmt.setString(2, exerciseCategory);
            pstmt.setString(3, exerciseType);
            pstmt.setFloat(4, exerciseMET);
            pstmt.setInt(5, exerciseCode);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                isUpdated = true;
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL 실행 중 오류 발생! 운동 코드: " + exerciseCode);
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
        return isUpdated;
    }

    // 🔹 운동 정보 삭제 메서드
    public boolean deleteExercise(int exerciseCode) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean isDeleted = false;

        try {
            conn = pool.getConnection();
            String sql = "DELETE FROM exercise WHERE exercise_code = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, exerciseCode);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                isDeleted = true;
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL 실행 중 오류 발생! 운동 코드: " + exerciseCode);
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
        return isDeleted;
    }

    public int getTotalBurnedCalories(String userId) {
        int totalBurnedCalories = 0;
        String sql = "SELECT SUM(exercise_calories) FROM exercise_log " +
                     "WHERE user_id = ? AND DATE(exercise_date) = CURDATE()";

        try (Connection conn = pool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                totalBurnedCalories = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalBurnedCalories;
    }


}