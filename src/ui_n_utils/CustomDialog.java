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
        getContentPane().setBackground(Color.WHITE);

        // 메시지 라벨
        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>");
        messageLabel.setFont(new Font("Inter", Font.BOLD, 16));
        messageLabel.setBounds(40, 25, 228, 82);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(messageLabel);

        // 확인 버튼 배경 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(120, 124, 70, 30);
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        add(buttonPanel);

        // 확인 버튼
        JButton confirmButton = new JButton("확인");
        confirmButton.setFont(new Font("Inter", Font.BOLD, 14));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setBackground(Color.BLACK);
        confirmButton.setBorderPainted(false);
        confirmButton.setFocusPainted(false);
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.addActionListener(e -> dispose());
        buttonPanel.add(confirmButton, BorderLayout.CENTER);
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