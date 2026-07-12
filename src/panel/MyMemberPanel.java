package panel;

import DB.UserDAO;
import DB.UserDAO.UpdateUserResult;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;
import main.MainUserPanel;
import model.LoginManager;
import model.UserBean;
import ui_n_utils.RoundedComponent;
import ui_n_utils.ValidationUtils;

public class MyMemberPanel extends JPanel implements ActionListener {
	private MainUserPanel mainUserPanel;
    private final RoundedComponent mainPanel, finishButton, backButton;
    private RoundedComponent[] fields;
    private UserDAO userDAO;

    public MyMemberPanel(MainUserPanel mainUserPanel) {
        this.mainUserPanel = mainUserPanel;
        this.userDAO = new UserDAO();
        setLayout(null);
        setBackground(new Color(192, 233, 147)); // 배경색 설정

        // 메인 패널 생성
        mainPanel = new RoundedComponent(380, 670, 30, "panel", " ", 
                new Color(192, 233, 147), Color.white, Color.black, " ", 0, 0);
        mainPanel.setBounds(21, 40, 380, 670);
        add(mainPanel); // 패널 추가

        // 회원 정보 레이블
        JLabel memberInfoLabel = new JLabel("회원정보");
        memberInfoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        memberInfoLabel.setForeground(Color.black);
        memberInfoLabel.setBounds(135, 100, 150, 30);
        mainPanel.add(memberInfoLabel);
        
        // 📌 뒤로가기 버튼
        backButton = new RoundedComponent(60, 60, 10, "Button", "X",
                                          Color.white, Color.white, Color.black, "맑은 고딕", Font.BOLD, 24);
        backButton.setBounds(310, 10, 60, 60);
        backButton.getButton().addActionListener(e -> mainUserPanel.showPanel("MyPage")); // 📌 MyPagePanel로 전환
        mainPanel.add(backButton);
        
        // 텍스트 필드 생성
       
        // 📌 회원 정보 라벨 & 입력 필드 배치 조정
        String[] memberInfo = {"이름", "이메일", "전화번호", "ID",
                "현재 비밀번호", "새 비밀번호", "새 비밀번호 확인"};
        fields = new RoundedComponent[memberInfo.length];

        int labelStartY = 155;
        int fieldStartY = 150;
        int spacing = 55;

        for (int i = 0; i < memberInfo.length; i++) {
            JLabel label = new JLabel(memberInfo[i]);
            label.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            label.setBounds(35, labelStartY + (i * spacing), 135, 25);
            mainPanel.add(label);

            String fieldType = i >= 4 ? "password" : "textField";
            fields[i] = new RoundedComponent(180, 36, 7, fieldType, "",
                    Color.lightGray, new Color(0xD9D9D9), Color.black, "맑은 고딕", Font.BOLD, 12);
            fields[i].setBounds(170, fieldStartY + (i * spacing), 180, 36);
            mainPanel.add(fields[i]);
        }
        
        // ID 필드는 수정 불가능하게 설정
        fields[3].getTextField().setEditable(false);
        fields[3].getTextField().setBackground(new Color(0xCCCCCC));
        
        JLabel passwordRuleLabel = new JLabel("새 비밀번호: 6~20자, 영문과 특수문자 포함");
        passwordRuleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
        passwordRuleLabel.setForeground(Color.DARK_GRAY);
        passwordRuleLabel.setBounds(65, 535, 280, 20);
        mainPanel.add(passwordRuleLabel);

        finishButton = new RoundedComponent(100, 40, 10, "button", "완료", 
                Color.BLACK, Color.BLACK, Color.WHITE, "맑은고딕", Font.BOLD, 14);
        finishButton.setBounds(140, 565, 100, 40);
        finishButton.getButton().addActionListener(this);
        mainPanel.add(finishButton);
    }
    
    // 패널이 표시될 때 사용자 정보 로드
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            loadUserInfo();
        }
    }
    
    // 사용자 정보 로드
    private void loadUserInfo() {
        UserBean user = LoginManager.getInstance().getCurrentUser();
        if (user != null) {
            fields[0].getTextField().setText(user.getUser_name());
            fields[1].getTextField().setText(user.getUser_email());
            fields[2].getTextField().setText(user.getUser_phone());
            fields[3].getTextField().setText(user.getUser_id());
            fields[4].setText("");
            fields[5].setText("");
            fields[6].setText("");
        }
    }
    
    // 사용자 정보 업데이트
    private void updateUserInfo() {
        // 필수 필드 검증
        if (fields[0].getTextField().getText().trim().isEmpty() ||
            fields[1].getTextField().getText().trim().isEmpty() ||
            fields[2].getTextField().getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "이름, 이메일, 전화번호는 필수 입력 항목입니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(this, "새 비밀번호 변경 시 현재 비밀번호를 입력해주세요.",
                            "입력 오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (passwordChangeRequested && !Arrays.equals(newPassword, confirmPassword)) {
                    JOptionPane.showMessageDialog(this, "새 비밀번호와 확인값이 일치하지 않습니다.",
                            "입력 오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (passwordChangeRequested && !ValidationUtils.isCreateUserPw(newPassword)) {
                    JOptionPane.showMessageDialog(this,
                            "새 비밀번호는 6~20자 영문과 특수문자를 포함해야 합니다.",
                            "입력 오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                UserBean updatedUser = new UserBean();
                updatedUser.setUser_id(user.getUser_id());
                updatedUser.setUser_name(fields[0].getTextField().getText().trim());
                updatedUser.setUser_email(fields[1].getTextField().getText().trim());
                updatedUser.setUser_phone(fields[2].getTextField().getText().trim());

                UpdateUserResult result = userDAO.updateUserWithOptionalRawPassword(
                        updatedUser, currentPassword, passwordChangeRequested ? newPassword : null);
                if (result == UpdateUserResult.CURRENT_PASSWORD_MISMATCH) {
                    JOptionPane.showMessageDialog(this, "현재 비밀번호가 올바르지 않습니다.",
                            "업데이트 실패", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (result != UpdateUserResult.SUCCESS) {
                    JOptionPane.showMessageDialog(this, "회원 정보 업데이트에 실패했습니다.",
                            "업데이트 실패", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                user.setUser_name(updatedUser.getUser_name());
                user.setUser_email(updatedUser.getUser_email());
                user.setUser_phone(updatedUser.getUser_phone());
                JOptionPane.showMessageDialog(this, "회원 정보가 성공적으로 업데이트되었습니다.", "업데이트 성공", JOptionPane.INFORMATION_MESSAGE);
                mainUserPanel.showPanel("MyPage");
            } finally {
                Arrays.fill(currentPassword, '\0');
                Arrays.fill(newPassword, '\0');
                Arrays.fill(confirmPassword, '\0');
                fields[4].setText("");
                fields[5].setText("");
                fields[6].setText("");
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == finishButton.getButton()) {
            updateUserInfo();
        }
    }
}
