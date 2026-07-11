package panel;

import DB.CalendarDAO;
import java.awt.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import model.LoginManager;

public class CalendarPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(0xF4F6F1);
    private static final Color PRIMARY_COLOR = new Color(0x609056);
    private static final Color TODAY_COLOR = new Color(0xC0E993);
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 M월");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final CalendarDAO calendarDAO;
    private final JLabel monthLabel;
    private final JLabel selectedDateLabel;
    private final JPanel calendarGridPanel;
    private final JPanel recordListPanel;
    private final JScrollPane recordScrollPane;
    private YearMonth displayedMonth;
    private LocalDate selectedDate;

    public CalendarPanel() {
        calendarDAO = new CalendarDAO();
        selectedDate = LocalDate.now();
        displayedMonth = YearMonth.from(selectedDate);

        setBounds(0, 90, 440, 736);
        setBackground(BACKGROUND_COLOR);
        setLayout(null);

        JButton previousButton = createMonthButton("<");
        previousButton.setBounds(25, 20, 48, 40);
        previousButton.addActionListener(e -> changeMonth(-1));
        add(previousButton);

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 24));
        monthLabel.setBounds(80, 20, 280, 40);
        add(monthLabel);

        JButton nextButton = createMonthButton(">");
        nextButton.setBounds(367, 20, 48, 40);
        nextButton.addActionListener(e -> changeMonth(1));
        add(nextButton);

        JPanel weekdayPanel = new JPanel(new GridLayout(1, 7, 4, 0));
        weekdayPanel.setBounds(25, 72, 390, 28);
        weekdayPanel.setOpaque(false);
        String[] weekdays = {"일", "월", "화", "수", "목", "금", "토"};
        for (int i = 0; i < weekdays.length; i++) {
            JLabel weekdayLabel = new JLabel(weekdays[i], SwingConstants.CENTER);
            weekdayLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 13));
            if (i == 0) {
                weekdayLabel.setForeground(new Color(0xC85050));
            } else if (i == 6) {
                weekdayLabel.setForeground(new Color(0x4F6FAE));
            }
            weekdayPanel.add(weekdayLabel);
        }
        add(weekdayPanel);

        calendarGridPanel = new JPanel(new GridLayout(6, 7, 4, 4));
        calendarGridPanel.setBounds(25, 104, 390, 270);
        calendarGridPanel.setOpaque(false);
        add(calendarGridPanel);

        selectedDateLabel = new JLabel();
        selectedDateLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        selectedDateLabel.setBounds(25, 390, 390, 30);
        add(selectedDateLabel);

        recordListPanel = new JPanel();
        recordListPanel.setLayout(new BoxLayout(recordListPanel, BoxLayout.Y_AXIS));
        recordListPanel.setBackground(Color.WHITE);

        recordScrollPane = new JScrollPane(recordListPanel);
        recordScrollPane.setBounds(25, 425, 390, 275);
        recordScrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xDDDDDD)));
        recordScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        recordScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        recordScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        recordScrollPane.getViewport().setBackground(Color.WHITE);
        add(recordScrollPane);

        renderCalendar();
        loadSelectedDateRecords();
    }

    public void refresh() {
        renderCalendar();
        loadSelectedDateRecords();
    }

    private JButton createMonthButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        button.setForeground(PRIMARY_COLOR);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1, true));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void changeMonth(int amount) {
        displayedMonth = displayedMonth.plusMonths(amount);
        selectedDate = displayedMonth.atDay(1);
        renderCalendar();
        loadSelectedDateRecords();
    }

    private void selectDate(LocalDate date) {
        selectedDate = date;
        renderCalendar();
        loadSelectedDateRecords();
    }

    private void renderCalendar() {
        monthLabel.setText(displayedMonth.format(MONTH_FORMATTER));
        selectedDateLabel.setText(selectedDate.format(DATE_FORMATTER) + " 기록");
        calendarGridPanel.removeAll();

        LocalDate firstDay = displayedMonth.atDay(1);
        int firstDayOffset = firstDay.getDayOfWeek().getValue() % 7;
        int daysInMonth = displayedMonth.lengthOfMonth();

        for (int cell = 0; cell < 42; cell++) {
            int day = cell - firstDayOffset + 1;
            if (day < 1 || day > daysInMonth) {
                JPanel emptyCell = new JPanel();
                emptyCell.setOpaque(false);
                calendarGridPanel.add(emptyCell);
                continue;
            }

            LocalDate date = displayedMonth.atDay(day);
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.setFont(new Font("Malgun Gothic", Font.PLAIN, 13));
            dayButton.setFocusPainted(false);
            dayButton.setMargin(new Insets(0, 0, 0, 0));
            dayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            dayButton.setOpaque(true);
            dayButton.setContentAreaFilled(true);
            styleDayButton(dayButton, date);
            dayButton.addActionListener(e -> selectDate(date));
            calendarGridPanel.add(dayButton);
        }

        calendarGridPanel.revalidate();
        calendarGridPanel.repaint();
    }

    private void styleDayButton(JButton button, LocalDate date) {
        button.setBackground(Color.WHITE);
        button.setForeground(Color.DARK_GRAY);
        button.setBorder(BorderFactory.createEmptyBorder());

        if (date.getDayOfWeek().getValue() == 7) {
            button.setForeground(new Color(0xC85050));
        } else if (date.getDayOfWeek().getValue() == 6) {
            button.setForeground(new Color(0x4F6FAE));
        }

        if (date.equals(LocalDate.now())) {
            button.setBackground(TODAY_COLOR);
            button.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true));
        }

        if (date.equals(selectedDate)) {
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true));
        }
    }

    private void loadSelectedDateRecords() {
        recordListPanel.removeAll();

        String userId = LoginManager.getInstance().getUserId();
        if (userId == null || userId.trim().isEmpty()) {
            addMessageLabel("로그인이 필요합니다.");
            refreshRecordList();
            return;
        }

        List<Map<String, Object>> exerciseLogs = calendarDAO.getExerciseLogsByDate(userId, selectedDate);
        List<Map<String, Object>> mealLogs = calendarDAO.getMealLogsByDate(userId, selectedDate);

        addSectionTitle("운동 기록");
        if (exerciseLogs.isEmpty()) {
            addMessageLabel("선택한 날짜의 운동 기록이 없습니다.");
        } else {
            for (Map<String, Object> log : exerciseLogs) {
                recordListPanel.add(createExerciseRecordPanel(log));
                recordListPanel.add(Box.createVerticalStrut(8));
            }
        }

        recordListPanel.add(Box.createVerticalStrut(8));
        addSectionTitle("식단 기록");
        if (mealLogs.isEmpty()) {
            addMessageLabel("선택한 날짜의 식단 기록이 없습니다.");
        } else {
            for (Map<String, Object> log : mealLogs) {
                recordListPanel.add(createMealRecordPanel(log));
                recordListPanel.add(Box.createVerticalStrut(8));
            }
        }

        refreshRecordList();
        SwingUtilities.invokeLater(() -> recordScrollPane.getViewport().setViewPosition(new Point(0, 0)));
    }

    private void addSectionTitle(String title) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(6, 12, 4, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setMaximumSize(new Dimension(360, 32));
        titleLabel.setPreferredSize(new Dimension(360, 32));
        recordListPanel.add(titleLabel);
    }

    private JPanel createExerciseRecordPanel(Map<String, Object> log) {
        JPanel panel = new JPanel(null);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(360, 112));
        panel.setPreferredSize(new Dimension(360, 112));
        panel.setBackground(new Color(0xF5F5F5));
        panel.setBorder(BorderFactory.createLineBorder(new Color(0xDDDDDD)));

        String exerciseName = (String) log.get("exercise_name");
        if (exerciseName == null || exerciseName.trim().isEmpty()) {
            exerciseName = "운동명 없음";
        }

        Object caloriesValue = log.get("exercise_calories");
        int calories = caloriesValue instanceof Number ? ((Number) caloriesValue).intValue() : 0;

        JTextArea nameArea = createWrappedTextArea(exerciseName, Font.BOLD, 15);
        nameArea.setBounds(16, 8, 328, 40);
        panel.add(nameArea);

        JLabel calorieLabel = new JLabel(calories + " kcal");
        calorieLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        calorieLabel.setBounds(16, 52, 120, 22);
        panel.add(calorieLabel);

        JLabel timeLabel = new JLabel("저장 시간: " + formatTime(log.get("exercise_date")));
        timeLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 13));
        timeLabel.setForeground(Color.DARK_GRAY);
        timeLabel.setBounds(16, 80, 220, 20);
        panel.add(timeLabel);

        return panel;
    }

    private JPanel createMealRecordPanel(Map<String, Object> log) {
        JPanel panel = new JPanel(null);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(360, 125));
        panel.setPreferredSize(new Dimension(360, 125));
        panel.setBackground(new Color(0xFFF9EE));
        panel.setBorder(BorderFactory.createLineBorder(new Color(0xE4D7BE)));

        String mealType = (String) log.get("meal_type");
        if (mealType == null || mealType.trim().isEmpty()) {
            mealType = "식사 유형 없음";
        }

        String foodNames = (String) log.get("food_names");
        if (foodNames == null || foodNames.trim().isEmpty()) {
            foodNames = "담긴 음식 없음";
        }

        Object caloriesValue = log.get("total_calories");
        double calories = caloriesValue instanceof Number ? ((Number) caloriesValue).doubleValue() : 0;

        JLabel typeLabel = new JLabel(mealType + " · " + formatTime(log.get("meal_time")));
        typeLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        typeLabel.setBounds(16, 10, 330, 24);
        panel.add(typeLabel);

        JTextArea foodArea = createWrappedTextArea(foodNames, Font.PLAIN, 14);
        foodArea.setForeground(Color.DARK_GRAY);
        foodArea.setBounds(16, 38, 328, 48);
        panel.add(foodArea);

        JLabel calorieLabel = new JLabel(String.format("총 %.0f kcal", calories));
        calorieLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 14));
        calorieLabel.setBounds(16, 92, 180, 22);
        panel.add(calorieLabel);

        return panel;
    }

    private JTextArea createWrappedTextArea(String text, int fontStyle, int fontSize) {
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(new Font("Malgun Gothic", fontStyle, fontSize));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setOpaque(false);
        textArea.setBorder(null);
        textArea.setMargin(new Insets(0, 0, 0, 0));
        return textArea;
    }

    private String formatTime(Object value) {
        if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime().toLocalTime().format(TIME_FORMATTER);
        }
        if (value instanceof Time) {
            return ((Time) value).toLocalTime().format(TIME_FORMATTER);
        }
        if (value instanceof LocalTime) {
            return ((LocalTime) value).format(TIME_FORMATTER);
        }
        return "";
    }

    private void addMessageLabel(String message) {
        JLabel messageLabel = new JLabel(message, SwingConstants.CENTER);
        messageLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 15));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setMaximumSize(new Dimension(360, 80));
        messageLabel.setPreferredSize(new Dimension(360, 80));
        recordListPanel.add(messageLabel);
    }

    private void refreshRecordList() {
        recordListPanel.revalidate();
        recordListPanel.repaint();
    }
}
