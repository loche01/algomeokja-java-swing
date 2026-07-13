package panel;

import DB.UserDAO;
import DB.UserDAO.UpdateUserResult;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import main.MainUserPanel;
import model.LoginManager;
import model.UserBean;
import ui_n_utils.AppTheme;
import ui_n_utils.PasswordDocumentFilter;
import ui_n_utils.PasswordVisibilityToggle;
import ui_n_utils.RoundedComponent;
import ui_n_utils.ValidationUtils;

public class MyMemberPanel extends JPanel implements ActionListener {
    private final MainUserPanel mainUserPanel;
    private final JButton finishButton;
    private final JButton backButton;
    private final RoundedComponent[] fields;
    private final PasswordVisibilityToggle[] passwordVisibilityToggles;
    private final UserDAO userDAO;

    public MyMemberPanel(MainUserPanel mainUserPanel) {
        this.mainUserPanel = mainUserPanel;
        this.userDAO = new UserDAO();
        setLayout(null);
        setBackground(AppTheme.BACKGROUND);

        JPanel mainCard = new JPanel(null);
        AppTheme.styleCard(mainCard);
        mainCard.setBounds(AppTheme.HORIZONTAL_MARGIN, 15, 380, 660);
        add(mainCard);

        JLabel titleLabel = new JLabel("회원정보 수정");
        titleLabel.setFont(AppTheme.TITLE_FONT);
        titleLabel.setForeground(AppTheme.TEXT);
        titleLabel.setBounds(24, 18, 200, 36);
        mainCard.add(titleLabel);

        backButton = new JButton("내 정보로");
        AppTheme.styleSecondaryButton(backButton);
        backButton.setBounds(240, 20, 110, 36);
        backButton.addActionListener(e -> mainUserPanel.showPanel("MyPage"));
        mainCard.add(backButton);

        JSeparator headerDivider = new JSeparator();
        headerDivider.setForeground(AppTheme.BORDER);
        headerDivider.setBounds(24, 70, 326, 1);
        mainCard.add(headerDivider);

        String[] memberInfo = {
                "이름", "이메일", "전화번호", "ID",
                "현재 비밀번호", "새 비밀번호", "새 비밀번호 확인"
        };
        fields = new RoundedComponent[memberInfo.length];
        passwordVisibilityToggles = new PasswordVisibilityToggle[3];

        int fieldStartY = 88;
        int spacing = 55;
        for (int i = 0; i < memberInfo.length; i++) {
            int rowY = fieldStartY + (i * spacing);
            JLabel label = new JLabel(memberInfo[i]);
            label.setFont(AppTheme.BODY_BOLD_FONT);
            label.setForeground(AppTheme.TEXT_SECONDARY);
            label.setBounds(24, rowY + 7, 116, 24);
            mainCard.add(label);

            boolean isPasswordField = i >= 4;
            String fieldType = isPasswordField ? "password" : "textField";
            int fieldWidth = isPasswordField ? 145 : 205;
            fields[i] = new RoundedComponent(
                    fieldWidth,
                    AppTheme.INPUT_HEIGHT,
                    7,
                    fieldType,
                    "",
                    AppTheme.BORDER,
                    AppTheme.INPUT_BACKGROUND,
                    AppTheme.TEXT,
                    Font.SANS_SERIF,
                    Font.PLAIN,
                    14);
            fields[i].setBounds(145, rowY, fieldWidth, AppTheme.INPUT_HEIGHT);
            AppTheme.styleInputField(fields[i].getTextField());
            mainCard.add(fields[i]);

            if (isPasswordField) {
                PasswordVisibilityToggle visibilityToggle = PasswordVisibilityToggle.attach(
                        (JPasswordField) fields[i].getComponent());
                passwordVisibilityToggles[i - 4] = visibilityToggle;
                JButton visibilityButton = visibilityToggle.getButton();
                AppTheme.styleSecondaryButton(visibilityButton);
                visibilityButton.setBounds(298, rowY, 52, AppTheme.INPUT_HEIGHT);
                mainCard.add(visibilityButton);
            }
        }

        PasswordDocumentFilter.install((JPasswordField) fields[5].getComponent());
        PasswordDocumentFilter.install((JPasswordField) fields[6].getComponent());

        fields[3].getTextField().setEditable(false);
        AppTheme.styleReadOnlyField(fields[3].getTextField());

        JLabel passwordRuleLabel = new JLabel(
                "<html>새 비밀번호는 6~20자이며,<br>영문과 특수문자를 포함합니다.</html>");
        passwordRuleLabel.setFont(AppTheme.CAPTION_FONT);
        passwordRuleLabel.setForeground(AppTheme.TEXT_SECONDARY);
        passwordRuleLabel.setBounds(145, 468, 205, 36);
        mainCard.add(passwordRuleLabel);

        finishButton = new JButton("변경사항 저장");
        AppTheme.stylePrimaryButton(finishButton);
        finishButton.setBounds(145, 530, 205, 44);
        finishButton.addActionListener(this);
        mainCard.add(finishButton);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            loadUserInfo();
        }
    }

    private void loadUserInfo() {
        clearPasswordFields();
        resetPasswordVisibility();

        UserBean user = LoginManager.getInstance().getCurrentUser();
        if (user != null) {
            fields[0].getTextField().setText(user.getUser_name());
            fields[1].getTextField().setText(user.getUser_email());
            fields[2].getTextField().setText(user.getUser_phone());
            fields[3].getTextField().setText(user.getUser_id());
        } else {
            for (int i = 0; i < 4; i++) {
                fields[i].setText("");
            }
        }
    }

    private void clearPasswordFields() {
        fields[4].setText("");
        fields[5].setText("");
        fields[6].setText("");
    }

    private void updateUserInfo() {
        if (fields[0].getTextField().getText().trim().isEmpty()
                || fields[1].getTextField().getText().trim().isEmpty()
                || fields[2].getTextField().getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "이름, 이메일, 전화번호는 필수 입력 항목입니다.",
                    "입력 오류",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserBean user = LoginManager.getInstance().getCurrentUser();
        if (user != null) {
            char[] currentPassword = ((JPasswordField) fields[4].getComponent()).getPassword();
            char[] newPassword = ((JPasswordField) fields[5].getComponent()).getPassword();
            char[] confirmPassword = ((JPasswordField) fields[6].getComponent()).getPassword();

            try {
                boolean passwordChangeRequested = newPassword.length > 0 || confirmPassword.length > 0;
                if (passwordChangeRequested && currentPassword.length == 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "새 비밀번호 변경 시 현재 비밀번호를 입력해주세요.",
                            "입력 오류",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (passwordChangeRequested && !Arrays.equals(newPassword, confirmPassword)) {
                    JOptionPane.showMessageDialog(
                            this,
                            "새 비밀번호와 확인값이 일치하지 않습니다.",
                            "입력 오류",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (passwordChangeRequested && !ValidationUtils.isCreateUserPw(newPassword)) {
                    JOptionPane.showMessageDialog(
                            this,
                            "새 비밀번호는 6~20자 영문과 특수문자를 포함해야 합니다.",
                            "입력 오류",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                UserBean updatedUser = new UserBean();
                updatedUser.setUser_id(user.getUser_id());
                updatedUser.setUser_name(fields[0].getTextField().getText().trim());
                updatedUser.setUser_email(fields[1].getTextField().getText().trim());
                updatedUser.setUser_phone(fields[2].getTextField().getText().trim());

                UpdateUserResult result = userDAO.updateUserWithOptionalRawPassword(
                        updatedUser,
                        currentPassword,
                        passwordChangeRequested ? newPassword : null);
                if (result == UpdateUserResult.CURRENT_PASSWORD_MISMATCH) {
                    JOptionPane.showMessageDialog(
                            this,
                            "현재 비밀번호가 올바르지 않습니다.",
                            "업데이트 실패",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (result != UpdateUserResult.SUCCESS) {
                    JOptionPane.showMessageDialog(
                            this,
                            "회원 정보 업데이트에 실패했습니다.",
                            "업데이트 실패",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                user.setUser_name(updatedUser.getUser_name());
                user.setUser_email(updatedUser.getUser_email());
                user.setUser_phone(updatedUser.getUser_phone());
                JOptionPane.showMessageDialog(
                        this,
                        "회원 정보가 성공적으로 업데이트되었습니다.",
                        "업데이트 성공",
                        JOptionPane.INFORMATION_MESSAGE);
                mainUserPanel.showPanel("MyPage");
            } finally {
                Arrays.fill(currentPassword, '\0');
                Arrays.fill(newPassword, '\0');
                Arrays.fill(confirmPassword, '\0');
                clearPasswordFields();
                resetPasswordVisibility();
            }
        }
    }

    private void resetPasswordVisibility() {
        for (PasswordVisibilityToggle visibilityToggle : passwordVisibilityToggles) {
            if (visibilityToggle != null) {
                visibilityToggle.reset();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == finishButton) {
            updateUserInfo();
        }
    }
}
