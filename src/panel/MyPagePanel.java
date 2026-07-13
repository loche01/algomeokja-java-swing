package panel;

import DB.BodyInfoDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import main.MainUserPanel;
import model.LoginManager;
import model.UserBean;
import ui_n_utils.AppTheme;

public class MyPagePanel extends JPanel {
    private final JButton editBodyButton;
    private final JButton editMemberButton;
    private final JButton logoutButton;
    private final MainUserPanel mainUserPanel;
    private final JLabel[] userInfoLabels;
    private final JLabel[] bodyInfoLabels;
    private final BodyInfoDAO bodyInfoDAO;

    public MyPagePanel(MainUserPanel mainUserPanel) {
        this.mainUserPanel = mainUserPanel;
        this.bodyInfoDAO = new BodyInfoDAO();
        setLayout(null);
        setBackground(AppTheme.BACKGROUND);

        JPanel mainCard = new JPanel(null);
        AppTheme.styleCard(mainCard);
        mainCard.setBounds(AppTheme.HORIZONTAL_MARGIN, 20, AppTheme.CARD_WIDTH, 650);
        add(mainCard);

        JLabel pageTitle = new JLabel("내 정보");
        AppTheme.styleScreenTitle(pageTitle);
        pageTitle.setBounds(24, 18, 180, 34);
        mainCard.add(pageTitle);

        JLabel pageDescription = new JLabel("회원정보와 신체정보를 한눈에 확인합니다.");
        AppTheme.styleScreenDescription(pageDescription);
        pageDescription.setBounds(24, 52, 326, 24);
        mainCard.add(pageDescription);

        JLabel memberInfoTitle = createSectionTitle("회원정보", 24, 84);
        mainCard.add(memberInfoTitle);

        editMemberButton = new JButton("회원정보 수정");
        AppTheme.styleSecondaryButton(editMemberButton);
        editMemberButton.setBounds(218, 79, 132, 34);
        editMemberButton.addActionListener(e -> mainUserPanel.showPanel("MyMember"));
        mainCard.add(editMemberButton);

        String[] memberInfo = {"이름", "이메일", "전화번호", "ID"};
        userInfoLabels = new JLabel[memberInfo.length];
        for (int i = 0; i < memberInfo.length; i++) {
            int rowY = 129 + (i * 37);
            mainCard.add(createRowLabel(memberInfo[i], rowY));
            userInfoLabels[i] = createValueLabel(rowY);
            mainCard.add(userInfoLabels[i]);
        }

        JLabel passwordGuide = new JLabel("비밀번호 변경은 회원정보 수정에서 할 수 있습니다.");
        passwordGuide.setFont(AppTheme.CAPTION_FONT);
        passwordGuide.setForeground(AppTheme.TEXT_SECONDARY);
        passwordGuide.setBounds(122, 277, 228, 20);
        mainCard.add(passwordGuide);

        JSeparator divider = new JSeparator();
        divider.setForeground(AppTheme.BORDER);
        divider.setBounds(24, 311, 326, 1);
        mainCard.add(divider);

        JLabel bodyInfoTitle = createSectionTitle("신체정보", 24, 334);
        mainCard.add(bodyInfoTitle);

        editBodyButton = new JButton("신체정보 수정");
        AppTheme.styleSecondaryButton(editBodyButton);
        editBodyButton.setBounds(218, 329, 132, 34);
        editBodyButton.addActionListener(e -> mainUserPanel.showPanel("MyBody"));
        mainCard.add(editBodyButton);

        String[] bodyInfo = {"키", "몸무게", "골격근량", "체지방량", "체지방률"};
        bodyInfoLabels = new JLabel[bodyInfo.length];
        for (int i = 0; i < bodyInfo.length; i++) {
            int rowY = 382 + (i * 37);
            mainCard.add(createRowLabel(bodyInfo[i], rowY));
            bodyInfoLabels[i] = createValueLabel(rowY);
            mainCard.add(bodyInfoLabels[i]);
        }

        logoutButton = new JButton("로그아웃");
        AppTheme.styleDangerButton(logoutButton);
        logoutButton.setBounds(24, 596, 326, 40);
        logoutButton.addActionListener(e -> confirmLogout());
        mainCard.add(logoutButton);

        updateUserInfo();
    }

    private JLabel createSectionTitle(String text, int x, int y) {
        JLabel label = new JLabel(text);
        AppTheme.styleSectionTitle(label);
        label.setBounds(x, y, 130, 28);
        return label;
    }

    private JLabel createRowLabel(String text, int y) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.BODY_BOLD_FONT);
        label.setForeground(AppTheme.TEXT_SECONDARY);
        label.setBounds(24, y, 90, 24);
        return label;
    }

    private JLabel createValueLabel(int y) {
        JLabel label = new JLabel("");
        label.setFont(AppTheme.BODY_FONT);
        label.setForeground(AppTheme.TEXT);
        label.setBounds(122, y, 228, 24);
        return label;
    }

    private void confirmLogout() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "로그아웃 하시겠습니까?",
                "로그아웃 확인",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        LoginManager.getInstance().logout();
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame instanceof main.MainFrame) {
            main.MainFrame mainFrame = (main.MainFrame) frame;
            mainFrame.showLoginAfterLogout();
        }

        JOptionPane.showMessageDialog(
                this,
                "로그아웃 되었습니다.",
                "로그아웃 성공",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            updateUserInfo();
        }
    }

    private void updateUserInfo() {
        UserBean user = LoginManager.getInstance().getCurrentUser();
        if (user == null) {
            clearDisplayedInfo();
            return;
        }

        userInfoLabels[0].setText(user.getUser_name());
        userInfoLabels[1].setText(user.getUser_email());
        userInfoLabels[2].setText(user.getUser_phone());
        userInfoLabels[3].setText(user.getUser_id());

        ResultSet resultSet = bodyInfoDAO.getLatestBodyInfo(user.getUser_id());
        try {
            if (resultSet != null && resultSet.next()) {
                bodyInfoLabels[0].setText(resultSet.getFloat("height") + " cm");
                bodyInfoLabels[1].setText(resultSet.getFloat("weight") + " kg");
                bodyInfoLabels[2].setText(resultSet.getFloat("muscle_mass") + " kg");
                bodyInfoLabels[3].setText(resultSet.getFloat("fat_mass") + " kg");
                bodyInfoLabels[4].setText(resultSet.getFloat("fat_rate") + " %");
            } else {
                clearBodyInfo();
            }

            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            clearBodyInfo();
        }
    }

    private void clearDisplayedInfo() {
        for (JLabel userInfoLabel : userInfoLabels) {
            userInfoLabel.setText("");
        }
        clearBodyInfo();
    }

    private void clearBodyInfo() {
        for (JLabel bodyInfoLabel : bodyInfoLabels) {
            bodyInfoLabel.setText("");
        }
    }
}
