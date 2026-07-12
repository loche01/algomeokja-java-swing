package DB;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;
import model.FoodBean;

/** 식단 헤더와 상세 저장의 트랜잭션 경계를 관리한다. */
public class MealSaveService {
    private final DBConnectionMgr pool;
    private final MealDAO mealDAO;
    private final MealLogDAO mealLogDAO;

    public MealSaveService() {
        pool = DBConnectionMgr.getInstance();
        mealDAO = new MealDAO();
        mealLogDAO = new MealLogDAO();
    }

    public boolean saveMealWithLogs(String userId, String mealType, Vector<FoodBean> foods) {
        if (userId == null || userId.trim().isEmpty()
                || mealType == null || mealType.trim().isEmpty()
                || foods == null || foods.isEmpty()) {
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

            int mealCode = mealDAO.insertMeal(conn, userId, mealType);
            if (mealCode <= 0) {
                throw new SQLException("식사 기록의 생성 키를 확인할 수 없습니다.");
            }

            mealLogDAO.insertMealLogs(conn, mealCode, foods);
            conn.commit();
            return true;
        } catch (Exception e) {
            rollback(conn, transactionStarted, e);
            System.err.println("식단 저장 트랜잭션 처리 중 오류가 발생했습니다.");
            e.printStackTrace();
            return false;
        } finally {
            restoreAutoCommitAndRelease(conn, originalAutoCommit);
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

    private void restoreAutoCommitAndRelease(Connection conn, boolean originalAutoCommit) {
        if (conn == null) {
            return;
        }

        try {
            conn.setAutoCommit(originalAutoCommit);
            pool.freeConnection(conn);
        } catch (SQLException restoreError) {
            System.err.println("식단 저장 후 DB 연결 상태 복구에 실패했습니다.");
            restoreError.printStackTrace();
            pool.removeConnection(conn);
        }
    }
}
