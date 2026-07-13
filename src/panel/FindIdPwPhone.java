package panel;

import DB.UserDAO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import main.MainFrame;
import ui_n_utils.AppTheme;
import ui_n_utils.CustomDialog;
import ui_n_utils.PasswordDocumentFilter;
import ui_n_utils.PasswordVisibilityToggle;
import ui_n_utils.SmartTextField;
import ui_n_utils.ValidationUtils;

public class FindIdPwPhone extends JPanel implements ActionListener {
    private final JButton findIdButton;
    private final JButton verifyIdButton;
    private final JButton verifyPasswordButton;
    private final JButton findPasswordButton;
    private final JButton backButton;
    private final SmartTextField nameField1;
    private final SmartTextField nameField2;
    private final SmartTextField idField2;
    private final SmartTextField phoneField1;
    private final SmartTextField phoneField2;
    private final JPanel idResultPanel;
    private final JLabel foundIdResultLabel;
    private final MainFrame mainFrame;
    private final UserDAO userDAO;
    private String verifiedId;
    private String verifiedIdName;
    private String verifiedIdPhone;
    private String verifiedPasswordUserId;
    private String verifiedPasswordName;
    private String verifiedPasswordPhone;

    public FindIdPwPhone(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userDAO = new UserDAO();
        setLayout(null);
        setBackground(AppTheme.BACKGROUND);
        setBounds(0, 0, 440, 956);

        JLabel titleLabel = new JLabel("아이디·비밀번호 찾기");
        AppTheme.styleScreenTitle(titleLabel);
        titleLabel.setBounds(30, 22, 245, 36);
        add(titleLabel);

        backButton = new JButton("로그인으로");
        AppTheme.styleSecondaryButton(backButton);
        backButton.setBounds(300, 22, 110, AppTheme.SECONDARY_BUTTON_HEIGHT);
        backButton.addActionListener(e -> returnToLogin());
        add(backButton);

        JLabel descriptionLabel = new JLabel(
                "<html>가입 시 등록한 이름과 휴대폰 번호로<br>사용자 정보를 확인합니다.</html>");
        AppTheme.styleScreenDescription(descriptionLabel);
        descriptionLabel.setBounds(30, 62, 350, 42);
        add(descriptionLabel);

        JPanel findIdCard = createCard(30, 118, 380, 278);
        findIdCard.add(createSectionTitle("아이디 찾기"));

        addFieldLabel(findIdCard, "이름", 62);
        nameField1 = createInputField("실명을 입력해주세요");
        nameField1.setBounds(105, 55, 245, AppTheme.INPUT_HEIGHT);
        findIdCard.add(nameField1);

        addFieldLabel(findIdCard, "휴대폰 번호", 112);
        phoneField1 = createInputField("010-1234-5678");
        phoneField1.setBounds(105, 105, 145, AppTheme.INPUT_HEIGHT);
        findIdCard.add(phoneField1);

        verifyIdButton = new JButton("사용자 확인");
        AppTheme.styleSecondaryButton(verifyIdButton);
        verifyIdButton.setBounds(258, 105, 92, AppTheme.INPUT_HEIGHT);
        verifyIdButton.addActionListener(this);
        findIdCard.add(verifyIdButton);

        idResultPanel = new JPanel(new BorderLayout());
        idResultPanel.setBackground(AppTheme.PRIMARY_LIGHT);
        idResultPanel.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        idResultPanel.setBounds(20, 163, 330, 50);
        foundIdResultLabel = new JLabel("", SwingConstants.CENTER);
        foundIdResultLabel.setFont(AppTheme.BODY_BOLD_FONT);
        foundIdResultLabel.setForeground(AppTheme.PRIMARY_DARK);
        idResultPanel.add(foundIdResultLabel, BorderLayout.CENTER);
        idResultPanel.setVisible(false);
        findIdCard.add(idResultPanel);

        findIdButton = new JButton("아이디 찾기");
        AppTheme.stylePrimaryButton(findIdButton);
        findIdButton.setBounds(105, 232, 245, 42);
        findIdButton.addActionListener(this);
        findIdCard.add(findIdButton);

        JPanel findPasswordCard = createCard(30, 414, 380, 328);
        findPasswordCard.add(createSectionTitle("비밀번호 재설정"));

        addFieldLabel(findPasswordCard, "이름", 65);
        nameField2 = createInputField("실명을 입력해주세요");
        nameField2.setBounds(105, 58, 245, AppTheme.INPUT_HEIGHT);
        findPasswordCard.add(nameField2);

        addFieldLabel(findPasswordCard, "아이디", 115);
        idField2 = createInputField("아이디를 입력해주세요");
        idField2.setBounds(105, 108, 245, AppTheme.INPUT_HEIGHT);
        findPasswordCard.add(idField2);

        addFieldLabel(findPasswordCard, "휴대폰 번호", 165);
        phoneField2 = createInputField("010-1234-5678");
        phoneField2.setBounds(105, 158, 145, AppTheme.INPUT_HEIGHT);
        findPasswordCard.add(phoneField2);

        verifyPasswordButton = new JButton("사용자 확인");
        AppTheme.styleSecondaryButton(verifyPasswordButton);
        verifyPasswordButton.setBounds(258, 158, 92, AppTheme.INPUT_HEIGHT);
        verifyPasswordButton.addActionListener(this);
        findPasswordCard.add(verifyPasswordButton);

        findPasswordButton = new JButton("새 비밀번호 설정");
        AppTheme.stylePrimaryButton(findPasswordButton);
        findPasswordButton.setBounds(105, 233, 245, 42);
        findPasswordButton.addActionListener(this);
        findPasswordCard.add(findPasswordButton);

        JLabel passwordGuide = new JLabel(
                "<html>사용자 확인 후 새 비밀번호를<br>설정할 수 있습니다.</html>");
        passwordGuide.setFont(AppTheme.CAPTION_FONT);
        passwordGuide.setForeground(AppTheme.TEXT_SECONDARY);
        passwordGuide.setBounds(105, 283, 245, 38);
        findPasswordCard.add(passwordGuide);
    }

