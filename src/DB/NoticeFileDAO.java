package DB;

import java.io.*;
import java.sql.*;
import java.util.*;

public class NoticeFileDAO {
    private DBConnectionMgr pool;

    public NoticeFileDAO() {
        try {
            pool = DBConnectionMgr.getInstance();  // 🔥 예외 처리 추가
        } catch (Exception e) {
            System.err.println("❌ DBConnectionMgr 초기화 오류 발생!");
            e.printStackTrace();
            pool = null;  // 예외 발생 시 pool을 null로 설정하여 이후의 NullPointerException 방지
        }
    }

    /**
     * 📂 파일 업로드
     */
    public boolean uploadFile(int noticeId, String adminId, File file) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        FileInputStream fileInputStream = null;
        boolean isUploaded = false;

        if (noticeId <= 0 || file == null || !file.exists()) {
            System.err.println("❌ 파일 업로드 실패: 유효하지 않은 데이터");
            return false;
        }

        try {
            // 🔹 예외 처리 추가
            try {
                conn = pool.getConnection();  // ✅ 예외 처리 추가하여 Unhandled Exception 해결
            } catch (Exception e) {
                System.err.println("❌ DB 연결 중 오류 발생!");
                e.printStackTrace();
                return false;  // DB 연결 실패 시 업로드 중단
            }

            conn.setAutoCommit(false);
            String sql = "INSERT INTO notice_files (notice_id, admin_id, file_name, file_data, upload_time) VALUES (?, ?, ?, ?, NOW())";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, noticeId);
            pstmt.setString(2, adminId);
            pstmt.setString(3, file.getName());

            // 🔹 FileInputStream을 try-with-resources 바깥에서 선언
            fileInputStream = new FileInputStream(file);
            pstmt.setBlob(4, fileInputStream);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                isUploaded = true;
                conn.commit();
            } else {
                System.err.println("❌ 파일 업로드 실패!");
            }
        } catch (SQLException | IOException e) {
            System.err.println("❌ SQL 실행 중 오류 발생!");
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    pool.freeConnection(conn);
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
        return isUploaded;
    }


    /**
     * 📂 특정 공지사항 ID에 첨부된 파일 목록 조회
     */
    public List<Map<String, Object>> getFilesByNoticeId(int noticeId) {
        List<Map<String, Object>> fileList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();  // 🔥 예외 처리 추가
            if (conn == null) {
                throw new SQLException("DB 연결을 가져올 수 없습니다.");
            }

            String sql = "SELECT file_id, file_name FROM notice_files WHERE notice_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, noticeId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> fileData = new HashMap<>();
                fileData.put("fileId", rs.getInt("file_id"));
                fileData.put("fileName", rs.getString("file_name"));
                fileList.add(fileData);
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL 실행 중 오류 발생!");
            e.printStackTrace();
        } catch (Exception e) { // 🔥 일반 예외 처리 추가
            System.err.println("❌ 예기치 않은 오류 발생!");
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
        return fileList;
    }

    /**
     * 📥 파일 다운로드 (DB에서 읽어서 로컬에 저장)
     */
    public boolean downloadFile(int fileId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection();  // 🔥 예외 처리 추가
            if (conn == null) {
                throw new SQLException("DB 연결을 가져올 수 없습니다.");
            }

            String sql = "SELECT file_name, file_data FROM notice_files WHERE file_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, fileId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String fileName = rs.getString("file_name");
                InputStream fileData = rs.getBinaryStream("file_data");

                File outputFile = new File(System.getProperty("user.home") + "/Downloads/" + fileName);
                try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileData.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                return true;
            }
        } catch (SQLException | IOException e) {
            System.err.println("❌ 파일 다운로드 중 오류 발생!");
            e.printStackTrace();
        } catch (Exception e) { // 🔥 일반 예외 처리 추가
            System.err.println("❌ 예기치 않은 오류 발생!");
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
        return false;
    }
    
    public List<Map<String, Object>> getFilesByNoticeNum(int noticeNum) {
        List<Map<String, Object>> fileList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // ✅ 예외 처리 추가 (DB 연결 실패 시 빈 리스트 반환)
            try {
                conn = pool.getConnection();
            } catch (Exception e) {
                System.err.println("❌ DB 연결 중 오류 발생!");
                e.printStackTrace();
                return fileList; // 🔹 DB 연결 실패 시 빈 리스트 반환
            }

            // ✅ `notice_num`이 아닌 `notice_id`로 변경 (DB 컬럼명 오류 수정)
            String sql = "SELECT file_id, file_name FROM notice_files WHERE notice_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, noticeNum);  // ✅ 여전히 noticeNum을 받아서 notice_id에 매핑
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> fileData = new HashMap<>();
                fileData.put("fileId", rs.getInt("file_id"));
                fileData.put("fileName", rs.getString("file_name"));
                fileList.add(fileData);
            }
        } catch (SQLException e) {
            System.err.println("❌ 공지사항 첨부파일 조회 중 SQL 오류가 발생했습니다.");
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
        return fileList;
    }

}
