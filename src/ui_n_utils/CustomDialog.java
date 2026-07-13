package ui_n_utils;

import java.awt.*;
import javax.swing.*;
import panel.LoginPanel;

public class CustomDialog extends JDialog {

    public CustomDialog(JFrame parent, String message, String title) {
        super(parent, title, true);
        setSize(319, 226);
        setLocationRelativeTo(parent);
        setLayout(null);
        getContentPane().setBackground(AppTheme.BACKGROUND);

        // 메시지 라벨
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        messageLabel.setFont(AppTheme.BODY_BOLD_FONT);
        messageLabel.setForeground(AppTheme.TEXT);
        messageLabel.setBounds(40, 25, 228, 82);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(messageLabel);

        // 확인 버튼
        JButton confirmButton = new JButton("확인");
        AppTheme.stylePrimaryButton(confirmButton);
        confirmButton.setBounds(104, 124, 110, 38);
        confirmButton.addActionListener(e -> dispose());
        add(confirmButton);
    }

    public CustomDialog(LoginPanel loginPanel, String message, String title) {
		// TODO Auto-generated constructor stub
	}

	public static void showDialog(JFrame loginPanel, String message, String title) {
        CustomDialog dialog = new CustomDialog(loginPanel, message, title);
        dialog.setVisible(true);
    }

	public static void showDialog(LoginPanel loginPanel, String message, String title) {
		// TODO Auto-generated method stub
		CustomDialog dialog = new CustomDialog(loginPanel, message, title);
        dialog.setVisible(true);
		
	}
	
}
