package panel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import ui_n_utils.RoundedComponent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Foodinfo extends JFrame{
	private JTextField timeField;
	private JTextField weightField;
	private JLabel calorieValue;
	private RoundedComponent timePanel, weightPanel, fatPanel, gPanel, searchField;
	private RoundedComponent BackButton, mainPanel, finishButton, aButton, bButton;

	public Foodinfo() {
		setTitle("운동 칼로리");
		setSize(440, 956);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setResizable(false);
		getContentPane().setBackground(new Color(192, 233, 147));
		// 상단 패널 추가
		JPanel topPanel = new JPanel();
		topPanel.setLayout(null); // null 레이아웃 사용
		topPanel.setBackground(Color.white);
		topPanel.setBounds(0, 0, 440, 109); // 위치와 크기 설정

		// 제목
		JLabel titleLabel = new JLabel("알고먹자");
		titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 50));
		titleLabel.setHorizontalAlignment(JLabel.LEFT);
		titleLabel.setForeground(new Color(0xC0E993));
		titleLabel.setBounds(20, 30, 300, 50); // 위치 및 크기 설정

		// 공지사항
		JLabel notice = new JLabel("<html>공지<br>사항</html>");
		notice.setFont(new Font("맑은 고딕", Font.BOLD, 30));
		notice.setHorizontalAlignment(JLabel.RIGHT);
		notice.setForeground(new Color(0xC0E993));
		notice.setBounds(240, 20, 180, 80); // 충분한 크기 설정

		// 상단 패널에 컴포넌트 추가
		topPanel.add(titleLabel);
		topPanel.add(notice);

		// 상단 패널을 JFrame에 추가
		add(topPanel, BorderLayout.NORTH);

		// 메인 패널 (운동 정보)
		mainPanel = new RoundedComponent(380, 600, 30, "panel", " ", new Color(192, 233, 147), Color.white, Color.black,
				" ", 0, 0);
		mainPanel.setBounds(20, 190, 380, 600);
		mainPanel.setLayout(null);
		mainPanel.setEnabled(false);
		
		// 뒤로가기 버튼 생성
				BackButton = new RoundedComponent(40, 40, 10, "button", "<", 
						Color.white, Color.white, Color.black, "Inter",Font.BOLD, 25);

				BackButton.setBounds(10, 10, 40, 40);
				mainPanel.add(BackButton);

		JLabel workoutTitle = new JLabel("아이스크림");
		workoutTitle.setForeground(new Color(0x609056));
		workoutTitle.setFont(new Font("Malgun Gothic", Font.BOLD, 26));
		workoutTitle.setBounds(130, 50, 300, 30);
		mainPanel.add(workoutTitle);

		//탄수화물 패널
		timePanel = new RoundedComponent(110, 110, 30, "panel", " ", new Color(0xF3F3F3),new Color(0xF3F3F3), Color.black,
				" ", 0, 0);
		timePanel.setBounds(15, 100, 110, 110);
		timePanel.setLayout(null);
		timePanel.setEnabled(false);

		JLabel timeLabel = new JLabel("탄수화물");
		timeLabel.setForeground(new Color(0xFFCBA4));
		timeLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
		timeLabel.setBounds(27, 22, 80, 20);
		timePanel.add(timeLabel);
		
		JLabel timeaLabel = new JLabel("400g");
		timeaLabel.setForeground(new Color(0xFFCBA4));
		timeaLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 24));
		timeaLabel.setBounds(27, 43, 80, 40);
		timePanel.add(timeaLabel);

		mainPanel.add(timePanel);

		// 단백질 패널
		weightPanel = new RoundedComponent(110, 110, 30, "panel", " ", 
				 new Color(0xFFCBA4), new Color(0xFFCBA4), Color.black," ", 0, 0);

		weightPanel.setBounds(135, 100, 110, 110);
		weightPanel.setLayout(null);
		weightPanel.setEnabled(false);

		JLabel weightLabel = new JLabel("단백질");
		weightLabel.setForeground(new Color(0x002D62));
		weightLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
		weightLabel.setBounds(33, 22, 80, 20);

		JLabel weightaLabel = new JLabel("400g");
		weightaLabel.setForeground(new Color(0x002D62));
		weightaLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 24));
		weightaLabel.setBounds(27, 43, 80, 40);
	
		weightPanel.add(weightLabel);
		weightPanel.add(weightaLabel);
		mainPanel.add(weightPanel);

		// 지방 패널
	     fatPanel = new RoundedComponent(110, 110, 30, "panel", " ", 
	    		 new Color(0x002D62), new Color(0x002D62), Color.black," ", 0, 0);

		fatPanel.setBounds(255, 100, 110, 110);
		fatPanel.setLayout(null);
		fatPanel.setEnabled(false);

		JLabel fatLabel = new JLabel("지방");
		fatLabel.setForeground(Color.white);
		fatLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
		fatLabel.setBounds(40, 22, 80, 20);

		JLabel fataLabel = new JLabel("400g");
		fataLabel.setForeground( Color.white);
		fataLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 24));
		fataLabel.setBounds(27, 43, 80, 40);

		fatPanel.add(fatLabel);
		fatPanel.add(fataLabel);
		mainPanel.add(fatPanel);
		
         // 단위
		JLabel workoutType = new JLabel("단위: g");
		workoutType.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
		workoutType.setForeground(Color.black);
		workoutType.setBounds(60, 245, 50, 30);
		mainPanel.add(workoutType);
		
		// 단위 패널
	     gPanel = new RoundedComponent(290, 33, 30, "panel", " ", 
	    		 new Color(0x609056), new Color(0x609056), Color.black," ", 0, 0);
		gPanel.setBounds(45, 275, 290, 33);
		gPanel.setLayout(null);
		gPanel.setEnabled(false);
		mainPanel.add(gPanel);
		
		aButton = new RoundedComponent(40, 20, 0, "button.left", "-",new Color(0x609056), new Color(0x609056),
 				Color.black, "Malgun Gothic", Font.BOLD, 36);
 		aButton.setBounds(8, 3, 40, 20); // 위치 설정
 		gPanel.add(aButton);
 		
 		searchField = new RoundedComponent(55, 35, 30, "textfield", "200", new Color(0x609056),new Color(0x609056), Color.black,
 				"Malgun Gothic", Font.BOLD , 18);
 		searchField.setBounds(120, 0, 55, 35);
 		gPanel.add(searchField);

 		bButton = new RoundedComponent(30, 20, 0, "button.left", "+", new Color(0x609056), new Color(0x609056),
 				Color.black, "Malgun Gothic", Font.BOLD, 28);
 		bButton.setBounds(250, 4, 40, 20); // 위치 설정
 		gPanel.add(bButton);
		
		// 총 열량
		JLabel calorieLabel = new JLabel("총 열량");
		calorieLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 25));
		calorieLabel.setBounds(145, 380, 200, 30);
		mainPanel.add(calorieLabel);

		calorieValue = new JLabel("150Kcal");
		calorieValue.setFont(new Font("Malgun Gothic", Font.BOLD, 26));
		calorieValue.setBounds(140, 420, 100, 30);
		mainPanel.add(calorieValue);

		// 담기 버튼 생성
		finishButton = new RoundedComponent(100, 40, 10, "button", "담기", 
				Color.BLACK, Color.BLACK, Color.WHITE, "Inter",
				Font.BOLD, 14);

		finishButton.setBounds(140, 500, 100, 40);
		mainPanel.add(finishButton);

		// RoundedComponent 내부의 JButton을 가져와서 ActionListener 추가
		JButton button = finishButton.getButton();
		if (button != null) {
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					calculateCalories();
				}
			});
		}

		add(mainPanel);

		// 하단 네비게이션 바
		JPanel bottomNav = new JPanel(new GridLayout(1, 4));
		bottomNav.setPreferredSize(new Dimension(440, 100));
		bottomNav.setBackground(new Color(192, 233, 147));

		String[] navItems = { "🏠 홈", "📅 캘린더", "🏋️ 운동", "👤 내 정보" };
		for (String item : navItems) {
			JButton btn = new JButton(item);
			btn.setBackground(Color.WHITE);
			btn.setFont(new Font("Malgun Gothic", Font.PLAIN, 16));
			bottomNav.add(btn);
		}

		setLayout(new BorderLayout());
		add(bottomNav, BorderLayout.SOUTH);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	// 칼로리 계산 함수
	private void calculateCalories() {
		try {
			double metValue = 6.0; // 데드리프트 MET 값
			double time = Double.parseDouble(timeField.getText()) / 60.0; // 분 → 시간 변환
			double weight = Double.parseDouble(weightField.getText());

			double calories = metValue * weight * time * 1.05;
			calorieValue.setText(String.format("%.1f Kcal", calories));
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "운동 시간과 체중을 숫자로 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) {
		new Foodinfo();
	}
}
