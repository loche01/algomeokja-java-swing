package panel;

import DB.FoodDAO;
import DB.MealSaveService;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.*;
import main.MainUserPanel;
import model.FoodBean;
import model.LoginManager;
import ui_n_utils.AppTheme;
import ui_n_utils.RoundedComponent;


// 체크 포인트
public class FoodListPanel extends JPanel {
    private static final String SEARCH_PLACEHOLDER = "음식 이름을 입력하세요";
    private MainUserPanel mainUserPanel;
    private JScrollPane searchScrollPane, favoriteScrollPane;
    private JTextField searchField; // 🔹 검색 입력 필드 추가
    private JButton searchButton, searchTabButton, favoriteTabButton;  // 🔹 검색 버튼 추가
    private JPanel contentPanel, searchContentPanel, favoriteContentPanel, selectedTabIndicator, searchPanel;
    private JLabel mealTypeLabel;
    
    // ✅ 음식별 g 값을 저장할 HashMap 추가
    private HashMap<String, Integer> foodWeightMap = new HashMap<>();
    
    public Vector<FoodBean> favoriteItems = new Vector<>(); // ✅ 담은 목록 저장
    private boolean isFavoriteList = false; // ✅ 현재 담은 목록인지 여부
    private FoodDAO foodDAO;
    private MealSaveService mealSaveService;
    private String userId;
    private RoundedComponent saveButtonComponent, backButtonComponent;
    private String currentMealType = "";  // 현재 선택된 식사 유형
    private JPanel favoritePanel; // 담은 목록 패널

