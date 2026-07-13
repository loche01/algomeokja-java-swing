package panel;

import DB.UserDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import main.MainFrame;
import ui_n_utils.CustomDialog;
import ui_n_utils.RoundedComponent;
import ui_n_utils.SmartTextField;
import ui_n_utils.ValidationUtils;



public class FindIdPwPhone extends JPanel implements ActionListener {
    private RoundedComponent findIdButton, sendCodeButton1, sendCodeButton2, findPwButton;
    private SmartTextField nameField1,nameField2,idField2,phoneField1,phoneField2;
    private MainFrame mainFrame;
    private UserDAO userDAO;
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
        setBackground(Color.WHITE);
        setBounds(0, 0, 440, 956);

        int formWidth = 270;
        int centerX = (getWidth() - formWidth) / 3;
        int startY = 100;

        // 상단 제목
        JLabel titleLabel = new JLabel("아이디·비밀번호 찾기", JLabel.LEFT);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 32));
        titleLabel.setBounds(centerX-30, startY-95, 310, 58);
        add(titleLabel);
        
        JButton backButton = new JButton("<");
        backButton.setBounds(360, 20, 60, 60);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> returnToLogin());
        add(backButton);
        
        // 구분선
        JSeparator divider = new JSeparator();
        divider.setBounds(centerX-60, startY -30, 440, 1);
        divider.setForeground(Color.black);
        add(divider);

        JLabel descriptionLabel = new JLabel(
                "가입 시 등록한 휴대폰 번호로 사용자 정보를 확인합니다.", JLabel.CENTER);
        descriptionLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        descriptionLabel.setForeground(Color.DARK_GRAY);
        descriptionLabel.setBounds(35, startY + 15, 370, 45);
        add(descriptionLabel);

        // ID 찾기 섹션
        JLabel findIdLabel = new JLabel("아이디 찾기", JLabel.CENTER);
        findIdLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        findIdLabel.setBounds(centerX+15, startY + 100, formWidth, 30);
        add(findIdLabel);

        // 이름 입력 필드
        addInputField("이름", centerX-10, startY + 165, formWidth);
        nameField1 = new SmartTextField("실명을 입력해주세요", 30);
        nameField1.setBounds(centerX+50, startY + 165, formWidth, 30);
        add(nameField1);
        
    	// 휴대전화 입력 필드
        addInputField("휴대전화", centerX-10, startY + 225, formWidth);
        phoneField1 = new SmartTextField("전화번호를 입력해주세요", 20);
        phoneField1.setBounds(centerX+50, startY + 225, 170, 30);
        add(phoneField1);

        sendCodeButton1 = new RoundedComponent(90, 30,0,"button", "사용자 확인",Color.black, Color.black, Color.white, "Inter", Font.BOLD, 13 );
        sendCodeButton1.setBounds(centerX + 230, startY + 225, 90, 30);
        sendCodeButton1.getButton().addActionListener(this);
        add(sendCodeButton1);
        
        // 아이디 찾기 버튼
        findIdButton = new RoundedComponent(198, 44, 35, "button", "아이디 찾기", new Color(0xC0E993),  new Color(0xC0E993), Color.white, "Inter", Font.BOLD, 15);
        findIdButton.setBounds(centerX+50, startY + 320, 198, 44);
        findIdButton.getButton().addActionListener(this);
        add(findIdButton);

        // 구분선
        JSeparator divider1 = new JSeparator();
        divider1.setBounds(centerX-60, startY + 390, 440, 2);
        divider1.setForeground(Color.black);
        add(divider1);

        // 비밀번호 찾기 섹션
        JLabel findPwLabel = new JLabel("비밀번호 찾기", JLabel.CENTER);
        findPwLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        findPwLabel.setBounds(centerX+18, startY + 410, formWidth, 30);
        add(findPwLabel);
        	
        // 이름 입력 필드
        addInputField("이름", centerX-10, startY + 475, formWidth);
        nameField2 = new SmartTextField("실명을 입력해주세요", 30);
        nameField2.setBounds(centerX+50, startY + 475, formWidth, 30);
        add(nameField2);
        
        // 아이디 찾기 버튼
        addInputField("아이디", centerX-10, startY + 535, formWidth);
        idField2 = new SmartTextField("아이디를 입력해주세요", 30);
        idField2.setBounds(centerX+50, startY + 535, formWidth, 30);
        add(idField2);
        
        //
        addInputField("휴대전화", centerX-10, startY + 595, formWidth);
        phoneField2 = new SmartTextField("전화번호를 입력해주세요", 30);
        phoneField2.setBounds(centerX+50, startY + 595, 170, 30);
        add(phoneField2);

        sendCodeButton2 = new RoundedComponent(90, 30, 0, "button","사용자 확인",Color.black, Color.black, Color.white, "Inter", Font.BOLD, 13 );
        sendCodeButton2.setBounds(centerX + 230, startY + 595, 90, 30);
        sendCodeButton2.getButton().addActionListener(this);
        
        add(sendCodeButton2);
        
        findPwButton = new RoundedComponent(198, 44, 35, "button", "새 비밀번호 설정", new Color(0xC0E993),  new Color(0xC0E993), Color.white, "Inter", Font.BOLD, 15);
        findPwButton.setBounds(centerX+50, startY + 700, 198, 44);
        findPwButton.getButton().addActionListener(this);
        add(findPwButton);

      //setVisible(true);
    }

    private void addInputField(String labelText, int x, int y, int width) {
        JLabel label = new JLabel(labelText);
        label.setBounds(x, y, 100, 20);
        add(label);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendCodeButton1.getButton()) {
            verifyIdOwner();
        } else if (e.getSource() == findIdButton.getButton()) {
            showFoundId();
        } else if (e.getSource() == sendCodeButton2.getButton()) {
            verifyPasswordOwner();
        } else if (e.getSource() == findPwButton.getButton()) {
            resetPassword();
        }
    }

    private void verifyIdOwner() {
        String userName = nameField1.getRealText().trim();
        String userPhone = phoneField1.getRealText().trim();

        if (!ValidationUtils.isValidName(userName)) {
            CustomDialog.showDialog(mainFrame, "이름을 올바르게 입력해주세요.", "사용자 확인");
            clearIdVerification();
            return;
        }
        if (!ValidationUtils.isValidPhone(userPhone)) {
            CustomDialog.showDialog(mainFrame, "휴대폰 번호를 010-1234-5678 형식으로 입력해주세요.", "사용자 확인");
            clearIdVerification();
            return;
        }

        String userId = userDAO.findUserIdByNameAndPhone(userName, userPhone);
        if (userId == null) {
            CustomDialog.showDialog(mainFrame, "입력한 정보와 일치하는 사용자를 찾을 수 없습니다.", "사용자 확인");
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
        if (verifiedId == null || !userName.equals(verifiedIdName) || !userPhone.equals(verifiedIdPhone)) {
            CustomDialog.showDialog(mainFrame, "이름과 휴대폰 번호로 사용자 확인을 먼저 완료해주세요.", "아이디 찾기");
            clearIdVerification();
            return;
        }

        JOptionPane.showMessageDialog(mainFrame,
                "회원님의 아이디는 " + verifiedId + " 입니다.",
                "아이디 찾기",
                JOptionPane.INFORMATION_MESSAGE);
        clearIdVerification();
        mainFrame.showPanel("login");
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
            CustomDialog.showDialog(mainFrame, "휴대폰 번호를 010-1234-5678 형식으로 입력해주세요.", "사용자 확인");
            clearPasswordVerification();
            return;
        }
        if (!userDAO.verifyUserIdentity(userId, userName, userPhone)) {
            CustomDialog.showDialog(mainFrame, "입력한 정보와 일치하는 사용자를 찾을 수 없습니다.", "사용자 확인");
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
            CustomDialog.showDialog(mainFrame, "이름·아이디·휴대폰 번호로 사용자 확인을 먼저 완료해주세요.", "비밀번호 재설정");
            clearPasswordVerification();
            return;
        }

        while (true) {
            JPasswordField newPasswordField = new JPasswordField(20);
            JPasswordField confirmPasswordField = new JPasswordField(20);
            JPanel resetPanel = new JPanel(new GridLayout(0, 1, 0, 6));
            resetPanel.add(new JLabel("새 비밀번호"));
            resetPanel.add(newPasswordField);
            resetPanel.add(new JLabel("새 비밀번호 확인"));
            resetPanel.add(confirmPasswordField);
            resetPanel.add(new JLabel("6~20자, 영문과 특수문자를 함께 사용하세요."));

            int result = JOptionPane.showConfirmDialog(mainFrame, resetPanel,
                    "새 비밀번호 설정", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                clearPasswordVerification();
                mainFrame.showPanel("login");
                return;
            }

            char[] newPasswordChars = newPasswordField.getPassword();
            char[] confirmPasswordChars = confirmPasswordField.getPassword();
            try {
                if (!Arrays.equals(newPasswordChars, confirmPasswordChars)) {
                    CustomDialog.showDialog(mainFrame, "새 비밀번호와 확인값이 일치하지 않습니다.", "비밀번호 재설정");
                    continue;
                }
                if (!ValidationUtils.isCreateUserPw(newPasswordChars)) {
                    CustomDialog.showDialog(mainFrame, "비밀번호는 6~20자 영문과 특수문자를 포함해야 합니다.", "비밀번호 재설정");
                    continue;
                }
                if (!userDAO.updateUserPasswordFromRaw(verifiedPasswordUserId, newPasswordChars)) {
                    CustomDialog.showDialog(mainFrame, "비밀번호를 변경하지 못했습니다. 잠시 후 다시 시도해주세요.", "비밀번호 재설정");
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
        mainFrame.showPanel("login");
    }

}
