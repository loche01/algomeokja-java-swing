package main;

import java.awt.*;
import javax.swing.*;
import model.ExerciseBean;
import model.UserBean;
import panel.*;
import ui_n_utils.HeaderUtil;
import ui_n_utils.NavUtil;
import ui_n_utils.TabUtil;

public class MainUserPanel extends JPanel {
    private HomeDailyPanel homeDailyPanel;
    private HomeMealPanel homeMealPanel;
    public FoodListPanel foodListPanel;
    public FoodInfoPanel foodInfoPanel;
    
    private HomeTargetPanel homeTargetPanel;
    private MymeGoalPanel mymegoal;
    
    private CalendarPanel calendarPanel;
    
    private ExerciseSearchPanel exerciseSearchPanel;
    private ExerciseListPanel exerciseListPanel;
    private ExerciseCaloriePanel exerciseCaloriePanel;
    
    private MyPagePanel myPagePanel;
    private MyMemberPanel myMemberPanel; // 📌 MyMemberPanel 추가
    private MyBodyPanel myBodyPanel;

    private NoticePanel noticePanel;
    private NoticeDetailPanel noticeDetailPanel;
    
    private JPanel navPanel, tabPanel; // 하단 네비게이션 바
    
    private UserBean loggedInUser; // userBean
    private JLabel userInfoLabel;  // 화면에 표시
    private CardLayout cardLayout;
    
    // 이전 패널 이름을 저장하는 변수 추가
    private String previousPanel = "HomeDaily"; // 기본값은 홈 화면
    private String currentPanel = "HomeDaily"; // 현재 패널

    
    public MainUserPanel() { 
        setBackground(Color.WHITE);
        setBounds(0, 0, 440, 956); // ✅ 프레임 크기에 맞춤
        setLayout(null);
        // 📌 1. 상단 패널 (HeaderUtil 적용)
        JPanel header = HeaderUtil.createHeader("알고먹자",this, e -> showPanel("Notice"));
        header.setBounds(0, 0, 440, 100);
        add(header);

        // 📌 2. 상단 탭 바 (TabUtil 적용)
        TabUtil.setPanelManager(this); // 🔹 TabUtil과 연결하여 패널 전환 가능하게 함
        tabPanel = TabUtil.createTabBar(e -> handleTabClick(e));
        add(tabPanel);

        // 📌 3. 메인 패널 (각 패널 크기 조정)
        // 🔹 패널 초기화
        homeDailyPanel = new HomeDailyPanel();
        homeDailyPanel.setName("HomeDaily"); // 패널 이름 설정
        
        homeMealPanel = new HomeMealPanel(this);
        homeMealPanel.setName("HomeMeal"); // 패널 이름 설정
        
        foodListPanel = new FoodListPanel(this);
        foodInfoPanel = new FoodInfoPanel(this);
        
        homeTargetPanel = new HomeTargetPanel(this);
        homeTargetPanel.setName("HomeTarget"); // 패널 이름 설정
        
        mymegoal = new MymeGoalPanel(this);
        
        calendarPanel = new CalendarPanel();
        
        exerciseSearchPanel = new ExerciseSearchPanel(this);
        exerciseListPanel = new ExerciseListPanel(this);
        exerciseCaloriePanel = new ExerciseCaloriePanel(this);
        
        myPagePanel = new MyPagePanel(this);
        myMemberPanel = new MyMemberPanel(this);
        myBodyPanel = new MyBodyPanel(this);
        
        noticePanel = new NoticePanel(this);
        noticeDetailPanel = new NoticeDetailPanel(this);
        
        // 📌 모든 패널의 위치 및 크기 설정
        JPanel[] panels = { homeDailyPanel, homeMealPanel, foodListPanel, foodInfoPanel,
                            homeTargetPanel, mymegoal, 
                            calendarPanel, 
                            exerciseSearchPanel, exerciseListPanel, exerciseCaloriePanel,
                            myPagePanel, myMemberPanel, myBodyPanel,  
                            noticePanel, noticeDetailPanel};

        for (JPanel panel : panels) {
            panel.setBounds(0, 140, 440, 686);
            add(panel);
            System.out.println("✅ MainUserPanel:"+ panel+"추가 완료");
        }

        // userInfoLabel 초기화
        userInfoLabel = new JLabel();
        userInfoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        userInfoLabel.setBounds(10, 10, 400, 25);
        add(userInfoLabel);

        // ✅ 패널 초기화 후 `showPanel()` 실행 - 항상 일일현황 패널로 시작
        showPanel("HomeDaily");
        TabUtil.setSelectedTab("일일 현황");
        
        // 📌 4. 하단 네비게이션 바 (NavUtil 적용)
        navPanel = NavUtil.createNavigationBar(this, e -> handleNavButtonClick(e));
        add(navPanel);
        
        // 초기화 시 네비게이션 바의 홈 버튼 선택
        ui_n_utils.NavUtil.selectHomeButton();
    }
    
