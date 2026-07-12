package ui_n_utils;

import model.UserBean;

public class UserSessionManager {
    private static UserSessionManager instance;
    private static UserBean currentUser; // 🔥 static 변수로 유지하여 세션 유지
    private static boolean isAdmin = false;

    private UserSessionManager() {} // 🔹 생성자 private으로 유지

    // 🔹 싱글톤 패턴을 보장하는 메서드
    public static synchronized UserSessionManager getInstance() {
        if (instance == null) {
            instance = new UserSessionManager();
        }
        return instance;
    }

    // 🔹 일반 사용자 로그인 설정
    public void setCurrentUser(UserBean user) {
        if (user != null && user.getUser_id() != null) {
            currentUser = user;
            isAdmin = false;  // ✅ 일반 유저 로그인 시 관리자 플래그 해제
            // System.out.println("✅ [UserSessionManager] 로그인 성공: " + user.getUser_id());
        } else {
            // System.out.println("❌ [UserSessionManager] 로그인 실패 - 유효하지 않은 유저 정보!");
        }
    }

    // 🔹 관리자 로그인 처리 메서드 ✅
    public void setAdminSession(String adminId) {
        currentUser = new UserBean();  // ✅ UserBean 객체 생성
        currentUser.setUser_id(adminId);
        currentUser.setUser_name("관리자"); // 관리자 이름 설정
        currentUser.setUser_role("ADMIN"); // 관리자 역할 설정
        isAdmin = true;  // ✅ 관리자 로그인 시 플래그 설정
    }

    // 🔹 현재 로그인한 사용자 가져오기
    public UserBean getCurrentUser() {
        return currentUser;
    }

    // 🔹 관리자 여부 확인 기능 추가 ✅
    public boolean isAdmin() {
        return isAdmin;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public void logout() {
        currentUser = null;
    }
    
    // 🔹 세션 초기화 (로그아웃 시 사용)
    public void clearSession() {
        currentUser = null;
        isAdmin = false;
    }

    // 🔹 세션 유지 확인 (디버깅용)
    public boolean isSessionValid() {
        return currentUser != null && currentUser.getUser_id() != null;
    }
}
