package panel;

import DB.GoalDAO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.Map;
import javax.swing.*;
import main.MainUserPanel;
import model.UserGoal;
import ui_n_utils.UserSessionManager;

public class HomeTargetPanel extends JPanel {  
    private MainUserPanel mainUserPanel;
    private int startWeight = 0;
    private int targetWeight = 0;
    private GoalDAO goalDAO;
    private int targetDuration = 0;
    
    private JLabel nowWeightLabel, weightChangeLabel, targetChangeLabel, durationLabel;
    private GraphPanel graphPanel;

    public HomeTargetPanel(MainUserPanel mainUserPanel) {
        this.mainUserPanel = mainUserPanel;
        this.goalDAO = new GoalDAO();

        setBounds(0, 40, 440, 700);
        setBackground(new Color(192, 233, 147));
        setLayout(null);

        initializeUIElements();

        SwingUtilities.invokeLater(() -> {
            repaint();
            loadUserTargetData();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(192, 233, 147));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private void initializeUIElements() {
        JLabel label = new JLabel("목표 달성");
        label.setFont(new Font("Inter", Font.BOLD, 42));
        label.setBounds(124, 45, 192, 40);
        label.setForeground(new Color(0x609056));
        add(label);

        // 체중계 이미지
        ImageIcon icon = new ImageIcon("C:\\Users\\dita_806\\Desktop\\project8\\src\\images\\target.png");
        JLabel imageLabel = new JLabel(icon);
        imageLabel.setBounds(65, 115, 300, 200);
        add(imageLabel);

        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mainUserPanel != null) {
                    mainUserPanel.showPanel("MymeGoal");
                }
            }
        });

        nowWeightLabel = new JLabel("", SwingConstants.CENTER);
        nowWeightLabel.setFont(new Font("Inter", Font.BOLD, 20));
        nowWeightLabel.setBounds(50, 320, 340, 50);
        nowWeightLabel.setForeground(Color.white);
        add(nowWeightLabel);

        graphPanel = new GraphPanel();
        graphPanel.setBounds(50, 380, 350, 80);
        add(graphPanel);

        weightChangeLabel = new JLabel("", SwingConstants.CENTER);
        weightChangeLabel.setFont(new Font("Inter", Font.BOLD, 20));
        weightChangeLabel.setBounds(50, 470, 340, 50);
        weightChangeLabel.setForeground(new Color(0x404040));
        add(weightChangeLabel);

        targetChangeLabel = new JLabel("", SwingConstants.CENTER);
        targetChangeLabel.setFont(new Font("Inter", Font.BOLD, 20));
        targetChangeLabel.setBounds(50, 520, 340, 50);
        targetChangeLabel.setForeground(new Color(0x404040));
        add(targetChangeLabel);

        durationLabel = new JLabel("", SwingConstants.CENTER);
        durationLabel.setFont(new Font("Inter", Font.BOLD, 20));
        durationLabel.setBounds(50, 570, 340, 50);
        durationLabel.setForeground(new Color(0x404040));
        add(durationLabel);
    }

    public void refreshData() {
        SwingUtilities.invokeLater(this::loadUserTargetData);
    }

    public void loadUserTargetData() {
        if (UserSessionManager.getInstance().getCurrentUser() == null) {
            System.err.println("❌ [UserSessionManager] 현재 로그인한 사용자가 없습니다.");
            return;
        }

        String userId = UserSessionManager.getInstance().getCurrentUser().getUser_id();
        System.out.println("✅ 목표 데이터 로드 시도: userId=" + userId);

        UserGoal goal = goalDAO.getUserGoal(userId);

        if (goal != null) {
            this.startWeight = goal.getStartWeight().intValue();
            this.targetWeight = goal.getTargetWeight().intValue();
            this.targetDuration = goal.getTargetDuration();
            
            System.out.println("✅ 목표 데이터 로드 성공! 시작 체중: " + startWeight + "kg, 목표 체중: " + targetWeight + "kg, 기간: " + targetDuration + "일");

            SwingUtilities.invokeLater(this::updateTargetUI);
        } else {
            System.err.println("❌ 목표 데이터 로드 실패! userId=" + userId);
            SwingUtilities.invokeLater(this::showNoDataMessage);
        }
    }

    private void showNoDataMessage() {
        if (nowWeightLabel != null) {
            nowWeightLabel.setText("<html><b style='font-size:20px;color:red;'>❌ 목표 데이터 없음</b></html>");
            weightChangeLabel.setText("");
            targetChangeLabel.setText("");
            durationLabel.setText("");
            graphPanel.setData(0, 0, 0);
            graphPanel.repaint();
        }
    }

    public void updateTargetUI() {
        if (nowWeightLabel == null || weightChangeLabel == null || targetChangeLabel == null) {
            System.err.println("❌ [오류] UI 요소가 초기화되지 않음! (updateUI 중단)");
            return;
        }

        String userId = UserSessionManager.getInstance().getCurrentUser().getUser_id();

        // 현재 체중 표시
        BigDecimal latestWeight = goalDAO.getLatestWeight(userId);
        int currentWeight = latestWeight != null ? latestWeight.intValue() : startWeight;
        
        nowWeightLabel.setText("<html>현재 체중은 <b style='font-size:20px;color:#002D62;'>" + currentWeight + "kg</b> 입니다.</html>");
        
        // 목표 기간 진행률은 기존 계산 흐름을 유지
        Map<String, Object> weightChangeData = goalDAO.calculateWeightChangeFromMealLogs(userId);
        double timeProgressRatio = (double) weightChangeData.get("timeProgressRatio");
        
        boolean isWeightLossGoal = targetWeight < startWeight;
        boolean isWeightGainGoal = targetWeight > startWeight;
        double weightChange = 0.0;
        double remainingWeight = 0.0;
        double progressPercent = 0.0;
        String changeDirection = isWeightGainGoal ? "증량" : "감량";

        if (isWeightLossGoal) {
            weightChange = startWeight - currentWeight;
            remainingWeight = currentWeight - targetWeight;
            int targetChange = startWeight - targetWeight;
            if (targetChange != 0) {
                progressPercent = (weightChange / targetChange) * 100.0;
            }
        } else if (isWeightGainGoal) {
            weightChange = currentWeight - startWeight;
            remainingWeight = targetWeight - currentWeight;
            int targetChange = targetWeight - startWeight;
            if (targetChange != 0) {
                progressPercent = (weightChange / targetChange) * 100.0;
            }
        }

        weightChange = Math.max(0.0, weightChange);
        remainingWeight = Math.max(0.0, remainingWeight);
        progressPercent = Math.max(0.0, Math.min(100.0, progressPercent));

        // 체중 변화 표시
        weightChangeLabel.setText("<html>현재까지 <b style='font-size:20px;color:#002D62;'>" +
                                 String.format("%.1f", weightChange) +
                                 "kg</b>의 " + changeDirection + "이 있습니다.</html>");
        
        // 목표까지 남은 체중 표시
        targetChangeLabel.setText("<html>목표 체중까지 <b style='font-size:20px; color:red;'>" + 
                                 String.format("%.1f", remainingWeight) + "kg</b> 남았습니다.</html>");
        
        // 목표 기간 표시
        durationLabel.setText("<html>목표 기간: <b style='font-size:20px;color:#002D62;'>" + 
                             targetDuration + "일</b> (진행률: " + 
                             String.format("%.1f", timeProgressRatio) + "%)</html>");
        
        // 그래프 데이터 갱신
        graphPanel.setData(startWeight, targetWeight, progressPercent);
        graphPanel.repaint();
    }

    class GraphPanel extends JPanel {
        private int startWeight, targetWeight;
        private double progressPercent;

        public GraphPanel() {
            setOpaque(false);
        }

        public void setData(int start, int target, double progress) {
            this.startWeight = start;
            this.targetWeight = target;
            this.progressPercent = progress;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(192, 233, 147));
            g2.fillRect(0, 0, getWidth(), getHeight());

            int width = getWidth();
            int height = getHeight();
            int barHeight = 30;
            int arc = 30;

            // 배경 바
            g2.setColor(new Color(0xD9D9D9));
            g2.fillRoundRect(0, (height - barHeight) / 2, width, barHeight, arc, arc);

            // 진행 바 (진행률에 따라 너비 조정)
            int progressWidth = (int) (width * (progressPercent / 100.0));
            if (progressWidth > 0) {
                g2.setColor(new Color(0x609056));
                g2.fillRoundRect(0, (height - barHeight) / 2, progressWidth, barHeight, arc, arc);
            }
            
            // 시작 체중과 목표 체중 표시
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Inter", Font.BOLD, 14));
            g2.drawString(startWeight + "kg", 10, (height - barHeight) / 2 - 5);
            g2.drawString(targetWeight + "kg", width - 50, (height - barHeight) / 2 - 5);
            
            // 진행률 표시
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Inter", Font.BOLD, 16));
            String progressText = String.format("%.1f%%", progressPercent);
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(progressText);
            g2.drawString(progressText, (width - textWidth) / 2, (height + barHeight) / 2 + 5);
        }
    }
}