    // 나머지 메서드는 그대로 유지
    public HomeTargetPanel getHomeTargetPanel() {
        return homeTargetPanel;
    }
    
    public MyPagePanel getMyPagePanel() {
        return myPagePanel; // MyPagePanel 인스턴스 반환
    }
    
    public NoticeDetailPanel getNoticeDetailPanel() {
        return noticeDetailPanel;
    }
    
	 // In MainUserPanel.java
	 // Add this new method
	 public void showExerciseListPanel(String category) {
	     // Reset the ExerciseListPanel with the selected category
	     exerciseListPanel.setCategory(category);
	     // Show the panel
	     showPanel("ExerciseList");
	 }
 
	// showExerciseCaloriePanel 메서드 구현
	public void showExerciseCaloriePanel(ExerciseBean exercise) {
	  // ExerciseCaloriePanel에 운동 정보 설정
	  exerciseCaloriePanel.updateExerciseInfo(exercise);
	  // 패널 표시
	  exerciseCaloriePanel.setBounds(0, 90, 440, 736);
	  showPanel("ExerciseCalorie");
	}
    
    // 📌 탭 바를 보이거나 숨기는 메서드 추가
    public void showTabBar(boolean isVisible) {
        tabPanel.setVisible(isVisible);
    }
    
    // 📌 패널 전환
    public void showPanel(String panelName) {
        // 이전 패널 이름 저장 (Notice 또는 noticeDetailPanel이 아닌 경우에만)
        if (!currentPanel.equals("Notice") && !currentPanel.equals("noticeDetailPanel")) {
            previousPanel = currentPanel;
            System.out.println("✅ 이전 패널 저장: " + previousPanel);
        }
        
        // 현재 패널 업데이트
        currentPanel = panelName;
        System.out.println("✅ 현재 패널 설정: " + currentPanel);
        
        // HomeDaily 패널로 이동할 경우 네비게이션 바의 홈 버튼도 선택
        if (panelName.equals("HomeDaily")) {
            ui_n_utils.NavUtil.selectHomeButton();
            // 상단 탭바의 일일현황 탭도 선택
            ui_n_utils.TabUtil.setSelectedTab("일일 현황");
            // 탭바 표시
            showTabBar(true);
        }
        
        JPanel[] panels = { homeDailyPanel, homeMealPanel, foodListPanel, foodInfoPanel,
							homeTargetPanel, mymegoal, 
			                calendarPanel, 
			                exerciseSearchPanel, exerciseListPanel, exerciseCaloriePanel,
			                myPagePanel, myMemberPanel, myBodyPanel,  
			                noticePanel, noticeDetailPanel};

        for (JPanel panel : panels) {
        	System.out.println("🔄 showPanel 호출됨: " + panelName);
            panel.setVisible(false);
            System.out.println("🔍 숨김 처리: " + panel.getClass().getName());
        }
        
        // HomeMeal 패널이 표시될 때 사용자 정보 즉시 업데이트
        if (panelName.equals("HomeMeal")) {
            // 패널을 먼저 표시하고 이벤트 발생
            homeMealPanel.setVisible(true);
            // 컴포넌트 리스너가 작동하도록 이벤트 발생
            homeMealPanel.dispatchEvent(new java.awt.event.ComponentEvent(
                homeMealPanel, java.awt.event.ComponentEvent.COMPONENT_SHOWN));
        }
        
        if (panelName.equals("Calendar") || panelName.equals("ExerciseSearch") ||
                panelName.equals("MyPage") || panelName.equals("Notice") || 
                panelName.equals("MyMember")|| panelName.equals("MyBody")
                || panelName.equals("foodList")|| panelName.equals("foodInfo")
                || panelName.equals("noticeDetailPanel")|| panelName.equals("MymeGoal")
                || panelName.equals("ExerciseList")|| panelName.equals("ExerciseCalorie")) {
                showTabBar(false);
                switch (panelName) {
                    case "Calendar": calendarPanel.refresh(); calendarPanel.setBounds(0, 90, 440, 736); calendarPanel.setVisible(true); break;
                    case "ExerciseSearch": exerciseSearchPanel.setBounds(0, 90, 440, 736); exerciseSearchPanel.setVisible(true); break;
                    case "MyPage": myPagePanel.setBounds(0, 90, 440, 736); myPagePanel.setVisible(true); break;
                    case "Notice": noticePanel.setBounds(0, 90, 440, 736); noticePanel.setVisible(true); break;
                    case "noticeDetailPanel" : noticeDetailPanel.setBounds(0, 90, 440, 736);noticeDetailPanel.setVisible(true); break;
                    case "MyMember": myMemberPanel.setBounds(0, 90, 440, 736); myMemberPanel.setVisible(true); break; // 📌 MyMemberPanel 추가
                    case "MyBody": myBodyPanel.setBounds(0, 90, 440, 736); myBodyPanel.setVisible(true); break; // 📌 myBodyPanel 추가
                    case "foodList": foodListPanel.setVisible(true); showTabBar(true); break; // 📌 myBodyPanel 추가
                    case "foodInfo": foodInfoPanel.setBounds(0, 90, 440, 736);foodInfoPanel.setVisible(true); showTabBar(false); break; 
                    case "MymeGoal": mymegoal.setBounds(0, 90, 440, 736); mymegoal.setVisible(true);break;
                    case "ExerciseList": exerciseListPanel.setBounds(0, 90, 440, 736); exerciseListPanel.setVisible(true); break;
                    case "ExerciseCalorie": exerciseCaloriePanel.setBounds(0, 90, 440, 736); exerciseCaloriePanel.setVisible(true); break;
                }
            } else {
                showTabBar(true);
                for (JPanel panel : panels) {
                    if (panel.getName() != null && panel.getName().equals(panelName)) {
                        panel.setVisible(true); 
                    }
                }
            }
        switch (panelName) {
            case "HomeDaily": homeDailyPanel.setVisible(true); break;
            case "HomeMeal":  /* 이미 위에서 처리됨 */ break;
            case "HomeTarget": homeTargetPanel.refreshData(); homeTargetPanel.setVisible(true); break;
           }
        printPanelComponents();
    }
    public void printPanelComponents() {
        System.out.println("📌 현재 MainUserPanel에 포함된 패널 목록:");
        for (Component comp : getComponents()) {
            System.out.println("  - " + comp.getClass().getName() + " | Visible: " + comp.isVisible());
        }
    }
    // 📌 탭 클릭 시 패널 전환
    private void handleTabClick(java.awt.event.ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        String buttonText = clickedButton.getText().trim();
        switch (buttonText) {
            case "일일 현황": showPanel("HomeDaily"); break;
            case "식단 기록": showPanel("HomeMeal"); break;
            case "목표 달성": showPanel("HomeTarget"); break;
        }
    }

