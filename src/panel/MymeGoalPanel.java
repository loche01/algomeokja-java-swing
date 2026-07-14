package panel;

import DB.GoalDAO;
import java.awt.Component;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import main.MainUserPanel;
import model.UserBean;
import model.UserGoal;
import ui_n_utils.AppTheme;
import ui_n_utils.UserSessionManager;

public class MymeGoalPanel extends JPanel implements ActionListener {
    private final MainUserPanel mainUserPanel;
    private final GoalDAO goalDAO;
    private final JLabel titleLabel;
    private final JButton saveButton;
    private final JTextField startWeightField;
    private final JTextField targetWeightField;
    private final JTextField durationField;

    public MymeGoalPanel(MainUserPanel mainUserPanel) {
        this.mainUserPanel = mainUserPanel;
        this.goalDAO = new GoalDAO();

        setLayout(null);
        setBounds(0, 0, 440, 736);
        setBackground(AppTheme.BACKGROUND);

        JPanel formCard = new JPanel(null);
        AppTheme.styleCard(formCard);
        formCard.setBounds(AppTheme.HORIZONTAL_MARGIN, 15, 380, 460);
        add(formCard);

        titleLabel = new JLabel("목표 설정");
        AppTheme.styleScreenTitle(titleLabel);
        titleLabel.setBounds(24, 18, 195, 36);
        formCard.add(titleLabel);

        JButton backButton = new JButton("목표 화면으로");
        AppTheme.styleSecondaryButton(backButton);
        backButton.setBounds(235, 20, 120, 36);
        backButton.addActionListener(e -> mainUserPanel.showPanel("HomeTarget"));
        formCard.add(backButton);

        JLabel descriptionLabel = new JLabel("체중 목표와 목표 기간을 입력해주세요.");
        AppTheme.styleScreenDescription(descriptionLabel);
        descriptionLabel.setBounds(24, 55, 290, 24);
        formCard.add(descriptionLabel);

        JSeparator headerDivider = new JSeparator();
        headerDivider.setForeground(AppTheme.BORDER);
        headerDivider.setBounds(24, 86, 331, 1);
        formCard.add(headerDivider);

        JLabel sectionTitle = new JLabel("목표 정보");
        AppTheme.styleSectionTitle(sectionTitle);
        sectionTitle.setBounds(24, 102, 160, 28);
        formCard.add(sectionTitle);

        startWeightField = addInputRow(formCard, "시작 체중", "kg", 142);
        targetWeightField = addInputRow(formCard, "목표 체중", "kg", 204);
        durationField = addInputRow(formCard, "목표 기간", "일", 266);

        JLabel inputGuide = new JLabel(
                "<html>체중은 숫자로 입력해주세요.<br>목표 기간은 1일 이상입니다.</html>");
        inputGuide.setFont(AppTheme.CAPTION_FONT);
        inputGuide.setForeground(AppTheme.TEXT_SECONDARY);
        inputGuide.setBounds(145, 323, 205, 40);
        formCard.add(inputGuide);

        saveButton = new JButton("목표 저장");
        AppTheme.stylePrimaryButton(saveButton);
        saveButton.setBounds(145, 392, 205, 44);
        saveButton.addActionListener(this);
        formCard.add(saveButton);
    }

    private JTextField addInputRow(JPanel card, String labelText, String unit, int y) {
        JLabel label = new JLabel(labelText);
        label.setFont(AppTheme.BODY_BOLD_FONT);
        label.setForeground(AppTheme.TEXT_SECONDARY);
        label.setBounds(24, y + 7, 110, 24);
        card.add(label);

        JTextField field = new JTextField();
        AppTheme.styleInputField(field);
        field.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        field.setHorizontalAlignment(SwingConstants.RIGHT);
        field.setBounds(145, y, 170, AppTheme.INPUT_HEIGHT);
        card.add(field);

        JLabel unitLabel = new JLabel(unit, SwingConstants.CENTER);
        unitLabel.setFont(AppTheme.BODY_BOLD_FONT);
        unitLabel.setForeground(AppTheme.TEXT_SECONDARY);
        unitLabel.setBounds(320, y + 7, 30, 24);
        card.add(unitLabel);
        return field;
    }

    private void loadGoalForm() {
        clearFields();
        setFormMode(false);

        UserBean currentUser = UserSessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        UserGoal existingGoal = goalDAO.getUserGoal(currentUser.getUser_id());
        if (existingGoal != null) {
            startWeightField.setText(existingGoal.getStartWeight().toString());
            targetWeightField.setText(existingGoal.getTargetWeight().toString());
            durationField.setText(String.valueOf(existingGoal.getTargetDuration()));
            setFormMode(true);
            return;
        }

        BigDecimal latestWeight = goalDAO.getLatestWeight(currentUser.getUser_id());
        if (latestWeight != null) {
            startWeightField.setText(latestWeight.toString());
        }
    }

    private void setFormMode(boolean editingExistingGoal) {
        titleLabel.setText(editingExistingGoal ? "목표 수정" : "목표 설정");
        saveButton.setText(editingExistingGoal ? "목표 수정" : "목표 저장");
    }

    private UserGoal getUserInput() {
        try {
            UserBean currentUser = UserSessionManager.getInstance().getCurrentUser();
            if (currentUser == null) {
                JOptionPane.showMessageDialog(
                        getDialogParent(),
                        "로그인이 필요합니다!",
                        "오류",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }

            if (startWeightField.getText().trim().isEmpty()
                    || targetWeightField.getText().trim().isEmpty()
                    || durationField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        getDialogParent(),
                        "모든 필드를 입력해주세요!",
                        "입력 오류",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }

            BigDecimal startWeight = new BigDecimal(startWeightField.getText().trim());
            BigDecimal targetWeight = new BigDecimal(targetWeightField.getText().trim());
            int targetDuration = Integer.parseInt(durationField.getText().trim());

            if (targetDuration <= 0) {
                JOptionPane.showMessageDialog(
                        getDialogParent(),
                        "목표 기간은 1일 이상이어야 합니다!",
                        "입력 오류",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }

            return new UserGoal(
                    currentUser.getUser_id(), startWeight, targetWeight, targetDuration);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    getDialogParent(),
                    "숫자 입력을 확인하세요!",
                    "입력 오류",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void clearFields() {
        startWeightField.setText("");
        targetWeightField.setText("");
        durationField.setText("");
    }

    private Component getDialogParent() {
        Window window = SwingUtilities.getWindowAncestor(this);
        return window != null ? window : this;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != saveButton) {
            return;
        }

        UserGoal goal = getUserInput();
        if (goal == null) {
            return;
        }

        boolean success = goalDAO.saveOrUpdateGoal(goal);
        if (success) {
            JOptionPane.showMessageDialog(
                    getDialogParent(),
                    "목표가 성공적으로 저장되었습니다.",
                    "저장 완료",
                    JOptionPane.INFORMATION_MESSAGE);
            mainUserPanel.showPanel("HomeTarget");
            clearFields();
        } else {
            JOptionPane.showMessageDialog(
                    getDialogParent(),
                    "목표 저장에 실패했습니다. 다시 시도해주세요.",
                    "저장 실패",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            loadGoalForm();
        }
    }
}
