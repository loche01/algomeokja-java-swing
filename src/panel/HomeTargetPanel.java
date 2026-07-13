package panel;

import DB.GoalDAO;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import main.MainUserPanel;
import model.UserBean;
import model.UserGoal;
import ui_n_utils.AppTheme;
import ui_n_utils.UserSessionManager;

public class HomeTargetPanel extends JPanel {
    private static final int CARD_WIDTH = 380;

    private final MainUserPanel mainUserPanel;
    private final GoalDAO goalDAO;
    private final JPanel summaryCard;
    private final JPanel progressCard;
    private final JPanel stateCard;
    private final JLabel startWeightValue;
    private final JLabel currentWeightValue;
    private final JLabel targetWeightValue;
    private final JLabel durationValue;
    private final JLabel progressPercentLabel;
    private final JLabel progressDetailLabel;
    private final JLabel stateTitleLabel;
    private final JLabel stateDescriptionLabel;
    private final JProgressBar progressBar;
    private final JButton goalActionButton;

    public HomeTargetPanel(MainUserPanel mainUserPanel) {
        this.mainUserPanel = mainUserPanel;
        this.goalDAO = new GoalDAO();

        setLayout(null);
        setBounds(0, 0, 440, 686);
        setBackground(AppTheme.BACKGROUND);

        JLabel pageTitle = new JLabel("목표 달성");
        pageTitle.setFont(AppTheme.TITLE_FONT);
        pageTitle.setForeground(AppTheme.TEXT);
        pageTitle.setBounds(AppTheme.HORIZONTAL_MARGIN, 18, CARD_WIDTH, 34);
        add(pageTitle);

        JLabel pageDescription = new JLabel("설정한 체중 목표의 진행 상황을 확인합니다.");
        pageDescription.setFont(AppTheme.BODY_FONT);
        pageDescription.setForeground(AppTheme.TEXT_SECONDARY);
        pageDescription.setBounds(AppTheme.HORIZONTAL_MARGIN, 54, CARD_WIDTH, 24);
        add(pageDescription);

        summaryCard = createCard(90, 210);
        summaryCard.add(createSectionTitle("목표 요약"));
        startWeightValue = addSummaryRow(summaryCard, "시작 체중", 53);
        currentWeightValue = addSummaryRow(summaryCard, "현재 체중", 89);
        targetWeightValue = addSummaryRow(summaryCard, "목표 체중", 125);
        durationValue = addSummaryRow(summaryCard, "목표 기간", 161);

        progressCard = createCard(315, 180);
        progressCard.add(createSectionTitle("진행률"));

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setForeground(AppTheme.PRIMARY);
        progressBar.setBackground(AppTheme.INPUT_BACKGROUND);
        progressBar.setBorderPainted(false);
        progressBar.setStringPainted(false);
        progressBar.setBounds(20, 55, 340, 22);
        progressCard.add(progressBar);

        progressPercentLabel = new JLabel("0.0%", SwingConstants.CENTER);
        progressPercentLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        progressPercentLabel.setForeground(AppTheme.TEXT);
        progressPercentLabel.setBounds(20, 82, 340, 32);
        progressCard.add(progressPercentLabel);

        progressDetailLabel = new JLabel("", SwingConstants.CENTER);
        progressDetailLabel.setFont(AppTheme.BODY_FONT);
        progressDetailLabel.setForeground(AppTheme.TEXT_SECONDARY);
        progressDetailLabel.setBounds(20, 118, 340, 42);
        progressCard.add(progressDetailLabel);

        stateCard = createCard(510, 135);
        stateTitleLabel = new JLabel("");
        stateTitleLabel.setFont(AppTheme.SECTION_TITLE_FONT);
        stateTitleLabel.setForeground(AppTheme.PRIMARY_DARK);
        stateTitleLabel.setBounds(20, 14, 340, 28);
        stateCard.add(stateTitleLabel);

        stateDescriptionLabel = new JLabel("");
        stateDescriptionLabel.setFont(AppTheme.CAPTION_FONT);
        stateDescriptionLabel.setForeground(AppTheme.TEXT_SECONDARY);
        stateDescriptionLabel.setBounds(20, 42, 340, 36);
        stateCard.add(stateDescriptionLabel);

        goalActionButton = new JButton("목표 설정");
        AppTheme.stylePrimaryButton(goalActionButton);
        goalActionButton.setBounds(20, 84, 340, 38);
        goalActionButton.addActionListener(e -> mainUserPanel.showPanel("MymeGoal"));
        stateCard.add(goalActionButton);

        showNoGoalState();
    }

