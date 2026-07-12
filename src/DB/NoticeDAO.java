package DB;

import model.NoticeBean;
import DB.DBConnectionMgr;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoticeDAO {

    private DBConnectionMgr pool;

    public NoticeDAO() {
        try {
            pool = DBConnectionMgr.getInstance();
        } catch (Exception e) {
            System.err.println("❌ DBConnectionMgr 초기화 오류 발생!");
            e.printStackTrace();
        }
    }

    // 🔹 공지사항 추가 (관리자 전용)
    public boolean addNotice(String adminId, String title, String content) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean isAdded = false;

        try {
            conn = pool.getConnection();
            String sql = "INSERT INTO notice (admin_id, notice_title, notice_coment, notice_time) VALUES (?, ?, ?, NOW())";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, adminId);
            pstmt.setString(2, title);
            pstmt.setString(3, content);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                isAdded = true;
            }

        } catch (SQLException e) {
            System.err.println("❌ 공지사항 추가 중 SQL 오류가 발생했습니다.");
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
        return isAdded;
    }

    // 🔹 공지사항 목록 조회
    public List<NoticeBean> getAllNotices() {
        List<NoticeBean> notices = new ArrayList<>();
        String sql = "SELECT * FROM notice ORDER BY notice_time DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = pool.getConnection(); // ✅ 예외 처리 추가
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                NoticeBean notice = new NoticeBean();
                notice.setNotice_num(rs.getInt("notice_num"));
                notice.setAdmin_id(rs.getString("admin_id"));
                notice.setNotice_title(rs.getString("notice_title"));
                notice.setNotice_content(rs.getString("notice_coment"));
                notice.setNotice_time(rs.getTimestamp("notice_time")); // ✅ 중요! notice_time 설정

                notices.add(notice);
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL 실행 중 오류 발생!");
            e.printStackTrace();
        } catch (Exception e) { // ✅ 추가된 예외 처리
            System.err.println("❌ 데이터베이스 연결 오류!");
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
        return notices;
    }

 // 🔹 공지사항 상세 조회 (ID로 찾기)
    public NoticeBean getNoticeById(int noticeNum) {
        NoticeBean notice = null;
        String sql = "SELECT * FROM notice WHERE notice_num = ?";

        try (Connection conn = pool.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, noticeNum);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    notice = new NoticeBean();
                    notice.setNotice_num(rs.getInt("notice_num"));
                    notice.setAdmin_id(rs.getString("admin_id"));
                    notice.setNotice_title(rs.getString("notice_title"));
                    notice.setNotice_content(rs.getString("notice_coment"));
                    notice.setNotice_time(rs.getTimestamp("notice_time"));

                }
            }
        } catch (SQLException e) {
            System.err.println("❌ 공지사항 상세 조회 중 SQL 오류가 발생했습니다.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ 알 수 없는 오류 발생!");
            e.printStackTrace();
        }
        
        return notice;
    }


    // 🔹 공지사항 수정 (관리자 전용)
    public boolean updateNotice(int noticeNum, String newTitle, String newContent) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean isUpdated = false;

        try {
            conn = pool.getConnection();
            String sql = "UPDATE notice SET notice_title = ?, notice_coment = ? WHERE notice_num = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newTitle);
            pstmt.setString(2, newContent);
            pstmt.setInt(3, noticeNum);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                isUpdated = true;
            } else {
                System.err.println("❌ 수정할 공지사항을 찾을 수 없습니다.");
            }

        } catch (SQLException e) {
            System.err.println("❌ 공지사항 수정 중 SQL 오류가 발생했습니다.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ 공지사항 수정 중 알 수 없는 오류가 발생했습니다.");
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

    // 🔹 공지사항 삭제 (관리자 전용)
    public boolean deleteNotice(int noticeNum) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean isDeleted = false;

        try {
            conn = pool.getConnection();
            String sql = "DELETE FROM notice WHERE notice_num = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, noticeNum);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                isDeleted = true;
            } else {
                System.err.println("❌ 삭제할 공지사항을 찾을 수 없습니다.");
            }

        } catch (SQLException e) {
            System.err.println("❌ 공지사항 삭제 중 SQL 오류가 발생했습니다.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ 공지사항 삭제 중 알 수 없는 오류가 발생했습니다.");
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
    
    public int getLastNoticeId() {
        int noticeId = -1;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // 🔹 예외 처리 추가
            try {
                conn = pool.getConnection();
            } catch (Exception e) { // 🔥 Exception 잡기
                System.err.println("❌ DB 연결 중 오류 발생!");
                e.printStackTrace();
                return noticeId; // 🔹 DB 연결 실패 시 -1 반환
            }

            String sql = "SELECT notice_num FROM notice ORDER BY notice_time DESC LIMIT 1";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                noticeId = rs.getInt("notice_num");
            }

        } catch (SQLException e) {
            System.err.println("❌ 최근 공지사항 ID 조회 오류 발생!");
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
        return noticeId;
    }
    public List<Map<String, Object>> getFilesByNoticeId(int noticeId) { 
        List<Map<String, Object>> fileList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // ✅ 예외 처리 추가
            try {
                conn = pool.getConnection();
            } catch (Exception e) {
                System.err.println("❌ DB 연결 중 오류 발생!");
                e.printStackTrace();
                return fileList; // 🔹 DB 연결 실패 시 빈 리스트 반환
            }
            
            // ✅ SQL에서 올바른 컬럼명(`notice_id`) 사용
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
