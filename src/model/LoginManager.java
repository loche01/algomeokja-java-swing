package model;

/**
 * 현재 로그인한 사용자 정보를 관리하는 싱글톤 클래스
 */
public class LoginManager {
    private static LoginManager instance;
    private UserBean currentUser;
    
    // 생성자는 private으로 선언 (싱글톤 패턴)
    private LoginManager() {
        currentUser = null;
    }
    
    // 싱글톤 인스턴스 얻기
    public static synchronized LoginManager getInstance() {
        if (instance == null) {
            instance = new LoginManager();
        }
        return instance;
    }
    
    // 로그인 설정 - UserSessionManager와 동기화
    public void setCurrentUser(UserBean user) {
        this.currentUser = user;
        // UserSessionManager와 동기화
        if (user != null) {
            if (user.isAdmin()) {
                // 관리자인 경우
                ui_n_utils.UserSessionManager.getInstance().setAdminSession(user.getUser_id());
                System.out.println("✅ [LoginManager] 관리자 정보 설정 완료: " + user.getUser_id());
            } else {
                // 일반 사용자인 경우
                ui_n_utils.UserSessionManager.getInstance().setCurrentUser(user);
            }
        }
    }
    
    // 로그아웃 - UserSessionManager와 동기화
    public void logout() {
        this.currentUser = null;
        ui_n_utils.UserSessionManager.getInstance().clearSession();
    }
    
    // 현재 사용자 반환
    public UserBean getCurrentUser() {
        // UserSessionManager에서 최신 정보 가져오기
        UserBean sessionUser = ui_n_utils.UserSessionManager.getInstance().getCurrentUser();
        if (sessionUser != null && (currentUser == null || !sessionUser.getUser_id().equals(currentUser.getUser_id()))) {
            currentUser = sessionUser;
        }
        return currentUser;
    }
    
    // 로그인 상태 확인
    public boolean isLoggedIn() {
        return getCurrentUser() != null;
    }
    
    // 현재 로그인한 사용자의 ID를 반환
    public String getUserId() {
        UserBean user = getCurrentUser();
        return user != null ? user.getUser_id() : null;
    }
    
    // 관리자 여부 확인
    public boolean isAdmin() {
        UserBean user = getCurrentUser();
        return user != null && user.isAdmin();
    }
} 