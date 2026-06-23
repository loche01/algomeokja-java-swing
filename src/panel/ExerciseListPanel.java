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
        setBackground(new Color(0xD9D9D9));
       
        // 데이터베이스 연결을 위한 DAO 초기화
        exerciseDAO = new ExerciseDAO();
        
        // 검색바 패널 생성
        JPanel searchPanel = new JPanel(null);
        searchPanel.setBounds(15, 50, 410, 40);
        searchPanel.setBackground(new Color(217, 217, 217));

        // 검색 필드 생성
        searchField = new JTextField(" 검색어를 입력하세요...");
        searchField.setBounds(40, 5, 300, 30);
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(null);

        // 검색 필드 클릭 시 플레이스홀더 효과 제거
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals(" 검색어를 입력하세요...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(" 검색어를 입력하세요...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        // 검색 버튼 생성 및 초기화
        ImageIcon searchIcon = new ImageIcon("C:/Users/dita_810/Desktop/project9/src/images/search.png");
        Image img = searchIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        searchButton = new JButton(new ImageIcon(img));

        // 속성 설정 및 이벤트 리스너 추가
        searchButton.setBounds(5, 5, 30, 30);
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

        // 패널에 둥근 테두리 효과 적용
        RoundedComponent roundedSearchPanel = new RoundedComponent(390, 40, 40, "panel", "",
                Color.LIGHT_GRAY, Color.WHITE, Color.black, "", Font.PLAIN, 0);
        roundedSearchPanel.setBounds(0, 0, 390, 40);
        searchPanel.add(roundedSearchPanel);

        // 검색 패널을 ExerciseSearchPanel에 추가
        add(searchPanel);
        
        // Set up the category title and search components
        setupComponents();
        
        // 운동 콘텐츠 패널 생성 (BoxLayout으로 변경)
        exerciseContentPanel = new JPanel();
        exerciseContentPanel.setLayout(new BoxLayout(exerciseContentPanel, BoxLayout.Y_AXIS));
        exerciseContentPanel.setBackground(new Color(0xD9D9D9));
        
        // 스크롤 패널에 콘텐츠 패널 추가
        exerciseScrollPane = new JScrollPane(exerciseContentPanel);
        exerciseScrollPane.setBounds(10, 175, 420, 566);
        exerciseScrollPane.setBorder(null);
        exerciseScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        exerciseScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // 스크롤바 숨기기 위한 스타일 설정
        exerciseScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        exerciseScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
        
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
        System.out.println("Setting category to: " + category);
        this.currentCategory = category;
        
        // 카테고리 버튼 텍스트 변경
        categoryButton.getButton().setText("< " + category);
        
        // 검색 필드 초기화
        searchField.setText(" 검색어를 입력하세요...");
        searchField.setForeground(Color.GRAY);
        
        // 해당 카테고리에 맞는 운동 불러오기
        loadExercisesForCategory(category);
    }

    // Helper method to set up all the components
    private void setupComponents() {
        // Category title button
        categoryButton = new RoundedComponent(110, 35, 35, "button", currentCategory, 
                Color.white, Color.white, Color.black, "Malgun Gothic", Font.BOLD, 14);
        categoryButton.setBounds(160, 110, 110, 35);
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
        System.out.println("Searching for: " + keyword + " in category: " + currentCategory);
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
            noResultsLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
            noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
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
            System.out.println(currentCategory + " 운동 데이터가 없습니다.");
            JLabel noDataLabel = new JLabel(currentCategory + " 운동 데이터가 없습니다.", SwingConstants.CENTER);
            noDataLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
            noDataLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            exerciseContentPanel.add(noDataLabel);
            return;
        }
        
        System.out.println("Adding " + exerciseList.size() + " exercises for category: " + currentCategory);
        
        int minItems = 6; // 최소 6개 아이템이 보이도록 설정
        
        // 운동 목록 추가
        for (ExerciseBean ex : exerciseList) {
            System.out.println("Adding exercise: " + ex.getExerciseName() + " (" + ex.getExerciseType() + ")");
            
            // 운동 아이템 생성하여 추가
            exerciseContentPanel.add(createExerciseItem(ex));
            exerciseContentPanel.add(Box.createVerticalStrut(2)); // 아이템 간 간격
        }
        
        // 아이템이 충분하지 않을 경우 빈 패널 추가
        for (int i = exerciseList.size(); i < minItems; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setPreferredSize(new Dimension(420, 72));
            emptyPanel.setBackground(new Color(0xD9D9D9));
            exerciseContentPanel.add(emptyPanel);
            exerciseContentPanel.add(Box.createVerticalStrut(10));
        }
        exerciseContentPanel.add(Box.createVerticalStrut(90));
        
        exerciseContentPanel.revalidate();
        exerciseContentPanel.repaint();
    }
    
 // 1. ExerciseListPanel의 createExerciseItem 메서드 수정 - 클릭 이벤트 업데이트
    private JPanel createExerciseItem(ExerciseBean ex) {
        // 감싸는 패널 생성
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new FlowLayout(FlowLayout.LEFT));
        wrapper.setPreferredSize(new Dimension(420, 90));  
        wrapper.setBackground(new Color(0xD9D9D9));
        
        // 둥근 네모 박스 패널 (운동 아이템)
        RoundedComponent itemPanel = new RoundedComponent(390, 72, 20, "panel", "", 
                Color.WHITE, Color.WHITE, Color.BLACK, "Malgun Gothic", Font.BOLD, 16);
        itemPanel.setLayout(null);
        itemPanel.setBounds(0, 0, 390, 72);
        
        // 운동 아이템 클릭 이벤트
        itemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                System.out.println("운동 아이템 클릭됨: " + ex.getExerciseName());
                
                // MainUserPanel의 참조가 필요합니다
                if (mainUserPanel != null) {
                    // 운동 정보를 칼로리 패널로 전달하고 해당 패널 표시
                    mainUserPanel.showExerciseCaloriePanel(ex);
                }
            }
        });
        
        // 운동명 레이블
        JLabel nameLabel = new JLabel(ex.getExerciseName());
        nameLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        nameLabel.setBounds(20, 10, 300, 25);
        
        // 운동 타입 레이블
        JLabel typeLabel = new JLabel(ex.getExerciseType());
        typeLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        typeLabel.setForeground(Color.GRAY);
        typeLabel.setBounds(20, 40, 350, 25);
        
        // 패널에 레이블 추가
        itemPanel.add(nameLabel);
        itemPanel.add(typeLabel);
        
        // 감싸는 패널에 아이템 패널 추가
        wrapper.add(itemPanel);
        
        return wrapper;
    }
}