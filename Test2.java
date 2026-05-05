import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Test2 extends JFrame {

    public Test2() {
        // 기본 프레임 설정
        setTitle("회원정보수정");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // 메인 패널 설정
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // 타이틀 라벨
        JLabel titleLabel = new JLabel("회원정보수정", JLabel.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        titleLabel.setBounds(0, 20, 400, 30);
        mainPanel.add(titleLabel);
        
        // 이름 라벨 및 필드
        JLabel nameLabel = new JLabel("이름");
        nameLabel.setBounds(50, 70, 100, 25);
        mainPanel.add(nameLabel);
        
        JTextField nameField = new JTextField();
        nameField.setBounds(150, 70, 200, 25);
        mainPanel.add(nameField);
        
        // 학번 라벨 및 필드
        JLabel usernameLabel = new JLabel("학번");
        usernameLabel.setBounds(50, 110, 100, 25);
        mainPanel.add(usernameLabel);
        
        JTextField usernameField = new JTextField("disable");
        usernameField.setEnabled(false);
        usernameField.setBounds(150, 110, 200, 25);
        mainPanel.add(usernameField);
        
        // 비밀번호 변경 라벨 및 필드
        JLabel passwordLabel = new JLabel("비밀번호 변경");
        passwordLabel.setBounds(50, 150, 100, 25);
        mainPanel.add(passwordLabel);
        
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(150, 150, 200, 25);
        mainPanel.add(passwordField);
        
        // 비밀번호 확인 라벨 및 필드
        JLabel confirmPasswordLabel = new JLabel("비밀번호 확인");
        confirmPasswordLabel.setBounds(50, 190, 100, 25);
        mainPanel.add(confirmPasswordLabel);
        
        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(150, 190, 200, 25);
        mainPanel.add(confirmPasswordField);
        
        // 일치/불일치 라벨
        JLabel matchLabel = new JLabel("일치/불일치");
        matchLabel.setBounds(50, 230, 100, 25);
        mainPanel.add(matchLabel);
        
        // 학과 라벨 및 콤보박스
        JLabel departmentLabel = new JLabel("학과");
        departmentLabel.setBounds(50, 270, 100, 25);
        mainPanel.add(departmentLabel);
        
        JComboBox<String> departmentComboBox = new JComboBox<>(new String[]{"선택하세요"});
        departmentComboBox.setBounds(150, 270, 200, 25);
        mainPanel.add(departmentComboBox);
        
        // 전화번호 라벨 및 필드
        JLabel phoneLabel = new JLabel("전화번호");
        phoneLabel.setBounds(50, 310, 100, 25);
        mainPanel.add(phoneLabel);
        
        JTextField phoneField = new JTextField();
        phoneField.setBounds(150, 310, 200, 25);
        mainPanel.add(phoneField);
        
        // 완료 버튼
        JButton confirmButton = new JButton("완료");
        confirmButton.setBounds(150, 350, 100, 30);
        confirmButton.setBackground(new Color(220, 220, 220));
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 버튼 클릭 시 처리
                JOptionPane.showMessageDialog(Test2.this, "회원정보가 수정되었습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        mainPanel.add(confirmButton);
        
        // 프레임에 패널 추가
        setContentPane(mainPanel);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Test2();
            }
        });
    }
} 