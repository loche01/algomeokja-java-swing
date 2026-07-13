package panel;

import javax.swing.*;

import DB.ExerciseDAO;
import main.MainUserPanel;
import ui_n_utils.AppTheme;
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
        setBackground(AppTheme.BACKGROUND);
        
        // DAO 초기화
        exerciseDAO = new ExerciseDAO();

        JLabel titleLabel = new JLabel("운동");
        titleLabel.setBounds(AppTheme.HORIZONTAL_MARGIN, 18, AppTheme.CARD_WIDTH, 34);
        AppTheme.styleScreenTitle(titleLabel);
        add(titleLabel);

        JLabel descriptionLabel = new JLabel("운동 부위를 선택하거나 이름으로 검색해보세요.");
        descriptionLabel.setBounds(AppTheme.HORIZONTAL_MARGIN, 54, AppTheme.CARD_WIDTH, 24);
        AppTheme.styleScreenDescription(descriptionLabel);
        add(descriptionLabel);

     // 검색바 패널 생성
        JPanel searchPanel = new JPanel(null);
        searchPanel.setBounds(AppTheme.HORIZONTAL_MARGIN, 92, AppTheme.CARD_WIDTH, AppTheme.INPUT_HEIGHT);
        searchPanel.setBackground(AppTheme.CARD);
        searchPanel.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        // 1️⃣ 검색 필드 생성
        searchField = new JTextField("어떤 운동을 하셨나요?");
        searchField.setBounds(42, 1, 328, 36);
        searchField.setFont(AppTheme.BODY_FONT);
        searchField.setForeground(AppTheme.TEXT_SECONDARY);
        searchField.setBackground(AppTheme.CARD);
        searchField.setBorder(null);

        // 검색 필드 클릭 시 플레이스홀더 효과 제거
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("어떤 운동을 하셨나요?")) {
                    searchField.setText("");
                    searchField.setForeground(AppTheme.TEXT);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("어떤 운동을 하셨나요?");
                    searchField.setForeground(AppTheme.TEXT_SECONDARY);
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
        searchButton.setBounds(6, 4, 30, 30);
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

        // 5️⃣ 검색 패널을 ExerciseSearchPanel에 추가
        add(searchPanel);

        JPanel categoryCard = new JPanel(null);
        categoryCard.setBounds(AppTheme.HORIZONTAL_MARGIN, 148, AppTheme.CARD_WIDTH, 230);
        AppTheme.styleCard(categoryCard);
        add(categoryCard);

        JLabel categoryTitle = new JLabel("운동 부위");
        categoryTitle.setBounds(18, 14, 200, 28);
        AppTheme.styleSectionTitle(categoryTitle);
        categoryCard.add(categoryTitle);

        // 운동 부위 버튼 ("가슴")
        button = new RoundedComponent(104, 38, 14, "button", "가슴", AppTheme.PRIMARY, AppTheme.PRIMARY_LIGHT, AppTheme.PRIMARY_DARK, Font.SANS_SERIF,
                Font.BOLD, 14);
        button.setBounds(16, 54, 104, 38);
        categoryCard.add(button);

        // 가슴 버튼에 이벤트 리스너 추가
        button.getButton().addActionListener(e -> {
            if (mainUserPanel != null) {
            	mainUserPanel.showExerciseListPanel("가슴");
            }
        });
        
        // 운동 부위 버튼 ("등")
        abutton = new RoundedComponent(104, 38, 14, "button", "등", AppTheme.PRIMARY, AppTheme.PRIMARY_LIGHT, AppTheme.PRIMARY_DARK, Font.SANS_SERIF,
                Font.BOLD, 14);
        abutton.setBounds(138, 54, 104, 38);
        categoryCard.add(abutton);

        // 등 버튼에 이벤트 리스너 추가
        abutton.getButton().addActionListener(e -> {
            if (mainUserPanel != null) {
            	mainUserPanel.showExerciseListPanel("등");
            }
        });

        // 운동 부위 버튼 ("하체")
        bbutton = new RoundedComponent(104, 38, 14, "button", "하체", AppTheme.PRIMARY, AppTheme.PRIMARY_LIGHT, AppTheme.PRIMARY_DARK, Font.SANS_SERIF,
                Font.BOLD, 14);
        bbutton.setBounds(260, 54, 104, 38);
        categoryCard.add(bbutton);

        // 하체 버튼에 이벤트 리스너 추가
        bbutton.getButton().addActionListener(e -> {
            if (mainUserPanel != null) {
            	mainUserPanel.showExerciseListPanel("하체");
            }
        });

        // 운동 부위 버튼 ("팔 & 어깨")
        cbutton = new RoundedComponent(104, 38, 14, "button", "팔&어깨", AppTheme.PRIMARY, AppTheme.PRIMARY_LIGHT, AppTheme.PRIMARY_DARK, Font.SANS_SERIF,
                Font.BOLD, 14);
        cbutton.setBounds(77, 108, 104, 38);
        categoryCard.add(cbutton);

        // 팔 & 어깨 버튼에 이벤트 리스너 추가
        cbutton.getButton().addActionListener(e -> {
            if (mainUserPanel != null) {
            	  mainUserPanel.showExerciseListPanel("팔&어깨");
            }
        });

        // 운동 부위 버튼 ("유산소 & 코어")
        dbutton = new RoundedComponent(104, 38, 14, "button", "유산소&코어", AppTheme.PRIMARY, AppTheme.PRIMARY_LIGHT, AppTheme.PRIMARY_DARK, Font.SANS_SERIF,
                Font.BOLD, 14);
        dbutton.setBounds(199, 108, 104, 38);
        categoryCard.add(dbutton);

        // 유산소 & 코어 버튼에 이벤트 리스너 추가
        dbutton.getButton().addActionListener(e -> {
            if (mainUserPanel != null) {
            	 // Pass "유산소 & 코어" category to ExerciseListPanel
                mainUserPanel.showExerciseListPanel("유산소&코어");
            }
        });

        // 전체 운동 버튼
        allButton = new RoundedComponent(104, 38, 14, "button", "전체", AppTheme.PRIMARY_DARK, AppTheme.PRIMARY_DARK, Color.WHITE, Font.SANS_SERIF,
                Font.BOLD, 14);
        allButton.setBounds(138, 162, 104, 38);
        categoryCard.add(allButton);

        // 전체 운동 버튼에 이벤트 리스너 추가
        allButton.getButton().addActionListener(e -> {
            if (mainUserPanel != null) {
                mainUserPanel.showExerciseListPanel("전체");
            }
        });

        // 검색 결과 없음 메시지 생성 (기본적으로 숨김)
        JLabel noResultLabel = new JLabel("검색한 운동이 없습니다", SwingConstants.CENTER);
        AppTheme.styleEmptyState(noResultLabel);
        noResultLabel.setBounds(AppTheme.HORIZONTAL_MARGIN, 394, AppTheme.CARD_WIDTH, 50);
        noResultLabel.setVisible(false); // 초기에는 숨김
        add(noResultLabel);
    }
    
    // 검색 기능 메서드
    private void performSearch(String keyword) {
        // 검색 결과 화면 연결 전까지 입력값을 콘솔에 출력하지 않는다.
    }
}
