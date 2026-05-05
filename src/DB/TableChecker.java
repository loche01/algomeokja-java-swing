package DB;

import java.sql.*;

/**
 * 데이터베이스 테이블 구조를 확인하는 유틸리티 클래스
 */
public class TableChecker {
    private static DBConnectionMgr pool;
    
    public static void main(String[] args) {
        try {
            pool = DBConnectionMgr.getInstance();
            checkTargetTable();
        } catch (Exception e) {
            System.err.println("❌ 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * target 테이블의 구조를 확인하고 출력합니다.
     */
    private static void checkTargetTable() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = pool.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            
            // 테이블 존재 여부 확인
            rs = metaData.getTables(null, null, "target", null);
            if (!rs.next()) {
                System.out.println("❌ target 테이블이 존재하지 않습니다!");
                return;
            }
            
            System.out.println("✅ target 테이블 구조 확인:");
            System.out.println("----------------------------------");
            
            // 컬럼 정보 조회
            rs = metaData.getColumns(null, null, "target", null);
            System.out.println("컬럼명\t\t데이터타입\t크기\t널허용");
            System.out.println("----------------------------------");
            
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String dataType = rs.getString("TYPE_NAME");
                int columnSize = rs.getInt("COLUMN_SIZE");
                String nullable = rs.getInt("NULLABLE") == 0 ? "NO" : "YES";
                
                System.out.println(columnName + "\t\t" + dataType + "\t" + columnSize + "\t" + nullable);
            }
            
            System.out.println("----------------------------------");
            
            // 테이블 데이터 확인
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT COUNT(*) FROM target");
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("✅ target 테이블에 " + count + "개의 레코드가 있습니다.");
            }
            
            // 테이블 구조 수정 필요 여부 확인
            rs = metaData.getColumns(null, null, "target", "age");
            boolean hasAgeColumn = rs.next();
            
            rs = metaData.getColumns(null, null, "target", "height");
            boolean hasHeightColumn = rs.next();
            
            rs = metaData.getColumns(null, null, "target", "gender");
            boolean hasGenderColumn = rs.next();
            
            if (hasAgeColumn || hasHeightColumn || hasGenderColumn) {
                System.out.println("⚠️ 테이블 구조 수정이 필요합니다!");
                System.out.println("다음 SQL 스크립트를 실행하세요:");
                System.out.println("----------------------------------");
                System.out.println("-- 기존 테이블 백업");
                System.out.println("CREATE TABLE target_backup AS SELECT * FROM target;");
                System.out.println();
                
                if (hasAgeColumn) System.out.println("ALTER TABLE target DROP COLUMN age;");
                if (hasHeightColumn) System.out.println("ALTER TABLE target DROP COLUMN height;");
                if (hasGenderColumn) System.out.println("ALTER TABLE target DROP COLUMN gender;");
                
                System.out.println("----------------------------------");
            } else {
                System.out.println("✅ 테이블 구조가 올바릅니다.");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ SQL 오류: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ 일반 오류: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) pool.freeConnection(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
} 