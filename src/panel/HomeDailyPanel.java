package panel;

import DB.MealDAO;
import DB.MealLogDAO;
import java.awt.*;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import model.LoginManager;
import ui_n_utils.RoundedComponent;

public class HomeDailyPanel extends JPanel {
	private RoundedComponent tPanel, tbPanel, tcPanel;
    private IntegratedSemiCircularGauge calorieGauge; // 내부 클래스로 구현한 반원형 게이지
    private JTextField aField, bField;
    private JLabel calorieInfoLabel, todayLabel;
    private JLabel carbValueLabel, proteinValueLabel, fatValueLabel;
    private JLabel currentCalorieLabel, targetCalorieLabel;
    private CustomProgressBar carbProgressBar, proteinProgressBar, fatProgressBar;
    
    // 영양소 목표량 (기본값)
    private double targetCalories = 1200.0;
    private double targetCarbs = 267.0; // g
    private double targetProtein = 138.0; // g
    private double targetFat = 36.0; // g
    
    // 현재 섭취량
    private double currentCalories = 0.0;
    private double currentCarbs = 0.0;
    private double currentProtein = 0.0;
    private double currentFat = 0.0;
    
    // DAO 객체
    private MealDAO mealDAO;
    private MealLogDAO mealLogDAO;
    
    // 타이머
    private Timer updateTimer;
	
