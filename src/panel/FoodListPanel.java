package panel;

import DB.FoodDAO;
import DB.MealDAO;
import DB.MealLogDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.*;
import main.MainUserPanel;
import model.FoodBean;
import model.LoginManager;
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
    private MealDAO mealDAO;
    private MealLogDAO mealLogDAO;
    private String userId;
    private RoundedComponent saveButtonComponent, backButtonComponent;
    private String currentMealType = "";  // 현재 선택된 식사 유형
    private JPanel favoritePanel; // 담은 목록 패널

    public FoodListPanel(MainUserPanel mainUserPanel) {
        this.mainUserPanel = mainUserPanel;
        this.foodDAO = new FoodDAO();
        this.mealDAO = new MealDAO();
        this.mealLogDAO = new MealLogDAO();

        setLayout(null);
        setBackground(new Color(0xF3F5F0));
        setBounds(0, 0, 440, 686);
        
     // 상단 탭바 패널
        JPanel tabPanel = new JPanel();
        tabPanel.setLayout(null);
        tabPanel.setBounds(0, 0, 440, 40);
        tabPanel.setBackground(new Color(160,212,104)); // 연한 초록색
        add(tabPanel);

        // 검색 탭 버튼
        searchTabButton = new JButton("음식 검색");
        searchTabButton.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        searchTabButton.setBounds(0, 5, 215, 30);
        searchTabButton.setBorderPainted(false);
        searchTabButton.setFocusPainted(false);
        searchTabButton.setContentAreaFilled(false);
        tabPanel.add(searchTabButton);

        // 담은 목록 탭 버튼
        favoriteTabButton = new JButton("담은 음식 (0)");
        favoriteTabButton.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        favoriteTabButton.setBounds(215, 5, 215, 30);
        favoriteTabButton.setBorderPainted(false);
        favoriteTabButton.setFocusPainted(false);
        favoriteTabButton.setContentAreaFilled(false);
        tabPanel.add(favoriteTabButton);

        // 선택된 탭 표시 (밑줄)
        selectedTabIndicator = new JPanel();
        selectedTabIndicator.setBounds(0, 35, 215, 5); // 기본적으로 '검색' 선택
        selectedTabIndicator.setBackground(new Color(96,144,86));
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
        mealTypeLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        mealTypeLabel.setForeground(new Color(0x406E38));
        mealTypeLabel.setBounds(20, 47, 400, 28);
        add(mealTypeLabel);

        // 검색바 패널
        searchPanel = new JPanel(null);
        searchPanel.setBounds(15, 80, 410, 42);
        searchPanel.setBackground(new Color(0xF3F5F0));

        searchField = new JTextField(SEARCH_PLACEHOLDER);
        searchField.setBounds(0, 0, 310, 42);
        searchField.setFont(new Font("Malgun Gothic", Font.PLAIN, 15));
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xB8C8B2), 1, true),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));
        
        // 🔹 검색 필드 클릭 시 플레이스홀더 효과 제거
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals(SEARCH_PLACEHOLDER)) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(SEARCH_PLACEHOLDER);
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        
        searchButton = new JButton("검색");
        searchButton.setBounds(320, 0, 90, 42);
        searchButton.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBackground(new Color(0x609056));
        searchButton.setOpaque(true);
        searchButton.setContentAreaFilled(true);
        searchButton.setBorder(BorderFactory.createLineBorder(new Color(0x609056), 1, true));
        searchButton.setFocusPainted(false);
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
        searchContentPanel = new JPanel();
        searchContentPanel.setLayout(new BoxLayout(searchContentPanel, BoxLayout.Y_AXIS));
        searchContentPanel.setBackground(new Color(0xF3F5F0));
        searchContentPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        
        // ✅ 검색 결과용 스크롤 패널
        searchScrollPane = new JScrollPane(searchContentPanel);
        searchScrollPane.setBounds(10, 132, 420, 472);
        searchScrollPane.setBorder(null);
        searchScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        searchScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        searchScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        searchScrollPane.getViewport().setBackground(new Color(0xF3F5F0));
        add(searchScrollPane);
        
        // ✅ 담은 목록 리스트 패널
        favoriteContentPanel = new JPanel();
        favoriteContentPanel.setLayout(new BoxLayout(favoriteContentPanel, BoxLayout.Y_AXIS));
        favoriteContentPanel.setBackground(new Color(0xF3F5F0));
        favoriteContentPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        // ✅ 담은 목록용 스크롤 패널
        favoriteScrollPane = new JScrollPane(favoriteContentPanel);
        favoriteScrollPane.setBounds(10, 80, 420, 524);
        favoriteScrollPane.setBorder(null);
        favoriteScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        favoriteScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        favoriteScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        favoriteScrollPane.getViewport().setBackground(new Color(0xF3F5F0));
        favoriteScrollPane.setVisible(false);
        add(favoriteScrollPane);
        
        backButtonComponent = new RoundedComponent(130, 42, 10, "button", "식단 화면",
                new Color(0x7A7A7A), Color.WHITE, new Color(0x4A4A4A), "Malgun Gothic", Font.BOLD, 15);
        backButtonComponent.setBounds(20, 620, 130, 42);
        backButtonComponent.getButton().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButtonComponent.getButton().addActionListener(e -> mainUserPanel.showPanel("HomeMeal"));
        add(backButtonComponent);

        saveButtonComponent = new RoundedComponent(180, 42, 10, "button", "식단 저장",
                new Color(0x609056), new Color(0x609056), Color.WHITE, "Malgun Gothic", Font.BOLD, 15);
        saveButtonComponent.setBounds(240, 620, 180, 42);
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
        selectedTabIndicator.setBounds(215, 35, 215, 5);
        searchPanel.setVisible(false);
        searchScrollPane.setVisible(false);
        favoriteScrollPane.setVisible(true);
        saveButtonComponent.setVisible(true);
        isFavoriteList = true;
        showFavoriteItems();
        
        saveButtonComponent.setBounds(240, 620, 180, 42);
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
            // meal 테이블에 새 레코드 추가 (meal_type 포함)
            int mealCode = mealDAO.insertMeal(userId, currentMealType);
            
            if (mealCode == -1) {
                JOptionPane.showMessageDialog(this,
                    "식사 기록을 생성하지 못했습니다. 잠시 후 다시 시도해주세요.",
                    "저장 오류",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // meal_log 테이블에 담은 음식 목록 추가
            boolean success = mealLogDAO.insertMealLogs(mealCode, favoriteItems);
            
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
    
    // ✅ 둥근 네모 박스를 생성하는 메서드
    private JPanel createRoundedItem(FoodBean food) {
    	
    	// ✅ 둥근 네모 박스 패널 (음식 아이템)
        RoundedComponent itemPanel = new RoundedComponent(390, 96, 18, "panel", "",
                new Color(0xD8E6D2), Color.WHITE, Color.BLACK, "Inter", Font.BOLD, 16);
        itemPanel.setLayout(null);
        itemPanel.setBounds(0, 0, 390, 96);
        itemPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        java.awt.event.MouseAdapter openDetailListener = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                openFoodDetail(food);
            }
        };

        // ✅ 음식 아이템을 클릭했을 때 FoodInfoPanel로 이동
        itemPanel.addMouseListener(openDetailListener);

        
        // ✅ 음식명
        JTextArea nameLabel = new JTextArea(food.getFoodName());
        nameLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 17));
        nameLabel.setBounds(14, 10, 268, 44);
        nameLabel.setLineWrap(true);
        nameLabel.setWrapStyleWord(true);
        nameLabel.setEditable(false);
        nameLabel.setFocusable(false);
        nameLabel.setOpaque(false);
        nameLabel.setToolTipText(food.getFoodName());
        nameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        nameLabel.addMouseListener(openDetailListener);
        
        // ✅ 그램 표시 라벨 수정
        JLabel weightLabel = new JLabel();
        if (isFavoriteList) {
            // 담은 목록에서는 설정된 그램 값 표시
            weightLabel.setText(food.getWeight() + "g");
        } else {
            // 검색 목록에서는 기본 100g 표시
            weightLabel.setText("100g");
        }
        weightLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        weightLabel.setForeground(Color.GRAY);
        weightLabel.setBounds(14, 65, 100, 20);
        weightLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        weightLabel.addMouseListener(openDetailListener);
        
        // ✅ 칼로리 정보 (실제 그램 기준으로 표시)
        JLabel kcalLabel = new JLabel();
        if (isFavoriteList) {
            // 담은 목록에서는 설정된 그램에 따른 실제 칼로리 표시
            kcalLabel.setText((int)food.getFoodKcal() + " kcal");
        } else {
            // 검색 목록에서는 100g 기준 칼로리 표시
            kcalLabel.setText((int)food.getFoodKcal() + " kcal");
        }
        kcalLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        kcalLabel.setBounds(177, 63, 105, 25);
        kcalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        kcalLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        kcalLabel.addMouseListener(openDetailListener);
       
     // ✅ 담기 버튼 추가
        RoundedComponent addButton = new RoundedComponent(82, 40, 12, "button", "담기",
                new Color(0x609056), new Color(0x609056), Color.WHITE, "Malgun Gothic", Font.BOLD, 14);
        addButton.setBounds(294, 14, 82, 40);
        addButton.getButton().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.getButton().addActionListener(e -> addToFavoriteList(food));
        
        if (!isFavoriteList) { // 담은 목록이 아닐 때만 + 버튼 추가
            itemPanel.add(addButton);
        }
        
        // ✅ 빼기 버튼 추가 (담은 목록에서만 표시)
        RoundedComponent removeButton = new RoundedComponent(82, 40, 12, "button", "빼기",
                new Color(0x7A7A7A), Color.WHITE, new Color(0x4A4A4A), "Malgun Gothic", Font.BOLD, 14);
        removeButton.setBounds(294, 14, 82, 40);
        removeButton.getButton().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        removeButton.getButton().addActionListener(e -> removeFavoriteItem(food));
        if(isFavoriteList) {
        	itemPanel.add(removeButton);
        }
        
        // ✅ 패널에 추가
        itemPanel.add(nameLabel);
        itemPanel.add(weightLabel);
        itemPanel.add(kcalLabel);
      
        // ✅ 감싸는 패널 추가
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout()); // FlowLayout 대신 BorderLayout 사용
        wrapper.setPreferredSize(new Dimension(390, 104));
        wrapper.setMaximumSize(new Dimension(390, 104));
        wrapper.setBackground(new Color(0xF3F5F0));
        wrapper.add(itemPanel, BorderLayout.CENTER);

        return wrapper;
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
        emptyPanel.setPreferredSize(new Dimension(390, 150));
        emptyPanel.setMaximumSize(new Dimension(390, 150));
        emptyPanel.setBackground(Color.WHITE);
        emptyPanel.setBorder(BorderFactory.createLineBorder(new Color(0xD8E6D2), 1, true));

        JLabel messageLabel = new JLabel(
                "<html><div style='text-align:center;width:340px'>" + message + "</div></html>",
                SwingConstants.CENTER);
        messageLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        messageLabel.setForeground(new Color(0x666666));
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
        searchField.setForeground(Color.GRAY);
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
        searchField.setForeground(Color.GRAY);
        updateFavoriteTabTitle();
        showFavoriteItems();
        
        // UI 업데이트를 확실히 하기 위해
        revalidate();
        repaint();
    }

    // 검색 탭으로 전환하는 메서드
    public void switchToSearchTab() {
        selectedTabIndicator.setBounds(0, 35, 215, 5);
        searchPanel.setVisible(true);
        searchScrollPane.setVisible(true);
        favoriteScrollPane.setVisible(false);
        saveButtonComponent.setVisible(false);
        isFavoriteList = false;
    }
}
