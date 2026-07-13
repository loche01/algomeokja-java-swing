package DB;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NoticeFileDAO {
    private static final int MAX_DISPLAY_FILE_NAME_LENGTH = 255;
    private static final String APP_DIRECTORY_NAME = ".algomeokja";
    private static final String ATTACHMENT_DIRECTORY_NAME = "notice-attachments";

    private final DBConnectionMgr pool;

    public NoticeFileDAO() {
        DBConnectionMgr connectionPool = null;
        try {
            connectionPool = DBConnectionMgr.getInstance();
        } catch (Exception e) {
            System.err.println("공지 첨부파일 저장소를 초기화하지 못했습니다.");
        }
        this.pool = connectionPool;
    }

    /**
     * 선택 파일을 앱 전용 저장소에 복사하고 기존 notice_files path 계약으로 등록합니다.
     * adminId 매개변수는 기존 공개 호출 계약을 유지하기 위해 남겨 둡니다.
     */
    public boolean uploadFile(int noticeId, String adminId, File file) {
        if (noticeId <= 0 || file == null || pool == null) {
            return false;
        }

        Path sourcePath;
        try {
            sourcePath = file.toPath().toAbsolutePath().normalize();
        } catch (InvalidPathException e) {
            return false;
        }
        if (!Files.isRegularFile(sourcePath) || !Files.isReadable(sourcePath)) {
            return false;
        }

        Path attachmentDirectory;
        Path storedPath = null;
        boolean databaseSaved = false;
        try {
            attachmentDirectory = getAttachmentDirectory();
            String originalFileName = sourcePath.getFileName().toString();
            String displayFileName = fitDisplayFileName(originalFileName);
            String storedFileName = createStoredFileName(originalFileName);
            storedPath = attachmentDirectory.resolve(storedFileName).normalize();
            if (!storedPath.getParent().equals(attachmentDirectory)) {
                return false;
            }

            Files.copy(sourcePath, storedPath);
            databaseSaved = insertFileRecord(noticeId, displayFileName, storedFileName);
            return databaseSaved;
        } catch (IOException | SecurityException e) {
            System.err.println("공지 첨부파일을 저장하지 못했습니다.");
            return false;
        } finally {
            if (!databaseSaved && storedPath != null) {
                deleteQuietly(storedPath);
            }
        }
    }

    public List<Map<String, Object>> getFilesByNoticeId(int noticeId) {
        List<Map<String, Object>> fileList = new ArrayList<>();
        if (noticeId <= 0 || pool == null) {
            return fileList;
        }

        Connection conn = null;
        String sql = "SELECT file_id, file_name FROM notice_files WHERE notice_id = ?";
        try {
            conn = pool.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, noticeId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> fileData = new HashMap<>();
                        fileData.put("fileId", rs.getInt("file_id"));
                        fileData.put("fileName", rs.getString("file_name"));
                        fileList.add(fileData);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("공지 첨부파일 목록을 불러오지 못했습니다.");
        } finally {
            freeConnection(conn);
        }
        return fileList;
    }

    public boolean downloadFile(int fileId) {
        if (fileId <= 0 || pool == null) {
            return false;
        }

        StoredFile storedFile = findStoredFile(fileId);
        if (storedFile == null) {
            return false;
        }

        Path destinationPath = null;
        try {
            Path attachmentDirectory = getAttachmentDirectory();
            Path sourcePath = attachmentDirectory.resolve(storedFile.storedFileName).normalize();
            if (!sourcePath.startsWith(attachmentDirectory)
                    || !Files.isRegularFile(sourcePath)
                    || !Files.isReadable(sourcePath)) {
                return false;
            }

            Path downloadDirectory = getDownloadDirectory();
            destinationPath = createAvailableDownloadPath(
                    downloadDirectory, storedFile.displayFileName);
            Files.copy(sourcePath, destinationPath);
            return true;
        } catch (IOException | InvalidPathException | SecurityException e) {
            if (destinationPath != null) {
                deleteQuietly(destinationPath);
            }
            System.err.println("공지 첨부파일을 내려받지 못했습니다.");
            return false;
        }
    }

    public List<Map<String, Object>> getFilesByNoticeNum(int noticeNum) {
        return getFilesByNoticeId(noticeNum);
    }

    /**
     * 공지 삭제 전에 앱 저장 첨부파일 이름을 준비합니다. 준비 결과에는 실제 경로가
     * 노출되지 않으며, 공지 삭제가 성공한 뒤 cleanupDeletedNoticeFiles()에 전달합니다.
     */
    public NoticeFileCleanup prepareNoticeFileCleanup(int noticeId) {
        if (noticeId <= 0 || pool == null) {
            return NoticeFileCleanup.unavailable(noticeId);
        }

        Connection conn = null;
        List<String> storedFileNames = new ArrayList<>();
        String sql = "SELECT file_path FROM notice_files WHERE notice_id = ?";
        try {
            conn = pool.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, noticeId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String storedFileName = rs.getString("file_path");
                        if (storedFileName != null && !storedFileName.isBlank()) {
                            storedFileNames.add(storedFileName);
                        }
                    }
                }
            }
            return NoticeFileCleanup.available(noticeId, storedFileNames);
        } catch (Exception e) {
            System.err.println("공지 첨부파일 삭제 정보를 준비하지 못했습니다.");
            return NoticeFileCleanup.unavailable(noticeId);
        } finally {
            freeConnection(conn);
        }
    }

    /**
     * 공지 DB 삭제가 성공한 뒤 첨부 DB 레코드와 앱 저장 복사본을 정리합니다.
     * 물리 파일 삭제 실패는 공지 삭제 결과를 되돌리지 않고 false로 알립니다.
     */
    public boolean cleanupDeletedNoticeFiles(NoticeFileCleanup preparedCleanup) {
        if (preparedCleanup == null || preparedCleanup.noticeId <= 0 || pool == null) {
            return false;
        }

        NoticeFileCleanup cleanup = preparedCleanup;
        if (!cleanup.available) {
            cleanup = prepareNoticeFileCleanup(cleanup.noticeId);
        }
        if (!cleanup.available || !deleteFileRecords(cleanup.noticeId)) {
            return false;
        }
        return deleteManagedStoredFiles(cleanup.storedFileNames);
    }

    private boolean insertFileRecord(int noticeId, String displayFileName, String storedFileName) {
        Connection conn = null;
        boolean originalAutoCommit = true;
        boolean transactionStarted = false;
        String sql = "INSERT INTO notice_files (notice_id, file_name, file_path, uploaded_time) "
                + "VALUES (?, ?, ?, NOW())";
        try {
            conn = pool.getConnection();
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            transactionStarted = true;

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, noticeId);
                pstmt.setString(2, displayFileName);
                pstmt.setString(3, storedFileName);
                if (pstmt.executeUpdate() != 1) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            rollbackQuietly(conn, transactionStarted);
            System.err.println("공지 첨부파일 정보를 저장하지 못했습니다.");
            return false;
        } finally {
            restoreAutoCommit(conn, originalAutoCommit, transactionStarted);
            freeConnection(conn);
        }
    }

    private StoredFile findStoredFile(int fileId) {
        Connection conn = null;
        String sql = "SELECT file_name, file_path FROM notice_files WHERE file_id = ?";
        try {
            conn = pool.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, fileId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        String displayFileName = rs.getString("file_name");
                        String storedFileName = rs.getString("file_path");
                        if (displayFileName != null && storedFileName != null) {
                            return new StoredFile(displayFileName, storedFileName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("공지 첨부파일 정보를 불러오지 못했습니다.");
        } finally {
            freeConnection(conn);
        }
        return null;
    }

    private boolean deleteFileRecords(int noticeId) {
        Connection conn = null;
        boolean originalAutoCommit = true;
        boolean transactionStarted = false;
        String sql = "DELETE FROM notice_files WHERE notice_id = ?";
        try {
            conn = pool.getConnection();
            originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            transactionStarted = true;

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, noticeId);
                pstmt.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (Exception e) {
            rollbackQuietly(conn, transactionStarted);
            System.err.println("삭제된 공지의 첨부파일 정보를 정리하지 못했습니다.");
            return false;
        } finally {
            restoreAutoCommit(conn, originalAutoCommit, transactionStarted);
            freeConnection(conn);
        }
    }

    private boolean deleteManagedStoredFiles(List<String> storedFileNames) {
        Path attachmentDirectory;
        try {
            attachmentDirectory = getAttachmentDirectoryPath();
            if (!Files.exists(attachmentDirectory)) {
                return true;
            }
        } catch (IOException | InvalidPathException | SecurityException e) {
            System.err.println("공지 첨부파일 저장소를 확인하지 못했습니다.");
            return false;
        }

        boolean allDeleted = true;
        for (String storedFileName : storedFileNames) {
            if (!isManagedStoredFileName(storedFileName)) {
                continue;
            }

            try {
                Path storedPath = attachmentDirectory.resolve(storedFileName)
                        .toAbsolutePath().normalize();
                if (!storedPath.startsWith(attachmentDirectory)
                        || !attachmentDirectory.equals(storedPath.getParent())) {
                    continue;
                }
                Files.deleteIfExists(storedPath);
            } catch (IOException | InvalidPathException | SecurityException e) {
                allDeleted = false;
            }
        }
        if (!allDeleted) {
            System.err.println("일부 공지 첨부파일을 정리하지 못했습니다.");
        }
        return allDeleted;
    }

    private boolean isManagedStoredFileName(String storedFileName) {
        try {
            Path relativePath = Path.of(storedFileName);
            if (relativePath.isAbsolute() || relativePath.getNameCount() != 1) {
                return false;
            }

            String fileName = relativePath.getFileName().toString();
            int dotIndex = fileName.lastIndexOf('.');
            String uuidPart = dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
            String extension = dotIndex > 0 ? fileName.substring(dotIndex) : "";
            UUID.fromString(uuidPart);
            return extension.equals(safeExtension(fileName));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private Path getAttachmentDirectory() throws IOException {
        Path directory = getAttachmentDirectoryPath();
        Files.createDirectories(directory);
        return directory;
    }

    private Path getAttachmentDirectoryPath() throws IOException {
        return getUserHomeDirectory()
                .resolve(APP_DIRECTORY_NAME)
                .resolve(ATTACHMENT_DIRECTORY_NAME)
                .toAbsolutePath()
                .normalize();
    }

    private Path getDownloadDirectory() throws IOException {
        Path directory = getUserHomeDirectory().resolve("Downloads").toAbsolutePath().normalize();
        Files.createDirectories(directory);
        return directory;
    }

    private Path getUserHomeDirectory() throws IOException {
        String userHome = System.getProperty("user.home");
        if (userHome == null || userHome.isBlank()) {
            throw new IOException("사용자 홈 디렉터리를 확인할 수 없습니다.");
        }
        return Path.of(userHome);
    }

    private String createStoredFileName(String originalFileName) {
        return UUID.randomUUID() + safeExtension(originalFileName);
    }

    private String safeExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex <= 0 || dotIndex == fileName.length() - 1) {
            return "";
        }

        String extension = fileName.substring(dotIndex);
        if (extension.length() > 17) {
            return "";
        }
        for (int i = 1; i < extension.length(); i++) {
            if (!Character.isLetterOrDigit(extension.charAt(i))) {
                return "";
            }
        }
        return extension;
    }

    private String fitDisplayFileName(String fileName) {
        if (fileName.codePointCount(0, fileName.length()) <= MAX_DISPLAY_FILE_NAME_LENGTH) {
            return fileName;
        }

        String extension = safeExtension(fileName);
        int extensionCodePoints = extension.codePointCount(0, extension.length());
        int baseCodePoints = MAX_DISPLAY_FILE_NAME_LENGTH - extensionCodePoints;
        int baseEnd = fileName.offsetByCodePoints(0, Math.max(baseCodePoints, 1));
        return fileName.substring(0, baseEnd) + extension;
    }

    private Path createAvailableDownloadPath(Path directory, String displayFileName)
            throws IOException {
        String safeFileName = sanitizeDownloadFileName(displayFileName);
        Path candidate = directory.resolve(safeFileName);
        if (!Files.exists(candidate)) {
            return candidate;
        }

        int dotIndex = safeFileName.lastIndexOf('.');
        String baseName = dotIndex > 0 ? safeFileName.substring(0, dotIndex) : safeFileName;
        String extension = dotIndex > 0 ? safeFileName.substring(dotIndex) : "";
        for (int suffix = 1; suffix <= 10_000; suffix++) {
            candidate = directory.resolve(baseName + " (" + suffix + ")" + extension);
            if (!Files.exists(candidate)) {
                return candidate;
            }
        }
        throw new IOException("저장 가능한 파일 이름을 만들 수 없습니다.");
    }

    private String sanitizeDownloadFileName(String fileName) {
        StringBuilder sanitized = new StringBuilder();
        for (int i = 0; i < fileName.length(); i++) {
            char character = fileName.charAt(i);
            if (character < 32 || "\\/:*?\"<>|".indexOf(character) >= 0) {
                sanitized.append('_');
            } else {
                sanitized.append(character);
            }
        }
        String result = sanitized.toString().trim();
        while (result.endsWith(".")) {
            result = result.substring(0, result.length() - 1).trim();
        }
        if (result.isEmpty() || ".".equals(result) || "..".equals(result)) {
            return "attachment";
        }

        int dotIndex = result.indexOf('.');
        String baseName = (dotIndex >= 0 ? result.substring(0, dotIndex) : result).toUpperCase();
        if (isWindowsReservedName(baseName)) {
            return "_" + result;
        }
        return result;
    }

    private boolean isWindowsReservedName(String baseName) {
        if ("CON".equals(baseName) || "PRN".equals(baseName)
                || "AUX".equals(baseName) || "NUL".equals(baseName)) {
            return true;
        }
        if (baseName.length() == 4) {
            String prefix = baseName.substring(0, 3);
            char suffix = baseName.charAt(3);
            return ("COM".equals(prefix) || "LPT".equals(prefix))
                    && suffix >= '1' && suffix <= '9';
        }
        return false;
    }

    private void rollbackQuietly(Connection conn, boolean transactionStarted) {
        if (conn == null || !transactionStarted) {
            return;
        }
        try {
            conn.rollback();
        } catch (SQLException ignored) {
            // 경로·SQL 상세를 출력하지 않고 원래 실패 결과를 유지합니다.
        }
    }

    private void restoreAutoCommit(Connection conn, boolean originalAutoCommit,
            boolean transactionStarted) {
        if (conn == null || !transactionStarted) {
            return;
        }
        try {
            conn.setAutoCommit(originalAutoCommit);
        } catch (SQLException ignored) {
            // 연결 반환은 계속 진행합니다.
        }
    }

    private void freeConnection(Connection conn) {
        if (conn != null && pool != null) {
            pool.freeConnection(conn);
        }
    }

    private void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException | SecurityException ignored) {
            // 실제 파일 경로를 출력하지 않습니다.
        }
    }

    private static class StoredFile {
        private final String displayFileName;
        private final String storedFileName;

        private StoredFile(String displayFileName, String storedFileName) {
            this.displayFileName = displayFileName;
            this.storedFileName = storedFileName;
        }
    }

    public static final class NoticeFileCleanup {
        private final int noticeId;
        private final List<String> storedFileNames;
        private final boolean available;

        private NoticeFileCleanup(int noticeId, List<String> storedFileNames,
                boolean available) {
            this.noticeId = noticeId;
            this.storedFileNames = List.copyOf(storedFileNames);
            this.available = available;
        }

        private static NoticeFileCleanup available(int noticeId,
                List<String> storedFileNames) {
            return new NoticeFileCleanup(noticeId, storedFileNames, true);
        }

        private static NoticeFileCleanup unavailable(int noticeId) {
            return new NoticeFileCleanup(noticeId, List.of(), false);
        }
    }
}
