package ui_n_utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import main.MainUserPanel;

public class TabUtil {
    private static JPanel selectedTabLine; // 선택된 탭 바 (검은색 라인)
    private static JButton selectedButton; // 현재 선택된 버튼
    private static JButton[] tabButtons;
    private static MainUserPanel panelManager;
    
    // 📌 MainUserPanel 연결
    public static void setPanelManager(MainUserPanel manager) {
        panelManager = manager;
    }
  
    // 📌 상단 탭 바 생성
    public static JPanel createTabBar(ActionListener listener) {
        JPanel tabPanel = new JPanel();
        tabPanel.setLayout(null);
        tabPanel.setBounds(0, 90, 440, 50);
        tabPanel.setBackground(AppTheme.CARD);

        // 📌 탭 버튼 생성
        JButton btnDaily = createTabButton("일일 현황", 0, 146, "HomeDaily", listener);
        JButton btnMeal = createTabButton("식단 기록", 147, 146, "HomeMeal", listener);
        JButton btnTarget = createTabButton("목표 달성", 294, 146, "HomeTarget", listener);
        tabButtons = new JButton[]{btnDaily, btnMeal, btnTarget};
        
        btnDaily.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        tabPanel.add(btnDaily);
        tabPanel.add(btnMeal);
        tabPanel.add(btnTarget);

        // 📌 선택된 탭 표시하는 검은색 라인 (초기값: "일일 현황" 선택)
        selectedTabLine = new JPanel();
        selectedTabLine.setBackground(AppTheme.PRIMARY_DARK);
        selectedTabLine.setBounds(0, 47, 146, 3); // 버튼 바로 아래 위치
        tabPanel.add(selectedTabLine);

        // 📌 기본 선택된 탭 설정
        setSelectedTab(btnDaily, "HomeDaily");

        return tabPanel;
    }

    // 📌 개별 탭 버튼 생성
    private static JButton createTabButton(String text, int x, int width, String panelName, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(AppTheme.BODY_BOLD_FONT);
        button.setBounds(x, 0, width, 47);
        
        // ✅ 버튼 배경 관련 설정 (잔상 제거)
        button.setBackground(AppTheme.CARD);
        button.setForeground(AppTheme.TEXT_SECONDARY);
        button.setOpaque(true); // 🔹 배경을 명확히 설정
        button.setContentAreaFilled(true);
        button.setBorderPainted(false); // 🔹 테두리 제거
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.CENTER);

        button.addActionListener(e -> {
            setSelectedTab(button, panelName);
            selectedTabLine.setBounds(x, 47, width, 3);
            if (listener != null) {
                listener.actionPerformed(e);
            }
        });

        return button;
    }

    // 📌 선택된 탭 변경 & 패널 전환 (변경 후)
    public static void setSelectedTab(String tabName) {
        JButton newSelectedButton = null;
        if (tabButtons != null) {
            for (JButton button : tabButtons) {
                if (tabName.equals(button.getText())) {
                    newSelectedButton = button;
                    break;
                }
            }
        }

        if (newSelectedButton != null) {
            setSelectedTab(newSelectedButton, null);
            selectedTabLine.setBounds(
                    newSelectedButton.getX(), 47, newSelectedButton.getWidth(), 3);
        }
    }
    
    
    // 📌 선택된 탭 변경 & 패널 전환
    private static void setSelectedTab(JButton button, String panelName) {
        selectedButton = button; // 현재 선택된 버튼 저장
        if (tabButtons != null) {
            for (JButton tabButton : tabButtons) {
                tabButton.setForeground(
                        tabButton == selectedButton ? AppTheme.PRIMARY_DARK : AppTheme.TEXT_SECONDARY);
            }
        }
    }
}
