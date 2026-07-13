package main;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JPanel;
import panel.*;
import ui_n_utils.UserSessionManager;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private String initialPanel = "HomeDaily"; // 로그인 후 초기 패널 (기본값: 일일현황)

    public MainFrame() {
        setTitle("알고먹자");
        setSize(440, 956);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout); 
        mainPanel.setBackground(Color.WHITE);

        // 패널 추가
        loginPanel = new LoginPanel(this);
        JoinPanel joinPanel = new JoinPanel(this);
        FindIdPwPhone findIdPwPhone = new FindIdPwPhone(this); // 🔹 추가
        BodyInfoSetPanel bodyInfoSetPanel = new BodyInfoSetPanel(this); // 🔹 BodyInfoSetPanel 추가
        MainUserPanel mainUserPanel = new MainUserPanel();
        MainAdminPanel mainAdminPanel = new MainAdminPanel();
        
        mainPanel.add(loginPanel, "login");
        mainPanel.add(joinPanel, "join");
        mainPanel.add(bodyInfoSetPanel, "bodyInfoSet");
        mainPanel.add(findIdPwPhone, "findIdPw");
        mainPanel.add(mainUserPanel, "mainUser");
        mainPanel.add(mainAdminPanel, "mainAdmin");
    
        
        add(mainPanel);
        setVisible(true);
    }

    // 🔹 화면 전환 메서드
    public void showPanel(String name) {
    	 if (UserSessionManager.getInstance().isAdmin()) {
    		 cardLayout.show(mainPanel, "mainAdmin");
    	    } else {
            cardLayout.show(mainPanel, name);
        }
    }

    public void showLoginAfterLogout() {
        loginPanel.resetForLogout();
        showPanel("login");
    }
    
  //🔹 BodyInfoSetPanel로 이동할 때 userId 전달
   public void moveToBodyInfoSet(String userId) {
       BodyInfoSetPanel bodyInfoSetPanel = (BodyInfoSetPanel) mainPanel.getComponent(2); // 패널 인덱스 확인 필요
       bodyInfoSetPanel.setUserId(userId); // userId 설정
       showPanel("bodyInfoSet");
        
    }

    // 🔹 로그인 후 초기 패널 설정 메서드
    public void setInitialPanel(String panelName) {
        this.initialPanel = panelName;
    }
    
    // 🔹 로그인 후 초기 패널 반환 메서드
    public String getInitialPanel() {
        return initialPanel;
    }

    // 🔹 MainUserPanel에서 특정 패널을 표시하는 메서드
    public void showMainUserPanel(String panelName) {
        // MainUserPanel 찾기
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof MainUserPanel) {
                MainUserPanel mainUserPanel = (MainUserPanel) comp;
                // 지정된 패널로 이동
                mainUserPanel.showPanel(panelName);
                
                // HomeDaily 패널로 이동할 경우 네비게이션 바의 홈 버튼도 선택
                if (panelName.equals("HomeDaily")) {
                    ui_n_utils.NavUtil.selectHomeButton();
                }
                break;
            }
        }
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
