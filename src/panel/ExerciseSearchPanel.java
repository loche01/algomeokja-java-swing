package panel;

import javax.swing.*;

import DB.ExerciseDAO;
import main.MainUserPanel;
import ui_n_utils.ClasspathIconLoader;
import ui_n_utils.RoundedComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExerciseSearchPanel extends JPanel {
    private RoundedComponent button, abutton, bbutton, cbutton, dbutton, allButton;
    private JTextField searchField;
    private JButton searchButton;
    private MainUserPanel mainUserPanel;
    private ExerciseDAO exerciseDAO;
    
    public ExerciseSearchPanel(MainUserPanel mainUserPanel) {
        this.mainUserPanel = mainUserPanel;
        setLayout(null);
        setBackground(new Color(0xD9D9D9));
        
        // DAO 초기화
        exerciseDAO = new ExerciseDAO();

     // 검색바 패널 생성
        JPanel searchPanel = new JPanel(null);
        searchPanel.setBounds(15, 50, 410, 40);
        searchPanel.setBackground(new Color(217, 217, 217));

        // 1️⃣ 검색 필드 생성
        searchField = new JTextField("어떤 운동을 하셨나요?");
        searchField.setBounds(40, 5, 300, 30);
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(null);

        // 검색 필드 클릭 시 플레이스홀더 효과 제거
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("어떤 운동을 하셨나요?")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("어떤 운동을 하셨나요?");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        // 2️⃣ 검색 버튼 생성 및 초기화
        ImageIcon searchIcon = ClasspathIconLoader.loadScaled(
                ExerciseSearchPanel.class, "/images/search.png", 20, 20);
        searchButton = searchIcon != null ? new JButton(searchIcon) : new JButton("검색");
        if (searchIcon == null) {
            searchButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            searchButton.setMargin(new Insets(0, 0, 0, 0));
        }
        searchButton.setToolTipText("운동 검색");

        // 3️⃣ 속성 설정 및 이벤트 리스너 추가
        searchButton.setBounds(5, 5, 30, 30);
        searchButton.setContentAreaFilled(false);
        searchButton.setBorderPainted(false);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = searchField.getText();
                if (!keyword.equals("어떤 운동을 하셨나요?") && !keyword.isEmpty()) {
                    performSearch(keyword);
                }
            }
        });

        // 4️⃣ 검색 패널에 검색 필드와 버튼 추가
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // 5️⃣ 패널에 둥근 테두리 효과 적용
        RoundedComponent roundedSearchPanel = new RoundedComponent(390, 40, 40, "panel", "",
                Color.LIGHT_GRAY, Color.WHITE, Color.black, "", Font.PLAIN, 0);
        roundedSearchPanel.setBounds(0, 0, 390, 40);
        searchPanel.add(roundedSearchPanel);

        // 6️⃣ 검색 패널을 ExerciseSearchPanel에 추가
        add(searchPanel);

        
        // 운동 부위 버튼 ("가슴")
        button = new RoundedComponent(110, 35, 30, "button", "가슴", Color.white, Color.white, Color.black, "맑은고딕",
                Font.BOLD, 14);
        button.setBounds(26, 115, 110, 35);
        add(button);

        // 가슴 버튼에 이벤트 리스너 추가
        button.getButton().addActionListener(e -> {
            if (mainUserPanel != null) {
            	mainUserPanel.showExerciseListPanel("가슴");
            }
        });
        
        // 운동 부위 버튼 ("등")
        abutton = new RoundedComponent(110, 35, 30, "button", "등", Color.white, Color.white, Color.black, "맑은고딕",
                Font.BOLD, 14);
        abutton.setBounds(156, 115, 110, 35);
        add(abutton);

        // 등 버튼에 이벤트 리스너 추가
        abutton.getButton().addActionListener(e -> {
            if (mainUserPanel != null) {
            	mainUserPanel.showExerciseListPanel("등");
            }
        });

        // 운동 부위 버튼 ("하체")
        bbutton = new RoundedComponent(110, 35, 30, "button", "하체", Color.white, Color.white, Color.black, "맑은고딕",
                Font.BOLD, 14);
        bbutton.setBounds(286, 115, 110, 35);
        add(bbutton);

        // 하체 버튼에 이벤트 리스너 추가
        bbutton.getButton().addActionListener(e -> {
            if (mainUserPanel != null) {
            	mainUserPanel.showExerciseListPanel("하체");
            }
        });

        // 운동 부위 버튼 ("팔 & 어깨")
        cbutton = new RoundedComponent(110, 35, 30, "button", "팔&어깨", Color.white, Color.white, Color.black, "맑은고딕",
                Font.BOLD, 14);
        cbutton.setBounds(90, 175, 110, 35);
        add(cbutton);

        // 팔 & 어깨 버튼에 이벤트 리스너 추가
        cbutton.getButton().addActionListener(e -> {
            if (mainUserPanel != null) {
            	  mainUserPanel.showExerciseListPanel("팔&어깨");
            }
        });

        // 운동 부위 버튼 ("유산소 & 코어")
        dbutton = new RoundedComponent(110, 35, 30, "button", "유산소&코어", Color.white, Color.white, Color.black, "맑은고딕",
                Font.BOLD, 14);
        dbutton.setBounds(230, 175, 110, 35);
        add(dbutton);

        // 유산소 & 코어 버튼에 이벤트 리스너 추가
        dbutton.getButton().addActionListener(e -> {
            if (mainUserPanel != null) {
            	 // Pass "유산소 & 코어" category to ExerciseListPanel
                mainUserPanel.showExerciseListPanel("유산소&코어");
            }
        });

        // 전체 운동 버튼
        allButton = new RoundedComponent(110, 35, 30, "button", "전체", Color.white, Color.white, Color.black, "맑은고딕",
                Font.BOLD, 14);
        allButton.setBounds(156, 235, 110, 35);
        add(allButton);

        // 전체 운동 버튼에 이벤트 리스너 추가
        allButton.getButton().addActionListener(e -> {
            if (mainUserPanel != null) {
                mainUserPanel.showExerciseListPanel("전체");
            }
        });

        // 검색 결과 없음 메시지 생성 (기본적으로 숨김)
        JLabel noResultLabel = new JLabel("검색한 운동이 없습니다", SwingConstants.CENTER);
        noResultLabel.setFont(new Font("맑은고딕", Font.BOLD, 16));
        noResultLabel.setBounds(20, 430, 400, 50);
        noResultLabel.setVisible(false); // 초기에는 숨김
        add(noResultLabel);
    }
    
    // 검색 기능 메서드
    private void performSearch(String keyword) {
        // 검색 결과 화면 연결 전까지 입력값을 콘솔에 출력하지 않는다.
    }
}
