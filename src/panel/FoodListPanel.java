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
    private MainUserPanel mainUserPanel;
    private JScrollPane searchScrollPane, favoriteScrollPane;
    private JTextField searchField; // 🔹 검색 입력 필드 추가
    private JButton searchButton, searchTabButton, favoriteTabButton;  // 🔹 검색 버튼 추가
    private JPanel contentPanel, searchContentPanel, favoriteContentPanel, selectedTabIndicator, searchPanel;
    
    // ✅ 음식별 g 값을 저장할 HashMap 추가
    private HashMap<String, Integer> foodWeightMap = new HashMap<>();
    
    public Vector<FoodBean> favoriteItems = new Vector<>(); // ✅ 담은 목록 저장
    private boolean isFavoriteList = false; // ✅ 현재 담은 목록인지 여부
    private FoodDAO foodDAO;
    private MealDAO mealDAO;
    private MealLogDAO mealLogDAO;
    private String userId;
    private RoundedComponent saveButtonComponent; // private으로 선언
    private String currentMealType = "";  // 현재 선택된 식사 유형
    private JPanel favoritePanel; // 담은 목록 패널

    public FoodListPanel(MainUserPanel mainUserPanel) {
        this.mainUserPanel = mainUserPanel;
        this.foodDAO = new FoodDAO();
        this.mealDAO = new MealDAO();
        this.mealLogDAO = new MealLogDAO();

        setLayout(null);
        setBackground(new Color(217, 217, 217)); 
        setBounds(0, 0, 440, 956);
        
     // 상단 탭바 패널
        JPanel tabPanel = new JPanel();
        tabPanel.setLayout(null);
        tabPanel.setBounds(0, 0, 440, 40);
        tabPanel.setBackground(new Color(160,212,104)); // 연한 초록색
        add(tabPanel);

        // 검색 탭 버튼
        searchTabButton = new JButton("검색");
        searchTabButton.setFont(new Font("Inter", Font.BOLD, 14));
        searchTabButton.setBounds(0, 5, 215, 30);
        searchTabButton.setBorderPainted(false);
        searchTabButton.setFocusPainted(false);
        searchTabButton.setContentAreaFilled(false);
        tabPanel.add(searchTabButton);

        // 담은 목록 탭 버튼
        favoriteTabButton = new JButton("담은 목록");
        favoriteTabButton.setFont(new Font("Inter", Font.BOLD, 14));
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

        // 검색바 패널
        searchPanel = new JPanel(null);
        searchPanel.setBounds(15, 50, 410, 40);
        searchPanel.setBackground(new Color(217, 217, 217));

        searchField = new JTextField(" 검색어를 입력하세요...");  // 🔹 플레이스홀더 효과 추가
        searchField.setBounds(40, 5, 300, 30);
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(null);
        
        // 🔹 검색 필드 클릭 시 플레이스홀더 효과 제거
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
        
        // 🔍 검색 아이콘 버튼 생성
        ImageIcon searchIcon = new ImageIcon("C:\\Users\\dita_806\\Desktop\\project8\\src\\images\\search.png"); // 이미지 경로 확인!
        Image img = searchIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH); // 이미지 크기 조정
        searchButton = new JButton(new ImageIcon(img));
        searchButton.setBounds(5, 5, 30, 30);
        searchButton.setContentAreaFilled(false);  // 버튼 배경 투명화
        searchButton.setBorderPainted(false);      // 버튼 테두리 제거
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // 🔹 패널에 둥근 테두리 효과 적용
        RoundedComponent roundedSearchPanel = new RoundedComponent(390, 40, 40, "panel", "",
                Color.LIGHT_GRAY, Color.WHITE, Color.black, "", Font.PLAIN, 0);
        roundedSearchPanel.setBounds(0, 0, 390, 40);
        searchPanel.add(roundedSearchPanel);

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
        searchContentPanel.setBackground(new Color(217, 217, 217));
        
        // ✅ 검색 결과용 스크롤 패널
        searchScrollPane = new JScrollPane(searchContentPanel);
        searchScrollPane.setBounds(10, 100, 420, 500);
        searchScrollPane.setBorder(null);
        searchScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        searchScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(searchScrollPane);
        
        // ✅ 담은 목록 리스트 패널
        favoriteContentPanel = new JPanel();
        favoriteContentPanel.setLayout(new BoxLayout(favoriteContentPanel, BoxLayout.Y_AXIS));
        favoriteContentPanel.setBackground(new Color(217, 217, 217));

        // ✅ 담은 목록용 스크롤 패널
        favoriteScrollPane = new JScrollPane(favoriteContentPanel);
        favoriteScrollPane.setBounds(10, 100, 420, 500);
        favoriteScrollPane.setBorder(null);
        favoriteScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        favoriteScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        favoriteScrollPane.setVisible(false);
        add(favoriteScrollPane);
        
        // 저장 버튼 초기화 및 위치 설정
        saveButtonComponent = new RoundedComponent(100, 40, 10, "button", "저장", 
                Color.BLACK, Color.BLACK, Color.WHITE, "Inter", Font.BOLD, 14);
        saveButtonComponent.setBounds(170, 620, 100, 40);
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
        if (searchKeyword.equals(" 검색어를 입력하세요...") || searchKeyword.isEmpty()) {
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
            System.out.println("✅ 담은 목록에 추가됨: " + food.getFoodName() + " (코드: " + food.getFoodCode() + ")");
        } else {
            System.out.println("⚠ 이미 담은 목록에 있음: " + food.getFoodName());
        }
        
        // 담은 목록 UI 업데이트
        if (isFavoriteList) {
            showFavoriteItems();
        }
    }
    
    // ✅ - 버튼을 눌렀을 때 호출될 메서드
    private void removeFavoriteItem(FoodBean food) {
        favoriteItems.remove(food);
        if (isFavoriteList) {
            showFavoriteItems();
        }
    }
    
    // ✅ 음식 리스트를 검색하여 표시
    private void populateItems(String searchKeyword) {
        searchContentPanel.removeAll(); // 🔹 기존 리스트 삭제

        Vector<FoodBean> foodData = foodDAO.getFoodListBySearch(searchKeyword); // 🔹 검색 결과 가져오기
        int minItems = 5; // ✅ 최소 5개 아이템이 보이도록 설정

        for (FoodBean food : foodData) {
            searchContentPanel.add(createRoundedItem(food));
            searchContentPanel.add(Box.createVerticalStrut(1));  // 🔹 아이템 간 간격 추가
        }

        // 🔹 리스트 개수가 부족할 경우 빈 패널 추가하여 공간 유지
        for (int i = foodData.size(); i < minItems; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setPreferredSize(new Dimension(420, 120)); // ✅ 간격 조정
            emptyPanel.setBackground(new Color(217, 217, 217));
            searchContentPanel.add(emptyPanel);
            searchContentPanel.add(Box.createVerticalStrut(1));
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
        
        // 실제 아이템 개수에 따라 패널 크기 조정
        int itemCount = favoriteItems.size();
        int minItems = 5; // 최소 5개 아이템이 보이도록 설정
        
        // 실제 아이템 추가
        for (FoodBean food : favoriteItems) {
            favoriteContentPanel.add(createRoundedItem(food));
            favoriteContentPanel.add(Box.createVerticalStrut(1));
        }
        
        // 최소 아이템 개수를 맞추기 위해 빈 패널 추가
        for (int i = itemCount; i < minItems; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setPreferredSize(new Dimension(420, 110));
            emptyPanel.setBackground(new Color(217, 217, 217));
            favoriteContentPanel.add(emptyPanel);
            favoriteContentPanel.add(Box.createVerticalStrut(1));
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
        
        // 저장 버튼 위치 재설정
        saveButtonComponent.setBounds(170, 620, 100, 40);
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
            // 저장 전 로그 출력
            System.out.println("🔹 DB 저장 시작: userId=" + userId + ", mealType=" + currentMealType);
            
            // meal 테이블에 새 레코드 추가 (meal_type 포함)
            int mealCode = mealDAO.insertMeal(userId, currentMealType);
            
            if (mealCode == -1) {
                throw new Exception("식사 기록 생성에 실패했습니다.");
            }
            
            System.out.println("✅ 생성된 meal_code: " + mealCode);
            
            // meal_log 테이블에 담은 음식 목록 추가
            boolean success = mealLogDAO.insertMealLogs(mealCode, favoriteItems);
            
            if (!success) {
                throw new Exception("식단 저장에 실패했습니다.");
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
                "식단 저장 중 오류가 발생했습니다: " + e.getMessage(),
                "저장 오류",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ✅ 둥근 네모 박스를 생성하는 메서드
    private JPanel createRoundedItem(FoodBean food) {
    	
    	// ✅ 둥근 네모 박스 패널 (음식 아이템)
    	RoundedComponent itemPanel = new RoundedComponent(390, 100, 20, "panel", "", 
                Color.WHITE, Color.WHITE, Color.BLACK, "Inter", Font.BOLD, 16);
        itemPanel.setLayout(null);
        itemPanel.setBounds(0, 0, 390, 100);
        // ✅ 음식 아이템을 클릭했을 때 FoodInfoPanel로 이동
        itemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                System.out.println("🍽️ 음식 아이템 클릭됨: " + food.getFoodName());
                
                if (isFavoriteList) {
                    // DB에서 원본 데이터를 가져옴
                    FoodBean originalFood = foodDAO.getFoodByName(food.getFoodName());
                    mainUserPanel.foodInfoPanel.updateFoodInfo(originalFood);
                } else {
                    mainUserPanel.foodInfoPanel.updateFoodInfo(food);
                }
                
                mainUserPanel.showPanel("foodInfo");
            }
        });

        
        // ✅ 음식명
        JLabel nameLabel = new JLabel(food.getFoodName());
        nameLabel.setFont(new Font("Inter", Font.BOLD, 18));
        nameLabel.setBounds(10, 10, 300, 25);
        
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
        weightLabel.setBounds(10, 70, 100, 20);
        
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
        kcalLabel.setBounds(270, 70, 100, 25);
        kcalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
       
     // ✅ + 버튼 추가 (담은 목록에 추가)
        RoundedComponent addButton = new RoundedComponent(60, 60, 20, "button", "+", 
                Color.WHITE, Color.WHITE, Color.BLACK, "Inter", Font.BOLD, 32);
        addButton.setBounds(330, 0, 60, 60);
        addButton.getButton().addActionListener(e -> addToFavoriteList(food));
        
        if (!isFavoriteList) { // 담은 목록이 아닐 때만 + 버튼 추가
            itemPanel.add(addButton);
        }
        
        // ✅ - 버튼 추가 (담은 목록에서만 표시)
        RoundedComponent removeButton = new RoundedComponent(60, 60, 20, "button", "-", 
                Color.WHITE, Color.WHITE, Color.BLACK, "Inter", Font.BOLD, 32);
        removeButton.setBounds(330, 0, 60, 60);
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
        wrapper.setPreferredSize(new Dimension(420, 110));
        wrapper.setBackground(new Color(217, 217, 217));
        wrapper.add(itemPanel, BorderLayout.CENTER);

        return wrapper;
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
	    
	    System.out.println("✅ 담은 목록의 음식 정보 업데이트: " + updatedFood.getFoodName() 
	                     + " (코드: " + updatedFood.getFoodCode() + ", " + updatedFood.getFoodKcal() + "kcal)");
	}

    // meal_type 설정 메서드
    public void setMealType(String mealType) {
        this.currentMealType = mealType;
        System.out.println("✅ 식사 시간대 설정: " + mealType);
        
        // 검색 탭으로 초기화
        switchToSearchTab();
        
        // 검색 필드 초기화
        searchField.setText(""); // JTextField는 직접 setText 메서드를 가지고 있음
        populateItems("");
    }

    // 패널 초기화 메서드
    private void clearPanel() {
        favoriteItems.clear();
        currentMealType = "";
        searchField.setText(" 검색어를 입력하세요...");
        searchField.setForeground(Color.GRAY);
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
