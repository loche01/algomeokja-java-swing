package panel;

import DB.JoinDAO;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import main.MainFrame;
import model.UserBean;
import ui_n_utils.CustomDialog;
import ui_n_utils.RoundedComponent;
import ui_n_utils.UIUtils;
import ui_n_utils.ValidationUtils;



public class JoinPanel extends JPanel implements ActionListener {

	private RoundedComponent userIdField, userEmailField, userNameField, userPhoneField;
	private RoundedComponent passwordField, confirmPasswordField, checkIdButton, joinButton;
	private RoundedComponent userBirthdateField;
	private JRadioButton maleRadioButton, femaleRadioButton;
	private ButtonGroup genderGroup;
	private JButton backButton;
	private MainFrame mainFrame;
	private JLabel idErrLbl, PwErrLbl, PwCkErrLbl;
	private boolean isIdChecked = false;
	
	public JoinPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setBackground(Color.WHITE);
		setLayout(null);

		// 🔹 상단 제목
		JLabel titleLabel = new JLabel("회원가입");
		titleLabel.setFont(new Font("Inter", Font.BOLD, 32));
		titleLabel.setBounds(21, 8, 181, 58);
		add(titleLabel);

		// 🔹 구분선 (얇은 검은 테두리)
		JSeparator separator = new JSeparator();
		separator.setBounds(-18, 70, 458, 1);
		separator.setForeground(Color.BLACK);
		add(separator);

		// 뒤로가기 버튼
		backButton = new JButton("<");
		backButton.setBounds(360, 20, 60, 60);
		backButton.addActionListener(this);
		backButton.setBorderPainted(false);
		backButton.setContentAreaFilled(false);
		backButton.setFocusPainted(false);
		add(backButton);
		backButton.setBounds(360, 20, 60, 60);
		backButton.addActionListener(this);
		add(backButton);