    public FoodListPanel(MainUserPanel mainUserPanel) {
        this.mainUserPanel = mainUserPanel;
        this.foodDAO = new FoodDAO();
        this.mealSaveService = new MealSaveService();

        setLayout(null);
        setBackground(AppTheme.BACKGROUND);
        setBounds(0, 0, 440, 686);
        
     // 상단 탭바 패널
        JPanel tabPanel = new JPanel();
        tabPanel.setLayout(null);
        tabPanel.setBounds(0, 0, 440, 40);
        tabPanel.setBackground(AppTheme.CARD);
        tabPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER));
        add(tabPanel);

        // 검색 탭 버튼
        searchTabButton = new JButton("음식 검색");
        searchTabButton.setFont(AppTheme.BODY_BOLD_FONT);
        searchTabButton.setForeground(AppTheme.PRIMARY_DARK);
        searchTabButton.setBounds(0, 3, 220, 34);
        searchTabButton.setBorderPainted(false);
        searchTabButton.setFocusPainted(false);
        searchTabButton.setContentAreaFilled(false);
        tabPanel.add(searchTabButton);

        // 담은 목록 탭 버튼
        favoriteTabButton = new JButton("담은 음식 (0)");
        favoriteTabButton.setFont(AppTheme.BODY_BOLD_FONT);
        favoriteTabButton.setForeground(AppTheme.TEXT_SECONDARY);
        favoriteTabButton.setBounds(220, 3, 220, 34);
        favoriteTabButton.setBorderPainted(false);
        favoriteTabButton.setFocusPainted(false);
        favoriteTabButton.setContentAreaFilled(false);
        tabPanel.add(favoriteTabButton);

        // 선택된 탭 표시 (밑줄)
        selectedTabIndicator = new JPanel();
        selectedTabIndicator.setBounds(0, 37, 220, 3); // 기본적으로 '검색' 선택
        selectedTabIndicator.setBackground(AppTheme.PRIMARY_DARK);
        tabPanel.add(selectedTabIndicator);

        // 탭 버튼에 액션 리스너 추가
        searchTabButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchToSearchTab();
            }
        });
        
        favoriteTabButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchToFavoriteTab();
            }
        });

        mealTypeLabel = new JLabel("선택한 식사: 식사 유형을 선택해주세요");
        mealTypeLabel.setFont(AppTheme.BODY_BOLD_FONT);
        mealTypeLabel.setForeground(AppTheme.PRIMARY_DARK);
        mealTypeLabel.setBounds(AppTheme.HORIZONTAL_MARGIN, 47, AppTheme.CARD_WIDTH, 28);
        add(mealTypeLabel);

        // 검색바 패널
        searchPanel = new JPanel(null);
        searchPanel.setBounds(AppTheme.HORIZONTAL_MARGIN, 80, AppTheme.CARD_WIDTH, 42);
        searchPanel.setBackground(AppTheme.BACKGROUND);

        searchField = new JTextField(SEARCH_PLACEHOLDER);
        searchField.setBounds(0, 0, 280, 42);
        AppTheme.styleInputField(searchField);
        searchField.setForeground(AppTheme.TEXT_SECONDARY);
        
        // 🔹 검색 필드 클릭 시 플레이스홀더 효과 제거
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals(SEARCH_PLACEHOLDER)) {
                    searchField.setText("");
                    searchField.setForeground(AppTheme.TEXT);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(SEARCH_PLACEHOLDER);
                    searchField.setForeground(AppTheme.TEXT_SECONDARY);
                }
            }
        });
        
        searchButton = new JButton("검색");
        searchButton.setBounds(290, 0, 90, 42);
        AppTheme.stylePrimaryButton(searchButton);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // 🔹 검색 버튼에 액션 리스너 추가
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        
        // 엔터 키로 검색 가능하도록 설정
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        // 검색 패널을 메인 패널에 추가
        add(searchPanel);
  
        // ✅ 검색 결과 리스트 패널
        searchContentPanel = new VerticalListPanel();
        searchContentPanel.setBackground(AppTheme.BACKGROUND);
        searchContentPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        
        // ✅ 검색 결과용 스크롤 패널
        searchScrollPane = new JScrollPane(searchContentPanel);
        searchScrollPane.setBounds(AppTheme.HORIZONTAL_MARGIN, 132, AppTheme.CARD_WIDTH, 472);
        searchScrollPane.setBorder(null);
        searchScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        searchScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        searchScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        searchScrollPane.getViewport().setBackground(AppTheme.BACKGROUND);
        add(searchScrollPane);
        
        // ✅ 담은 목록 리스트 패널
        favoriteContentPanel = new VerticalListPanel();
        favoriteContentPanel.setBackground(AppTheme.BACKGROUND);
        favoriteContentPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        // ✅ 담은 목록용 스크롤 패널
        favoriteScrollPane = new JScrollPane(favoriteContentPanel);
        favoriteScrollPane.setBounds(AppTheme.HORIZONTAL_MARGIN, 80, AppTheme.CARD_WIDTH, 524);
        favoriteScrollPane.setBorder(null);
        favoriteScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        favoriteScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        favoriteScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        favoriteScrollPane.getViewport().setBackground(AppTheme.BACKGROUND);
        favoriteScrollPane.setVisible(false);
        add(favoriteScrollPane);
        
        backButtonComponent = new RoundedComponent(130, 42, 10, "button", "식단 화면",
                AppTheme.PRIMARY, AppTheme.CARD, AppTheme.PRIMARY_DARK, Font.SANS_SERIF, Font.BOLD, 14);
        backButtonComponent.setBounds(AppTheme.HORIZONTAL_MARGIN, 620, 130, 42);
        backButtonComponent.getButton().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButtonComponent.getButton().addActionListener(e -> mainUserPanel.showPanel("HomeMeal"));
        add(backButtonComponent);

        saveButtonComponent = new RoundedComponent(180, 42, 10, "button", "식단 저장",
                AppTheme.PRIMARY_DARK, AppTheme.PRIMARY_DARK, Color.WHITE, Font.SANS_SERIF, Font.BOLD, 14);
        saveButtonComponent.setBounds(230, 620, 180, 42);
        saveButtonComponent.getButton().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveButtonComponent.getButton().addActionListener(e -> {
            if (favoriteItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "담은 음식이 없습니다.", 
                    "저장 실패", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            saveMealToDatabase();
        });
        add(saveButtonComponent);
        saveButtonComponent.setVisible(false);
        
        populateItems("");
    }
    // ✅ 검색 수행 메서드 추가
    private void performSearch() {
        String searchKeyword = searchField.getText().trim();
        if (searchKeyword.equals(SEARCH_PLACEHOLDER) || searchKeyword.isEmpty()) {
            searchKeyword = ""; // 🔹 검색어가 없을 경우 전체 리스트 표시
        }
        populateItems(searchKeyword);
    }
    
    // 담은 목록에 음식 추가 메서드
    public void addToFavoriteList(FoodBean food) {
        if (food == null) return;
        
        // 음식 코드가 0이면 DB에서 조회하여 설정
        if (food.getFoodCode() == 0) {
            FoodBean dbFood = foodDAO.getFoodByName(food.getFoodName());
            if (dbFood != null) {
                food.setFoodCode(dbFood.getFoodCode());
            }
        }
        
        // 이미 담은 목록에 있는지 확인
        boolean alreadyExists = false;
        for (FoodBean item : favoriteItems) {
            if (item.getFoodName().equals(food.getFoodName())) {
                alreadyExists = true;
                break;
            }
        }
        
        if (!alreadyExists) {
            favoriteItems.add(food);
        }
        updateFavoriteTabTitle();
        
        // 담은 목록 UI 업데이트
        if (isFavoriteList) {
            showFavoriteItems();
        }
    }
    
    // ✅ - 버튼을 눌렀을 때 호출될 메서드
    private void removeFavoriteItem(FoodBean food) {
        favoriteItems.remove(food);
        updateFavoriteTabTitle();
        if (isFavoriteList) {
            showFavoriteItems();
        }
    }
    
    // ✅ 음식 리스트를 검색하여 표시
    private void populateItems(String searchKeyword) {
        searchContentPanel.removeAll(); // 🔹 기존 리스트 삭제

        Vector<FoodBean> foodData = foodDAO.getFoodListBySearch(searchKeyword); // 🔹 검색 결과 가져오기

        if (foodData.isEmpty()) {
            searchContentPanel.add(createEmptyMessage("검색 결과가 없습니다. 음식 이름을 확인해주세요."));
        } else {
            for (FoodBean food : foodData) {
                searchContentPanel.add(createRoundedItem(food));
                searchContentPanel.add(Box.createVerticalStrut(8));
            }
        }

        searchContentPanel.revalidate();
        searchContentPanel.repaint();
        
        // 스크롤바를 맨 위로 초기화
        SwingUtilities.invokeLater(() -> {
            searchScrollPane.getVerticalScrollBar().setValue(0);
        });
    }
    
 // ✅ 담은 목록 표시
    public void showFavoriteItems() {
        favoriteContentPanel.removeAll();

        updateFavoriteTabTitle();
        if (favoriteItems.isEmpty()) {
            favoriteContentPanel.add(createEmptyMessage("담은 음식이 없습니다. 음식 검색에서 먼저 담아주세요."));
        } else {
            for (FoodBean food : favoriteItems) {
                favoriteContentPanel.add(createRoundedItem(food));
                favoriteContentPanel.add(Box.createVerticalStrut(8));
            }
        }
        
        favoriteContentPanel.revalidate();
        favoriteContentPanel.repaint();
    }
    
    public void switchToFavoriteTab() {
        selectedTabIndicator.setBounds(220, 37, 220, 3);
        searchTabButton.setForeground(AppTheme.TEXT_SECONDARY);
        favoriteTabButton.setForeground(AppTheme.PRIMARY_DARK);
        searchPanel.setVisible(false);
        searchScrollPane.setVisible(false);
        favoriteScrollPane.setVisible(true);
        saveButtonComponent.setVisible(true);
        isFavoriteList = true;
        showFavoriteItems();
        
        saveButtonComponent.setBounds(230, 620, 180, 42);
    }
    
    // 담은 목록을 DB에 저장하는 메서드
    private void saveMealToDatabase() {
        if (currentMealType == null || currentMealType.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "식사 유형을 선택해주세요.",
                "저장 불가",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String userId = LoginManager.getInstance().getUserId();
        if (userId == null || userId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "로그인 후 이용 가능합니다.",
                "로그인 필요",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            boolean success = mealSaveService.saveMealWithLogs(userId, currentMealType, favoriteItems);
            
            if (!success) {
                JOptionPane.showMessageDialog(this,
                    "선택한 음식 정보를 저장하지 못했습니다. 담은 목록을 확인한 뒤 다시 시도해주세요.",
                    "저장 오류",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 성공 메시지 표시
            JOptionPane.showMessageDialog(this,
                "식단이 성공적으로 저장되었습니다.",
                "저장 성공",
                JOptionPane.INFORMATION_MESSAGE);
                
            // 식단 저장 후 패널 초기화
            clearPanel();
            
            // 등록된 식단 홈 화면을 갱신한 뒤 복귀
            SwingUtilities.invokeLater(() -> {
                mainUserPanel.getHomeMealPanel().updateCalories();
                mainUserPanel.showPanel("HomeMeal");
                mainUserPanel.revalidate();
                mainUserPanel.repaint();
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "식단 저장 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
                "저장 오류",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ✅ 음식 행 생성
    private JPanel createRoundedItem(FoodBean food) {
        JPanel itemPanel = new JPanel(new BorderLayout(12, 0));
        itemPanel.setPreferredSize(new Dimension(0, 104));
        itemPanel.setMinimumSize(new Dimension(0, 104));
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 104));
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        itemPanel.setBackground(AppTheme.CARD);
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 12)));
        itemPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        itemPanel.setToolTipText("클릭하여 상세 보기");

        java.awt.event.MouseAdapter openDetailListener = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                openFoodDetail(food);
            }
        };

        // ✅ 음식 아이템을 클릭했을 때 FoodInfoPanel로 이동
        itemPanel.addMouseListener(openDetailListener);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        infoPanel.addMouseListener(openDetailListener);

        JTextArea nameLabel = new JTextArea(food.getFoodName());
        nameLabel.setFont(AppTheme.BODY_BOLD_FONT);
        nameLabel.setForeground(AppTheme.TEXT);
        nameLabel.setLineWrap(true);
        nameLabel.setWrapStyleWord(true);
        nameLabel.setEditable(false);
        nameLabel.setFocusable(false);
        nameLabel.setOpaque(false);
        nameLabel.setRows(2);
        nameLabel.setPreferredSize(new Dimension(0, 48));
        nameLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameLabel.setToolTipText(food.getFoodName());
        nameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nameLabel.addMouseListener(openDetailListener);

        JPanel metaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        metaPanel.setOpaque(false);
        metaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        metaPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        metaPanel.addMouseListener(openDetailListener);

        JLabel weightLabel = new JLabel(isFavoriteList
                ? "적용 중량 " + food.getWeight() + "g"
                : "기준 중량 100g");
        weightLabel.setFont(AppTheme.CAPTION_FONT);
        weightLabel.setForeground(AppTheme.TEXT_SECONDARY);
        weightLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        weightLabel.addMouseListener(openDetailListener);

        JLabel dividerLabel = new JLabel("  |  ");
        dividerLabel.setForeground(AppTheme.DISABLED);
        dividerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dividerLabel.addMouseListener(openDetailListener);

        JLabel kcalLabel = new JLabel("열량 " + (int) food.getFoodKcal() + " kcal");
        kcalLabel.setFont(AppTheme.CAPTION_FONT.deriveFont(Font.BOLD));
        kcalLabel.setForeground(AppTheme.PRIMARY_DARK);
        kcalLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        kcalLabel.addMouseListener(openDetailListener);

        metaPanel.add(weightLabel);
        metaPanel.add(dividerLabel);
        metaPanel.add(kcalLabel);
        infoPanel.add(nameLabel);
        infoPanel.add(metaPanel);

        JButton actionButton = new JButton(isFavoriteList ? "빼기" : "상세 보기");
        actionButton.setPreferredSize(new Dimension(90, 42));
        actionButton.setMinimumSize(new Dimension(90, 42));
        actionButton.setMaximumSize(new Dimension(90, 42));
        if (isFavoriteList) {
            AppTheme.styleSecondaryButton(actionButton);
            actionButton.addActionListener(e -> removeFavoriteItem(food));
        } else {
            AppTheme.stylePrimaryButton(actionButton);
            actionButton.addActionListener(e -> openFoodDetail(food));
        }

        itemPanel.add(infoPanel, BorderLayout.CENTER);
        itemPanel.add(actionButton, BorderLayout.EAST);
        return itemPanel;
    }

    private void openFoodDetail(FoodBean food) {
        if (isFavoriteList) {
            FoodBean originalFood = foodDAO.getFoodByName(food.getFoodName());
            mainUserPanel.foodInfoPanel.updateFoodInfo(originalFood);
        } else {
            mainUserPanel.foodInfoPanel.updateFoodInfo(food);
        }
        mainUserPanel.showPanel("foodInfo");
    }

    private JPanel createEmptyMessage(String message) {
        JPanel emptyPanel = new JPanel(new BorderLayout());
        emptyPanel.setPreferredSize(new Dimension(0, 150));
        emptyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        emptyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        AppTheme.styleCard(emptyPanel);

        JLabel messageLabel = new JLabel(
                "<html><div style='text-align:center;width:340px'>" + message + "</div></html>",
                SwingConstants.CENTER);
        AppTheme.styleEmptyState(messageLabel);
        emptyPanel.add(messageLabel, BorderLayout.CENTER);
        return emptyPanel;
    }

    private void updateFavoriteTabTitle() {
        favoriteTabButton.setText("담은 음식 (" + favoriteItems.size() + ")");
    }
	
	
	public Vector<FoodBean> getFavoriteItems() {
	    return this.favoriteItems;
	}
	
	// 담은 목록에서 음식을 수정할 수 있는 메서드
	public void updateFavoriteItem(FoodBean updatedFood) {
	    // 음식 코드가 0이면 DB에서 조회하여 설정
	    if (updatedFood.getFoodCode() == 0) {
	        FoodBean dbFood = foodDAO.getFoodByName(updatedFood.getFoodName());
	        if (dbFood != null) {
	            updatedFood.setFoodCode(dbFood.getFoodCode());
	        }
	    }
	    
	    // 기존 음식을 업데이트
	    for (int i = 0; i < favoriteItems.size(); i++) {
	        FoodBean food = favoriteItems.get(i);
	        if (food.getFoodName().equals(updatedFood.getFoodName())) {
	            favoriteItems.set(i, updatedFood);
	            break;
	        }
	    }
	    
	    // UI 업데이트
	    showFavoriteItems();
	    
        // 자동 저장은 하지 않음 - 사용자가 저장 버튼을 눌러야 저장됨
	}

    // meal_type 설정 메서드
    public void setMealType(String mealType) {
        this.currentMealType = mealType;
        mealTypeLabel.setText("선택한 식사: " + mealType);
        
        // 검색 탭으로 초기화
        switchToSearchTab();
        
        // 검색 필드 초기화
        searchField.setText(SEARCH_PLACEHOLDER);
        searchField.setForeground(AppTheme.TEXT_SECONDARY);
        populateItems("");
    }

    public String getCurrentMealType() {
        return currentMealType;
    }

    // 패널 초기화 메서드
    private void clearPanel() {
        favoriteItems.clear();
        currentMealType = "";
        mealTypeLabel.setText("선택한 식사: 식사 유형을 선택해주세요");
        searchField.setText(SEARCH_PLACEHOLDER);
        searchField.setForeground(AppTheme.TEXT_SECONDARY);
        updateFavoriteTabTitle();
        showFavoriteItems();
        
        // UI 업데이트를 확실히 하기 위해
        revalidate();
        repaint();
    }

    // 검색 탭으로 전환하는 메서드
    public void switchToSearchTab() {
        selectedTabIndicator.setBounds(0, 37, 220, 3);
        searchTabButton.setForeground(AppTheme.PRIMARY_DARK);
        favoriteTabButton.setForeground(AppTheme.TEXT_SECONDARY);
        searchPanel.setVisible(true);
        searchScrollPane.setVisible(true);
        favoriteScrollPane.setVisible(false);
        saveButtonComponent.setVisible(false);
        isFavoriteList = false;
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