    private JPanel createCard(int y, int height) {
        JPanel card = new JPanel(null);
        AppTheme.styleCard(card);
        card.setBounds(AppTheme.HORIZONTAL_MARGIN, y, CARD_WIDTH, height);
        add(card);
        return card;
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.SECTION_TITLE_FONT);
        label.setForeground(AppTheme.PRIMARY_DARK);
        label.setBounds(20, 14, 200, 28);
        return label;
    }

    private JLabel addSummaryRow(JPanel card, String title, int y) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(AppTheme.BODY_BOLD_FONT);
        titleLabel.setForeground(AppTheme.TEXT_SECONDARY);
        titleLabel.setBounds(20, y, 110, 28);
        card.add(titleLabel);

        JLabel valueLabel = new JLabel("");
        valueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        valueLabel.setForeground(AppTheme.TEXT);
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        valueLabel.setBounds(135, y, 225, 28);
        card.add(valueLabel);
        return valueLabel;
    }

    public void refreshTargetData() {
        resetDisplayedData();

        UserBean currentUser = UserSessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            showUnavailableState();
            return;
        }

        UserGoal goal = goalDAO.getUserGoal(currentUser.getUser_id());
        if (goal == null || goal.getStartWeight() == null || goal.getTargetWeight() == null) {
            showNoGoalState();
            return;
        }

        BigDecimal latestWeight = goalDAO.getLatestWeight(currentUser.getUser_id());
        Map<String, Object> weightChangeData =
                goalDAO.calculateWeightChangeFromMealLogs(currentUser.getUser_id());
        renderGoal(goal, latestWeight, weightChangeData);
    }

    public void refreshData() {
        refreshTargetData();
    }

    public void loadUserTargetData() {
        refreshTargetData();
    }

    private void resetDisplayedData() {
        setSummaryValue(startWeightValue, "");
        setSummaryValue(currentWeightValue, "");
        setSummaryValue(targetWeightValue, "");
        setSummaryValue(durationValue, "");
        progressBar.setValue(0);
        progressPercentLabel.setText("0.0%");
        progressDetailLabel.setText("");
        stateTitleLabel.setText("");
        stateDescriptionLabel.setText("");
        goalActionButton.setText("목표 설정");
    }

    private void renderGoal(UserGoal goal, BigDecimal latestWeight, Map<String, Object> progressData) {
        summaryCard.setVisible(true);
        progressCard.setVisible(true);
        stateCard.setBounds(AppTheme.HORIZONTAL_MARGIN, 510, CARD_WIDTH, 135);

        setSummaryValue(startWeightValue, formatWeight(goal.getStartWeight()));
        setSummaryValue(
                currentWeightValue,
                latestWeight != null ? formatWeight(latestWeight) : "최근 기록 없음");
        setSummaryValue(targetWeightValue, formatWeight(goal.getTargetWeight()));

        double timeProgress = safePercentage(valueAsDouble(progressData, "timeProgressRatio"));
        String durationText = goal.getTargetDuration() > 0
                ? goal.getTargetDuration() + "일" : "기간 정보 없음";
        if (goal.getTargetDuration() > 0 && goal.getCreatedAt() != null) {
            durationText += " · " + formatPercent(timeProgress);
        }
        setSummaryValue(durationValue, durationText);

        double startWeight = finiteValue(goal.getStartWeight());
        double targetWeight = finiteValue(goal.getTargetWeight());
        double currentWeight = latestWeight != null ? finiteValue(latestWeight) : startWeight;
        double rawProgress = calculateWeightProgress(startWeight, currentWeight, targetWeight);
        double visualProgress = safePercentage(rawProgress);

        progressBar.setValue((int) Math.round(visualProgress));
        progressPercentLabel.setText(formatPercent(visualProgress));
        progressDetailLabel.setText(createProgressDetail(
                startWeight, currentWeight, targetWeight, latestWeight != null));

        boolean goalReached = rawProgress >= 100.0;
        boolean sameStartAndTarget = Double.compare(startWeight, targetWeight) == 0;
        if (goalReached) {
            stateTitleLabel.setText("목표를 달성했습니다.");
            stateDescriptionLabel.setText("설정한 목표 체중에 도달한 상태입니다.");
        } else if (sameStartAndTarget) {
            stateTitleLabel.setText("목표 체중을 확인해주세요.");
            stateDescriptionLabel.setText("시작 체중과 목표 체중이 같습니다.");
        } else {
            stateTitleLabel.setText("목표를 향해 진행 중입니다.");
            stateDescriptionLabel.setText("최근 기록을 기준으로 진행 상황을 표시합니다.");
        }
        goalActionButton.setText("목표 수정");
        goalActionButton.setBounds(20, 84, 340, 38);
    }

    private double calculateWeightProgress(double start, double current, double target) {
        if (!Double.isFinite(start) || !Double.isFinite(current) || !Double.isFinite(target)) {
            return 0.0;
        }

        if (target < start) {
            double targetChange = start - target;
            double progress = targetChange == 0.0
                    ? 0.0 : ((start - current) / targetChange) * 100.0;
            return finiteOrZero(progress);
        }
        if (target > start) {
            double targetChange = target - start;
            double progress = targetChange == 0.0
                    ? 0.0 : ((current - start) / targetChange) * 100.0;
            return finiteOrZero(progress);
        }
        return 0.0;
    }

    private String createProgressDetail(
            double start, double current, double target, boolean hasCurrentWeight) {
        if (!hasCurrentWeight) {
            return "최근 체중 기록이 없어 진행률을 0%로 표시합니다.";
        }
        if (Double.compare(start, target) == 0) {
            return "시작 체중과 목표 체중이 같습니다.";
        }

        boolean gainGoal = target > start;
        double changedWeight = gainGoal ? current - start : start - current;
        double remainingWeight = gainGoal ? target - current : current - target;
        changedWeight = Math.max(0.0, finiteOrZero(changedWeight));
        remainingWeight = Math.max(0.0, finiteOrZero(remainingWeight));
        String direction = gainGoal ? "증량" : "감량";
        return String.format(
                Locale.ROOT,
                "현재까지 %.1f kg %s · 목표까지 %.1f kg",
                changedWeight,
                direction,
                remainingWeight);
    }

    private void showNoGoalState() {
        summaryCard.setVisible(false);
        progressCard.setVisible(false);
        stateCard.setBounds(AppTheme.HORIZONTAL_MARGIN, 100, CARD_WIDTH, 170);
        stateTitleLabel.setText("아직 설정된 목표가 없습니다.");
        stateDescriptionLabel.setText(
                "<html>목표를 설정하면 진행 상황을<br>확인할 수 있습니다.</html>");
        goalActionButton.setText("목표 설정");
        goalActionButton.setBounds(20, 112, 340, 40);
    }

    private void showUnavailableState() {
        showNoGoalState();
        stateTitleLabel.setText("목표 정보를 표시할 수 없습니다.");
        stateDescriptionLabel.setText("로그인 상태를 확인한 뒤 다시 시도해주세요.");
    }

    private void setSummaryValue(JLabel label, String value) {
        label.setText(value);
        label.setToolTipText(value.isEmpty() ? null : value);
        label.setFont(new Font(
                Font.SANS_SERIF,
                Font.BOLD,
                value.length() > 18 ? 14 : 18));
    }

    private String formatWeight(BigDecimal weight) {
        BigDecimal displayValue = weight.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
        return displayValue.toPlainString() + " kg";
    }

    private String formatPercent(double percentage) {
        return String.format(Locale.ROOT, "%.1f%%", safePercentage(percentage));
    }

    private double valueAsDouble(Map<String, Object> values, String key) {
        if (values == null) {
            return 0.0;
        }
        Object value = values.get(key);
        return value instanceof Number ? ((Number) value).doubleValue() : 0.0;
    }

    private double finiteValue(BigDecimal value) {
        return value == null ? 0.0 : finiteOrZero(value.doubleValue());
    }

    private double finiteOrZero(double value) {
        return Double.isFinite(value) ? value : 0.0;
    }

    private double safePercentage(double value) {
        if (!Double.isFinite(value)) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(100.0, value));
    }
}
