package panel;

import DB.BodyInfoDAO;
import DB.GoalDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import javax.swing.*;
import main.MainUserPanel;
import model.UserGoal;
import ui_n_utils.UserSessionManager; 

public class MymeGoalPanel extends JPanel implements ActionListener {
    private JPanel mainPanel;
    private JButton finishButton;
    private JTextField startWeightField, targetWeightField, durationField;
    private MainUserPanel mainUserPanel;
    private GoalDAO goalDAO;
    private BodyInfoDAO bodyInfoDAO;

    public MymeGoalPanel(MainUserPanel mainUserPanel) {
        this.mainUserPanel = mainUserPanel;
        this.goalDAO = new GoalDAO();
        this.bodyInfoDAO = new BodyInfoDAO();

        setLayout(null);
        setBackground(new Color(192, 233, 147));
        setBounds(0, 40, 440, 736);

        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBounds(21, 40, 380, 670);
        add(mainPanel);

        JLabel memberInfoLabel = new JLabel("목표관리");
        memberInfoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        memberInfoLabel.setBounds(135, 40, 200, 30);
        mainPanel.add(memberInfoLabel);

        // 시작 체중 필드
        mainPanel.add(createLabel("시작 체중(kg): ", 60, 100));
        startWeightField = createTextField(160, 100);
        mainPanel.add(startWeightField);

        // 목표 체중 필드
        mainPanel.add(createLabel("목표 체중(kg): ", 60, 150));
        targetWeightField = createTextField(160, 150);
        mainPanel.add(targetWeightField);

        // 기간 설정 필드 (일수)
        mainPanel.add(createLabel("목표 기간(일): ", 60, 200));
        durationField = createTextField(160, 200);
        mainPanel.add(durationField);

        // 완료 버튼
        finishButton = new JButton("완료");
        finishButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        finishButton.setBackground(Color.BLACK);
        finishButton.setForeground(Color.WHITE);
        finishButton.setBounds(140, 260, 100, 40);
        mainPanel.add(finishButton);
        finishButton.addActionListener(this);
        
        // 패널이 표시될 때마다 최신 체중 정보 로드
        loadLatestWeight();
    }

    private JTextField createTextField(int x, int y) {
        JTextField field = new JTextField();
        field.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        field.setBackground(new Color(0xD9D9D9));
        field.setHorizontalAlignment(JTextField.RIGHT);
        field.setBorder(BorderFactory.createLineBorder(new Color(0xD9D9D9), 2));
        field.setBounds(200, y, 120, 30);
        return field;
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        label.setBounds(x, y, 180, 30);
        return label;
    }
    
    // 최신 체중 정보 로드
    public void loadLatestWeight() {
        if (UserSessionManager.getInstance().getCurrentUser() == null) {
            return;
        }
        
        String userId = UserSessionManager.getInstance().getCurrentUser().getUser_id();
        
        // 기존 목표 정보 로드
        UserGoal existingGoal = goalDAO.getUserGoal(userId);
        if (existingGoal != null) {
            startWeightField.setText(existingGoal.getStartWeight().toString());
            targetWeightField.setText(existingGoal.getTargetWeight().toString());
            durationField.setText(String.valueOf(existingGoal.getTargetDuration()));
            return;
        }
        
        // 기존 목표가 없는 경우 bodyinfo에서 최신 체중 가져오기
        BigDecimal latestWeight = goalDAO.getLatestWeight(userId);
        if (latestWeight != null) {
            startWeightField.setText(latestWeight.toString());
        } else {
            startWeightField.setText("");
        }
        
        targetWeightField.setText("");
        durationField.setText("");
    }

    private UserGoal getUserInput() {
        try {
            if (UserSessionManager.getInstance().getCurrentUser() == null) {
                JOptionPane.showMessageDialog(this, "로그인이 필요합니다!", "오류", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            String userId = UserSessionManager.getInstance().getCurrentUser().getUser_id();
            
            // 필수 입력 필드 검증
            if (startWeightField.getText().trim().isEmpty() || 
                targetWeightField.getText().trim().isEmpty() || 
                durationField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 필드를 입력해주세요!", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            BigDecimal startWeight = new BigDecimal(startWeightField.getText().trim());
            BigDecimal targetWeight = new BigDecimal(targetWeightField.getText().trim());
            int targetDuration = Integer.parseInt(durationField.getText().trim());

            if (targetDuration <= 0) {
                JOptionPane.showMessageDialog(this, "목표 기간은 1일 이상이어야 합니다!", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            return new UserGoal(userId, startWeight, targetWeight, targetDuration);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "숫자 입력을 확인하세요!", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void clearFields() {
        startWeightField.setText("");
        targetWeightField.setText("");
        durationField.setText("");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == finishButton) {
            UserGoal goal = getUserInput();
            if (goal != null) {
                System.out.println("✅ 목표 데이터 저장 시도: " + 
                                  "시작 체중=" + goal.getStartWeight() + 
                                  ", 목표 체중=" + goal.getTargetWeight() + 
                                  ", 기간=" + goal.getTargetDuration() + "일");
                
                boolean success = goalDAO.saveOrUpdateGoal(goal);
                
                if (success) {
                    System.out.println("✅ 목표 데이터 저장 성공!");
                    JOptionPane.showMessageDialog(this, "목표가 성공적으로 저장되었습니다.", "저장 완료", JOptionPane.INFORMATION_MESSAGE);
                    mainUserPanel.getHomeTargetPanel().loadUserTargetData();
                    mainUserPanel.showPanel("HomeTarget");
                    clearFields();
                } else {
                    System.err.println("❌ 목표 데이터 저장 실패!");
                    JOptionPane.showMessageDialog(this, "목표 저장에 실패했습니다. 다시 시도해주세요.", "저장 실패", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    // 패널이 표시될 때마다 호출되도록 설정
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            loadLatestWeight();
        }
    }
}