    // 📌 네비게이션 바 버튼 클릭 시 패널 전환
    private void handleNavButtonClick(java.awt.event.ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        String buttonText = clickedButton.getText().trim();

        switch (buttonText) {
            case "🏠 홈": 
                showPanel("HomeDaily"); 
                showTabBar(true);
                TabUtil.setSelectedTab("일일 현황");
                break;
            case "📅 캘린더": 
                showPanel("Calendar"); 
                showTabBar(false);
                break;
            case "🏋 운동": 
                showPanel("ExerciseSearch"); 
                showTabBar(false);
                break;
            case "👤 내 정보": 
                showPanel("MyPage"); 
                showTabBar(false);
                break;
        }
    }
    
 // ★ userBean 전달
    public void setLoggedInUser(UserBean user) {
        if (user == null) {
            System.err.println("❌ 로그인 사용자 정보가 null입니다.");
            return;
        }
        
        this.loggedInUser = user;
        
        if (userInfoLabel != null) {
            userInfoLabel.setText("환영합니다, " + user.getUser_name() + "님(" + user.getUser_id() + ")");
            System.out.println("✅ 로그인 성공! 아이디: " + user.getUser_id() + ", 이름: " + user.getUser_name());
        }
    }

    // 필요하면 getter
    public UserBean getLoggedInUser() {
        return loggedInUser;
    }

    // HomeMealPanel getter 메서드 추가
    public HomeMealPanel getHomeMealPanel() {
        return homeMealPanel;
    }

    // 이전 패널로 돌아가는 메서드 추가
    public void goToPreviousPanel() {
        System.out.println("🔙 이전 패널로 돌아가기: " + previousPanel);
        showPanel(previousPanel);
    }

    // 이전 패널 이름 getter 메서드 추가
    public String getPreviousPanel() {
        return previousPanel;
    }
}
