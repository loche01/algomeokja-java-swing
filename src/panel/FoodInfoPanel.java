package panel;

import java.awt.*;
import javax.swing.*;
import main.MainUserPanel;
import model.FoodBean;
import ui_n_utils.AppTheme;
import ui_n_utils.RoundedComponent;

public class FoodInfoPanel extends JPanel {
    private static final int DETAIL_CARD_Y = 67;
    private static final int DETAIL_CARD_HEIGHT = 569;
    private static final int FINISH_BUTTON_WIDTH = 330;
    private MainUserPanel mainUserPanel;
    private JLabel foodNameLabel, kcalLabel, mealTypeLabel;
    private RoundedComponent proteinPanel, carbPanel, fatPanel, gPanel, searchField;
    private RoundedComponent backButton, mainPanel, finishButton, aButton, bButton;
    private int weightValue = 100; // 🔹 기본 g 값 (100g)
    private FoodBean currentFood;
    private FoodListPanel foodListPanel;
    
    public FoodInfoPanel(MainUserPanel mainUserPanel) {
        this.mainUserPanel = mainUserPanel;
        
        setLayout(null);
        setBackground(AppTheme.BACKGROUND);
        setBounds(0, 0, 440, 736);

        // 메인 패널 생성
        mainPanel = new RoundedComponent(AppTheme.CARD_WIDTH, DETAIL_CARD_HEIGHT, 20, "panel", " ",
                AppTheme.BORDER, AppTheme.CARD, AppTheme.TEXT, " ", 0, 0);
        mainPanel.setBounds(
                AppTheme.HORIZONTAL_MARGIN, DETAIL_CARD_Y,
                AppTheme.CARD_WIDTH, DETAIL_CARD_HEIGHT);
        mainPanel.setLayout(null);
        add(mainPanel);

        // 뒤로가기 버튼
        backButton = new RoundedComponent(100, 40, 10, "button", "목록으로",
                AppTheme.PRIMARY, AppTheme.CARD, AppTheme.PRIMARY_DARK, Font.SANS_SERIF, Font.BOLD, 13);
        backButton.setBounds(20, 22, 100, 40);
        backButton.getButton().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.getButton().addActionListener(e ->{
        	 resetFoodInfo(); // 🔹 뒤로가기 시 음식 정보 초기화
        	mainUserPanel.showPanel("foodList");
        }); 
        mainPanel.add(backButton);

        mealTypeLabel = new JLabel("선택한 식사: -", SwingConstants.RIGHT);
        mealTypeLabel.setForeground(AppTheme.PRIMARY_DARK);
        mealTypeLabel.setFont(AppTheme.BODY_BOLD_FONT);
        mealTypeLabel.setBounds(126, 28, 230, 28);
        mainPanel.add(mealTypeLabel);

        // 음식명 라벨
        foodNameLabel = new JLabel("음식 이름", SwingConstants.CENTER);
        foodNameLabel.setForeground(AppTheme.TEXT);
        foodNameLabel.setFont(AppTheme.TITLE_FONT);
        foodNameLabel.setBounds(0, 78, AppTheme.CARD_WIDTH, 56);
        foodNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        foodNameLabel.setVerticalAlignment(SwingConstants.CENTER);
        mainPanel.add(foodNameLabel);

        // 🔹 탄수화물 패널
		carbPanel = createNutritionPanel(16, 150, "탄수화물", "",
				AppTheme.PRIMARY_LIGHT, AppTheme.PRIMARY_DARK);
        mainPanel.add(carbPanel);
        
        // 🔹 단백질 패널
		proteinPanel = createNutritionPanel(138, 150, "단백질", "",
				AppTheme.PRIMARY_LIGHT, AppTheme.PRIMARY_DARK);
        mainPanel.add(proteinPanel);

        // 🔹 지방 패널
		fatPanel = createNutritionPanel(260, 150, "지방", "",
				AppTheme.PRIMARY_LIGHT, AppTheme.PRIMARY_DARK);
        mainPanel.add(fatPanel);

        JLabel weightLabel = new JLabel("선택 중량 (10g 단위)", SwingConstants.CENTER);
        weightLabel.setFont(AppTheme.BODY_BOLD_FONT);
        weightLabel.setForeground(AppTheme.PRIMARY_DARK);
        weightLabel.setBounds(50, 282, 300, 26);
        mainPanel.add(weightLabel);

        // 🔹 단위 선택 패널
        gPanel = new RoundedComponent(300, 50, 24, "panel", " ",
                AppTheme.BORDER, AppTheme.INPUT_BACKGROUND, AppTheme.TEXT, " ", 0, 0);
        gPanel.setBounds(50, 315, 300, 50);
        gPanel.setLayout(null);
        mainPanel.add(gPanel);

        // 🔹 + / - 버튼 및 텍스트 필드
		aButton = createRoundButton("-10g", 10, 5, 80, 40, 14);
        aButton.getButton().addActionListener(e -> updateWeight(-10)); // 🔹 10g 감소
        aButton.getButton().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gPanel.add(aButton);
        
        searchField = new RoundedComponent(80, 40, 20, "textfield", weightValue + "g",
                AppTheme.BORDER, AppTheme.CARD, AppTheme.TEXT,
                Font.SANS_SERIF, Font.BOLD , 16);
        searchField.setBounds(110, 5, 80, 40);
        searchField.getTextField().setHorizontalAlignment(SwingConstants.CENTER);
        searchField.getTextField().setEditable(false);
        searchField.getTextField().setFocusable(false);
        AppTheme.styleReadOnlyField(searchField.getTextField());
        gPanel.add(searchField);
		
		bButton = createRoundButton("+10g", 210, 5, 80, 40, 14);
        bButton.getButton().addActionListener(e -> updateWeight(10)); // 🔹 10g 증가
        bButton.getButton().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gPanel.add(bButton);
		

        // 🔹 총 열량 라벨
        JLabel calorieLabel = new JLabel("총 열량");
        calorieLabel.setFont(AppTheme.SECTION_TITLE_FONT);
		calorieLabel.setForeground(AppTheme.TEXT);
		calorieLabel.setBounds(20, 395, 360, 30);
		calorieLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(calorieLabel);

        kcalLabel = new JLabel();
        kcalLabel.setFont(AppTheme.TITLE_FONT);
		kcalLabel.setForeground(AppTheme.PRIMARY_DARK);
		kcalLabel.setBounds(20, 425, 360, 48);
		kcalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(kcalLabel);

        // 🔹 담기 버튼
        finishButton = new RoundedComponent(FINISH_BUTTON_WIDTH, 48, 12, "button", "담은 목록에 반영",
                AppTheme.PRIMARY_DARK, AppTheme.PRIMARY_DARK, Color.WHITE, Font.SANS_SERIF,
                Font.BOLD, 14);
        finishButton.setBounds(
                (AppTheme.CARD_WIDTH - FINISH_BUTTON_WIDTH) / 2,
                499, FINISH_BUTTON_WIDTH, 48);
        finishButton.getButton().setHorizontalAlignment(SwingConstants.CENTER);
        finishButton.getButton().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        mainPanel.add(finishButton);
        finishButton.getButton().addActionListener(e -> {
            FoodBean updatedFood = getUpdatedFood(); // 수정된 g 기준 반영
            
            boolean isUpdate = false;
            // 담은 목록에 이미 있는지 확인
            for (FoodBean food : mainUserPanel.foodListPanel.favoriteItems) {
                if (food.getFoodName().equals(currentFood.getFoodName())) {
                    isUpdate = true;
                    break;
                }
            }
            
            if (isUpdate) {
                // 이미 존재하는 항목 업데이트 (저장은 하지 않음)
                mainUserPanel.foodListPanel.updateFavoriteItem(updatedFood);
                JOptionPane.showMessageDialog(this, "담은 목록의 음식이 업데이트되었습니다!");
            } else {
                // 새로운 항목 추가
                mainUserPanel.foodListPanel.addToFavoriteList(updatedFood);
                JOptionPane.showMessageDialog(this, "음식이 담은 목록에 추가되었습니다!");
            }

            mainUserPanel.foodListPanel.switchToFavoriteTab(); // '담은 목록' 탭으로 이동
            mainUserPanel.showPanel("foodList"); // FoodListPanel 화면으로 전환
        });

        mainPanel.add(finishButton);
    }

