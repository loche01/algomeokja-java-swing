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
	        headerPanel.setBackground(Color.white);


		     // 제목 버튼 (JLabel → JButton 변경)
		        JButton titleButton = new JButton(titleText);
		        titleButton.setFont(new Font("Inter", Font.BOLD, 50));
		        titleButton.setHorizontalAlignment(JButton.LEFT);
		        titleButton.setForeground(new Color(0xC0E993));
		        titleButton.setBorderPainted(false);
		        titleButton.setContentAreaFilled(false);
		        titleButton.setFocusPainted(false);

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
	        JButton noticeButton = new JButton("<html>공지<br>사항</html>");
	        noticeButton.setFont(new Font("Inter", Font.BOLD, 20));
	        noticeButton.setHorizontalAlignment(JButton.RIGHT);
	        noticeButton.setForeground(new Color(0xC0E993));
	        noticeButton.setBorderPainted(false);
	        noticeButton.setContentAreaFilled(false);
	        noticeButton.setFocusPainted(false);
	        noticeButton.setBounds(320, 20, 100, 80);
	        noticeButton.addActionListener(noticeListener); // 버튼 클릭 이벤트 등록

	        // 위치 설정
	        titleButton.setBounds(10, 20, 250, 80);
	        headerPanel.add(titleButton);
	        headerPanel.add(noticeButton);
	        headerPanel.setBounds(0, 0, 440, 110);

	        return headerPanel;
	    }
	 private static void showNoticePage(JFrame parentFrame) {
	        JOptionPane.showMessageDialog(parentFrame, "공지사항 페이지로 이동합니다!", "공지사항", JOptionPane.INFORMATION_MESSAGE);
	        // 실제 공지사항 화면으로 전환하는 코드 추가 가능
	    }
	
		public static JPanel createAdminHeader(String titleText, ActionListener noticeListener) {
			JPanel headerPanel = new JPanel(null);
			headerPanel.setBackground(Color.white);
		
			JButton titleButton = new JButton(titleText);
			titleButton.setFont(new Font("Inter", Font.BOLD, 50));
			titleButton.setHorizontalAlignment(JButton.LEFT);
			titleButton.setForeground(new Color(0xC0E993));
			titleButton.setBorderPainted(false);
			titleButton.setContentAreaFilled(false);
			titleButton.setFocusPainted(false);
			
			JButton noticeButton = new JButton("<html>공지<br>사항</html>");
			noticeButton.setFont(new Font("Inter", Font.BOLD, 20));
			noticeButton.setHorizontalAlignment(JButton.RIGHT);
			noticeButton.setForeground(new Color(0xC0E993));
			noticeButton.setBorderPainted(false);
			noticeButton.setContentAreaFilled(false);
			noticeButton.setFocusPainted(false);
			noticeButton.setBounds(320, 20, 100, 80);
			noticeButton.addActionListener(noticeListener);
		
			titleButton.setBounds(10, 20, 250, 80);
			headerPanel.add(titleButton);
			headerPanel.add(noticeButton);
			headerPanel.setBounds(0, 0, 440, 110);
		
			return headerPanel;
		}
		
}
