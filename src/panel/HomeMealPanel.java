package panel;

import DB.MealDAO;
import DB.MealLogDAO;
import DB.UserDAO;
import java.awt.*;
import java.time.LocalTime;
import javax.swing.*;
import main.MainUserPanel;
import model.LoginManager;
import model.UserBean;
import ui_n_utils.RoundedComponent;
import java.util.Map;
import java.util.HashMap;
import javax.swing.SwingWorker;


public class HomeMealPanel extends JPanel {
	private MainUserPanel mainUserPanel;
	private RoundedComponent aPanel, bPanel, cPanel, dPanel, ePanel;
	private JLabel breakfastLabel, lunchLabel, dinnerLabel, snackLabel;
	private MealDAO mealDAO;
	private MealLogDAO mealLogDAO;
	private Timer updateTimer;
	private JLabel aaLabel;  // 아침
	private JLabel baLabel;  // 점심
	private JLabel daLabel;  // 저녁
	private JLabel caLabel;  // 간식
	private JPanel tabPanel;
	private JLabel userIdLabel;
	private UserDAO userDAO;
	private JLabel mealGuideLabel;
	private String cachedUserId;
	private UserBean cachedUser;
	
	public HomeMealPanel(MainUserPanel mainUserPanel) {
		this.mainUserPanel = mainUserPanel;
		this.mealDAO = new MealDAO();
		this.mealLogDAO = new MealLogDAO();
		this.userDAO = new UserDAO();
		
		setLayout(null);
		setBackground(new Color(192, 233, 147));
		setBounds(0, 140, 440, 686);
		
		// 사용자 이름과 안내 문구를 분리해 긴 이름에서도 겹치지 않도록 배치
		userIdLabel = new JLabel();
		userIdLabel.setFont(new Font("Inter", Font.BOLD, 28));
		userIdLabel.setBounds(22, 18, 396, 38);
		userIdLabel.setForeground(Color.white);
		add(userIdLabel);
		
		mealGuideLabel = new JLabel("식사별 기록을 확인하고 음식을 추가해보세요");
		mealGuideLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
		mealGuideLabel.setBounds(22, 58, 396, 26);
		mealGuideLabel.setForeground(new Color(255, 255, 255));
		mealGuideLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
		add(mealGuideLabel);

		aaLabel = new JLabel("아직이에요");
		aPanel = createMealCard("아침", 22, 100, aaLabel);
		add(aPanel);

		baLabel = new JLabel("아직이에요");
		bPanel = createMealCard("점심", 230, 100, baLabel);
		add(bPanel);

		daLabel = new JLabel("아직이에요");
		dPanel = createMealCard("저녁", 22, 286, daLabel);
		add(dPanel);

		caLabel = new JLabel("아직이에요");
		cPanel = createMealCard("간식", 230, 286, caLabel);
		add(cPanel);
		
		//물섭취
		ePanel = new RoundedComponent(396, 150, 30, "panel", " ", new Color(0x609056), new Color(0x609056), Color.black,
				" ", 0, 0);
		ePanel.setBounds(22, 472, 396, 150);
		ePanel.setLayout(null);
		add(ePanel);
		
		JLabel eLabel = new JLabel("물 섭취");
		eLabel.setForeground(Color.white);
		eLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 22));
		eLabel.setBounds(18, 16, 120, 30);
		ePanel.add(eLabel);
		
		JLabel waterMessageLabel = new JLabel("오늘도 잊지 말고 물을 챙겨주세요");
		waterMessageLabel.setForeground(Color.WHITE);
		waterMessageLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
		waterMessageLabel.setBounds(18, 60, 360, 28);
		ePanel.add(waterMessageLabel);

		JLabel waterGuideLabel = new JLabel("충분한 수분 섭취로 건강한 하루를 만들어요");
		waterGuideLabel.setForeground(new Color(0xC0E993));
		waterGuideLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
		waterGuideLabel.setBounds(18, 96, 360, 24);
		ePanel.add(waterGuideLabel);

		// 패널이 표시될 때마다 정보 업데이트를 위한 리스너 추가
		addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentShown(java.awt.event.ComponentEvent e) {
				// 패널이 표시될 때 즉시 사용자 정보 업데이트
				updateUserNameLabel();
				// 칼로리 정보는 백그라운드에서 업데이트
				SwingUtilities.invokeLater(() -> updateCalories());
			}
		});
	}

	private RoundedComponent createMealCard(String mealType, int x, int y, JLabel calorieLabel) {
		RoundedComponent card = new RoundedComponent(188, 170, 24, "panel", " ",
				new Color(0x609056), new Color(0x609056), Color.BLACK, " ", 0, 0);
		card.setBounds(x, y, 188, 170);
		card.setLayout(null);

		JLabel titleLabel = new JLabel(mealType);
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 22));
		titleLabel.setBounds(16, 14, 100, 30);
		card.add(titleLabel);

		JLabel recordLabel = new JLabel("오늘 기록");
		recordLabel.setForeground(new Color(0xE4F4D3));
		recordLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 13));
		recordLabel.setBounds(16, 55, 100, 22);
		card.add(recordLabel);

		calorieLabel.setForeground(new Color(0xC0E993));
		calorieLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 19));
		calorieLabel.setBounds(16, 78, 156, 30);
		card.add(calorieLabel);

		JButton addButton = createMealAddButton();
		addButton.addActionListener(e -> openFoodList(mealType));
		card.add(addButton);

		return card;
	}

	private JButton createMealAddButton() {
		JButton button = new JButton("음식 추가");
		button.setBounds(16, 124, 156, 34);
		button.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
		button.setForeground(new Color(0x406E38));
		button.setBackground(Color.WHITE);
		button.setOpaque(true);
		button.setFocusPainted(false);
		button.setContentAreaFilled(true);
		button.setBorder(BorderFactory.createLineBorder(new Color(0xE4F4D3), 1, true));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		return button;
	}

	private void openFoodList(String mealType) {
		String userId = LoginManager.getInstance().getUserId();
		if (userId != null) {
			mainUserPanel.foodListPanel.setMealType(mealType);
			mainUserPanel.showPanel("foodList");
		}
	}

	// 사용자 이름만 업데이트하는 간소화된 메서드
	private void updateUserNameLabel() {
		UserBean currentUser = LoginManager.getInstance().getCurrentUser();
		if (currentUser != null && currentUser.getUser_name() != null) {
			userIdLabel.setText(currentUser.getUser_name() + "님");
		} else {
			userIdLabel.setText("게스트님");
		}
	}

	// 칼로리 정보 업데이트 - 백그라운드에서 처리
	public void updateCalories() {
		String userId = LoginManager.getInstance().getUserId();
		if (userId == null) {
			resetLabels();
			return;
		}

		// 자정이 지났는지 확인
		if (LocalTime.now().isBefore(LocalTime.of(0, 1))) {
			resetLabels();
			return;
		}

		// 백그라운드에서 칼로리 정보 조회
		SwingWorker<Map<String, Double>, Void> worker = new SwingWorker<>() {
			@Override
			protected Map<String, Double> doInBackground() {
				Map<String, Double> caloriesMap = new HashMap<>();
				caloriesMap.put("아침", mealDAO.getTotalCaloriesByTimeSlot(userId, "아침"));
				caloriesMap.put("점심", mealDAO.getTotalCaloriesByTimeSlot(userId, "점심"));
				caloriesMap.put("저녁", mealDAO.getTotalCaloriesByTimeSlot(userId, "저녁"));
				caloriesMap.put("간식", mealDAO.getTotalCaloriesByTimeSlot(userId, "간식"));
				return caloriesMap;
			}

			@Override
			protected void done() {
				try {
					Map<String, Double> caloriesMap = get();
					updatePanelCalories("아침", aaLabel, caloriesMap.get("아침"));
					updatePanelCalories("점심", baLabel, caloriesMap.get("점심"));
					updatePanelCalories("저녁", daLabel, caloriesMap.get("저녁"));
					updatePanelCalories("간식", caLabel, caloriesMap.get("간식"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		worker.execute();
	}

	private void updatePanelCalories(String timeSlot, JLabel label, double calories) {
		if (label == null) return;
		
		if (calories > 0) {
			label.setText(String.format("%.0f kcal", calories));
			label.setForeground(Color.WHITE);
		} else {
			label.setText("아직이에요");
			label.setForeground(new Color(0xC0E993));
		}
	}

	private void resetLabels() {
		resetLabel(aaLabel);
		resetLabel(baLabel);
		resetLabel(daLabel);
		resetLabel(caLabel);
		revalidate();
		repaint();
	}

	private void resetLabel(JLabel label) {
		if (label != null) {
			label.setText("아직이에요");
			label.setForeground(new Color(0xC0E993));
		}
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		if (updateTimer != null) {
			updateTimer.stop();
		}
	}
}