    // 🔹 영양 성분 패널 생성 함수
    private RoundedComponent createNutritionPanel(int x, int y, String title, String value, Color bgColor,Color ftColor) {
        RoundedComponent panel = new RoundedComponent(104, 110, 18, "panel", " ",
                bgColor, bgColor, Color.black, " ", 0, 0);
        panel.setBounds(x, y, 104, 110);
        panel.setLayout(null);

        JLabel label = new JLabel(title);
        label.setForeground(ftColor);
        label.setFont(AppTheme.BODY_BOLD_FONT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBounds(8, 20, 88, 20);
        panel.add(label);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(ftColor);
        valueLabel.setFont(AppTheme.SECTION_TITLE_FONT);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setBounds(8, 46, 88, 40);
        panel.add(valueLabel);

        return panel;
    }
    // ✅ 담은 목록에 추가하는 메서드
    private void addToFavoriteList() {
        if (currentFood == null) return; // 음식 정보가 없으면 실행 안 함

        // ✅ 선택한 음식 정보를 g 값과 함께 새로운 FoodBean으로 생성
        FoodBean foodToSave = new FoodBean();
        foodToSave.setFoodName(currentFood.getFoodName());
        foodToSave.setFoodKcal(calculateKcal(currentFood)); // 현재 g 값에 맞는 칼로리 저장
        foodToSave.setCarb(calculateCarb(currentFood)); // 현재 g 값에 맞는 탄수화물 저장
        foodToSave.setProtein(calculateProtein(currentFood)); // 현재 g 값에 맞는 단백질 저장
        foodToSave.setFat(calculateFat(currentFood)); // 현재 g 값에 맞는 지방 저장

        mainUserPanel.foodListPanel.addToFavoriteList(foodToSave); // ✅ FoodListPanel에 추가
        JOptionPane.showMessageDialog(this, "음식이 담은 목록에 추가되었습니다!");
    }
    
    public void updateFoodInfo(FoodBean food) {
        if (food == null) return;
        
        resetFoodInfo(); // 기존 정보 초기화
        
        this.currentFood = new FoodBean(); // 새로운 FoodBean 객체 생성
        this.currentFood.setFoodName(food.getFoodName());
        this.currentFood.setFoodKcal(food.getFoodKcal());
        this.currentFood.setCarb(food.getCarb());
        this.currentFood.setProtein(food.getProtein());
        this.currentFood.setFat(food.getFat());
        
        updateUIValues(); // UI 값 업데이트
    }
    
    public void updateFoodInfoFromFavorite(FoodBean food) {
        if (food == null) return;
        
        resetFoodInfo(); // 기존 정보 초기화
        
        // 원본 100g 기준 값으로 FoodBean 생성
        this.currentFood = new FoodBean();
        this.currentFood.setFoodName(food.getFoodName());
        this.currentFood.setFoodKcal(food.getFoodKcal());
        this.currentFood.setCarb(food.getCarb());
        this.currentFood.setProtein(food.getProtein());
        this.currentFood.setFat(food.getFat());
        
        updateUIValues(); // UI 값 업데이트
    }

    // UI 값을 업데이트하는 메서드 추가
    private void updateUIValues() {
        foodNameLabel.setText("<html><div style='text-align:center;width:330px'>"
                + escapeHtml(currentFood.getFoodName()) + "</div></html>");
        foodNameLabel.setToolTipText(currentFood.getFoodName());
        updateMealTypeLabel();
        kcalLabel.setText(calculateKcal(currentFood) + " kcal");
        
        // 영양소 값 UI 업데이트
        ((JLabel) carbPanel.getComponent(2)).setText(calculateCarb(currentFood) + "g");
        ((JLabel) proteinPanel.getComponent(2)).setText(calculateProtein(currentFood) + "g");
        ((JLabel) fatPanel.getComponent(2)).setText(calculateFat(currentFood) + "g");
        
        // g 값 초기화
        weightValue = 100;
        searchField.getTextField().setText(weightValue + "g");
        
        repaint();
        revalidate();
    }

    // 🔹 둥근 버튼 생성 함수
    private RoundedComponent createRoundButton(String text, int x, int y, int width, int height, int fontSize) {
        RoundedComponent button = new RoundedComponent(width, height, 10, "button", text,
                AppTheme.PRIMARY, AppTheme.CARD,
                AppTheme.PRIMARY_DARK, Font.SANS_SERIF, Font.BOLD, fontSize);
        button.setBounds(x, y, width, height);
        return button;
    }

    // 🔹 g 값을 기반으로 칼로리 계산
    private int calculateKcal(FoodBean food) {
    	if (food == null) return 0; // 🔹 food가 null이면 0 반환
        return (int) ((food.getFoodKcal() * weightValue) / 100.0); // 100g 기준 값 비례 연산
    }
    
    // ✅ g 값에 맞춰 탄수화물 계산
    private double calculateCarb(FoodBean food) {
        if (food == null) return 0;
        return Math.round((food.getCarb() * weightValue) / 100.0 * 10) / 10.0;
    }

    // ✅ g 값에 맞춰 단백질 계산
    private double calculateProtein(FoodBean food) {
        if (food == null) return 0;
        return Math.round((food.getProtein() * weightValue) / 100.0 * 10) / 10.0;
    }

    // ✅ g 값에 맞춰 지방 계산
    private double calculateFat(FoodBean food) {
        if (food == null) return 0;
        return Math.round((food.getFat() * weightValue) / 100.0 * 10) / 10.0;
    }
    
    // ✅ g 값 조정 및 UI 업데이트
    private void updateWeight(int change) {
    	if (currentFood == null) return; // 🔹 food 정보가 없으면 실행하지 않음

    	
        weightValue += change;
        if (weightValue < 10) weightValue = 10; // 🔹 최소 10g
        if (weightValue > 500) weightValue = 500; // 🔹 최대 500g

        searchField.getTextField().setText(weightValue + "g"); // 🔹 g 값 업데이트
        kcalLabel.setText(calculateKcal(currentFood) + " kcal"); // 🔹 칼로리 재계산

        ((JLabel) carbPanel.getComponent(2)).setText(calculateCarb(currentFood) + "g");
        ((JLabel) proteinPanel.getComponent(2)).setText(calculateProtein(currentFood) + "g");
        ((JLabel) fatPanel.getComponent(2)).setText(calculateFat(currentFood) + "g");

        repaint();
        revalidate();
    }
    
 // ✅ 음식 정보를 초기화하는 메서드 추가
    public void resetFoodInfo() {
        currentFood = null; // 현재 음식 정보 초기화
        weightValue = 100; // 기본 g 값 초기화
        
        foodNameLabel.setText("음식 이름");
        foodNameLabel.setToolTipText(null);
        mealTypeLabel.setText("선택한 식사: -");
        kcalLabel.setText("0 kcal");
        searchField.getTextField().setText(weightValue + "g");

        ((JLabel) carbPanel.getComponent(2)).setText("0g");
        ((JLabel) proteinPanel.getComponent(2)).setText("0g");
        ((JLabel) fatPanel.getComponent(2)).setText("0g");

        repaint();
        revalidate();
    }

    // getUpdatedFood 메서드 추가
    private FoodBean getUpdatedFood() {
        FoodBean updatedFood = new FoodBean();
        updatedFood.setFoodCode(currentFood.getFoodCode()); // 음식 코드 유지
        updatedFood.setFoodName(currentFood.getFoodName());
        updatedFood.setFoodKcal(calculateKcal(currentFood));
        updatedFood.setCarb(calculateCarb(currentFood));
        updatedFood.setProtein(calculateProtein(currentFood));
        updatedFood.setFat(calculateFat(currentFood));
        updatedFood.setWeight(weightValue); // 현재 설정된 그램 값 저장
        return updatedFood;
    }

    private void updateMealTypeLabel() {
        String mealType = mainUserPanel.foodListPanel.getCurrentMealType();
        mealTypeLabel.setText(mealType == null || mealType.isBlank()
                ? "선택한 식사: -"
                : "선택한 식사: " + mealType);
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

}
