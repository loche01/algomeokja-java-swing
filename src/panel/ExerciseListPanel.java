package panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import main.MainUserPanel;
import DB.ExerciseDAO;
import model.ExerciseBean;
import ui_n_utils.AppTheme;
import ui_n_utils.ClasspathIconLoader;
import ui_n_utils.RoundedComponent;

public class ExerciseListPanel extends JPanel {
    private RoundedComponent categoryButton;
    private JTextField searchField; 
    private JButton searchButton;
    private List<ExerciseBean> exerciseList;
    private ExerciseDAO exerciseDAO;
    private String currentCategory = "가슴"; // Default category
    private MainUserPanel mainUserPanel;
    
    // 스크롤 패널 추가
    private JScrollPane exerciseScrollPane;
    private JPanel exerciseContentPanel; // 운동 버튼을 담을 콘텐츠 패널

    // 1. 기본 생성자 추가
    public ExerciseListPanel() {
        setLayout(null);
        setBackground(AppTheme.BACKGROUND);
       
        // 데이터베이스 연결을 위한 DAO 초기화
        exerciseDAO = new ExerciseDAO();
        
        JLabel titleLabel = new JLabel("운동 목록");
        titleLabel.setBounds(AppTheme.HORIZONTAL_MARGIN, 18, AppTheme.CARD_WIDTH, 34);
        AppTheme.styleScreenTitle(titleLabel);
        add(titleLabel);

        JLabel descriptionLabel = new JLabel("선택한 부위의 운동을 확인하고 기록할 수 있습니다.");
        descriptionLabel.setBounds(AppTheme.HORIZONTAL_MARGIN, 54, AppTheme.CARD_WIDTH, 24);
        AppTheme.styleScreenDescription(descriptionLabel);
        add(descriptionLabel);

        // 검색바 패널 생성
        JPanel searchPanel = new JPanel(null);
        searchPanel.setBounds(AppTheme.HORIZONTAL_MARGIN, 136, AppTheme.CARD_WIDTH, AppTheme.INPUT_HEIGHT);
        searchPanel.setBackground(AppTheme.CARD);
        searchPanel.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));

        // 검색 필드 생성
        searchField = new JTextField(" 검색어를 입력하세요...");
        searchField.setBounds(42, 1, 328, 36);
        searchField.setFont(AppTheme.BODY_FONT);
        searchField.setForeground(AppTheme.TEXT_SECONDARY);
        searchField.setBackground(AppTheme.CARD);
        searchField.setBorder(null);

        // 검색 필드 클릭 시 플레이스홀더 효과 제거
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals(" 검색어를 입력하세요...")) {
                    searchField.setText("");
                    searchField.setForeground(AppTheme.TEXT);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(" 검색어를 입력하세요...");
                    searchField.setForeground(AppTheme.TEXT_SECONDARY);
                }
            }
        });

        // 검색 버튼 생성 및 초기화
        ImageIcon searchIcon = ClasspathIconLoader.loadScaled(
                ExerciseListPanel.class, "/images/search.png", 20, 20);
        searchButton = searchIcon != null ? new JButton(searchIcon) : new JButton("검색");
        if (searchIcon == null) {
            searchButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            searchButton.setMargin(new Insets(0, 0, 0, 0));
        }
        searchButton.setToolTipText("운동 검색");

        // 속성 설정 및 이벤트 리스너 추가
        searchButton.setBounds(6, 4, 30, 30);
        searchButton.setContentAreaFilled(false);
        searchButton.setBorderPainted(false);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = searchField.getText();
                performSearch(keyword);
            }
        });

        // 엔터 키를 눌러도 검색이 실행되도록 설정
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = searchField.getText();
                performSearch(keyword);
            }
        });

        // 검색 패널에 검색 필드와 버튼 추가
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // 검색 패널을 ExerciseSearchPanel에 추가
        add(searchPanel);
        
        // Set up the category title and search components
        setupComponents();
        
        // 운동 콘텐츠 패널 생성 (BoxLayout으로 변경)
        exerciseContentPanel = new VerticalListPanel();
        exerciseContentPanel.setBackground(AppTheme.BACKGROUND);
        
        // 스크롤 패널에 콘텐츠 패널 추가
        exerciseScrollPane = new JScrollPane(exerciseContentPanel);
        exerciseScrollPane.setBounds(AppTheme.HORIZONTAL_MARGIN, 190, AppTheme.CARD_WIDTH, 500);
        exerciseScrollPane.setBorder(null);
        exerciseScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        exerciseScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        exerciseScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // 스크롤바 숨기기 위한 스타일 설정
        exerciseScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        exerciseScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        exerciseScrollPane.getViewport().setBackground(AppTheme.BACKGROUND);
        
        add(exerciseScrollPane);
        
        // 마우스 휠로 스크롤 가능하게 설정
        setupWheelScrolling();
        
        // Load default exercises (가슴)
        loadExercisesForCategory(currentCategory);
    }

    // 2. MainUserPanel 매개변수가 있는 생성자 수정
    public ExerciseListPanel(MainUserPanel mainUserPanel) {
        this(); // 이제 기본 생성자를 호출할 수 있음
        this.mainUserPanel = mainUserPanel;
    }

    // Method to set category and refresh panel
    public void setCategory(String category) {
        this.currentCategory = category;
        
        // 카테고리 버튼 텍스트 변경
        categoryButton.getButton().setText("< " + category);
        
        // 검색 필드 초기화
        searchField.setText(" 검색어를 입력하세요...");
        searchField.setForeground(AppTheme.TEXT_SECONDARY);
        
        // 해당 카테고리에 맞는 운동 불러오기
        loadExercisesForCategory(category);
    }

    // Helper method to set up all the components
    private void setupComponents() {
        // Category title button
        categoryButton = new RoundedComponent(130, 38, 14, "button", currentCategory,
                AppTheme.PRIMARY, AppTheme.CARD, AppTheme.PRIMARY_DARK, Font.SANS_SERIF, Font.BOLD, 14);
        categoryButton.setBounds(AppTheme.HORIZONTAL_MARGIN, 90, 130, 38);
        categoryButton.getButton().addActionListener(e -> {
            if (mainUserPanel != null) {
                mainUserPanel.showPanel("ExerciseSearch");
            }
        });
        add(categoryButton);
    }

    // Helper method to load exercises for a category
    private void loadExercisesForCategory(String category) {
        if ("전체".equals(category)) {
            exerciseList = exerciseDAO.getAllExercises();
        } else {
            exerciseList = exerciseDAO.getExercisesByCategory(category);
        }
        
        // Clear and recreate exercise buttons
        clearExerciseButtons();
        addExerciseButtons();
    }

    // 마우스 휠 스크롤링 설정
    private void setupWheelScrolling() {
        // 마우스 휠 스크롤링
        exerciseScrollPane.addMouseWheelListener(e -> {
            JScrollBar verticalScrollBar = exerciseScrollPane.getVerticalScrollBar();
            int notches = e.getWheelRotation();
            // 더 부드러운 스크롤링을 위해 속도 조정
            int increment = (verticalScrollBar.getUnitIncrement() * 2) * notches;
            int newValue = verticalScrollBar.getValue() + increment;
            
            // 스크롤 범위 제한
            if (newValue < 0) {
                newValue = 0;
            } else if (newValue > verticalScrollBar.getMaximum() - verticalScrollBar.getVisibleAmount()) {
                newValue = verticalScrollBar.getMaximum() - verticalScrollBar.getVisibleAmount();
            }
            
            verticalScrollBar.setValue(newValue);
        });
        
        // 키보드 화살표 키 스크롤링 추가
        exerciseScrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(KeyStroke.getKeyStroke("UP"), "scrollUp");
        exerciseScrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
            .put(KeyStroke.getKeyStroke("DOWN"), "scrollDown");
            
        exerciseScrollPane.getActionMap().put("scrollUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JScrollBar verticalScrollBar = exerciseScrollPane.getVerticalScrollBar();
                int newValue = verticalScrollBar.getValue() - verticalScrollBar.getUnitIncrement();
                if (newValue < 0) newValue = 0;
                verticalScrollBar.setValue(newValue);
            }
        });
        
        exerciseScrollPane.getActionMap().put("scrollDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JScrollBar verticalScrollBar = exerciseScrollPane.getVerticalScrollBar();
                int newValue = verticalScrollBar.getValue() + verticalScrollBar.getUnitIncrement();
                if (newValue > verticalScrollBar.getMaximum() - verticalScrollBar.getVisibleAmount()) {
                    newValue = verticalScrollBar.getMaximum() - verticalScrollBar.getVisibleAmount();
                }
                verticalScrollBar.setValue(newValue);
            }
        });
    }

    // 검색 기능 구현
    private void performSearch(String keyword) {
        String searchKeyword = keyword != null ? keyword.trim() : "";
        if (searchKeyword.isEmpty() || searchKeyword.equals("검색어를 입력하세요...")) {
            loadExercisesForCategory(currentCategory);
            return;
        }
        String lowerKeyword = searchKeyword.toLowerCase();
        
        // 검색 결과를 저장할 리스트
        List<ExerciseBean> searchResults = new ArrayList<>();
        
        // 현재 카테고리의 운동들 중에서 검색어를 포함하는 운동만 찾기
        for (ExerciseBean exercise : exerciseList) {
            String exerciseName = exercise.getExerciseName() != null ? exercise.getExerciseName().toLowerCase() : "";
            String exerciseType = exercise.getExerciseType() != null ? exercise.getExerciseType().toLowerCase() : "";
            if (exerciseName.contains(lowerKeyword) || exerciseType.contains(lowerKeyword)) {
                searchResults.add(exercise);
            }
        }
        
        // 검색 결과 표시
        clearExerciseButtons();
        
        if (searchResults.isEmpty()) {
            JLabel noResultsLabel = new JLabel("검색 결과가 없습니다.", SwingConstants.CENTER);
            AppTheme.styleEmptyState(noResultsLabel);
            noResultsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            noResultsLabel.setMinimumSize(new Dimension(0, 80));
            noResultsLabel.setPreferredSize(new Dimension(AppTheme.CARD_WIDTH, 80));
            noResultsLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            exerciseContentPanel.add(noResultsLabel);
        } else {
            // 검색 결과를 임시로 exerciseList에 저장
            List<ExerciseBean> originalList = exerciseList;
            exerciseList = searchResults;
            
            // 검색 결과 표시
            addExerciseButtons();
            
            // 원래 목록 복원
            exerciseList = originalList;
        }
        
        exerciseContentPanel.revalidate();
        exerciseContentPanel.repaint();
    }

    // Clear all exercise buttons from the panel
    private void clearExerciseButtons() {
        exerciseContentPanel.removeAll();
        exerciseContentPanel.revalidate();
        exerciseContentPanel.repaint();
    }

    // Add exercise buttons based on current category
    private void addExerciseButtons() {
        if (exerciseList == null || exerciseList.isEmpty()) {
            JLabel noDataLabel = new JLabel(currentCategory + " 운동 데이터가 없습니다.", SwingConstants.CENTER);
            AppTheme.styleEmptyState(noDataLabel);
            noDataLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            noDataLabel.setMinimumSize(new Dimension(0, 80));
            noDataLabel.setPreferredSize(new Dimension(AppTheme.CARD_WIDTH, 80));
            noDataLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            exerciseContentPanel.add(noDataLabel);
            return;
        }
        
        // 운동 목록 추가
        for (ExerciseBean ex : exerciseList) {
            // 운동 아이템 생성하여 추가
            exerciseContentPanel.add(createExerciseItem(ex));
            exerciseContentPanel.add(Box.createVerticalStrut(AppTheme.SMALL_GAP));
        }
        
        exerciseContentPanel.revalidate();
        exerciseContentPanel.repaint();
    }
    
 // 1. ExerciseListPanel의 createExerciseItem 메서드 수정 - 클릭 이벤트 업데이트
    private JPanel createExerciseItem(ExerciseBean ex) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setOpaque(true);
        itemPanel.setBackground(AppTheme.CARD);
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(9, 18, 8, 18)));
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        itemPanel.setMinimumSize(new Dimension(0, 86));
        itemPanel.setPreferredSize(new Dimension(AppTheme.CARD_WIDTH, 86));
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        itemPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 운동 아이템 클릭 이벤트
        java.awt.event.MouseAdapter openExerciseListener = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // MainUserPanel의 참조가 필요합니다
                if (mainUserPanel != null) {
                    // 운동 정보를 칼로리 패널로 전달하고 해당 패널 표시
                    mainUserPanel.showExerciseCaloriePanel(ex);
                }
            }
        };
        itemPanel.addMouseListener(openExerciseListener);

        // 운동명 레이블
        JTextArea nameLabel = new JTextArea(ex.getExerciseName() != null ? ex.getExerciseName() : "");
        nameLabel.setFont(AppTheme.BODY_BOLD_FONT);
        nameLabel.setForeground(AppTheme.TEXT);
        nameLabel.setLineWrap(true);
        nameLabel.setWrapStyleWord(true);
        nameLabel.setRows(2);
        nameLabel.setEditable(false);
        nameLabel.setFocusable(false);
        nameLabel.setOpaque(false);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameLabel.setMinimumSize(new Dimension(0, 36));
        nameLabel.setPreferredSize(new Dimension(0, 36));
        nameLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        nameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nameLabel.setToolTipText(ex.getExerciseName());
        nameLabel.addMouseListener(openExerciseListener);

        // 운동 타입 레이블
        JLabel typeLabel = new JLabel(ex.getExerciseType() != null ? ex.getExerciseType() : "");
        typeLabel.setFont(AppTheme.CAPTION_FONT);
        typeLabel.setForeground(AppTheme.TEXT_SECONDARY);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        typeLabel.setMinimumSize(new Dimension(0, 20));
        typeLabel.setPreferredSize(new Dimension(0, 20));
        typeLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        typeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        typeLabel.addMouseListener(openExerciseListener);

        // 패널에 레이블 추가
        itemPanel.add(nameLabel);
        itemPanel.add(typeLabel);

        return itemPanel;
    }

    private static class VerticalListPanel extends JPanel implements Scrollable {
        VerticalListPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return Math.max(visibleRect.height - 32, 16);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
