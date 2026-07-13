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
import ui_n_utils.AppTheme;

public class CalendarPanel extends JPanel {
    private static final Color BACKGROUND_COLOR = AppTheme.BACKGROUND;
    private static final Color PRIMARY_COLOR = AppTheme.PRIMARY;
    private static final Color TODAY_COLOR = AppTheme.PRIMARY_LIGHT;
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
        previousButton.setBounds(30, 20, 44, 38);
        previousButton.addActionListener(e -> changeMonth(-1));
        add(previousButton);

        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(AppTheme.TITLE_FONT);
        monthLabel.setForeground(AppTheme.TEXT);
        monthLabel.setBounds(82, 20, 276, 38);
        add(monthLabel);

        JButton nextButton = createMonthButton(">");
        nextButton.setBounds(366, 20, 44, 38);
        nextButton.addActionListener(e -> changeMonth(1));
        add(nextButton);

        JPanel weekdayPanel = new JPanel(new GridLayout(1, 7, 4, 0));
        weekdayPanel.setBounds(30, 72, 380, 28);
        weekdayPanel.setOpaque(false);
        String[] weekdays = {"일", "월", "화", "수", "목", "금", "토"};
        for (int i = 0; i < weekdays.length; i++) {
            JLabel weekdayLabel = new JLabel(weekdays[i], SwingConstants.CENTER);
            weekdayLabel.setFont(AppTheme.CAPTION_FONT.deriveFont(Font.BOLD));
            weekdayLabel.setForeground(AppTheme.TEXT_SECONDARY);
            weekdayPanel.add(weekdayLabel);
        }
        add(weekdayPanel);

        calendarGridPanel = new JPanel(new GridLayout(6, 7, 4, 4));
        calendarGridPanel.setBounds(30, 104, 380, 270);
        calendarGridPanel.setOpaque(false);
        add(calendarGridPanel);

        selectedDateLabel = new JLabel();
        selectedDateLabel.setFont(AppTheme.SECTION_TITLE_FONT);
        selectedDateLabel.setForeground(AppTheme.TEXT);
        selectedDateLabel.setBounds(30, 390, 380, 30);
        add(selectedDateLabel);

        recordListPanel = new JPanel();
        recordListPanel.setLayout(new BoxLayout(recordListPanel, BoxLayout.Y_AXIS));
        recordListPanel.setBackground(AppTheme.CARD);

        recordScrollPane = new JScrollPane(recordListPanel);
        recordScrollPane.setBounds(30, 425, 380, 275);
        recordScrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        recordScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        recordScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        recordScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        recordScrollPane.getViewport().setBackground(AppTheme.CARD);
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
        AppTheme.styleSecondaryButton(button);
        button.setFont(AppTheme.SECTION_TITLE_FONT);
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
            dayButton.setFont(AppTheme.CAPTION_FONT);
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
        button.setBackground(AppTheme.CARD);
        button.setForeground(AppTheme.TEXT);
        button.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));

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
        titleLabel.setFont(AppTheme.BODY_BOLD_FONT);
        titleLabel.setForeground(AppTheme.PRIMARY_DARK);
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
        panel.setBackground(AppTheme.INPUT_BACKGROUND);
        panel.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));

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
        calorieLabel.setFont(AppTheme.BODY_FONT);
        calorieLabel.setBounds(16, 52, 120, 22);
        panel.add(calorieLabel);

        JLabel timeLabel = new JLabel("저장 시간: " + formatTime(log.get("exercise_date")));
        timeLabel.setFont(AppTheme.CAPTION_FONT);
        timeLabel.setForeground(AppTheme.TEXT_SECONDARY);
        timeLabel.setBounds(16, 80, 220, 20);
        panel.add(timeLabel);

        return panel;
    }

    private JPanel createMealRecordPanel(Map<String, Object> log) {
        JPanel panel = new JPanel(null);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(360, 125));
        panel.setPreferredSize(new Dimension(360, 125));
        panel.setBackground(AppTheme.INPUT_BACKGROUND);
        panel.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));

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
        typeLabel.setFont(AppTheme.BODY_BOLD_FONT);
        typeLabel.setBounds(16, 10, 330, 24);
        panel.add(typeLabel);

        JTextArea foodArea = createWrappedTextArea(foodNames, Font.PLAIN, 14);
        foodArea.setForeground(AppTheme.TEXT_SECONDARY);
        foodArea.setBounds(16, 38, 328, 48);
        panel.add(foodArea);

        JLabel calorieLabel = new JLabel(String.format("총 %.0f kcal", calories));
        calorieLabel.setFont(AppTheme.BODY_FONT);
        calorieLabel.setBounds(16, 92, 180, 22);
        panel.add(calorieLabel);

        return panel;
    }

    private JTextArea createWrappedTextArea(String text, int fontStyle, int fontSize) {
        JTextArea textArea = new JTextArea(text);
        textArea.setFont(new Font(Font.SANS_SERIF, fontStyle, fontSize));
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
        AppTheme.styleEmptyState(messageLabel);
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
