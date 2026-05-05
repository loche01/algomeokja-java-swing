package panel;

import DB.BodyInfoDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import main.MainUserPanel;
import model.LoginManager;
import model.UserBean;
import ui_n_utils.RoundedComponent;


public class MyPagePanel extends JPanel implements ActionListener {
    private RoundedComponent editBodyBtn, editMemberBtn, mainPanel, logoutBtn, a; 
    private MainUserPanel mainUserPanel;
    private JLabel[] userInfoLabels;
    private JLabel[] bodyInfoLabels;
    private BodyInfoDAO bodyInfoDAO;
    
    public MyPagePanel(MainUserPanel mainUserPanel) {
        this.mainUserPanel = mainUserPanel;
        this.bodyInfoDAO = new BodyInfoDAO();
        setLayout(null);
        setBackground(new Color(192, 233, 147)); // 기존 배경 유지
        
        // 메인 패널
        mainPanel = new RoundedComponent(380, 670, 30, "panel", " ", 
                new Color(192, 233, 147), Color.white, Color.black, " ", 0, 0);
        mainPanel.setBounds(21, 40, 380, 670);
        add(mainPanel);

        // 회원 정보
        JLabel memberInfoLabel = new JLabel("회원정보");
        memberInfoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        memberInfoLabel.setForeground(Color.RED);
        memberInfoLabel.setBounds(40, 40, 100, 30);
        mainPanel.add(memberInfoLabel);
        
        // 수정 버튼
        editMemberBtn = new RoundedComponent(100, 40, 10, "Button", "수정", 
                Color.white, Color.white, Color.red, "맑은 고딕", Font.BOLD, 17);
        editMemberBtn.setBounds(270, 30, 100, 40);
        editMemberBtn.getButton().addActionListener(this);
        mainPanel.add(editMemberBtn);
        editMemberBtn.getButton().addActionListener(e -> mainUserPanel.showPanel("MyMember")); // 📌 MyMemberPanel로 전환
        
        // 회원 정보 라벨 추가
        String[] memberInfo = {"이름", "이메일", "전화번호", "ID", "비밀번호"};
        userInfoLabels = new JLabel[memberInfo.length];
        
        for (int i = 0; i < memberInfo.length; i++) {
            JLabel label = new JLabel(memberInfo[i]);
            label.setFont(new Font("맑은 고딕", Font.BOLD, 15));
            label.setBounds(40, 80 + (i * 45), 100, 25);
            mainPanel.add(label);
            
            // 사용자 정보를 표시할 라벨 추가 - 초기값은 빈 문자열
            userInfoLabels[i] = new JLabel("");
            userInfoLabels[i].setFont(new Font("맑은 고딕", Font.PLAIN, 15));
            userInfoLabels[i].setBounds(150, 80 + (i * 45), 200, 25);
            mainPanel.add(userInfoLabels[i]);
        }

        // 구분선
        JSeparator divider = new JSeparator();
        divider.setBounds(0, 320, 380, 2);
        divider.setBackground(Color.black);
        mainPanel.add(divider);

        // 신체 정보
        JLabel bodyInfoLabel = new JLabel("신체정보");
        bodyInfoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        bodyInfoLabel.setForeground(Color.RED);
        bodyInfoLabel.setBounds(40, 350, 100, 30);
        mainPanel.add(bodyInfoLabel);

        // 수정 버튼
        editBodyBtn = new RoundedComponent(100, 40, 10, "Button", "수정", 
                Color.white, Color.white, Color.red, "맑은 고딕", Font.BOLD, 17);
        editBodyBtn.setBounds(270, 340, 100, 50);
        editBodyBtn.getButton().addActionListener(e -> mainUserPanel.showPanel("MyBody"));
        mainPanel.add(editBodyBtn);

        // 신체 정보 라벨 추가
        String[] bodyInfo = {"키", "몸무게", "골격근량", "체지방량", "체지방률", ""};
        bodyInfoLabels = new JLabel[bodyInfo.length];
        
        for (int i = 0; i < bodyInfo.length; i++) {
            JLabel label = new JLabel(bodyInfo[i]);
            label.setFont(new Font("맑은 고딕", Font.BOLD, 15));
            label.setBounds(40, 400 + (i * 45), 100, 30);
            mainPanel.add(label);
            
            // 신체 정보를 표시할 라벨 추가 - 초기값은 빈 문자열
            bodyInfoLabels[i] = new JLabel("");
            bodyInfoLabels[i].setFont(new Font("맑은 고딕", Font.PLAIN, 15));
            bodyInfoLabels[i].setBounds(150, 400 + (i * 45), 200, 30);
            mainPanel.add(bodyInfoLabels[i]);
        }
        
        // 로그아웃 버튼 추가
        logoutBtn = new RoundedComponent(120, 40, 15, "button", "로그아웃", 
                Color.BLACK, Color.BLACK, Color.WHITE, "맑은 고딕", Font.BOLD, 16);
        logoutBtn.setBounds(130, 620, 120, 40); // 패널 하단 중앙에 위치, 더 아래로 이동
        mainPanel.add(logoutBtn);

        // 로그아웃 버튼 클릭 이벤트 처리
        logoutBtn.getButton().addActionListener(e -> {
            // 확인 대화상자 표시
            int option = JOptionPane.showConfirmDialog(
                this,
                "로그아웃 하시겠습니까?",
                "로그아웃 확인",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            // 사용자가 '예'를 선택한 경우
            if (option == JOptionPane.YES_OPTION) {
                // 로그아웃 처리
                LoginManager.getInstance().logout();
                
                // 로그인 화면으로 이동
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (frame instanceof main.MainFrame) {
                    main.MainFrame mainFrame = (main.MainFrame) frame;
                    mainFrame.showPanel("login");
                }
                
                // 로그아웃 성공 메시지
                JOptionPane.showMessageDialog(
                    this,
                    "로그아웃 되었습니다.",
                    "로그아웃 성공",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        
        a = new RoundedComponent(0, 0, 10, "button", "", 
                Color.BLACK, Color.BLACK, Color.WHITE, "맑은고딕", Font.BOLD, 14);
        a.setBounds(140, 530, 100, 40);
        a.getButton().addActionListener(this);
        mainPanel.add(a);
        // 패널이 표시될 때 사용자 정보 업데이트
        updateUserInfo();
    }
    
    // 패널이 표시될 때 사용자 정보 업데이트
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            updateUserInfo();
        }
    }
    
    // 사용자 정보 업데이트
    private void updateUserInfo() {
        UserBean user = LoginManager.getInstance().getCurrentUser();
        if (user != null) {
            // 회원 정보 업데이트
            userInfoLabels[0].setText(user.getUser_name());
            userInfoLabels[1].setText(user.getUser_email());
            userInfoLabels[2].setText(user.getUser_phone());
            userInfoLabels[3].setText(user.getUser_id());
            userInfoLabels[4].setText("*****"); // 비밀번호는 보안상 표시하지 않음
            
            // 신체 정보 업데이트
            ResultSet rs = bodyInfoDAO.getLatestBodyInfo(user.getUser_id());
            try {
                if (rs != null && rs.next()) {
                    bodyInfoLabels[0].setText(rs.getFloat("height") + " cm");
                    bodyInfoLabels[1].setText(rs.getFloat("weight") + " kg");
                    bodyInfoLabels[2].setText(rs.getFloat("muscle_mass") + " kg");
                    bodyInfoLabels[3].setText(rs.getFloat("fat_mass") + " kg");
                    bodyInfoLabels[4].setText(rs.getFloat("fat_rate") + " %");
                } else {
                    // 신체 정보가 없는 경우 빈 문자열 표시
                    bodyInfoLabels[0].setText("");
                    bodyInfoLabels[1].setText("");
                    bodyInfoLabels[2].setText("");
                    bodyInfoLabels[3].setText("");
                    bodyInfoLabels[4].setText("");
                }
                
                // ResultSet 닫기
                if (rs != null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
                for (int i = 0; i < bodyInfoLabels.length; i++) {
                    bodyInfoLabels[i].setText("");
                }
            }
        } else {
            // 로그인되지 않은 경우
            for (int i = 0; i < userInfoLabels.length; i++) {
                userInfoLabels[i].setText("");
            }
            
            for (int i = 0; i < bodyInfoLabels.length; i++) {
                bodyInfoLabels[i].setText("");
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == editBodyBtn.getButton()) {
            JOptionPane.showMessageDialog(this, "신체 정보 수정 버튼 클릭됨", "알림", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
