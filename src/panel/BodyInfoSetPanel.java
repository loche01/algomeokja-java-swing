package panel;

import DB.BodyInfoDAO;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import main.MainFrame;
import ui_n_utils.AppTheme;
import ui_n_utils.RoundedComponent;

public class BodyInfoSetPanel extends JPanel implements ActionListener {
    private RoundedComponent registerButton, skipButton;
    private JTextField aField, bField, passaField, cField, dField;
    private MainFrame mainFrame; // MainFrame과 연결
    private String userId; //  로그인한 사용자의 ID 저장
    private JPanel formCard;

    public BodyInfoSetPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(null);
        setBackground(AppTheme.BACKGROUND);
        setBounds(0, 0, 440, 956); //  크기 설정

        formCard = new JPanel(null);
        AppTheme.styleCard(formCard);
        formCard.setBounds(AppTheme.HORIZONTAL_MARGIN, 70, AppTheme.CARD_WIDTH, 700);
        add(formCard);

        JLabel titleLabel = new JLabel("신체정보", SwingConstants.LEFT);
        AppTheme.styleScreenTitle(titleLabel);
        titleLabel.setBounds(24, 20, 181, 34);
        formCard.add(titleLabel);

        JLabel descriptionLabel = new JLabel("신체정보를 입력하거나 나중에 등록할 수 있습니다.");
        AppTheme.styleScreenDescription(descriptionLabel);
        descriptionLabel.setBounds(24, 54, 326, 24);
        formCard.add(descriptionLabel);

        // 구분선
        JSeparator divider1 = new JSeparator();
        divider1.setBounds(24, 86, 326, 1);
        divider1.setForeground(AppTheme.BORDER);
        formCard.add(divider1);

        // 🔹 입력 필드 추가
        addInputField("키", "cm", 112, aField = new JTextField());
        addInputField("몸무게", "kg", 172, bField = new JTextField());
        addInputField("체지방률", "%", 232, passaField = new JTextField());
        addInputField("체지방량", "kg", 292, cField = new JTextField());
        addInputField("골격근량", "kg", 352, dField = new JTextField());

        // 🔹 확인 버튼
        registerButton = new RoundedComponent(205, 44, 10, "button", "신체정보 저장",
                AppTheme.PRIMARY_DARK, AppTheme.PRIMARY_DARK, Color.WHITE, Font.SANS_SERIF, Font.BOLD, 14);
        registerButton.setBounds(145, 438, 205, 44);
        registerButton.getButton().addActionListener(this);
        formCard.add(registerButton);

        // 🔹 Skip 버튼
        skipButton = new RoundedComponent(205, 40, 10, "button", "나중에 입력",
                AppTheme.PRIMARY, AppTheme.CARD, AppTheme.PRIMARY_DARK, Font.SANS_SERIF, Font.BOLD, 13);
        skipButton.setBounds(145, 496, 205, 40);
        skipButton.getButton().addActionListener(this);
        formCard.add(skipButton);
    }

    // 🔹 사용자 ID 설정 메서드 (MainFrame에서 전달)
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // 🔹 입력 필드와 단위를 추가하는 메서드
    private void addInputField(String label, String unit, int y, JTextField textField) {
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(AppTheme.BODY_BOLD_FONT);
        fieldLabel.setForeground(AppTheme.TEXT_SECONDARY);
        fieldLabel.setBounds(24, y + 7, 110, 24);
        formCard.add(fieldLabel);

        AppTheme.styleInputField(textField);
        textField.setHorizontalAlignment(SwingConstants.RIGHT);
        textField.setBounds(145, y, 175, AppTheme.INPUT_HEIGHT);
        formCard.add(textField);

        JLabel unitLabel = new JLabel(unit);
        unitLabel.setFont(AppTheme.BODY_FONT);
        unitLabel.setForeground(AppTheme.TEXT_SECONDARY);
        unitLabel.setBounds(326, y + 9, 30, 20);
        formCard.add(unitLabel);

    }


    // 🔹 버튼 클릭 이벤트 처리
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton.getButton()) {
            try {
                // 🔹 userId가 설정되지 않은 경우 방어 코드 추가
                if (userId == null || userId.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "❌ 로그인 정보를 불러올 수 없습니다. 다시 시도하세요.");
                    return;
                }

                // 🔹 입력값 가져오기
                float height = parseFloatWithValidation(aField.getText(), "키");
                float weight = parseFloatWithValidation(bField.getText(), "몸무게");
                float bodyFatRate = parseFloatWithValidation(passaField.getText(), "체지방률");
                float bodyFatMass = parseFloatWithValidation(cField.getText(), "체지방량");
                float muscleMass = parseFloatWithValidation(dField.getText(), "골격근량");

                // 🔹 DAO를 이용해 DB에 저장
                BodyInfoDAO bodyInfoDAO = new BodyInfoDAO();
                boolean result = bodyInfoDAO.saveBodyInfo(userId, height, weight, bodyFatRate, bodyFatMass, muscleMass);

                if (result) {
                    JOptionPane.showMessageDialog(this, "✅ 신체 정보가 저장되었습니다!");
                    mainFrame.showPanel("mainUser");
                } else {
                    JOptionPane.showMessageDialog(this, "❌ 저장 실패! 다시 시도해주세요.");
                }

            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == skipButton.getButton()) {
            mainFrame.showPanel("mainUser");
        }
    }

    // 🔹 입력값 검증 메서드
    private float parseFloatWithValidation(String value, String fieldName) throws NumberFormatException {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ 올바른 " + fieldName + " 값을 입력해주세요.");
            throw e;
        }
    }
}
