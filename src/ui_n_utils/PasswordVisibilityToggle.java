package ui_n_utils;

import java.awt.Font;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JPasswordField;

public final class PasswordVisibilityToggle {
    private final JPasswordField passwordField;
    private final JButton button;
    private final char hiddenEchoChar;

    private PasswordVisibilityToggle(JPasswordField passwordField) {
        this.passwordField = passwordField;
        this.hiddenEchoChar = passwordField.getEchoChar();
        this.button = createButton();
        this.button.addActionListener(e -> toggle());
        reset();
    }

    public static PasswordVisibilityToggle attach(JPasswordField passwordField) {
        return new PasswordVisibilityToggle(passwordField);
    }

    public JButton getButton() {
        return button;
    }

    public void reset() {
        passwordField.setEchoChar(hiddenEchoChar);
        updateButton(false);
    }

    private void toggle() {
        boolean showPassword = passwordField.getEchoChar() != 0;
        int caretPosition = passwordField.getCaretPosition();
        passwordField.setEchoChar(showPassword ? (char) 0 : hiddenEchoChar);
        updateButton(showPassword);
        passwordField.requestFocusInWindow();
        passwordField.setCaretPosition(
                Math.min(caretPosition, passwordField.getDocument().getLength()));
    }

    private JButton createButton() {
        JButton visibilityButton = new JButton("보기");
        AppTheme.styleSecondaryButton(visibilityButton);
        visibilityButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        visibilityButton.setMargin(new Insets(0, 3, 0, 3));
        return visibilityButton;
    }

    private void updateButton(boolean showingPassword) {
        String text = showingPassword ? "숨김" : "보기";
        button.setText(text);
        button.setToolTipText(showingPassword ? "비밀번호 숨기기" : "비밀번호 보기");
        button.getAccessibleContext().setAccessibleName(button.getToolTipText());
    }
}
