//package ui_n_utils;
package ui_n_utils;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import main.MainUserPanel;

/*
add(NavUtil.createNavigationBar(this));
이거만 추가하면 됩니다.
*/
public class NavUtil {
    private static BottonNav selectedButton; // 현재 선택된 버튼
    private static MainUserPanel mainUserPanel; // 패널 전환을 위한 MainUserPanel 참조
    // 하단 네비게이션 바 생성
    private static BottonNav[] navButtons; // 네비게이션 버튼 배열 저장
    
    public static JPanel createNavigationBar(MainUserPanel panelManager, ActionListener listener) {
    	 mainUserPanel = panelManager;
    	 if (panelManager == null) {
    	        System.out.println("🚨 [오류] MainUserPanel이 NavUtil에 전달되지 않음!");
    	    }
    	JPanel navPanel = new JPanel();
        navPanel.setLayout(null);
        navPanel.setBounds(0, 826, 440, 90); 
        navPanel.setBackground(Color.WHITE);

        // 네비게이션 버튼 생성
        BottonNav btnHome = new BottonNav(
                "홈", "/images/home1.png", "/images/home.png");
        BottonNav btnCalendar = new BottonNav(
                "캘린더", "/images/calender1.png", "/images/calender.png");
        BottonNav btnWorkout = new BottonNav(
                "운동", "/images/gym1.png", "/images/gym.png");
        BottonNav btnProfile = new BottonNav(
                "내 정보", "/images/profile1.png", "/images/profile.png");

        // 위치 설정
        btnHome.setBounds(0, 0, 100, 100);
        btnCalendar.setBounds(105, 0, 100, 100);
        btnWorkout.setBounds(215, 0, 100, 100);
        btnProfile.setBounds(325, 0, 100, 100);

        // 버튼 배열
        BottonNav[] buttons = {btnHome, btnCalendar, btnWorkout, btnProfile};
        navButtons = buttons; // 버튼 배열 저장

        // 기본 선택 버튼 (홈)
        setSelectedButton(btnHome, buttons);

        // 버튼 이벤트 리스너 추가
        for (BottonNav btn : buttons) {
            btn.addActionListener(e -> {
                setSelectedButton(btn, buttons); //  선택 상태 변경 추가
                switchPage(btn.getText());
                if (listener != null) listener.actionPerformed(e); // 원래 이벤트 실행
            });
            navPanel.add(btn);
        }

        return navPanel;
    }
    
    // 📌 화면 전환 메서드 수정
    private static void switchPage(String pageName) {
        if (mainUserPanel != null) {
            // 🔹 MainUserPanel에서 정의한 패널 이름과 일치하도록 수정
            switch (pageName) {
                case "홈": 
                    mainUserPanel.showPanel("HomeDaily"); 
                    break;
                case "캘린더": 
                    mainUserPanel.showPanel("Calendar"); 
                    break;
                case "운동": 
                    mainUserPanel.showPanel("ExerciseSearch"); 
                    break;
                case "내 정보": 
                    mainUserPanel.showPanel("MyPage"); 
                    break;
                default:
                    System.out.println("🚨 [오류] 알 수 없는 페이지: " + pageName);
            }
        } else {
            System.out.println("🚨 mainUserPanel이 null입니다! 패널 전환 불가");
        }
    }

    
    // 📌 버튼 선택 상태 관리
    private static void setSelectedButton(BottonNav selectedBtn, BottonNav[] buttons) {
        for (BottonNav btn : buttons) btn.setSelected(btn == selectedBtn);
        //selectedBtn.setForeground(Color.BLACK);
        selectedButton = selectedBtn;
    }

    //  네비게이션 버튼 클래스 (이전 BottonNav)
    public static class BottonNav extends JButton {
    	private ImageIcon defaultIcon, selectedIcon;
        //private JLabel textLabel;

        public BottonNav(String text, String defaultIconPath, String selectedIconPath) {
            setPreferredSize(new Dimension(80, 80));
            setBackground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);

            // 아이콘 로드
            defaultIcon = ClasspathIconLoader.loadScaled(
                    NavUtil.class, defaultIconPath, 50, 50);
            selectedIcon = ClasspathIconLoader.loadScaled(
                    NavUtil.class, selectedIconPath, 50, 50);
            
            // 텍스트 스타일 적용
            setText(text);
            setFont(new Font("맑은 고딕", Font.BOLD, 11));
            
            setHorizontalTextPosition(SwingConstants.CENTER);
            setVerticalTextPosition(
                    defaultIcon == null && selectedIcon == null
                            ? SwingConstants.CENTER : SwingConstants.BOTTOM);
            setToolTipText(text);
        }

        public void setSelected(boolean isSelected) {
            ImageIcon requestedIcon = isSelected ? selectedIcon : defaultIcon;
            ImageIcon fallbackIcon = isSelected ? defaultIcon : selectedIcon;
            setIcon(requestedIcon != null ? requestedIcon : fallbackIcon);
            setForeground(isSelected ? Color.BLACK : Color.GRAY);
        }
        
    }

    // 홈 버튼을 선택 상태로 만드는 메서드
    public static void selectHomeButton() {
        if (navButtons != null && navButtons.length > 0) {
            // 첫 번째 버튼이 홈 버튼
            BottonNav homeButton = navButtons[0];
            setSelectedButton(homeButton, navButtons);
        } else {
            System.out.println("❌ 네비게이션 버튼이 초기화되지 않았습니다.");
        }
    }
}
