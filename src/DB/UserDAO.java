package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import model.UserBean;
import security.PasswordHasher;

public class UserDAO {
    private DBConnectionMgr pool;
    
    public UserDAO() {
        try {
            pool = DBConnectionMgr.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 사용자 정보를 업데이트합니다.
     * @param user 업데이트할 사용자 정보
     * @param oldPassword 이전 비밀번호 (비밀번호 변경 시 필요)
     * @return 업데이트 성공 여부
     */
    public boolean updateUser(UserBean user, String oldPassword) {
        Connection con = null;
        PreparedStatement pstmt = null;
        boolean result = false;
        
        try {
            con = pool.getConnection();
            
            // 비밀번호 변경 여부 확인
            boolean passwordChanged = user.getUser_pwd() != null && !user.getUser_pwd().isEmpty() && !user.getUser_pwd().equals(oldPassword);
            
            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE user SET ");
            sql.append("user_name=?, user_phone=?, user_email=?");
            
            // 비밀번호가 변경된 경우에만 업데이트
            if (passwordChanged) {
                sql.append(", user_pwd=?");
            }
            
            sql.append(" WHERE user_id=?");
            
            pstmt = con.prepareStatement(sql.toString());
            
            pstmt.setString(1, user.getUser_name());
            pstmt.setString(2, user.getUser_phone());
            pstmt.setString(3, user.getUser_email());
            
            if (passwordChanged) {
                pstmt.setString(4, user.getUser_pwd());
                pstmt.setString(5, user.getUser_id());
            } else {
                pstmt.setString(4, user.getUser_id());
            }
            
            int count = pstmt.executeUpdate();
            if (count > 0) {
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(con, pstmt);
        }
        
        return result;
    }
    
    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     * @param userId 조회할 사용자 ID
     * @return 사용자 정보
     */
    public UserBean getUserById(String userId) {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        UserBean user = null;
        
        try {
            con = pool.getConnection();
            String sql = "SELECT * FROM user WHERE user_id=?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                user = new UserBean();
                user.setUser_id(rs.getString("user_id"));
                user.setUser_pwd(rs.getString("user_pwd"));
                user.setUser_name(rs.getString("user_name"));
                user.setUser_phone(rs.getString("user_phone"));
                user.setUser_email(rs.getString("user_email"));
                user.setUser_createdtime(rs.getTimestamp("user_createdtime"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.freeConnection(con, pstmt, rs);
        }
        
        return user;
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
