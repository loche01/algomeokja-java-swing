package panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import main.MainFrame;
import ui_n_utils.RoundedComponent;
import ui_n_utils.SmartTextField;



public class FindIdPwPhone extends JPanel implements ActionListener {
    private RoundedComponent findIdButton, phoneTabButton,emailTabButton,sendCodeButton1,sendCodeButton2,  findPwButton;
    private SmartTextField nameField1,nameField2,idField2,phoneField1,verifyField1,phoneField2, verifyField2;
    private MainFrame mainFrame;
    public FindIdPwPhone(MainFrame mainFrame) {
    	this.mainFrame = mainFrame;
        setLayout(null);
        setBackground(Color.WHITE);
        setBounds(0, 0, 440, 956);

        int formWidth = 270;
        int centerX = (getWidth() - formWidth) / 3;
        int startY = 100;

        // 상단 제목
        JLabel titleLabel = new JLabel("ID/PW 찾기", JLabel.LEFT);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 32));
        titleLabel.setBounds(centerX-30, startY-95, 200, 58);
        add(titleLabel);
        
        JButton backButton = new JButton("<");
        backButton.setBounds(360, 20, 60, 60);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> mainFrame.showPanel("login")); // 🔹 로그인 화면으로 돌아가기
        add(backButton);
        
        // 구분선
        JSeparator divider = new JSeparator();
        divider.setBounds(centerX-60, startY -30, 440, 1);
        divider.setForeground(Color.black);
        add(divider);

        // 탭 버튼 (휴대전화/이메일)
        phoneTabButton = new RoundedComponent(150,40, 35, "button", "휴대전화로 찾기", Color.black, Color.black, Color.white, "Inter", Font.BOLD, 15);//휴대 전화 찾
        phoneTabButton.setBounds(centerX, startY + 25, 150, 40);
        add(phoneTabButton);
        
        // 탭 버튼 (휴대전화/이메일)
        emailTabButton = new RoundedComponent(150, 40, 35,"button", "이메일로 찾기",  Color.LIGHT_GRAY, Color.lightGray, Color.black, "Inter", Font.BOLD, 15);
        emailTabButton.setBounds(centerX + 170, startY + 25, 150, 40);
        add(emailTabButton);

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

        sendCodeButton1 = new RoundedComponent(90, 30,0,"button", "인증 전송",Color.black, Color.black, Color.white, "Inter", Font.BOLD, 15 );
        sendCodeButton1.setBounds(centerX + 230, startY + 225, 90, 30);
        sendCodeButton1.getButton().addActionListener(this);
        add(sendCodeButton1);
        
        // 인증번호 입력 필드
        verifyField1 = new SmartTextField("인증번호를 입력해주세", 30);
        verifyField1.setBounds(centerX+50, startY + 265, formWidth, 30);
        add(verifyField1);
        
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

        sendCodeButton2 = new RoundedComponent(90, 30, 0, "button","인증 전송",Color.black, Color.black, Color.white, "Inter", Font.BOLD, 15 );
        sendCodeButton2.setBounds(centerX + 230, startY + 595, 90, 30);
        sendCodeButton2.getButton().addActionListener(this);
        
        add(sendCodeButton2);
        // 인증번호 입력 필드
        verifyField2 = new SmartTextField("인증번호를 입력해 주세요", 30);
        verifyField2.setBounds(centerX+50, startY + 635, formWidth, 30);
        add(verifyField2);
        
        findPwButton = new RoundedComponent(198, 44, 35, "button", "비밀번호 찾기", new Color(0xC0E993),  new Color(0xC0E993), Color.white, "Inter", Font.BOLD, 15);
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
            System.out.println("ID 찾기 - 인증번호 전송 클릭!");
        } else if (e.getSource() == findIdButton.getButton()) {
            System.out.println("아이디 찾기 버튼 클릭!");
        } else if (e.getSource() == sendCodeButton2.getButton()) {
            System.out.println("비밀번호 찾기 - 인증번호 전송 클릭!");
        } else if (e.getSource() == findPwButton.getButton()) {
            System.out.println("비밀번호 찾기 버튼 클릭!");
        }
    }

}