    public HomeDailyPanel() {
        setBounds(0, 140, 440, 700);
        setBackground(new Color(192, 233, 147)); // 연한 초록색 (#C0E993)
        setLayout(null);

        // DAO 초기화
        mealDAO = new MealDAO();
        mealLogDAO = new MealLogDAO();
        
        // 목표 영양소 값이 0 이하인 경우 기본값 설정
        if (targetCalories <= 0) targetCalories = 1200.0;
        if (targetCarbs <= 0) targetCarbs = 267.0;
        if (targetProtein <= 0) targetProtein = 138.0;
        if (targetFat <= 0) targetFat = 36.0;

        todayLabel = new JLabel("오늘 하루");
        todayLabel.setBounds(124, 25, 192, 60);
        todayLabel.setFont(new Font("Inter", Font.BOLD, 42));
        todayLabel.setForeground(new Color(107, 142, 78));
        add(todayLabel);

        // 칼로리 표시 레이블
        currentCalorieLabel = new JLabel("0");
        currentCalorieLabel.setForeground(Color.black);
        currentCalorieLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 34));
        currentCalorieLabel.setBounds(110, 100, 100, 30);
        add(currentCalorieLabel);

        JLabel slashLabel = new JLabel("/");
        slashLabel.setForeground(Color.black);
        slashLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 34));
        slashLabel.setBounds(190, 100, 20, 30);
        add(slashLabel);

        targetCalorieLabel = new JLabel("1200");
        targetCalorieLabel.setForeground(Color.white);
        targetCalorieLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 24));
        targetCalorieLabel.setBounds(205, 100, 100, 30);
        add(targetCalorieLabel);
		
		JLabel cTitle = new JLabel("kcal");
	    cTitle.setForeground(Color.black);
		cTitle.setFont(new Font("Malgun Gothic", Font.BOLD, 24));
        cTitle.setBounds(270, 100, 100, 30);
		add(cTitle);

        // 반원형 게이지 생성 및 추가 (내부 클래스 활용)
        calorieGauge = new IntegratedSemiCircularGauge(300, 150);
        calorieGauge.setBounds(60, 250, 330, 150);
        // 현재 섭취한 칼로리의 퍼센트 설정 (초기값 0%)
        calorieGauge.setPercentage(0);
        add(calorieGauge);
        
		//탄수화물 패널
		tPanel = new RoundedComponent(35, 35, 35, "panel", " ", new Color(0xF3F3F3),new Color(0xF3F3F3), Color.black,
				" ", 0, 0);
        tPanel.setBounds(60, 175, 35, 35); // Y 위치를 게이지 아래로 조정
		tPanel.setLayout(null);
		tPanel.setEnabled(false);
        add(tPanel);
        
		JLabel tLabel = new JLabel("탄");
		tLabel.setForeground(Color.black);
		tLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		tLabel.setBounds(8, 6, 40, 20);
		tPanel.add(tLabel);
		
		JLabel taLabel = new JLabel("37%");
        taLabel.setForeground(Color.black);
		taLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
        taLabel.setBounds(105, 175, 45, 30); // Y 위치를 게이지 아래로 조정
		add(taLabel);
		
		//단백질 패널
		tbPanel = new RoundedComponent(35, 35, 35, "panel", " ", new Color(0xF6F6B5),new Color(0xF6F6B5), Color.black,
				" ", 0, 0);
        tbPanel.setBounds(170, 175, 35, 35); // Y 위치를 게이지 아래로 조정
		tbPanel.setLayout(null);
		tbPanel.setEnabled(false);
        add(tbPanel);
        
		JLabel tbLabel = new JLabel("단");
		tbLabel.setForeground(Color.black);
		tbLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		tbLabel.setBounds(8, 6, 40, 20);
		tbPanel.add(tbLabel);
		
		JLabel tbaLabel = new JLabel("20%");
        tbaLabel.setForeground(Color.black);
		tbaLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
        tbaLabel.setBounds(215, 175, 45, 30); // Y 위치를 게이지 아래로 조정
		add(tbaLabel);
		
		//지방 패널
		tcPanel = new RoundedComponent(35, 35, 35, "panel", " ", new Color(0x3B467A),new Color(0x3B467A), Color.black,
				" ", 0, 0);
        tcPanel.setBounds(278, 175, 35, 35); // Y 위치를 게이지 아래로 조정
		tcPanel.setLayout(null);
		tcPanel.setEnabled(false);
        add(tcPanel);
        
		JLabel tcLabel = new JLabel("지");
		tcLabel.setForeground(Color.white);
		tcLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
		tcLabel.setBounds(8, 6, 40, 20);
		tcPanel.add(tcLabel);
		
		JLabel tcaLabel = new JLabel("20%");
        tcaLabel.setForeground(Color.black);
		tcaLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
        tcaLabel.setBounds(323, 175, 45, 30); // Y 위치를 게이지 아래로 조정
		add(tcaLabel);
		
        // 추가된 부분: 칼로리 남은 정보 라벨
        calorieInfoLabel = new JLabel("0 kcal 소모 | 0 kcal 더 먹을 수 있어요");
        calorieInfoLabel.setForeground(Color.black);
        calorieInfoLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        calorieInfoLabel.setBounds(78, 435, 330, 20);
        add(calorieInfoLabel);
        
        // 수정: 영양소 섹션을 커스텀 프로그레스 바로 변경
        // 탄수화물 프로그레스 바 섹션
        JLabel carbLabel = new JLabel("탄수화물");
        carbLabel.setForeground(Color.black);
        carbLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        carbLabel.setBounds(50, 505, 100, 20);
        add(carbLabel);
        
        // 커스텀 둥근 프로그레스 바 추가 (탄수화물)
        carbProgressBar = new CustomProgressBar(0, new Color(0x609056));
        carbProgressBar.setBounds(30, 540, 100, 15);
        add(carbProgressBar);
        
        // 탄수화물 값 라벨
        carbValueLabel = new JLabel("0 / " + (int)targetCarbs + "g");
        carbValueLabel.setForeground(Color.black);
        carbValueLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        carbValueLabel.setBounds(45, 565, 100, 25);
        add(carbValueLabel);
        
        // 단백질 프로그레스 바 섹션
        JLabel proteinLabel = new JLabel("단백질");
        proteinLabel.setForeground(Color.black);
        proteinLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        proteinLabel.setBounds(185, 505, 100, 20);
        add(proteinLabel);
        
        // 커스텀 둥근 프로그레스 바 추가 (단백질)
        proteinProgressBar = new CustomProgressBar(0, new Color(0x609056));
        proteinProgressBar.setBounds(160, 540, 100, 15);
        add(proteinProgressBar);
        
        // 단백질 값 라벨
        proteinValueLabel = new JLabel("0 / " + (int)targetProtein + "g");
        proteinValueLabel.setForeground(Color.black);
        proteinValueLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        proteinValueLabel.setBounds(175, 565, 100, 25);
        add(proteinValueLabel);
        
        // 지방 프로그레스 바 섹션
        JLabel fatLabel = new JLabel("지방");
        fatLabel.setForeground(Color.black);
        fatLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        fatLabel.setBounds(325, 505, 100, 20);
        add(fatLabel);
        
        // 커스텀 둥근 프로그레스 바 추가 (지방)
        fatProgressBar = new CustomProgressBar(0, new Color(0x609056));
        fatProgressBar.setBounds(290, 540, 100, 15);
        add(fatProgressBar);
        
        // 지방 값 라벨
        fatValueLabel = new JLabel("0 / " + (int)targetFat + "g");
        fatValueLabel.setForeground(Color.black);
        fatValueLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        fatValueLabel.setBounds(310, 565, 100, 25);
        add(fatValueLabel);
        
        // 타이머 설정 - 30초마다 업데이트
        startUpdateTimer();
        
        // 초기 데이터 로드
        updateNutritionData();
    }
    
    // 타이머 시작 메서드
    private void startUpdateTimer() {
        updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> updateNutritionData());
            }
        }, 0, 30000); // 30초마다 업데이트
    }
    
    // 타이머 정지 메서드
    public void stopUpdateTimer() {
        if (updateTimer != null) {
            updateTimer.cancel();
            updateTimer = null;
        }
    }
    
    // 패널이 제거될 때 타이머도 정지
    @Override
    public void removeNotify() {
        stopUpdateTimer();
        super.removeNotify();
    }
    
    // 영양 데이터 업데이트 메서드
    public void updateNutritionData() {
        String userId = LoginManager.getInstance().getUserId();
        if (userId == null || userId.isEmpty()) {
            System.out.println("로그인된 사용자가 없습니다.");
            return;
        }
        
        try {
            // MealLogDAO를 사용하여 오늘 섭취한 영양소 정보 가져오기
            Map<String, Double> nutritionMap = mealLogDAO.getTodayNutrition(userId);
            
            // 영양소 정보 업데이트 (null 체크 추가)
            currentCalories = nutritionMap.getOrDefault("calories", 0.0);
            currentCarbs = nutritionMap.getOrDefault("carbs", 0.0);
            currentProtein = nutritionMap.getOrDefault("protein", 0.0);
            currentFat = nutritionMap.getOrDefault("fat", 0.0);
            
            // UI 업데이트
            updateNutritionUI();
        } catch (Exception e) {
            System.out.println("영양소 정보 업데이트 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // UI 업데이트 메서드
    private void updateNutritionUI() {
        // 칼로리 정보 업데이트
        currentCalorieLabel.setText(String.format("%d", (int)currentCalories));
        targetCalorieLabel.setText(String.format("%d", (int)targetCalories));
        
        // 칼로리 퍼센트 계산 및 게이지 업데이트
        int caloriePercent = 0;
        if (targetCalories > 0) {
            caloriePercent = (int)((currentCalories / targetCalories) * 100);
            caloriePercent = Math.min(caloriePercent, 100); // 100%를 넘지 않도록
        }
        calorieGauge.setPercentage(caloriePercent);
        
        // 남은 칼로리 정보 업데이트
        double remainingCalories = targetCalories - currentCalories;
        if (remainingCalories < 0) {
            calorieInfoLabel.setText(String.format("%d kcal 초과 | 0 kcal 남았어요", (int)Math.abs(remainingCalories)));
        } else {
            calorieInfoLabel.setText(String.format("0 kcal 소모 | %d kcal 더 먹을 수 있어요", (int)remainingCalories));
        }
        
        // 영양소 프로그레스바 및 값 업데이트
        // 탄수화물
        int carbPercent = 0;
        if (targetCarbs > 0) {
            carbPercent = (int)((currentCarbs / targetCarbs) * 100);
            carbPercent = Math.min(carbPercent, 100);
        }
        carbProgressBar.setProgress(carbPercent);
        carbValueLabel.setText(String.format("%d / %dg", (int)currentCarbs, (int)targetCarbs));
        
        // 단백질
        int proteinPercent = 0;
        if (targetProtein > 0) {
            proteinPercent = (int)((currentProtein / targetProtein) * 100);
            proteinPercent = Math.min(proteinPercent, 100);
        }
        proteinProgressBar.setProgress(proteinPercent);
        proteinValueLabel.setText(String.format("%d / %dg", (int)currentProtein, (int)targetProtein));
        
        // 지방
        int fatPercent = 0;
        if (targetFat > 0) {
            fatPercent = (int)((currentFat / targetFat) * 100);
            fatPercent = Math.min(fatPercent, 100);
        }
        fatProgressBar.setProgress(fatPercent);
        fatValueLabel.setText(String.format("%d / %dg", (int)currentFat, (int)targetFat));
        
        // 패널 갱신
        revalidate();
        repaint();
    }
    
    // SemiCircularGauge2 클래스의 기능을 내부 클래스로 통합
    class IntegratedSemiCircularGauge extends JPanel {
        private int percentage; // 게이지 값 (0~100%)
        private int gaugeWidth; // 게이지의 너비
        private int gaugeHeight; // 게이지의 높이
        
        // 생성자: 게이지 크기 설정
        public IntegratedSemiCircularGauge(int gaugeWidth, int gaugeHeight) {
            this.gaugeWidth = gaugeWidth;
            this.gaugeHeight = gaugeHeight;
            this.percentage = 0; // 초기 퍼센트 값 (0%)
            setPreferredSize(new Dimension(gaugeWidth, gaugeHeight)); // 패널 크기 설정
            setOpaque(false); // 배경을 투명하게 설정
        }
        
        // 퍼센트 값 설정 메서드
        public void setPercentage(int percentage) {
            this.percentage = Math.max(0, Math.min(100, percentage)); // 0~100 범위로 제한
            repaint(); // 다시 그리기
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            // 부드러운 그래픽을 위한 안티앨리어싱 설정
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 선 두께 계산
            int strokeWidth = 31; // 원하는 두께로 설정
            
            // 패널 높이의 약 80%를 사용하도록 반원의 크기 조정
            int usableHeight = (int)(gaugeHeight * 0.8);
            
            // 반원의 지름 계산 (반원의 높이는 반지름이어야 함)
            // 가로 길이와 사용 가능한 높이의 2배 중 작은 값을 선택
            int arcDiameter = Math.min(gaugeWidth - strokeWidth * 2, usableHeight * 2);
            
            // 중앙 정렬을 위한 X 좌표 계산
            int arcX = (gaugeWidth - arcDiameter) / 2;
            
            // 상단에 여백을 두고 Y 좌표 계산
            // gaugeHeight의 20%를 상단 여백으로 사용
            int topMargin = (int)(gaugeHeight * 0.1);
            int arcY = topMargin;
            
            // 배경 반원 그리기
            g2.setColor(new Color(0x609056));
            g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawArc(arcX, arcY, arcDiameter, arcDiameter, 0, 180);
            
            // 진행된 반원 그리기
            g2.setColor(Color.white); // 흰색
            int arcAngle = (int) (180 * (percentage / 100.0));
            g2.drawArc(arcX, arcY, arcDiameter, arcDiameter, 180, -arcAngle);
            
            // 중앙 퍼센트 텍스트 표시
            String percentageText = percentage + "%";
            int fontSize = Math.min(39, gaugeWidth / 6); // 글꼴 크기 조정
            Font font = new Font("Inter", Font.BOLD, fontSize);
            g2.setFont(font);
            FontMetrics metrics = g2.getFontMetrics(font);
            
            int textX = (gaugeWidth - metrics.stringWidth(percentageText)) / 2;
            // 텍스트를 반원의 중앙 아래쪽에 배치 (반원 중심보다 약간 아래)
            int textY = arcY + (arcDiameter / 3) + (metrics.getHeight() / 2);
            
            g2.setColor(Color.BLACK);
            g2.drawString(percentageText, textX, textY);
        }
    }
    
    // 커스텀 프로그레스 바 내부 클래스
    class CustomProgressBar extends JPanel {
        private int progress;
        private Color progressColor;
        private final int arc = 15; // 둥근 모서리 반경

        public CustomProgressBar(int progress, Color progressColor) {
            this.progress = progress;
            this.progressColor = progressColor;
            setOpaque(false); // 배경을 투명하게 설정
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            
            // 배경 막대 (흰색)
            g2.setColor(Color.white);
            g2.fillRoundRect(0, 0, width, height, arc, arc);

            // 진행 막대 (색상)
            int progressWidth = (int) (width * progress / 100.0);
            g2.setColor(progressColor);
            g2.fillRoundRect(0, 0, progressWidth, height, arc, arc);
        }

        public void setProgress(int progress) {
            this.progress = progress;
            repaint();
        }
    }
}