package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.UserBean;
import security.PasswordHasher;

public class UserDAO {
    public enum UpdateUserResult {
        SUCCESS,
        CURRENT_PASSWORD_REQUIRED,
        CURRENT_PASSWORD_MISMATCH,
        HASH_ERROR,
        ERROR
    }

    private DBConnectionMgr pool;
    
    public UserDAO() {
        try {
            pool = DBConnectionMgr.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public UpdateUserResult validateCurrentPassword(String userId, char[] currentRawPassword) {
        if (currentRawPassword == null || currentRawPassword.length == 0) {
            return UpdateUserResult.CURRENT_PASSWORD_REQUIRED;
        }

        Connection con = null;
        try {
            con = pool.getConnection();
            String storedPassword = findStoredPassword(con, userId);
            if (storedPassword == null) {
                return UpdateUserResult.ERROR;
            }
            return matchesStoredPassword(currentRawPassword, storedPassword)
                    ? UpdateUserResult.SUCCESS : UpdateUserResult.CURRENT_PASSWORD_MISMATCH;
        } catch (Exception e) {
            e.printStackTrace();
            return UpdateUserResult.ERROR;
        } finally {
            pool.freeConnection(con);
        }
    }

    public UpdateUserResult updateUserInfoAndPassword(UserBean user,
            char[] currentRawPassword, char[] newRawPassword) {
        if (currentRawPassword == null || currentRawPassword.length == 0) {
            return UpdateUserResult.CURRENT_PASSWORD_REQUIRED;
        }
        if (newRawPassword == null || newRawPassword.length == 0) {
            return UpdateUserResult.ERROR;
        }

        Connection con = null;
        try {
            con = pool.getConnection();
            String storedPassword = findStoredPassword(con, user.getUser_id());
            if (storedPassword == null) {
                return UpdateUserResult.ERROR;
            }
            if (!matchesStoredPassword(currentRawPassword, storedPassword)) {
                return UpdateUserResult.CURRENT_PASSWORD_MISMATCH;
            }

            String encodedPassword;
            try {
                encodedPassword = PasswordHasher.hash(newRawPassword);
            } catch (RuntimeException e) {
                System.err.println("회원정보의 새 비밀번호를 안전하게 처리하지 못했습니다.");
                return UpdateUserResult.HASH_ERROR;
            }

            String updateSql = "UPDATE user SET user_name=?, user_phone=?, user_email=?, user_pwd=? "
                    + "WHERE user_id=? AND user_pwd=?";
            try (PreparedStatement updateStatement = con.prepareStatement(updateSql)) {
                updateStatement.setString(1, user.getUser_name());
                updateStatement.setString(2, user.getUser_phone());
                updateStatement.setString(3, user.getUser_email());
                updateStatement.setString(4, encodedPassword);
                updateStatement.setString(5, user.getUser_id());
                updateStatement.setString(6, storedPassword);
                return updateStatement.executeUpdate() == 1
                        ? UpdateUserResult.SUCCESS : UpdateUserResult.ERROR;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return UpdateUserResult.ERROR;
        } finally {
            pool.freeConnection(con);
        }
    }

    public UpdateUserResult updateBasicUserInfo(UserBean user) {
        Connection con = null;
        try {
            con = pool.getConnection();
            String sql = "UPDATE user SET user_name=?, user_phone=?, user_email=? WHERE user_id=?";
            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
                pstmt.setString(1, user.getUser_name());
                pstmt.setString(2, user.getUser_phone());
                pstmt.setString(3, user.getUser_email());
                pstmt.setString(4, user.getUser_id());
                return pstmt.executeUpdate() == 1 ? UpdateUserResult.SUCCESS : UpdateUserResult.ERROR;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return UpdateUserResult.ERROR;
        } finally {
            pool.freeConnection(con);
        }
    }

    private String findStoredPassword(Connection con, String userId) throws SQLException {
        String selectSql = "SELECT user_pwd FROM user WHERE user_id=?";
        try (PreparedStatement selectStatement = con.prepareStatement(selectSql)) {
            selectStatement.setString(1, userId);
            try (ResultSet rs = selectStatement.executeQuery()) {
                return rs.next() ? rs.getString("user_pwd") : null;
            }
        }
    }

    private boolean matchesStoredPassword(char[] rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        if (PasswordHasher.hasRecognizedPrefix(storedPassword)) {
            return PasswordHasher.verify(rawPassword, storedPassword);
        }

        int difference = rawPassword.length ^ storedPassword.length();
        int length = Math.max(rawPassword.length, storedPassword.length());
        for (int i = 0; i < length; i++) {
            char rawCharacter = i < rawPassword.length ? rawPassword[i] : 0;
            char storedCharacter = i < storedPassword.length() ? storedPassword.charAt(i) : 0;
            difference |= rawCharacter ^ storedCharacter;
        }
        return difference == 0;
    }

    public String findUserIdByNameAndPhone(String userName, String userPhone) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String foundUserId = null;
        int matchCount = 0;

        try {
            con = pool.getConnection();
            String sql = "SELECT user_id FROM user WHERE user_name=? AND user_phone=? ORDER BY user_id";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, userName);
            pstmt.setString(2, userPhone);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                foundUserId = rs.getString("user_id");
                matchCount++;
                if (matchCount > 1) {
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            pool.freeConnection(con, pstmt, rs);
        }

        return matchCount == 1 ? foundUserId : null;
    }

    public boolean verifyUserIdentity(String userId, String userName, String userPhone) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = pool.getConnection();
            String sql = "SELECT COUNT(*) FROM user WHERE user_id=? AND user_name=? AND user_phone=?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, userName);
            pstmt.setString(3, userPhone);
            rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            pool.freeConnection(con, pstmt, rs);
        }
    }

    public boolean updateUserPasswordFromRaw(String userId, char[] rawPassword) {
        Connection con = null;
        PreparedStatement pstmt = null;

        String encodedPassword;
        try {
            encodedPassword = PasswordHasher.hash(rawPassword);
        } catch (RuntimeException e) {
            System.err.println("새 비밀번호를 안전하게 처리하지 못했습니다.");
            return false;
        }

        try {
            con = pool.getConnection();
            String sql = "UPDATE user SET user_pwd=? WHERE user_id=?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, encodedPassword);
            pstmt.setString(2, userId);
            return pstmt.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            pool.freeConnection(con, pstmt);
        }
    }
}
