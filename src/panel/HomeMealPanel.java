package panel;

import DB.MealDAO;
import DB.MealLogDAO;
import DB.UserDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		setBounds(0, 140, 440, 700);
		
		// 사용자 이름 표시 레이블 - 너비 조정
		userIdLabel = new JLabel();
		userIdLabel.setFont(new Font("Inter", Font.BOLD, 32));
		userIdLabel.setBounds(22, 25, 180, 40); // 너비를 380에서 180으로 줄임
		userIdLabel.setForeground(Color.white);
		add(userIdLabel);
		
		// 식단 입력 안내 메시지 - 위치 조정하여 유저 이름 오른쪽에 배치
		mealGuideLabel = new JLabel("식단을 입력해주세요");
		mealGuideLabel.setFont(new Font("Inter", Font.BOLD, 18));
		mealGuideLabel.setBounds(220, 35, 200, 25);
		mealGuideLabel.setForeground(new Color(255, 255, 255));
		mealGuideLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
		add(mealGuideLabel);

		//아침
		aPanel = new RoundedComponent(180, 180, 30, "panel", " ", new Color(0x609056),new Color(0x609056), Color.black,
				" ", 0, 0);
		aPanel.setBounds(22, 100, 180, 180);
		aPanel.setLayout(null);
		aPanel.setEnabled(false);
		add(aPanel);
		
		JLabel aLabel = new JLabel("아침");
		aLabel.setForeground(Color.white);
		aLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 22));
		aLabel.setBounds(15, 15, 80, 20);
		aPanel.add(aLabel);
		
		aaLabel = new JLabel("아직이에요");
		aaLabel.setForeground(new Color(0xC0E993));
		aaLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
		aaLabel.setBounds(20, 145, 80, 20);
		aPanel.add(aaLabel);
		
		// 사과 이미지 로드 및 추가
		ImageIcon originalIcon = new ImageIcon("C:\\Users\\dita_806\\Desktop\\project8\\src\\images\\사과.PNG"); // 원본 이미지 경로 설정
		Image originalImage = originalIcon.getImage(); // 원본 이미지 가져오기

		// 원하는 크기로 이미지 조정 (예: 50x50)
		Image resizedImage = originalImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH); 
		ImageIcon resizedIcon = new ImageIcon(resizedImage); // 조정된 이미지로 ImageIcon 생성

		JLabel imageLabel = new JLabel(resizedIcon);
		imageLabel.setBounds(20, 90, 50, 50); // 이미지 위치와 크기 설정
		aPanel.add(imageLabel); // aPanel에 이미지 추가

		// 식단 추가 버튼 생성
		JButton crossButton = createMealAddButton();

		// 버튼 클릭 이벤트 리스너 추가
		crossButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String userId = LoginManager.getInstance().getUserId();
				if (userId != null) {
					mainUserPanel.foodListPanel.setMealType("아침");
					mainUserPanel.showPanel("foodList");
				}
			}
		});

		aPanel.add(crossButton); // aPanel에 십자가 버튼 추가

		//점심
		bPanel = new RoundedComponent(180, 180, 30, "panel", " ", new Color(0x609056), new Color(0x609056), Color.black,
				" ", 0, 0);
		bPanel.setBounds(222, 100, 180, 180);
		bPanel.setLayout(null);
		bPanel.setEnabled(false);
		add(bPanel);
		
		JLabel bLabel = new JLabel("점심");
		bLabel.setForeground(Color.white);
		bLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 22));
		bLabel.setBounds(15, 15, 80, 20);
		bPanel.add(bLabel);
		
		baLabel = new JLabel("아직이에요");
		baLabel.setForeground(new Color(0xC0E993));
		baLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
		baLabel.setBounds(20, 145, 80, 20);
		bPanel.add(baLabel);
		
		// 점심 이미지 로드 및 추가
		ImageIcon originalIcona = new ImageIcon("C:\\Users\\dita_806\\Desktop\\project8\\src\\images\\점심.PNG"); // 원본 이미지 경로 설정
		Image originalImagea = originalIcona.getImage(); // 원본 이미지 가져오기

		// 원하는 크기로 이미지 조정 (예: 50x50)
		Image resizedImagea = originalImagea.getScaledInstance(50, 50, Image.SCALE_SMOOTH); 
		ImageIcon resizedIcona = new ImageIcon(resizedImagea); // 조정된 이미지로 ImageIcon 생성

		JLabel imageLabela = new JLabel(resizedIcona);
		imageLabela.setBounds(20, 90, 50, 50); // 이미지 위치와 크기 설정
		bPanel.add(imageLabela); // aPanel에 이미지 추가
		
		// 식단 추가 버튼 생성
		JButton crossaButton = createMealAddButton();

		// 버튼 클릭 이벤트 리스너 추가
		crossaButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String userId = LoginManager.getInstance().getUserId();
				if (userId != null) {
					mainUserPanel.foodListPanel.setMealType("점심");
					mainUserPanel.showPanel("foodList");
				}
			}
		});

		bPanel.add(crossaButton); // aPanel에 십자가 버튼 추가

		
		//저녁
		dPanel = new RoundedComponent(180, 180, 30, "panel", " ",new Color(0x609056), new Color(0x609056), Color.black,
				" ", 0, 0);
		dPanel.setBounds(22, 300, 180, 180);
		dPanel.setLayout(null);
		dPanel.setEnabled(false);
		add(dPanel);
		
		JLabel dLabel = new JLabel("저녁");
		dLabel.setForeground( Color.white);
		dLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 22));
		dLabel.setBounds(15, 15, 80, 20);
		dPanel.add(dLabel);
		
		daLabel = new JLabel("아직이에요");
		daLabel.setForeground(new Color(0xC0E993));
		daLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
		daLabel.setBounds(20, 145, 80, 20);
		dPanel.add(daLabel);
		
		// 저녁 이미지 로드 및 추가
		ImageIcon originalIconb = new ImageIcon("C:\\Users\\dita_806\\Desktop\\project8\\src\\images\\저녁.PNG"); // 원본 이미지 경로 설정
		Image originalImageb = originalIconb.getImage(); // 원본 이미지 가져오기

		// 원하는 크기로 이미지 조정 (예: 50x50)
		Image resizedImageb = originalImageb.getScaledInstance(50, 50, Image.SCALE_SMOOTH); 
		ImageIcon resizedIconb = new ImageIcon(resizedImageb); // 조정된 이미지로 ImageIcon 생성

		JLabel imageLabelb = new JLabel(resizedIconb);
		imageLabelb.setBounds(20, 90, 50, 50); // 이미지 위치와 크기 설정
		dPanel.add(imageLabelb); // aPanel에 이미지 추가

		// 식단 추가 버튼 생성
		JButton crossbButton = createMealAddButton();

		// 버튼 클릭 이벤트 리스너 추가
		crossbButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String userId = LoginManager.getInstance().getUserId();
				if (userId != null) {
					mainUserPanel.foodListPanel.setMealType("저녁");
					mainUserPanel.showPanel("foodList");
				}
			}
		});

		dPanel.add(crossbButton); // aPanel에 십자가 버튼 추가

		
		//간식
		cPanel = new RoundedComponent(180, 180, 30, "panel", " ", new Color(0x609056), new Color(0x609056), Color.black,
				" ", 0, 0);
		cPanel.setBounds(222, 300, 180, 180);
		cPanel.setLayout(null);
		cPanel.setEnabled(false);
		add(cPanel);
		
		caLabel = new JLabel("아직이에요");
		caLabel.setForeground(new Color(0xC0E993));
		caLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
		caLabel.setBounds(20, 145, 80, 20);
		cPanel.add(caLabel);
		
		JLabel cLabel = new JLabel("간식");
		cLabel.setForeground(Color.white);
		cLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 22));
		cLabel.setBounds(15, 15, 80, 20);
		cPanel.add(cLabel);
		
		// 간식 이미지 로드 및 추가
		ImageIcon originalIconc = new ImageIcon("C:\\Users\\dita_806\\Desktop\\project8\\src\\images\\간식.PNG"); // 원본 이미지 경로 설정
		Image originalImagec = originalIconc.getImage(); // 원본 이미지 가져오기

		// 원하는 크기로 이미지 조정 (예: 50x50)
		Image resizedImagec = originalImagec.getScaledInstance(50, 50, Image.SCALE_SMOOTH); 
		ImageIcon resizedIconc = new ImageIcon(resizedImagec); // 조정된 이미지로 ImageIcon 생성

		JLabel imageLabelc = new JLabel(resizedIconc);
		imageLabelc.setBounds(20, 90, 50, 50); // 이미지 위치와 크기 설정
		cPanel.add(imageLabelc); // aPanel에 이미지 추가

		// 식단 추가 버튼 생성
		JButton crosscButton = createMealAddButton();

		// 버튼 클릭 이벤트 리스너 추가
		crosscButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String userId = LoginManager.getInstance().getUserId();
				if (userId != null) {
					mainUserPanel.foodListPanel.setMealType("간식");
					mainUserPanel.showPanel("foodList");
				}
			}
		});

		cPanel.add(crosscButton); // aPanel에 십자가 버튼 추가

		
		//물섭취
		ePanel = new RoundedComponent(380, 150, 30, "panel", " ", new Color(0x609056), new Color(0x609056), Color.black,
				" ", 0, 0);
		ePanel.setBounds(22, 500, 400, 150);
		ePanel.setLayout(null);
		ePanel.setEnabled(false);
		add(ePanel);
		
		JLabel eLabel = new JLabel("물 섭취");
		eLabel.setForeground(Color.white);
		eLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 22));
		eLabel.setBounds(15, 15, 80, 20);
		ePanel.add(eLabel);
		
		// 간식 이미지 로드 및 추가
		ImageIcon originalIconwa = new ImageIcon("C:\\Users\\dita_806\\Desktop\\project8\\src\\images\\물섭취.PNG"); // 원본 이미지 경로 설정
		Image originalImagewa = originalIconwa.getImage(); // 원본 이미지 가져오기

		// 원하는 크기로 이미지 조정 (예: 50x50)
		Image resizedImagewa = originalImagewa.getScaledInstance(350, 70, Image.SCALE_SMOOTH); 
		ImageIcon resizedIconwa = new ImageIcon(resizedImagewa); // 조정된 이미지로 ImageIcon 생성

		JLabel imageLabelwa = new JLabel(resizedIconwa);
		imageLabelwa.setBounds(15, 60, 350, 70); // 이미지 위치와 크기 설정
		ePanel.add(imageLabelwa); // aPanel에 이미지 추가

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

	private JButton createMealAddButton() {
		JButton button = new JButton("추가");
		button.setBounds(115, 15, 50, 50);
		button.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setContentAreaFilled(false);
		button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1, true));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		return button;
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
