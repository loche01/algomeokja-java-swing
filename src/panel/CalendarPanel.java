package panel;

import DB.ExerciseLogDAO;
import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import model.LoginManager;

public class CalendarPanel extends JPanel {
    private final ExerciseLogDAO exerciseLogDAO;
    private final JPanel recordListPanel;

    public CalendarPanel() {
        exerciseLogDAO = new ExerciseLogDAO();

        setBounds(0, 155, 440, 700);
        setBackground(new Color(0xD9D9D9));
        setLayout(null);

        JLabel label = new JLabel("오늘 운동 기록");
        label.setFont(new Font("Malgun Gothic", Font.BOLD, 22));
        label.setBounds(30, 30, 250, 40);
        add(label);

        recordListPanel = new JPanel();
        recordListPanel.setLayout(new BoxLayout(recordListPanel, BoxLayout.Y_AXIS));
        recordListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(recordListPanel);
        scrollPane.setBounds(25, 90, 390, 560);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane);

        loadTodayExerciseLogs();
    }

    public void refresh() {
        loadTodayExerciseLogs();
    }

    public void loadTodayExerciseLogs() {
        recordListPanel.removeAll();

        String userId = LoginManager.getInstance().getUserId();
        if (userId == null || userId.trim().isEmpty()) {
            addMessageLabel("로그인이 필요합니다.");
            refreshRecordList();
            return;
        }

        List<Map<String, Object>> logs = exerciseLogDAO.getTodayExerciseLogs(userId);
        if (logs.isEmpty()) {
            addMessageLabel("오늘 저장된 운동 기록이 없습니다.");
        } else {
            for (Map<String, Object> log : logs) {
                recordListPanel.add(createRecordPanel(log));
                recordListPanel.add(Box.createVerticalStrut(8));
            }
        }

        refreshRecordList();
    }

    private JPanel createRecordPanel(Map<String, Object> log) {
        JPanel panel = new JPanel(null);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(370, 96));
        panel.setPreferredSize(new Dimension(370, 96));
        panel.setBackground(new Color(0xF5F5F5));
        panel.setBorder(BorderFactory.createLineBorder(new Color(0xDDDDDD)));

        String exerciseName = (String) log.get("exercise_name");
        if (exerciseName == null || exerciseName.trim().isEmpty()) {
            exerciseName = "운동명 없음";
        }

        Object caloriesValue = log.get("exercise_calories");
        int calories = caloriesValue instanceof Number ? ((Number) caloriesValue).intValue() : 0;

        JLabel nameLabel = new JLabel(exerciseName);
        nameLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        nameLabel.setBounds(16, 10, 330, 24);
        panel.add(nameLabel);

        JLabel calorieLabel = new JLabel(calories + " Kcal");
        calorieLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        calorieLabel.setBounds(16, 40, 120, 22);
        panel.add(calorieLabel);

        JLabel timeLabel = new JLabel("저장 시간: " + formatExerciseTime(log.get("exercise_date")));
        timeLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 13));
        timeLabel.setForeground(Color.DARK_GRAY);
        timeLabel.setBounds(16, 66, 220, 20);
        panel.add(timeLabel);

        return panel;
    }

    private String formatExerciseTime(Object value) {
        if (value instanceof Timestamp) {
            return new SimpleDateFormat("HH:mm").format((Timestamp) value);
        }
        if (value instanceof Date) {
            return new SimpleDateFormat("HH:mm").format((Date) value);
        }
        return "";
    }

    private void addMessageLabel(String message) {
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        messageLabel.setMaximumSize(new Dimension(360, 80));
        messageLabel.setPreferredSize(new Dimension(360, 80));
        recordListPanel.add(messageLabel);
    }

    private void refreshRecordList() {
        recordListPanel.revalidate();
        recordListPanel.repaint();
    }
}
