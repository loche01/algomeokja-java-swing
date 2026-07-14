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
import ui_n_utils.AppTheme;
import ui_n_utils.RoundedComponent;

public class MyBodyPanel extends JPanel implements ActionListener {
    private MainUserPanel mainUserPanel;
    private JPanel mainPanel;
    private RoundedComponent finishButton, backButton;
    private JTextField[] fields;
    private String[] units = {"cm", "kg", "kg", "kg", "%"};
    private BodyInfoDAO bodyInfoDAO;

    public MyBodyPanel(MainUserPanel mainUserPanel) {
        this.mainUserPanel = mainUserPanel;
        this.bodyInfoDAO = new BodyInfoDAO();
        setLayout(null);
        setBackground(AppTheme.BACKGROUND);
        setBounds(0,40,440,736);

        // 메인 패널 생성
        mainPanel = new JPanel(null);
        AppTheme.styleCard(mainPanel);
        mainPanel.setBounds(AppTheme.HORIZONTAL_MARGIN, 15, AppTheme.CARD_WIDTH, 448);
        add(mainPanel); // 패널 추가

        // 회원 정보 레이블
        JLabel memberInfoLabel = new JLabel("신체정보 수정");
        AppTheme.styleScreenTitle(memberInfoLabel);
        memberInfoLabel.setBounds(24, 18, 200, 34);
        mainPanel.add(memberInfoLabel);

        // 📌 뒤로가기 버튼
        backButton = new RoundedComponent(110, 36, 10, "Button", "내 정보로",
                AppTheme.PRIMARY, AppTheme.CARD, AppTheme.PRIMARY_DARK, Font.SANS_SERIF, Font.BOLD, 13);
        backButton.setBounds(240, 20, 110, 36);
        backButton.getButton().addActionListener(e -> mainUserPanel.showPanel("MyPage")); // 📌 MyPagePanel로 전환
        mainPanel.add(backButton);

        JLabel descriptionLabel = new JLabel("현재 신체 측정값을 입력해주세요.");
        AppTheme.styleScreenDescription(descriptionLabel);
        descriptionLabel.setBounds(24, 58, 210, 24);
        mainPanel.add(descriptionLabel);

        // 📌 신체정보 필드 라벨 및 입력 필드 배치
        String[] bodyInfo = {"키", "몸무게", "골격근량", "체지방량", "체지방률"};
        fields = new JTextField[5];

        int fieldStartY = 100;
        int spacing = 54;

        for (int i = 0; i < bodyInfo.length; i++) {
            JLabel label = new JLabel(bodyInfo[i]);
            label.setFont(AppTheme.BODY_BOLD_FONT);
            label.setForeground(AppTheme.TEXT_SECONDARY);
            label.setBounds(24, fieldStartY + (i * spacing) + 7, 110, 24);
            mainPanel.add(label);

            fields[i] = new JTextField();
            AppTheme.styleInputField(fields[i]);
            fields[i].setHorizontalAlignment(JTextField.RIGHT);
            fields[i].setBounds(145, fieldStartY + (i * spacing), 175, AppTheme.INPUT_HEIGHT);
            mainPanel.add(fields[i]);

            JLabel unitLabel = new JLabel(units[i]);
            unitLabel.setFont(AppTheme.BODY_FONT);
            unitLabel.setForeground(AppTheme.TEXT_SECONDARY);
            unitLabel.setBounds(326, fieldStartY + (i * spacing) + 9, 30, 20);
            mainPanel.add(unitLabel);
        }

        finishButton = new RoundedComponent(205, 44, 10, "button", "변경사항 저장",
                AppTheme.PRIMARY_DARK, AppTheme.PRIMARY_DARK, Color.WHITE, Font.SANS_SERIF, Font.BOLD, 14);
        finishButton.setBounds(145, 380, 205, 44);
        mainPanel.add(finishButton);
        finishButton.getButton().addActionListener(this);

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
