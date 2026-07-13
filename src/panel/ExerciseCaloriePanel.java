package panel;

import DB.ExerciseLogDAO;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import main.MainUserPanel;
import model.ExerciseBean;
import model.LoginManager;
import ui_n_utils.AppTheme;
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
        setBackground(AppTheme.BACKGROUND);
        setPreferredSize(new Dimension(440, 856)); // 네비게이션 바 제외 크기
        this.mainUserPanel = mainUserPanel;
        // 현재 로그인된 사용자 ID 가져오기
        this.currentUserId = LoginManager.getInstance().getUserId();

        // ExerciseLogDAO 초기화
        exerciseLogDAO = new ExerciseLogDAO();

        // ── 메인 패널 구성 ──
        mainPanel = new RoundedComponent(380, 570, 20, "panel", " ",
                AppTheme.BORDER, AppTheme.CARD, AppTheme.TEXT, " ", 0, 0);
        mainPanel.setBounds(AppTheme.HORIZONTAL_MARGIN, 20, AppTheme.CARD_WIDTH, 570);
        mainPanel.setLayout(null);
        mainPanel.setEnabled(false);

        // 뒤로가기 버튼
        BackButton = new RoundedComponent(104, 38, 10, "button", "운동 목록",
                AppTheme.PRIMARY, AppTheme.CARD, AppTheme.PRIMARY_DARK, Font.SANS_SERIF, Font.BOLD, 13);
        BackButton.setBounds(20, 18, 104, 38);
        BackButton.getButton().addActionListener(e -> mainUserPanel.goToPreviousPanel());
        mainPanel.add(BackButton);

        // 운동 제목과 타입
        workoutTitle = new JLabel("");
        workoutTitle.setFont(AppTheme.TITLE_FONT);
        workoutTitle.setForeground(AppTheme.TEXT);
        workoutTitle.setBounds(24, 74, 332, 34);
        mainPanel.add(workoutTitle);

        workoutType = new JLabel("");
        workoutType.setFont(AppTheme.BODY_FONT);
        workoutType.setForeground(AppTheme.TEXT_SECONDARY);
        workoutType.setBounds(24, 108, 332, 24);
        mainPanel.add(workoutType);

        // ── 운동 시간 입력 패널 ──
        timePanel = new RoundedComponent(330, 50, 20, "panel", " ",
                AppTheme.BORDER, AppTheme.INPUT_BACKGROUND, AppTheme.TEXT, " ", 0, 0);
        timePanel.setBounds(25, 150, 330, 50);
        timePanel.setLayout(null);
        timePanel.setEnabled(false);

        JLabel timeLabel = new JLabel("운동 시간");
        timeLabel.setFont(AppTheme.BODY_BOLD_FONT);
        timeLabel.setForeground(AppTheme.TEXT);
        timeLabel.setBounds(10, 15, 80, 20);
        timePanel.add(timeLabel);

        timeField = new JTextField();
        AppTheme.styleInputField(timeField);
        timeField.setHorizontalAlignment(JTextField.RIGHT);
        timeField.setBounds(205, 6, 90, 38);
        timeField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculateCalories(); }
            public void removeUpdate(DocumentEvent e) { calculateCalories(); }
            public void changedUpdate(DocumentEvent e) { calculateCalories(); }
        });
        timePanel.add(timeField);

        JLabel minLabel = new JLabel("분");
        minLabel.setFont(AppTheme.BODY_FONT);
        minLabel.setForeground(AppTheme.TEXT_SECONDARY);
        minLabel.setBounds(300, 15, 20, 20);
        timePanel.add(minLabel);
        mainPanel.add(timePanel);

        // ── 현재 체중 입력 패널 ──
        weightPanel = new RoundedComponent(330, 50, 20, "panel", " ",
                AppTheme.BORDER, AppTheme.INPUT_BACKGROUND, AppTheme.TEXT, " ", 0, 0);
        weightPanel.setBounds(25, 220, 330, 50);
        weightPanel.setLayout(null);
        weightPanel.setEnabled(false);

        JLabel weightLabel = new JLabel("현재 체중");
        weightLabel.setFont(AppTheme.BODY_BOLD_FONT);
        weightLabel.setForeground(AppTheme.TEXT);
        weightLabel.setBounds(10, 15, 80, 20);
        weightPanel.add(weightLabel);

        weightField = new JTextField();
        AppTheme.styleInputField(weightField);
        weightField.setHorizontalAlignment(JTextField.RIGHT);
        weightField.setBounds(205, 6, 90, 38);
        weightField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { calculateCalories(); }
            public void removeUpdate(DocumentEvent e) { calculateCalories(); }
            public void changedUpdate(DocumentEvent e) { calculateCalories(); }
        });
        weightPanel.add(weightField);

        JLabel kgLabel = new JLabel("kg");
        kgLabel.setFont(AppTheme.BODY_FONT);
        kgLabel.setForeground(AppTheme.TEXT_SECONDARY);
        kgLabel.setBounds(300, 15, 20, 20);
        weightPanel.add(kgLabel);
        mainPanel.add(weightPanel);

        // ── 예상 소모 칼로리 표시 ──
        JLabel calorieLabel = new JLabel("예상 소모 칼로리");
        calorieLabel.setFont(AppTheme.SECTION_TITLE_FONT);
        calorieLabel.setForeground(AppTheme.TEXT);
        calorieLabel.setBounds(25, 314, 200, 30);
        mainPanel.add(calorieLabel);

        calorieValue = new JLabel("0 Kcal");
        calorieValue.setFont(AppTheme.SECTION_TITLE_FONT);
        calorieValue.setForeground(AppTheme.PRIMARY_DARK);
        calorieValue.setHorizontalAlignment(SwingConstants.RIGHT);
        calorieValue.setBounds(230, 314, 125, 30);
        mainPanel.add(calorieValue);

        // ── 저장 버튼 ──
        finishButton = new RoundedComponent(330, 44, 10, "button", "운동 기록 저장",
                AppTheme.PRIMARY_DARK, AppTheme.PRIMARY_DARK, Color.WHITE, Font.SANS_SERIF, Font.BOLD, 14);
        finishButton.setBounds(25, 390, 330, 44);
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