    private JPanel createCard(int x, int y, int width, int height) {
        JPanel card = new JPanel(null);
        AppTheme.styleCard(card);
        card.setBounds(x, y, width, height);
        add(card);
        return card;
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        AppTheme.styleSectionTitle(label);
        label.setBounds(20, 17, 220, 28);
        return label;
    }

    private void addFieldLabel(JPanel card, String text, int y) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.BODY_BOLD_FONT);
        label.setForeground(AppTheme.TEXT_SECONDARY);
        label.setBounds(20, y, 82, 24);
        card.add(label);
    }

    private SmartTextField createInputField(String placeholder) {
        SmartTextField field = new SmartTextField(placeholder, 20);
        AppTheme.styleInputField(field);
        field.setForeground(AppTheme.TEXT_SECONDARY);
        return field;
    }

    public void resetForEntry() {
        resetInputField(nameField1);
        resetInputField(phoneField1);
        resetInputField(nameField2);
        resetInputField(idField2);
        resetInputField(phoneField2);
        clearIdVerification();
        clearPasswordVerification();
        hideFoundIdResult();
        revalidate();
        repaint();
        SwingUtilities.invokeLater(backButton::requestFocusInWindow);
    }

    private void resetInputField(SmartTextField field) {
        field.resetToPlaceholder();
        field.setForeground(AppTheme.TEXT_SECONDARY);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == verifyIdButton) {
            verifyIdOwner();
        } else if (e.getSource() == findIdButton) {
            showFoundId();
        } else if (e.getSource() == verifyPasswordButton) {
            verifyPasswordOwner();
        } else if (e.getSource() == findPasswordButton) {
            resetPassword();
        }
    }

    private void verifyIdOwner() {
        hideFoundIdResult();
        String userName = nameField1.getRealText().trim();
        String userPhone = phoneField1.getRealText().trim();

        if (!ValidationUtils.isValidName(userName)) {
            CustomDialog.showDialog(mainFrame, "이름을 올바르게 입력해주세요.", "사용자 확인");
            clearIdVerification();
            return;
        }
        if (!ValidationUtils.isValidPhone(userPhone)) {
            CustomDialog.showDialog(
                    mainFrame,
                    "휴대폰 번호를 010-1234-5678 형식으로 입력해주세요.",
                    "사용자 확인");
            clearIdVerification();
            return;
        }

        String userId = userDAO.findUserIdByNameAndPhone(userName, userPhone);
        if (userId == null) {
            CustomDialog.showDialog(
                    mainFrame,
                    "입력한 정보와 일치하는 사용자를 찾을 수 없습니다.",
                    "사용자 확인");
            clearIdVerification();
            return;
        }

        verifiedId = userId;
        verifiedIdName = userName;
        verifiedIdPhone = userPhone;
        CustomDialog.showDialog(mainFrame, "사용자 확인이 완료되었습니다.", "사용자 확인");
    }

    private void showFoundId() {
        String userName = nameField1.getRealText().trim();
        String userPhone = phoneField1.getRealText().trim();
        if (verifiedId == null
                || !userName.equals(verifiedIdName)
                || !userPhone.equals(verifiedIdPhone)) {
            CustomDialog.showDialog(
                    mainFrame,
                    "이름과 휴대폰 번호로 사용자 확인을 먼저 완료해주세요.",
                    "아이디 찾기");
            clearIdVerification();
            hideFoundIdResult();
            return;
        }

        foundIdResultLabel.setText("회원님의 아이디: " + verifiedId);
        idResultPanel.setVisible(true);
        clearIdVerification();
    }

    private void hideFoundIdResult() {
        foundIdResultLabel.setText("");
        idResultPanel.setVisible(false);
    }

    private void clearIdVerification() {
        verifiedId = null;
        verifiedIdName = null;
        verifiedIdPhone = null;
    }

    private void verifyPasswordOwner() {
        String userName = nameField2.getRealText().trim();
        String userId = idField2.getRealText().trim();
        String userPhone = phoneField2.getRealText().trim();

        if (!ValidationUtils.isValidName(userName)) {
            CustomDialog.showDialog(mainFrame, "이름을 올바르게 입력해주세요.", "사용자 확인");
            clearPasswordVerification();
            return;
        }
        if (userId.isEmpty()) {
            CustomDialog.showDialog(mainFrame, "아이디를 입력해주세요.", "사용자 확인");
            clearPasswordVerification();
            return;
        }
        if (!ValidationUtils.isValidPhone(userPhone)) {
            CustomDialog.showDialog(
                    mainFrame,
                    "휴대폰 번호를 010-1234-5678 형식으로 입력해주세요.",
                    "사용자 확인");
            clearPasswordVerification();
            return;
        }
        if (!userDAO.verifyUserIdentity(userId, userName, userPhone)) {
            CustomDialog.showDialog(
                    mainFrame,
                    "입력한 정보와 일치하는 사용자를 찾을 수 없습니다.",
                    "사용자 확인");
            clearPasswordVerification();
            return;
        }

        verifiedPasswordUserId = userId;
        verifiedPasswordName = userName;
        verifiedPasswordPhone = userPhone;
        CustomDialog.showDialog(mainFrame, "사용자 확인이 완료되었습니다.", "사용자 확인");
    }

    private void resetPassword() {
        String userName = nameField2.getRealText().trim();
        String userId = idField2.getRealText().trim();
        String userPhone = phoneField2.getRealText().trim();
        if (verifiedPasswordUserId == null
                || !userId.equals(verifiedPasswordUserId)
                || !userName.equals(verifiedPasswordName)
                || !userPhone.equals(verifiedPasswordPhone)) {
            CustomDialog.showDialog(
                    mainFrame,
                    "이름·아이디·휴대폰 번호로 사용자 확인을 먼저 완료해주세요.",
                    "비밀번호 재설정");
            clearPasswordVerification();
            return;
        }

        while (true) {
            JPasswordField newPasswordField = new JPasswordField(20);
            JPasswordField confirmPasswordField = new JPasswordField(20);
            PasswordDocumentFilter.install(newPasswordField);
            PasswordDocumentFilter.install(confirmPasswordField);
            AppTheme.styleInputField(newPasswordField);
            AppTheme.styleInputField(confirmPasswordField);

            PasswordVisibilityToggle newPasswordVisibility =
                    PasswordVisibilityToggle.attach(newPasswordField);
            PasswordVisibilityToggle confirmPasswordVisibility =
                    PasswordVisibilityToggle.attach(confirmPasswordField);

            JPanel newPasswordRow = new JPanel(new BorderLayout(6, 0));
            newPasswordRow.setBackground(AppTheme.CARD);
            newPasswordRow.add(newPasswordField, BorderLayout.CENTER);
            JButton newPasswordVisibilityButton = newPasswordVisibility.getButton();
            AppTheme.styleSecondaryButton(newPasswordVisibilityButton);
            newPasswordVisibilityButton.setPreferredSize(new Dimension(54, 34));
            newPasswordRow.add(newPasswordVisibilityButton, BorderLayout.EAST);

            JPanel confirmPasswordRow = new JPanel(new BorderLayout(6, 0));
            confirmPasswordRow.setBackground(AppTheme.CARD);
            confirmPasswordRow.add(confirmPasswordField, BorderLayout.CENTER);
            JButton confirmPasswordVisibilityButton = confirmPasswordVisibility.getButton();
            AppTheme.styleSecondaryButton(confirmPasswordVisibilityButton);
            confirmPasswordVisibilityButton.setPreferredSize(new Dimension(54, 34));
            confirmPasswordRow.add(confirmPasswordVisibilityButton, BorderLayout.EAST);

            JLabel newPasswordLabel = new JLabel("새 비밀번호");
            newPasswordLabel.setFont(AppTheme.BODY_BOLD_FONT);
            JLabel confirmPasswordLabel = new JLabel("새 비밀번호 확인");
            confirmPasswordLabel.setFont(AppTheme.BODY_BOLD_FONT);
            JLabel passwordRuleLabel = new JLabel("6~20자, 영문과 특수문자를 함께 사용하세요.");
            passwordRuleLabel.setFont(AppTheme.CAPTION_FONT);
            passwordRuleLabel.setForeground(AppTheme.TEXT_SECONDARY);

            JPanel resetPanel = new JPanel(new GridLayout(0, 1, 0, 6));
            resetPanel.setBackground(AppTheme.CARD);
            resetPanel.add(newPasswordLabel);
            resetPanel.add(newPasswordRow);
            resetPanel.add(confirmPasswordLabel);
            resetPanel.add(confirmPasswordRow);
            resetPanel.add(passwordRuleLabel);

            int result = JOptionPane.showConfirmDialog(
                    mainFrame,
                    resetPanel,
                    "새 비밀번호 설정",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                clearPasswordVerification();
                mainFrame.showPanel("login");
                return;
            }

            char[] newPasswordChars = newPasswordField.getPassword();
            char[] confirmPasswordChars = confirmPasswordField.getPassword();
            try {
                if (!Arrays.equals(newPasswordChars, confirmPasswordChars)) {
                    CustomDialog.showDialog(
                            mainFrame,
                            "새 비밀번호와 확인값이 일치하지 않습니다.",
                            "비밀번호 재설정");
                    continue;
                }
                if (!ValidationUtils.isCreateUserPw(newPasswordChars)) {
                    CustomDialog.showDialog(
                            mainFrame,
                            "비밀번호는 6~20자 영문과 특수문자를 포함해야 합니다.",
                            "비밀번호 재설정");
                    continue;
                }
                if (!userDAO.updateUserPasswordFromRaw(verifiedPasswordUserId, newPasswordChars)) {
                    CustomDialog.showDialog(
                            mainFrame,
                            "비밀번호를 변경하지 못했습니다. 잠시 후 다시 시도해주세요.",
                            "비밀번호 재설정");
                    return;
                }

                CustomDialog.showDialog(mainFrame, "새 비밀번호가 설정되었습니다.", "비밀번호 재설정");
                clearPasswordVerification();
                mainFrame.showPanel("login");
                return;
            } finally {
                Arrays.fill(newPasswordChars, '\0');
                Arrays.fill(confirmPasswordChars, '\0');
            }
        }
    }

    private void clearPasswordVerification() {
        verifiedPasswordUserId = null;
        verifiedPasswordName = null;
        verifiedPasswordPhone = null;
    }

    private void returnToLogin() {
        clearIdVerification();
        clearPasswordVerification();
        hideFoundIdResult();
        mainFrame.showPanel("login");
    }
}
