package panel;

import DB.JoinDAO;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import javax.swing.*;
import main.MainFrame;
import model.UserBean;
import ui_n_utils.AppTheme;
import ui_n_utils.CustomDialog;
import ui_n_utils.PasswordDocumentFilter;
import ui_n_utils.PasswordVisibilityToggle;
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
	private PasswordVisibilityToggle passwordVisibilityToggle, confirmPasswordVisibilityToggle;
	private boolean isIdChecked = false;
	
	public JoinPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setBackground(AppTheme.BACKGROUND);
		setLayout(null);

		// 🔹 상단 제목
		JLabel titleLabel = new JLabel("회원가입");
		AppTheme.styleScreenTitle(titleLabel);
		titleLabel.setBounds(30, 16, 190, 36);
		add(titleLabel);

		JLabel descriptionLabel = new JLabel("기본 정보를 입력해 계정을 만들어주세요.");
		AppTheme.styleScreenDescription(descriptionLabel);
		descriptionLabel.setBounds(30, 52, 260, 24);
		add(descriptionLabel);

		// 🔹 구분선 (얇은 검은 테두리)
		JSeparator separator = new JSeparator();
		separator.setBounds(30, 88, AppTheme.CARD_WIDTH, 1);
		separator.setForeground(AppTheme.BORDER);
		add(separator);

		// 뒤로가기 버튼
		backButton = new JButton("로그인으로");
		backButton.setBounds(300, 22, 110, AppTheme.SECONDARY_BUTTON_HEIGHT);
		backButton.addActionListener(this);
		AppTheme.styleSecondaryButton(backButton);
		add(backButton);
		backButton.setBounds(300, 22, 110, AppTheme.SECONDARY_BUTTON_HEIGHT);
		backButton.addActionListener(this);
		add(backButton);

		// 🔹 아이디
		add(UIUtils.createRequiredLabel("아이디", AppTheme.TEXT, 32, 112,
				Font.SANS_SERIF, Font.BOLD, 14));
		// 아이디 필드
		userIdField = new RoundedComponent(245, AppTheme.INPUT_HEIGHT, 14, "textfield", "",
				AppTheme.BORDER, AppTheme.INPUT_BACKGROUND, AppTheme.TEXT,
				Font.SANS_SERIF, Font.PLAIN, 14);
		userIdField.setBounds(32, 134, 245, AppTheme.INPUT_HEIGHT);
		add(userIdField);
		// 중복확인 버튼
		checkIdButton = new RoundedComponent(100, AppTheme.INPUT_HEIGHT, 14,
				"button", "중복 확인", AppTheme.PRIMARY, AppTheme.CARD,
				AppTheme.PRIMARY_DARK, Font.SANS_SERIF, Font.BOLD, 13);
		checkIdButton.setBounds(290, 134, 100, AppTheme.INPUT_HEIGHT);
		checkIdButton.getButton().addActionListener(this);
		add(checkIdButton);
		// 형식 오류 메시지 (초기에는 보이지 않도록 설정)
		idErrLbl = UIUtils.createErrorLabel(32, 176, 11);
		idErrLbl.setFont(AppTheme.CAPTION_FONT);
		idErrLbl.setForeground(AppTheme.ERROR);
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
		add(UIUtils.createRequiredLabel("비밀번호", AppTheme.TEXT, 32, 196,
				Font.SANS_SERIF, Font.BOLD, 14));
		passwordField = new RoundedComponent(298, AppTheme.INPUT_HEIGHT, 14, "password", "",
				AppTheme.BORDER, AppTheme.INPUT_BACKGROUND, AppTheme.TEXT,
				Font.SANS_SERIF, Font.PLAIN, 14);
		passwordField.setBounds(32, 218, 298, AppTheme.INPUT_HEIGHT);
		add(passwordField);
		PasswordDocumentFilter.install((JPasswordField) passwordField.getComponent());
		passwordVisibilityToggle = PasswordVisibilityToggle.attach(
				(JPasswordField) passwordField.getComponent());
		JButton passwordVisibilityButton = passwordVisibilityToggle.getButton();
		AppTheme.styleSecondaryButton(passwordVisibilityButton);
		passwordVisibilityButton.setBounds(338, 218, 52, AppTheme.INPUT_HEIGHT);
		add(passwordVisibilityButton);
		// 형식 오류 메시지 (초기에는 보이지 않도록 설정)
		PwErrLbl = UIUtils.createErrorLabel(32, 260, 11);
		PwErrLbl.setFont(AppTheme.CAPTION_FONT);
		PwErrLbl.setForeground(AppTheme.ERROR);
		add(PwErrLbl);
		// 실시간으로 형식 검사 (KeyListener 적용)
		passwordField.getComponent().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				char[] userPassword = ((JPasswordField) passwordField.getComponent()).getPassword();
				try {
					if (userPassword.length == 0) {
						UIUtils.showError(PwErrLbl, "비밀번호를 입력하세요.");
					} else if (!ValidationUtils.isCreateUserPw(userPassword)) {
						UIUtils.showError(PwErrLbl, "비밀번호는 영문(대소문자) + 특수문자를 포함한 6~20자여야 합니다.");
					} else {
						UIUtils.hideError(PwErrLbl);
					}
				} finally {
					Arrays.fill(userPassword, '\0');
				}
			}
		}); // -- passwordField.addKeyListener

		// 🔹비밀번호 확인
		// 확인 필드
		add(UIUtils.createRequiredLabel("비밀번호 확인", AppTheme.TEXT, 32, 280,
				Font.SANS_SERIF, Font.BOLD, 14));
		confirmPasswordField = new RoundedComponent(298, AppTheme.INPUT_HEIGHT, 14, "password", "",
				AppTheme.BORDER, AppTheme.INPUT_BACKGROUND, AppTheme.TEXT,
				Font.SANS_SERIF, Font.PLAIN, 14);
		confirmPasswordField.setBounds(32, 302, 298, AppTheme.INPUT_HEIGHT);
		add(confirmPasswordField);
		PasswordDocumentFilter.install((JPasswordField) confirmPasswordField.getComponent());
		confirmPasswordVisibilityToggle = PasswordVisibilityToggle.attach(
				(JPasswordField) confirmPasswordField.getComponent());
		JButton confirmPasswordVisibilityButton = confirmPasswordVisibilityToggle.getButton();
		AppTheme.styleSecondaryButton(confirmPasswordVisibilityButton);
		confirmPasswordVisibilityButton.setBounds(338, 302, 52, AppTheme.INPUT_HEIGHT);
		add(confirmPasswordVisibilityButton);
		// 형식 오류 메시지 (초기에는 보이지 않도록 설정)
		PwCkErrLbl = UIUtils.createErrorLabel(32, 344, 11);
		PwCkErrLbl.setFont(AppTheme.CAPTION_FONT);
		PwCkErrLbl.setForeground(AppTheme.ERROR);
		add(PwCkErrLbl);
		confirmPasswordField.getComponent().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				char[] userPassword = ((JPasswordField) passwordField.getComponent()).getPassword();
				char[] confirmPassword = ((JPasswordField) confirmPasswordField.getComponent()).getPassword();
				try {
					if (confirmPassword.length == 0) {
						UIUtils.showError(PwCkErrLbl, "비밀번호 확인을 입력하세요.");
					} else if (!Arrays.equals(userPassword, confirmPassword)) {
						UIUtils.showError(PwCkErrLbl, "비밀번호가 일치하지 않습니다.");
					} else {
						UIUtils.hideError(PwCkErrLbl);
					}
				} finally {
					Arrays.fill(userPassword, '\0');
					Arrays.fill(confirmPassword, '\0');
				}
			}
		}); // -- confirmPasswordField.addKeyListener

		// 이름 필드
		add(UIUtils.createRequiredLabel("이름", AppTheme.TEXT, 32, 364,
				Font.SANS_SERIF, Font.BOLD, 14));
		userNameField = new RoundedComponent(358, AppTheme.INPUT_HEIGHT, 14, "textfield", "",
				AppTheme.BORDER, AppTheme.INPUT_BACKGROUND, AppTheme.TEXT,
				Font.SANS_SERIF, Font.PLAIN, 14);
		userNameField.setBounds(32, 386, 358, AppTheme.INPUT_HEIGHT);
		add(userNameField);

		// 이메일 필드
		add(UIUtils.createRequiredLabel("이메일", AppTheme.TEXT, 32, 444,
				Font.SANS_SERIF, Font.BOLD, 14));
		userEmailField = new RoundedComponent(358, AppTheme.INPUT_HEIGHT, 14, "textfield", "",
				AppTheme.BORDER, AppTheme.INPUT_BACKGROUND, AppTheme.TEXT,
				Font.SANS_SERIF, Font.PLAIN, 14);
		userEmailField.setBounds(32, 466, 358, AppTheme.INPUT_HEIGHT);
		add(userEmailField);

		// 휴대폰 필드
		add(UIUtils.createRequiredLabel("휴대폰 번호", AppTheme.TEXT, 32, 524,
				Font.SANS_SERIF, Font.BOLD, 14));
		userPhoneField = new RoundedComponent(358, AppTheme.INPUT_HEIGHT, 14, "textfield", "",
				AppTheme.BORDER, AppTheme.INPUT_BACKGROUND, AppTheme.TEXT,
				Font.SANS_SERIF, Font.PLAIN, 14);
		userPhoneField.setBounds(32, 546, 358, AppTheme.INPUT_HEIGHT);
		add(userPhoneField);
		JTextField phoneField = (JTextField) userPhoneField.getComponent();
        phoneField.setText("-을 포함하여 작성해주세요");
        phoneField.setForeground(AppTheme.TEXT_SECONDARY);
        phoneField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (phoneField.getText().equals("-을 포함하여 작성해주세요")) {
                    phoneField.setText("");
                    phoneField.setForeground(AppTheme.TEXT);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (phoneField.getText().isEmpty()) {
                    phoneField.setText("-을 포함하여 작성해주세요");
                    phoneField.setForeground(AppTheme.TEXT_SECONDARY);
                }
            }
        });
		
        // 생년월일 필드 추가
        add(UIUtils.createRequiredLabel("생년월일", AppTheme.TEXT, 32, 600,
                Font.SANS_SERIF, Font.BOLD, 14));
        userBirthdateField = new RoundedComponent(358, AppTheme.INPUT_HEIGHT, 14, "textfield", "",
                AppTheme.BORDER, AppTheme.INPUT_BACKGROUND, AppTheme.TEXT,
                Font.SANS_SERIF, Font.PLAIN, 14);
        userBirthdateField.setBounds(32, 622, 358, AppTheme.INPUT_HEIGHT);
        add(userBirthdateField);
        JTextField birthdateField = (JTextField) userBirthdateField.getComponent();
        birthdateField.setText("YYYY-MM-DD 형식으로 입력하세요");
        birthdateField.setForeground(AppTheme.TEXT_SECONDARY);
        birthdateField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (birthdateField.getText().equals("YYYY-MM-DD 형식으로 입력하세요")) {
                    birthdateField.setText("");
                    birthdateField.setForeground(AppTheme.TEXT);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (birthdateField.getText().isEmpty()) {
                    birthdateField.setText("YYYY-MM-DD 형식으로 입력하세요");
                    birthdateField.setForeground(AppTheme.TEXT_SECONDARY);
                }
            }
        });
        
        // 성별 선택 라디오 버튼 추가
        add(UIUtils.createRequiredLabel("성별", AppTheme.TEXT, 32, 676,
                Font.SANS_SERIF, Font.BOLD, 14));
        
        // 라디오 버튼 그룹 생성
        genderGroup = new ButtonGroup();
        
        // 남성 라디오 버튼
        maleRadioButton = new JRadioButton("남성");
        maleRadioButton.setFont(AppTheme.BODY_FONT);
        maleRadioButton.setForeground(AppTheme.TEXT);
        maleRadioButton.setBounds(32, 698, 80, 30);
        maleRadioButton.setBackground(AppTheme.BACKGROUND);
        genderGroup.add(maleRadioButton);
        add(maleRadioButton);
        
        // 여성 라디오 버튼
        femaleRadioButton = new JRadioButton("여성");
        femaleRadioButton.setFont(AppTheme.BODY_FONT);
        femaleRadioButton.setForeground(AppTheme.TEXT);
        femaleRadioButton.setBounds(120, 698, 80, 30);
        femaleRadioButton.setBackground(AppTheme.BACKGROUND);
        genderGroup.add(femaleRadioButton);
        add(femaleRadioButton);

		// 회원가입 버튼
		joinButton = new RoundedComponent(358, AppTheme.PRIMARY_BUTTON_HEIGHT, 14,
				"button", "회원가입", AppTheme.PRIMARY_DARK, AppTheme.PRIMARY_DARK,
				Color.WHITE, Font.SANS_SERIF, Font.BOLD, 14);
		joinButton.setBounds(32, 750, 358, AppTheme.PRIMARY_BUTTON_HEIGHT);
		joinButton.getButton().addActionListener(this);
		add(joinButton);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			if (passwordVisibilityToggle != null) {
				passwordVisibilityToggle.reset();
			}
			if (confirmPasswordVisibilityToggle != null) {
				confirmPasswordVisibilityToggle.reset();
			}
		}
	}


	@Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == joinButton.getButton()) {
            // 🔹 입력값 가져오기
            String userId = userIdField.getText();
            char[] userPassword = ((JPasswordField) passwordField.getComponent()).getPassword();
            char[] confirmPassword = ((JPasswordField) confirmPasswordField.getComponent()).getPassword();
            String userName = userNameField.getText();
            String userPhone = userPhoneField.getText();
            String userEmail = userEmailField.getText();
            String userBirthdate = userBirthdateField.getText();

            try {
            
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
            if (userId.isEmpty() || userPassword.length == 0 || confirmPassword.length == 0 ||
                userName.isEmpty() || userPhone.isEmpty() || userEmail.isEmpty() || 
                userBirthdate.isEmpty() || userGender.isEmpty()) {
                CustomDialog.showDialog(mainFrame, "모든 필수 입력 사항을 입력해주세요.", "회원가입 오류");
                return;
            }
            
            // 🔹 비밀번호 불일치 경고 무시 검증
            if(!Arrays.equals(userPassword, confirmPassword)) {
                CustomDialog.showDialog(mainFrame, "비밀번호가 일치하지 않습니다.", "회원가입 오류");
                return;
            }

            if (!ValidationUtils.isCreateUserPw(userPassword)) {
                CustomDialog.showDialog(mainFrame,
                        "비밀번호는 영문(대소문자) + 특수문자를 포함한 6~20자여야 합니다.",
                        "회원가입 오류");
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
            user.setUser_name(userName);
            user.setUser_phone(userPhone);
            user.setUser_email(userEmail);
            user.setUser_birthdate(userBirthdate);
            user.setUser_gender(userGender);
            
            // 🔹 회원가입 처리
            boolean isJoin = joinDAO.joinUserWithRawPassword(user, userPassword);
            
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
            } finally {
                Arrays.fill(userPassword, '\0');
                Arrays.fill(confirmPassword, '\0');
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
