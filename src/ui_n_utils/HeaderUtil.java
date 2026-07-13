// package ui_n_utils;
package ui_n_utils;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import main.MainUserPanel;
//add(HeaderUtil.createHeader("알고먹자", this)); 
//이거 추가하면 됩니다.
public class HeaderUtil {
	 public static JPanel createHeader(String titleText,
			 MainUserPanel mainPanel, ActionListener noticeListener) {
	        JPanel headerPanel = new JPanel(null);
	        headerPanel.setBackground(AppTheme.CARD);
	        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER));


		     // 제목 버튼 (JLabel → JButton 변경)
		        JButton titleButton = new JButton(titleText);
		        styleBrandButton(titleButton);
		        titleButton.setHorizontalAlignment(JButton.LEFT);

		        // ✅ 버튼 클릭 시 홈 화면으로 이동하고 네비게이션 바도 홈으로 설정
		        titleButton.addActionListener(e -> {
		            // 홈 화면으로 이동
		            mainPanel.showPanel("HomeDaily");
		            // 탭을 일일 현황으로 설정
		            TabUtil.setSelectedTab("일일 현황");
		            // 네비게이션 바의 홈 버튼을 선택 상태로 만들기
		            NavUtil.selectHomeButton();
		        });
	        
	        
	        // 공지사항 버튼 (JButton)
	        JButton noticeButton = new JButton("공지사항");
	        AppTheme.styleSecondaryButton(noticeButton);
	        noticeButton.setBounds(300, 31, 110, AppTheme.SECONDARY_BUTTON_HEIGHT);
	        noticeButton.addActionListener(noticeListener); // 버튼 클릭 이벤트 등록

	        // 위치 설정
	        titleButton.setBounds(20, 20, 250, 58);
	        headerPanel.add(titleButton);
	        headerPanel.add(noticeButton);
	        headerPanel.setBounds(0, 0, 440, 110);

	        return headerPanel;
	    }
	 private static void showNoticePage(JFrame parentFrame) {
	        JOptionPane.showMessageDialog(parentFrame, "공지사항 페이지로 이동합니다!", "공지사항", JOptionPane.INFORMATION_MESSAGE);
	        // 실제 공지사항 화면으로 전환하는 코드 추가 가능
	    }
	
		public static JPanel createAdminHeader(String titleText, ActionListener noticeListener,
				ActionListener logoutListener) {
			JPanel headerPanel = new JPanel(null);
			headerPanel.setBackground(AppTheme.CARD);
			headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER));
			JButton titleButton = new JButton(titleText);
			styleBrandButton(titleButton);
			titleButton.setHorizontalAlignment(JButton.LEFT);
			JButton noticeButton = new JButton("공지사항");
			AppTheme.styleSecondaryButton(noticeButton);
			noticeButton.setBounds(218, 31, 88, AppTheme.SECONDARY_BUTTON_HEIGHT);
			noticeButton.addActionListener(noticeListener);

			JButton logoutButton = new JButton("로그아웃");
			AppTheme.styleDangerButton(logoutButton);
			logoutButton.setBounds(314, 31, 96, AppTheme.SECONDARY_BUTTON_HEIGHT);
			logoutButton.addActionListener(logoutListener);

			titleButton.setBounds(20, 20, 190, 58);
			headerPanel.add(titleButton);
			headerPanel.add(noticeButton);
			headerPanel.add(logoutButton);
			headerPanel.setBounds(0, 0, 440, 110);
		
			return headerPanel;
		}

		private static void styleBrandButton(JButton button) {
			button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 32));
			button.setForeground(AppTheme.PRIMARY);
			button.setBorderPainted(false);
			button.setContentAreaFilled(false);
			button.setFocusPainted(false);
			button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		
}
