package panel;

import DB.ExerciseLogDAO;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import main.MainUserPanel;
import model.ExerciseBean;
import model.LoginManager;
import ui_n_utils.RoundedComponent;

public class ExerciseCaloriePanel extends JPanel {
    private JTextField timeField;
    private JTextField weightField;
    private JLabel calorieValue;
    private RoundedComponent timePanel, weightPanel;
    private RoundedComponent mainPanel, BackButton, finishButton;
    private JLabel workoutTitle;
    private JLabel workoutType;
    private double exerciseMET = 6.0; // 기본 MET 값
    private MainUserPanel mainUserPanel; // MainUserPanel 참조
    private int currentExerciseCode = 1; // 기본 운동 코드
    private String currentExerciseName; // 현재 선택된 운동명
    private String currentUserId; // 현재 로그인된 사용자 ID
    private ExerciseLogDAO exerciseLogDAO;

    public ExerciseCaloriePanel(MainUserPanel mainUserPanel) {
        setLayout(null);
        setBackground(new Color(192, 233, 147));
        setPreferredSize(new Dimension(440, 856)); // 네비게이션 바 제외 크기
        this.mainUserPanel = mainUserPanel;
        // 현재 로그인된 사용자 ID 가져오기
        this.currentUserId = LoginManager.getInstance().getUserId();

        // ExerciseLogDAO 초기화
        exerciseLogDAO = new ExerciseLogDAO();

        // ── 메인 패널 구성 ──
        mainPanel = new RoundedComponent(380, 600, 30, "panel", " ",
                new Color(192, 233, 147), Color.white, Color.black, " ", 0, 0);
        mainPanel.setBounds(20, 110, 380, 600);
        mainPanel.setLayout(null);
        mainPanel.setEnabled(false);

        // 뒤로가기 버튼
        BackButton = new RoundedComponent(40, 40, 10, "button", "<",
                Color.white, Color.white, Color.black, "Inter", Font.BOLD, 25);
        BackButton.setBounds(10, 10, 40, 40);
        BackButton.getButton().addActionListener(e -> mainUserPanel.goToPreviousPanel());
        mainPanel.add(BackButton);

        // 운동 제목과 타입
        workoutTitle = new JLabel("");
        workoutTitle.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
        workoutTitle.setBounds(30, 60, 300, 30);
        mainPanel.add(workoutTitle);

        workoutType = new JLabel("");
        workoutType.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        workoutType.setForeground(Color.gray);
        workoutType.setBounds(30, 90, 200, 20);
        mainPanel.add(workoutType);

        // ── 운동 시간 입력 패널 ──
        timePanel = new RoundedComponent(330, 50, 20, "panel", " ",
                Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.black, " ", 0, 0);
        timePanel.setBounds(25, 150, 330, 50);
        timePanel.setLayout(null);
        timePanel.setEnabled(false);

        JLabel timeLabel = new JLabel("운동 시간");
        timeLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        timeLabel.setBounds(10, 15, 80, 20);
        timePanel.add(timeLabel);

        timeField = new JTextField();
        timeField.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        timeField.setBackground(Color.LIGHT_GRAY);
        timeField.setHorizontalAlignment(JTextField.RIGHT);
        timeField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        timeField.setBounds(215, 12, 80, 25);
        timeField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculateCalories(); }
            public void removeUpdate(DocumentEvent e) { calculateCalories(); }
            public void changedUpdate(DocumentEvent e) { calculateCalories(); }
        });
        timePanel.add(timeField);

        JLabel minLabel = new JLabel("분");
        minLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        minLabel.setBounds(300, 15, 20, 20);
        timePanel.add(minLabel);
        mainPanel.add(timePanel);

        // ── 현재 체중 입력 패널 ──
        weightPanel = new RoundedComponent(330, 50, 20, "panel", " ",
                Color.LIGHT_GRAY, Color.LIGHT_GRAY, Color.black, " ", 0, 0);
        weightPanel.setBounds(25, 220, 330, 50);
        weightPanel.setLayout(null);
        weightPanel.setEnabled(false);

        JLabel weightLabel = new JLabel("현재 체중");
        weightLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        weightLabel.setBounds(10, 15, 80, 20);
        weightPanel.add(weightLabel);

        weightField = new JTextField();
        weightField.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        weightField.setBackground(Color.LIGHT_GRAY);
        weightField.setHorizontalAlignment(JTextField.RIGHT);
        weightField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
        weightField.setBounds(215, 12, 80, 25);
        weightField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculateCalories(); }
            public void removeUpdate(DocumentEvent e) { calculateCalories(); }
            public void changedUpdate(DocumentEvent e) { calculateCalories(); }
        });
        weightPanel.add(weightField);

        JLabel kgLabel = new JLabel("kg");
        kgLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        kgLabel.setBounds(300, 15, 20, 20);
        weightPanel.add(kgLabel);
        mainPanel.add(weightPanel);

        // ── 예상 소모 칼로리 표시 ──
        JLabel calorieLabel = new JLabel("예상 소모 칼로리");
        calorieLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 17));
        calorieLabel.setBounds(50, 320, 200, 30);
        mainPanel.add(calorieLabel);

        calorieValue = new JLabel("0 Kcal");
        calorieValue.setFont(new Font("Malgun Gothic", Font.BOLD, 20));
        calorieValue.setBounds(250, 320, 100, 30);
        mainPanel.add(calorieValue);

        // ── 저장 버튼 ──
        finishButton = new RoundedComponent(100, 40, 10, "button", "저장",
                Color.BLACK, Color.BLACK, Color.WHITE, "Inter", Font.BOLD, 14);
        finishButton.setBounds(140, 420, 100, 40);
        JButton finishBtn = finishButton.getButton();
        if (finishBtn != null) {
            finishBtn.addActionListener(e -> {
                if (validateInputs() && saveExerciseLog()) {//->The method validateInputs() is undefined for the type ExerciseCaloriePanel
                    JOptionPane.showMessageDialog(ExerciseCaloriePanel.this, "운동이 기록되었습니다!", "성공", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(ExerciseCaloriePanel.this, "저장 중 오류 발생", "오류", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
        mainPanel.add(finishButton);
        add(mainPanel);
    }

    public void updateExerciseInfo(ExerciseBean exercise) {
        if (exercise == null) {
            return;
        }

        this.currentExerciseCode = exercise.getExerciseCode();
        this.currentExerciseName = exercise.getExerciseName();
        this.exerciseMET = exercise.getExerciseMET();

        workoutTitle.setText(exercise.getExerciseName());
        workoutType.setText(exercise.getExerciseType());
        resetInputFields();
    }

    private void resetInputFields() {
        timeField.setText("");
        weightField.setText("");
        calorieValue.setText("0 Kcal");
    }

    private boolean validateInputs() {
        if (timeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "운동 시간을 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            timeField.requestFocus();
            return false;
        }
        if (weightField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "체중을 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            weightField.requestFocus();
            return false;
        }
        try {
            int time = Integer.parseInt(timeField.getText().trim());
            if (time <= 0) {
                JOptionPane.showMessageDialog(this, "운동 시간은 0보다 커야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                timeField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "운동 시간은 숫자로 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            timeField.requestFocus();
            return false;
        }
        try {
            double weight = Double.parseDouble(weightField.getText().trim());
            if (weight <= 0) {
                JOptionPane.showMessageDialog(this, "체중은 0보다 커야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                weightField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "체중은 숫자로 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            weightField.requestFocus();
            return false;
        }
        return true;
    }

    private boolean saveExerciseLog() {
        // 로그인된 사용자 ID 다시 가져오기 시도
        if (currentUserId == null || currentUserId.isEmpty()) {
            this.currentUserId = LoginManager.getInstance().getUserId();
        }

        if (currentUserId == null || currentUserId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "로그인이 필요합니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            int runtime = Integer.parseInt(timeField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());
            String calorieText = calorieValue.getText().replaceAll("[^0-9.]", "");
            double kcal = Double.parseDouble(calorieText);

            return exerciseLogDAO.saveExerciseLog(currentExerciseCode, currentExerciseName, currentUserId, runtime, weight, kcal);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    private void calculateCalories() {
        try {
            if (!timeField.getText().isEmpty() && !weightField.getText().isEmpty()) {
                double time = Double.parseDouble(timeField.getText()) / 60.0;
                double weight = Double.parseDouble(weightField.getText());
                double calories = exerciseMET * weight * time * 1.05;
                calorieValue.setText(String.format("%.1f Kcal", calories));
            }
        } catch (NumberFormatException e) {
            calorieValue.setText("0 Kcal");
        }
    }
}