		// 🔹 아이디
		add(UIUtils.createRequiredLabel("아이디", Color.black, 32, 164, "Inter",Font.BOLD,15)); // 라벨
		// 아이디 필드
		userIdField = new RoundedComponent(245, 41, 15, "textfield", ""
				, Color.black, Color.WHITE, Color.black, "Inter",Font.PLAIN, 15);
		userIdField.setBounds(32, 185, 245, 41);
		add(userIdField);
		// 중복확인 버튼
		checkIdButton = new RoundedComponent(100, 41, 15, "button", "ID 중복확인", 
				Color.BLACK, Color.BLACK, Color.WHITE,"Inter", Font.BOLD, 14);
		checkIdButton.setBounds(290, 185, 100, 41);
		checkIdButton.getButton().addActionListener(this);
		add(checkIdButton);
		// 형식 오류 메시지 (초기에는 보이지 않도록 설정)
		idErrLbl = UIUtils.createErrorLabel(32, 230, 10);
		add(idErrLbl);
		// 실시간으로 형식 검사 (KeyListener 적용)
		userIdField.getComponent().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String userId = userIdField.getText().trim();
				if (userId.isEmpty()) {
					UIUtils.showError(idErrLbl, "아이디를 입력하세요.");
				} else if (!ValidationUtils.isValidUserId(userId)) {
					UIUtils.showError(idErrLbl, "아이디는 6~20자 사이의 영문(대소문자), 특수문자, 숫자로 만들수 있습니다.");
				} else {
					UIUtils.hideError(idErrLbl);
				}
			}
		});

		// 🔹 비밀번호
		// 비밀번호 필드
		add(UIUtils.createRequiredLabel("비밀번호",Color.black, 32, 250, "Inter",Font.BOLD,15)); // 라벨
		passwordField = new RoundedComponent(358, 41, 15, "password", "", Color.BLACK, Color.WHITE, Color.BLACK,
				"Inter", Font.PLAIN, 15);
		passwordField.setBounds(32, 271, 358, 41);
		add(passwordField);
		// 형식 오류 메시지 (초기에는 보이지 않도록 설정)
		PwErrLbl = UIUtils.createErrorLabel(32, 316, 11);
		add(PwErrLbl);
		// 실시간으로 형식 검사 (KeyListener 적용)
		passwordField.getComponent().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String userpw = passwordField.getText().trim();
				if (userpw.isEmpty()) {
					UIUtils.showError(PwErrLbl, "비밀번호를 입력하세요.");
				} else if (!ValidationUtils.isCreateUserPw(userpw)) {
					UIUtils.showError(PwErrLbl, "비밀번호는 영문(대소문자) + 특수문자를 포함한 6~20자여야 합니다.");
				} else {
					UIUtils.hideError(PwErrLbl);
				}
			}
		}); // -- passwordField.addKeyListener

		// 🔹비밀번호 확인
		// 확인 필드
		add(UIUtils.createRequiredLabel("비밀번호 확인", Color.black, 32, 336, "Inter",Font.BOLD,15));
		confirmPasswordField = new RoundedComponent(358, 41, 15, "password", "", Color.BLACK, Color.WHITE, Color.BLACK,
				"Inter", Font.PLAIN, 15);
		confirmPasswordField.setBounds(32, 357, 358, 41);
		add(confirmPasswordField);
		// 형식 오류 메시지 (초기에는 보이지 않도록 설정)
		PwCkErrLbl = UIUtils.createErrorLabel(32, 402, 11);
		add(PwCkErrLbl);
		confirmPasswordField.getComponent().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				String userPw = new String(passwordField.getText()).trim();
				String confirmPw = new String(confirmPasswordField.getText()).trim();
				if (confirmPw.isEmpty()) {
					UIUtils.showError(PwCkErrLbl, "비밀번호 확인을 입력하세요.");
				} else if (!userPw.equals(confirmPw)) {
					UIUtils.showError(PwCkErrLbl, "비밀번호가 일치하지 않습니다.");
				} else {
					UIUtils.hideError(PwCkErrLbl);
				}
			}
		}); // -- confirmPasswordField.addKeyListener

		// 이름 필드
		add(UIUtils.createRequiredLabel("이름", Color.black,32, 422, "Inter",Font.BOLD,15));
		userNameField = new RoundedComponent(358, 41, 15, "textfield", "", Color.BLACK, Color.WHITE, Color.BLACK,
				"Inter", Font.PLAIN, 15);
		userNameField.setBounds(32, 443, 358, 41);
		add(userNameField);

		// 이메일 필드
		add(UIUtils.createRequiredLabel("이메일", Color.black, 32, 508,"Inter",Font.BOLD,15));
		userEmailField = new RoundedComponent(358, 41, 15, "textfield", "", Color.BLACK, Color.WHITE, Color.BLACK,
				"Inter", Font.PLAIN, 15);
		userEmailField.setBounds(32, 529, 358, 41);
		add(userEmailField);

		// 휴대폰 필드
		add(UIUtils.createRequiredLabel("휴대폰 번호", Color.black, 32, 594, "Inter",Font.BOLD,15));
		userPhoneField = new RoundedComponent(270, 40, 15, "textfield", "", Color.BLACK, Color.WHITE, Color.BLACK,
				"Inter", Font.PLAIN, 15);
		userPhoneField.setBounds(32, 615, 270, 40);
		add(userPhoneField);
		JTextField phoneField = (JTextField) userPhoneField.getComponent();
        phoneField.setText("-을 포함하여 작성해주세요");
        phoneField.setForeground(Color.GRAY);
        phoneField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (phoneField.getText().equals("-을 포함하여 작성해주세요")) {
                    phoneField.setText("");
                    phoneField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (phoneField.getText().isEmpty()) {
                    phoneField.setText("-을 포함하여 작성해주세요");
                    phoneField.setForeground(Color.GRAY);
                }
            }
        });
		
        // 생년월일 필드 추가
        add(UIUtils.createRequiredLabel("생년월일", Color.black, 32, 665, "Inter", Font.BOLD, 15));
        userBirthdateField = new RoundedComponent(270, 40, 15, "textfield", "", Color.BLACK, Color.WHITE, Color.BLACK,
                "Inter", Font.PLAIN, 15);
        userBirthdateField.setBounds(32, 686, 270, 40);
        add(userBirthdateField);
        JTextField birthdateField = (JTextField) userBirthdateField.getComponent();
        birthdateField.setText("YYYY-MM-DD 형식으로 입력하세요");
        birthdateField.setForeground(Color.GRAY);
        birthdateField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (birthdateField.getText().equals("YYYY-MM-DD 형식으로 입력하세요")) {
                    birthdateField.setText("");
                    birthdateField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (birthdateField.getText().isEmpty()) {
                    birthdateField.setText("YYYY-MM-DD 형식으로 입력하세요");
                    birthdateField.setForeground(Color.GRAY);
                }
            }
        });
        
        // 성별 선택 라디오 버튼 추가
        add(UIUtils.createRequiredLabel("성별", Color.black, 32, 736, "Inter", Font.BOLD, 15));
        
        // 라디오 버튼 그룹 생성
        genderGroup = new ButtonGroup();
        
        // 남성 라디오 버튼
        maleRadioButton = new JRadioButton("남성");
        maleRadioButton.setFont(new Font("Inter", Font.PLAIN, 14));
        maleRadioButton.setBounds(32, 757, 80, 30);
        maleRadioButton.setBackground(Color.WHITE);
        genderGroup.add(maleRadioButton);
        add(maleRadioButton);
        
        // 여성 라디오 버튼
        femaleRadioButton = new JRadioButton("여성");
        femaleRadioButton.setFont(new Font("Inter", Font.PLAIN, 14));
        femaleRadioButton.setBounds(120, 757, 80, 30);
        femaleRadioButton.setBackground(Color.WHITE);
        genderGroup.add(femaleRadioButton);
        add(femaleRadioButton);

		// 회원가입 버튼
		joinButton = new RoundedComponent(187, 52, 20, "button", "회원가입", Color.BLACK, Color.BLACK, Color.WHITE, "Inter",
				Font.BOLD, 20);
		joinButton.setBounds(127, 810, 187, 52);
		joinButton.getButton().addActionListener(this);
		add(joinButton);
	}
		
	
	@Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == joinButton.getButton()) {
        	// 🔹 입력값 가져오기
        	String userId = userIdField.getText();
            String userPwd = passwordField.getText();
            String confirmPwd = confirmPasswordField.getText();
            String userName = userNameField.getText();
            String userPhone = userPhoneField.getText();
            String userEmail = userEmailField.getText();
            String userBirthdate = userBirthdateField.getText();
            
            // 성별 값 가져오기
            String userGender = "";
            if (maleRadioButton.isSelected()) {
                userGender = "남성";
            } else if (femaleRadioButton.isSelected()) {
                userGender = "여성";
            }
            
            // 생년월일 형식 검증
            if (userBirthdate.equals("YYYY-MM-DD 형식으로 입력하세요") || userBirthdate.isEmpty()) {
                CustomDialog.showDialog(mainFrame, "생년월일을 입력해주세요.", "회원가입 오류");
                return;
            }
            
            // 생년월일 형식 검증 (YYYY-MM-DD)
            if (!ValidationUtils.isValidDate(userBirthdate)) {
                CustomDialog.showDialog(mainFrame, "생년월일은 YYYY-MM-DD 형식으로 입력해주세요.", "회원가입 오류");
                return;
            }
            
            // 성별 선택 검증
            if (userGender.isEmpty()) {
                CustomDialog.showDialog(mainFrame, "성별을 선택해주세요.", "회원가입 오류");
                return;
            }
            
        	// 🔹 필수 체크 사항 검증
            if(!isIdChecked) {
            	CustomDialog.showDialog(mainFrame, "아이디 중복검사를 해주세요", "회원가입 오류");
            	return;
            }
            
            // 🔹 아이디 중복 체크 했는지 검증
            if (userId.isEmpty() || userPwd.isEmpty() || confirmPwd.isEmpty() || 
                userName.isEmpty() || userPhone.isEmpty() || userEmail.isEmpty() || 
                userBirthdate.isEmpty() || userGender.isEmpty()) {
                CustomDialog.showDialog(mainFrame, "모든 필수 입력 사항을 입력해주세요.", "회원가입 오류");
                return;
            }
            
            // 🔹 비밀번호 불일치 경고 무시 검증
            if(!userPwd.equals(confirmPwd)) {
            	CustomDialog.showDialog(mainFrame, "비밀번호가 일치하지 않습니다.", "회원가입 오류");
            	return;
            }
            
           
            
            // 🔹 이메일 양식 검증
            if (!ValidationUtils.isValidEmail(userEmail)) {
            	CustomDialog.showDialog(mainFrame, "이메일 양식이 올바르지 않습니다.", "회원가입 오류");
            	return;
            }
           
        
            
            if (!ValidationUtils.isValidPhone(userPhone)) {
                CustomDialog.showDialog(mainFrame, "올바른 전화번호 형식을 입력하세요. 예) 010-1234-5678", "회원가입 오류");
                return;
            }
            
            JoinDAO joinDAO = new JoinDAO();

            // 🔹 이메일 중복 체크
            try {
                if (joinDAO.isEmailExists(userEmail)) {
                    CustomDialog.showDialog(mainFrame, "이미 사용 중인 이메일입니다.", "회원가입 오류");
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                CustomDialog.showDialog(mainFrame, "이메일 중복 확인 중 오류 발생", "회원가입 오류");
                return;
            }
            
         //  전화번호 중복 체크 추가
            try {
                if (joinDAO.isPhoneExists(userPhone)) {
                    CustomDialog.showDialog(mainFrame, "이미 사용 중인 전화번호입니다.", "회원가입 오류");
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                CustomDialog.showDialog(mainFrame, "전화번호 중복 확인 중 오류 발생", "회원가입 오류");
                return;
            }
            
            // 🔹 회원 객체 생성
            UserBean user = new UserBean();
            user.setUser_id(userId);
            user.setUser_pwd(userPwd);
            user.setUser_name(userName);
            user.setUser_phone(userPhone);
            user.setUser_email(userEmail);
            user.setUser_birthdate(userBirthdate);
            user.setUser_gender(userGender);
            
            // 🔹 회원가입 처리
            boolean isJoin = joinDAO.joinUser(user);
            
            if (isJoin) {
                CustomDialog.showDialog(mainFrame, userId + "님 환영합니다! 회원가입이 완료되었습니다!", "회원가입 완료");
                UIUtils.clearFields(userIdField, passwordField, confirmPasswordField, userNameField, userPhoneField, userEmailField, userBirthdateField);
                genderGroup.clearSelection(); // 성별 선택 초기화
                isIdChecked = false; // 아이디 중복 확인 상태 초기화
                mainFrame.moveToBodyInfoSet(userId);
            } 
            else {
                CustomDialog.showDialog(mainFrame, "회원가입에 실패했습니다. 다시 시도해주세요.", "회원가입 오류");
                return;
            }
        } //-- if  회원가입 버튼 작동
        
        // 🔹 아이디 중복확인
        else if (e.getSource() == checkIdButton.getButton()) {
            String userId = userIdField.getText().trim();
            try {
                JoinDAO joinDAO = new JoinDAO();
                boolean idCheck = joinDAO.isUserIdExists(userId);
                if (idCheck) {
                    CustomDialog.showDialog(mainFrame, "이미 사용 중인 아이디입니다.", "알림");
                } else if(userId.isEmpty()) {
                	CustomDialog.showDialog(mainFrame, "아이디를 입력하세요.", null);
                } else if(!ValidationUtils.isValidUserId(userId)) {
                	CustomDialog.showDialog(mainFrame, "아이디는 6~20자 사이의 영문(대소문자), 특수문자, 숫자로 만들수 있습니다.", "알림");
                } else if(!idCheck) {
                	CustomDialog.showDialog(mainFrame, "사용 가능한 아이디입니다.", "알림");
                    isIdChecked=true;
                    return;
                } 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "오류 발생: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        } //--else if checkIdButton
        else if (e.getSource() == backButton) {
            mainFrame.showPanel("login");
        }
    }
} //-- actionPerformed // --End
