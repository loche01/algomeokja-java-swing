package panel;

import DB.BodyInfoDAO;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import main.MainUserPanel;
import model.LoginManager;
import model.UserBean;
import ui_n_utils.RoundedComponent;

public class MyBodyPanel extends JPanel implements ActionListener {
    private MainUserPanel mainUserPanel;
    private RoundedComponent mainPanel, finishButton, backButton, a;
    private RoundedComponent[] buttons;
    private JTextField[] fields;
    private String[] units = {"cm", "kg", "kg", "kg", "%"};
    private BodyInfoDAO bodyInfoDAO;

    public MyBodyPanel(MainUserPanel mainUserPanel) {
        this.mainUserPanel = mainUserPanel;
        this.bodyInfoDAO = new BodyInfoDAO();
        setLayout(null);
        setBackground(new Color(192, 233, 147)); // 배경색 설정
        setBounds(0,40,440,736);

        // 메인 패널 생성
        mainPanel = new RoundedComponent(380, 670, 30, "panel", " ",
                new Color(192, 233, 147), Color.white, Color.black, " ", 0, 0);
        mainPanel.setBounds(21, 40, 380, 670);
        add(mainPanel); // 패널 추가

        // 회원 정보 레이블
        JLabel memberInfoLabel = new JLabel("신체정보");
        memberInfoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        memberInfoLabel.setForeground(Color.black);
        memberInfoLabel.setBounds(135, 80, 150, 30);
        mainPanel.add(memberInfoLabel);

        // 📌 뒤로가기 버튼
        backButton = new RoundedComponent(60, 60, 10, "Button", "X",
                                          Color.white, Color.white, Color.black, "맑은 고딕", Font.BOLD, 24);
        backButton.setBounds(310, 10, 60, 60);
        backButton.getButton().addActionListener(e -> mainUserPanel.showPanel("MyPage")); // 📌 MyPagePanel로 전환
        mainPanel.add(backButton);

        // 📌 신체정보 필드 라벨 및 입력 필드 배치
        String[] bodyInfo = {"키", "몸무게", "골격근량", "체지방량", "체지방률"};
        fields = new JTextField[5];
        buttons = new RoundedComponent[5];
        
        int labelStartY = 180;
        int fieldStartY = 180;
        int spacing = 65;

        for (int i = 0; i < bodyInfo.length; i++) {
            JLabel label = new JLabel(bodyInfo[i]);
            label.setFont(new Font("맑은 고딕", Font.BOLD, 17));
            label.setBounds(65, labelStartY + (i * spacing), 100, 25);
            mainPanel.add(label);

            fields[i] = new JTextField();
            fields[i].setFont(new Font("Malgun Gothic", Font.BOLD, 16));
            fields[i].setBackground(new Color(0xD9D9D9));
            fields[i].setHorizontalAlignment(JTextField.RIGHT);
            fields[i].setBorder(BorderFactory.createLineBorder(new Color(0xD9D9D9), 2));
            fields[i].setBounds(203, fieldStartY + (i * spacing), 80, 25);
            mainPanel.add(fields[i]);

            JLabel unitLabel = new JLabel(units[i]);
            unitLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
            unitLabel.setBounds(290, fieldStartY + (i * spacing) + 2, 30, 20);
            mainPanel.add(unitLabel);

            buttons[i] = new RoundedComponent(140, 35, 7, "button", " ",
                    Color.lightGray, new Color(0xD9D9D9), Color.black, "맑은 고딕", Font.BOLD, 12);
            buttons[i].setBounds(180, fieldStartY + (i * spacing), 140, 35);
            mainPanel.add(buttons[i]);
        }

        // 완료 버튼 생성 - 검은색 배경으로 설정
        finishButton = new RoundedComponent(100, 40, 10, "button", "완료",
                Color.BLACK, Color.BLACK, Color.WHITE, "맑은 고딕", Font.BOLD, 14);
        finishButton.setBounds(140, 530, 100, 40);
        mainPanel.add(finishButton);
        finishButton.getButton().addActionListener(this);
        
        a = new RoundedComponent(0, 0, 10, "button", "", 
        Color.BLACK, Color.BLACK, Color.WHITE, "맑은고딕", Font.BOLD, 14);
        a.setBounds(140, 530, 100, 40);
        a.getButton().addActionListener(this);
        mainPanel.add(a);

        // 패널이 표시될 때 신체 정보 로드
        loadBodyInfo();
    }
    
    // 패널이 표시될 때 신체 정보 로드
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            loadBodyInfo();
        }
    }
    
    // 신체 정보 로드
    private void loadBodyInfo() {
        UserBean user = LoginManager.getInstance().getCurrentUser();
        if (user != null) {
            try {
                ResultSet rs = bodyInfoDAO.getLatestBodyInfo(user.getUser_id());
                if (rs != null && rs.next()) {
                    // 데이터베이스에서 가져온 정보로 필드 설정
                    fields[0].setText(String.valueOf(rs.getFloat("height")));
                    fields[1].setText(String.valueOf(rs.getFloat("weight")));
                    fields[2].setText(String.valueOf(rs.getFloat("muscle_mass")));
                    fields[3].setText(String.valueOf(rs.getFloat("fat_mass")));
                    fields[4].setText(String.valueOf(rs.getFloat("fat_rate")));
                } else {
                    // 정보가 없는 경우 기본값 설정
                    fields[0].setText("175");
                    fields[1].setText("70");
                    fields[2].setText("30");
                    fields[3].setText("15");
                    fields[4].setText("21");
                }
                
                // ResultSet 닫기
                if (rs != null) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "신체 정보를 불러오는 중 오류가 발생했습니다.", "데이터베이스 오류", JOptionPane.ERROR_MESSAGE);
                
                // 오류 발생 시 기본값 설정
                fields[0].setText("175");
                fields[1].setText("70");
                fields[2].setText("30");
                fields[3].setText("15");
                fields[4].setText("21");
            }
        }
    }
    
    // 신체 정보 업데이트
    private void updateBodyInfo() {
        UserBean user = LoginManager.getInstance().getCurrentUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "로그인이 필요합니다.", "로그인 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 입력값 검증
        for (int i = 0; i < fields.length; i++) {
            String value = fields[i].getText().trim();
            if (value.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 필드를 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                float numValue = Float.parseFloat(value);
                if (numValue <= 0) {
                    JOptionPane.showMessageDialog(this, "유효한 값을 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "숫자만 입력해주세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        try {
            // 신체 정보 저장
            float height = Float.parseFloat(fields[0].getText().trim());
            float weight = Float.parseFloat(fields[1].getText().trim());
            float muscleMass = Float.parseFloat(fields[2].getText().trim());
            float fatMass = Float.parseFloat(fields[3].getText().trim());
            float fatRate = Float.parseFloat(fields[4].getText().trim());
            
            boolean success = bodyInfoDAO.saveBodyInfo(
                user.getUser_id(), 
                height, 
                weight, 
                fatRate, 
                fatMass, 
                muscleMass
            );
            
            if (success) {
                JOptionPane.showMessageDialog(this, "신체 정보가 성공적으로 업데이트되었습니다.", "업데이트 성공", JOptionPane.INFORMATION_MESSAGE);
                mainUserPanel.showPanel("MyPage");
            } else {
                JOptionPane.showMessageDialog(this, "신체 정보 업데이트에 실패했습니다.", "업데이트 실패", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "신체 정보 저장 중 오류가 발생했습니다: " + e.getMessage(), "데이터베이스 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == finishButton.getButton()) {
            updateBodyInfo();
        }
    }
}
