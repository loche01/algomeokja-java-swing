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
import ui_n_utils.RoundedComponent;

public class BodyInfoSetPanel extends JPanel implements ActionListener {
    private RoundedComponent registerButton, skipButton;
    private JTextField aField, bField, passaField, cField, dField;
    private MainFrame mainFrame; // MainFrame과 연결
    private String userId; //  로그인한 사용자의 ID 저장

    public BodyInfoSetPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(null);
        setBackground(Color.WHITE);
        setBounds(0, 0, 440, 956); //  크기 설정

        int formWidth = 308;
        int centerX = (getWidth() - formWidth) / 3;
        int startY = 100; // 시작 Y 위치

        JLabel titleLabel = new JLabel("신체정보", SwingConstants.LEFT);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 32)); // 폰트 설정
        titleLabel.setBounds(centerX - 20, startY - 95, 181, 58); // 위치 설정
        add(titleLabel);

        // 구분선
        JSeparator divider1 = new JSeparator();
        divider1.setBounds(centerX - 60, startY - 30, 440, 2);
        divider1.setForeground(Color.black);
        add(divider1);

        // 🔹 입력 필드 추가
        addInputField("키", "cm", centerX, startY + 75, aField = new JTextField());
        addInputField("몸무게", "kg", centerX, startY + 145, bField = new JTextField());
        addInputField("체지방률", "%", centerX, startY + 215, passaField = new JTextField());
        addInputField("체지방량", "kg", centerX, startY + 285, cField = new JTextField());
        addInputField("골격근량", "kg", centerX, startY + 355, dField = new JTextField());

        // 🔹 확인 버튼
        registerButton = new RoundedComponent(135, 46, 10, "button", "확인", Color.BLACK, Color.BLACK, Color.WHITE, "Jua", Font.BOLD, 24);
        registerButton.setBounds(centerX + 105, startY + 455, 135, 46);
        registerButton.getButton().addActionListener(this);
        add(registerButton);

        // 🔹 Skip 버튼
        skipButton = new RoundedComponent(135, 46, 10, "button", "Skip", Color.GRAY, Color.GRAY, Color.WHITE, "Jua", Font.BOLD, 24);
        skipButton.setBounds(centerX + 105, startY + 515, 135, 46);
        skipButton.getButton().addActionListener(this);
        add(skipButton);
    }

    // 🔹 사용자 ID 설정 메서드 (MainFrame에서 전달)
    public void setUserId(String userId) {
        this.userId = userId;
    }

    // 🔹 입력 필드와 단위를 추가하는 메서드
    private void addInputField(String label, String unit, int centerX, int y, JTextField textField) {
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setBounds(centerX + 15, y, 100, 20);
        add(fieldLabel);

        textField.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        textField.setBackground(Color.white);
        textField.setHorizontalAlignment(SwingConstants.RIGHT);
        textField.setBorder(BorderFactory.createLineBorder(Color.white, 2));
        textField.setBounds(centerX + 205, y + 28, 80, 25);
        add(textField);

        JLabel unitLabel = new JLabel(unit);
        unitLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        unitLabel.setBounds(centerX + 290, y + 30, 30, 20);
        add(unitLabel);

        RoundedComponent inputButton = new RoundedComponent(308, 41, 10, "button", "", Color.black, Color.white, Color.black, "Malgun Gothic", Font.PLAIN, 15);
        inputButton.setBounds(centerX + 15, y + 20, 308, 41);
        add(inputButton);

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
