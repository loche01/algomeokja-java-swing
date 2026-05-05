package ui_n_utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import main.MainUserPanel;

public class TabUtil {
    private static JPanel selectedTabLine; // 선택된 탭 바 (검은색 라인)
    private static JButton selectedButton; // 현재 선택된 버튼
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
        tabPanel.setBackground(Color.WHITE);

        // 📌 탭 버튼 생성
        JButton btnDaily = createTabButton("일일 현황", 0, 146, "HomeDaily", listener);
        JButton btnMeal = createTabButton("식단 기록", 147, 146, "HomeMeal", listener);
        JButton btnTarget = createTabButton("목표 달성", 294, 146, "HomeTarget", listener);
        
        btnDaily.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        tabPanel.add(btnDaily);
        tabPanel.add(btnMeal);
        tabPanel.add(btnTarget);

        // 📌 선택된 탭 표시하는 검은색 라인 (초기값: "일일 현황" 선택)
        selectedTabLine = new JPanel();
        selectedTabLine.setBackground(Color.BLACK);
        selectedTabLine.setBounds(0, 44, 146, 6); // 버튼 바로 아래 위치
        tabPanel.add(selectedTabLine);

        // 📌 기본 선택된 탭 설정
        setSelectedTab(btnDaily, "HomeDaily");

        return tabPanel;
    }

    // 📌 개별 탭 버튼 생성
    private static JButton createTabButton(String text, int x, int width, String panelName, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Inter", Font.BOLD, 16));
        button.setBounds(x, 0, width, 44);
        
        // ✅ 버튼 배경 관련 설정 (잔상 제거)
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setOpaque(true); // 🔹 배경을 명확히 설정
        button.setContentAreaFilled(false); // 🔹 기본 배경 제거 (회색 잔상 방지)
        button.setBorderPainted(false); // 🔹 테두리 제거
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.CENTER);

        button.addActionListener(e -> {
            setSelectedTab(button, panelName);
            selectedTabLine.setBounds(x, 44, width, 6);
            if (listener != null) {
                listener.actionPerformed(e);
            }
        });

        return button;
    }

    // 📌 선택된 탭 변경 & 패널 전환 (변경 후)
    public static void setSelectedTab(String tabName) {
        // 🔹 현재 선택된 버튼을 찾음
        JButton newSelectedButton = null;
        int newX = 0, newWidth = 146; // 기본값 (일일 현황)

        switch (tabName) {
            case "일일 현황":
                newSelectedButton = selectedButton; // 기존 선택된 버튼
                newX = 0;
                newWidth = 146;
                break;
            case "식단 기록":
                newSelectedButton = selectedButton;
                newX = 147;
                newWidth = 146;
                break;
            case "목표 달성":
                newSelectedButton = selectedButton;
                newX = 294;
                newWidth = 146;
                break;
        }

        if (newSelectedButton != null) {
            // 🔹 선택된 버튼 스타일 변경
            newSelectedButton.setBackground(Color.LIGHT_GRAY);
            newSelectedButton.setForeground(Color.BLACK);
            selectedTabLine.setBounds(newX, 44, newWidth, 6);
        }
    }
    
    
    // 📌 선택된 탭 변경 & 패널 전환
    private static void setSelectedTab(JButton button, String panelName) {
        selectedButton = button; // 현재 선택된 버튼 저장
    }
}
