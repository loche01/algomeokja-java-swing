package panel;

import javax.swing.*;

import ui_n_utils.RoundedComponent;
import ui_n_utils.SmartTextField;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FindIdPwEmail extends JFrame implements ActionListener {
    private RoundedComponent findIdButton, phoneTabButton,emailTabButton,sendCodeButton1,sendCodeButton2,  findPwButton;
    private SmartTextField nameField1,nameField2,idField2,phoneField1,verifyField1,phoneField2, verifyField2;
    public FindIdPwEmail() {
        setTitle("ID/PW 찾기");
        setSize(440, 956);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.white);
        setLayout(null);
        setResizable(false);

        int formWidth = 270;
        int centerX = (getWidth() - formWidth) / 3;
        int startY = 100;

        // 상단 제목
        JLabel titleLabel = new JLabel("ID/PW 찾기", JLabel.LEFT);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 32));
        titleLabel.setBounds(centerX-30, startY-95, 200, 58);
        add(titleLabel);
        
        // 구분선
        JSeparator divider = new JSeparator();
        divider.setBounds(centerX-60, startY -30, 440, 2);
        add(divider);

        // 탭 버튼 (휴대전화/이메일)
        phoneTabButton = new RoundedComponent(150,40, 35, "button", "휴대전화로 찾기", Color.black, Color.black, Color.white, "Inter", Font.BOLD, 15);//휴대 전화 찾
        phoneTabButton.setBounds(centerX, startY + 25, 150, 40);
        add(phoneTabButton);

        emailTabButton = new RoundedComponent(150, 40, 35,"button", "이메일로 찾기",  Color.LIGHT_GRAY, Color.lightGray, Color.black, "Inter", Font.BOLD, 15);
        emailTabButton.setBounds(centerX + 170, startY + 25, 150, 40);
        add(emailTabButton);

        // ID 찾기 섹션
        JLabel findIdLabel = new JLabel("아이디 찾기", JLabel.CENTER);
        findIdLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        findIdLabel.setBounds(centerX+15, startY + 100, formWidth, 30);
        add(findIdLabel);

        addInputField("이름", centerX-10, startY + 165, formWidth);
        nameField1 = new SmartTextField("실명을 입력해주세요", 30);
        nameField1.setBounds(centerX+50, startY + 165, formWidth, 30);
        add(nameField1);

        addInputField("이메일", centerX-10, startY + 225, formWidth);
        phoneField1 = new SmartTextField("이메일을 입력해주세요", 20);
        phoneField1.setBounds(centerX+50, startY + 225, 170, 30);
        add(phoneField1);

        sendCodeButton1 = new RoundedComponent(90, 30,0,"button", "인증 전송",Color.black, Color.black, Color.white, "Inter", Font.BOLD, 15 );
        sendCodeButton1.setBounds(centerX + 230, startY + 225, 90, 30);
        sendCodeButton1.getButton().addActionListener(this);
        add(sendCodeButton1);

        verifyField1 = new SmartTextField("인증번호를 입력해주세요", 30);
        verifyField1.setBounds(centerX+50, startY + 265, formWidth, 30);
        add(verifyField1);

        findIdButton = new RoundedComponent(198, 44, 35, "button", "아이디 찾기", new Color(0xC0E993),  new Color(0xC0E993), Color.white, "Inter", Font.BOLD, 15);
        findIdButton.setBounds(centerX+50, startY + 320, 198, 44);
        findIdButton.getButton().addActionListener(this);
        add(findIdButton);

        // 구분선
        JSeparator divider1 = new JSeparator();
        divider1.setBounds(centerX-60, startY + 390, 440, 2);
        add(divider1);

        // 비밀번호 찾기 섹션
        JLabel findPwLabel = new JLabel("비밀번호 찾기", JLabel.CENTER);
        findPwLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        findPwLabel.setBounds(centerX+18, startY + 410, formWidth, 30);
        add(findPwLabel);

        addInputField("이름", centerX-10, startY + 475, formWidth);
        nameField2 = new SmartTextField("실명을 입력해주세요", 30);
        nameField2.setBounds(centerX+50, startY + 475, formWidth, 30);
        add(nameField2);

        addInputField("아이디", centerX-10, startY + 535, formWidth);
        idField2 = new SmartTextField("아이디를 입력해주세요", 30);
        idField2.setBounds(centerX+50, startY + 535, formWidth, 30);
        add(idField2);

        addInputField("이메일", centerX-10, startY + 595, formWidth);
        phoneField2 = new SmartTextField("이메일을 입력해주세요", 30);
        phoneField2.setBounds(centerX+50, startY + 595, 170, 30);
        add(phoneField2);

        sendCodeButton2 = new RoundedComponent(90, 30, 0, "button","인증 전송",Color.black, Color.black, Color.white, "Inter", Font.BOLD, 15 );
        sendCodeButton2.setBounds(centerX + 230, startY + 595, 90, 30);
        sendCodeButton2.getButton().addActionListener(this);
       
        add(sendCodeButton2);

        verifyField2 = new SmartTextField("인증번호를 입력해 주세요", 30);
        verifyField2.setBounds(centerX+50, startY + 635, formWidth, 30);
        add(verifyField2);

        findPwButton = new RoundedComponent(198, 44, 35, "button", "비밀번호 찾기", new Color(0xC0E993),  new Color(0xC0E993), Color.white, "Inter", Font.BOLD, 15);
        findPwButton.setBounds(centerX+50, startY + 700, 198, 44);
        findPwButton.getButton().addActionListener(this);
        add(findPwButton);

      setVisible(true);
    }

    private void addInputField(String labelText, int x, int y, int width) {
        JLabel label = new JLabel(labelText);
        label.setBounds(x, y, 100, 20);
        add(label);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 이메일 기반 찾기 화면은 아직 실제 기능과 연결되지 않았다.
    }

    public static void main(String[] args) {
        new FindIdPwEmail();
    }
}
