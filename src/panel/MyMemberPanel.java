package panel;

import DB.UserDAO;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import main.MainUserPanel;
import model.LoginManager;
import model.UserBean;
import ui_n_utils.RoundedComponent;

public class MyMemberPanel extends JPanel implements ActionListener {
	private MainUserPanel mainUserPanel;
    private final RoundedComponent mainPanel, finishButton, backButton, a;
    private RoundedComponent[] fields;
    private UserDAO userDAO;
    private String oldPassword;

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
        String[] memberInfo = {"이름", "이메일", "전화번호", "ID", "비밀번호"};
        fields = new RoundedComponent[5];

        int labelStartY = 180; // 📌 라벨 시작 위치
        int fieldStartY = 180; // 📌 입력 필드 시작 위치
        int spacing = 65; // 📌 간격

        for (int i = 0; i < memberInfo.length; i++) {
            JLabel label = new JLabel(memberInfo[i]);
            label.setFont(new Font("맑은 고딕", Font.BOLD, 17));
            label.setBounds(65, labelStartY + (i * spacing), 100, 25);
            mainPanel.add(label);

            fields[i] = new RoundedComponent(140, 32, 7, "textField", " ", 
                    Color.lightGray, new Color(0xD9D9D9), Color.black, "맑은 고딕", Font.BOLD, 12);
            fields[i].setBounds(180, fieldStartY + (i * spacing), 140, 35);
            mainPanel.add(fields[i]);
        }
        
        // ID 필드는 수정 불가능하게 설정
        fields[3].getTextField().setEditable(false);
        fields[3].getTextField().setBackground(new Color(0xCCCCCC));
        
        // 완료 버튼 생성
        finishButton = new RoundedComponent(100, 40, 10, "button", "완료", 
                Color.BLACK, Color.BLACK, Color.WHITE, "맑은고딕", Font.BOLD, 14);
        finishButton.setBounds(140, 530, 100, 40);
        finishButton.getButton().addActionListener(this);
        mainPanel.add(finishButton);

        // 완료 버튼 생성
        a = new RoundedComponent(0, 0, 10, "button", "", 
                Color.BLACK, Color.BLACK, Color.WHITE, "맑은고딕", Font.BOLD, 14);
        a.setBounds(140, 530, 100, 40);
        a.getButton().addActionListener(this);
        mainPanel.add(a);
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
            fields[4].getTextField().setText("");  // 비밀번호는 보안상 표시하지 않음
            
            // 현재 비밀번호 저장
            oldPassword = user.getUser_pwd();
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
            // 사용자 정보 업데이트
            user.setUser_name(fields[0].getTextField().getText().trim());
            user.setUser_email(fields[1].getTextField().getText().trim());
            user.setUser_phone(fields[2].getTextField().getText().trim());
            
            // 비밀번호가 입력된 경우에만 업데이트
            String newPassword = fields[4].getTextField().getText().trim();
            if (!newPassword.isEmpty()) {
                user.setUser_pwd(newPassword);
            }
            
            // DB 업데이트
            boolean success = userDAO.updateUser(user, oldPassword);
            if (success) {
                JOptionPane.showMessageDialog(this, "회원 정보가 성공적으로 업데이트되었습니다.", "업데이트 성공", JOptionPane.INFORMATION_MESSAGE);
                mainUserPanel.showPanel("MyPage");
            } else {
                JOptionPane.showMessageDialog(this, "회원 정보 업데이트에 실패했습니다.", "업데이트 실패", JOptionPane.ERROR_MESSAGE);
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
