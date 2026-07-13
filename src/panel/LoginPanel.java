package panel;

import DB.LoginDAO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import javax.swing.*;
import main.MainFrame;
import model.LoginManager;
import model.UserBean;
import ui_n_utils.AppTheme;
import ui_n_utils.CustomDialog;
import ui_n_utils.PasswordVisibilityToggle;
import ui_n_utils.RoundedComponent;
import ui_n_utils.UIUtils;

public class LoginPanel extends JPanel implements ActionListener {
	
	private RoundedComponent loginField, passwordField, loginButton, signUpButton;

	private JCheckBox rememberMe;
	private LoginDAO loginDAO;
	private MainFrame mainFrame;
	private JPasswordField passwordInput;
	private PasswordVisibilityToggle passwordVisibilityToggle;

	private Properties properties;
    private static final String PROPERTIES_FILE = "user.properties";
	
	public LoginPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		loginDAO = new LoginDAO(); // DAO 인스턴스 생성
		
		properties = new Properties();
		
		setBackground(AppTheme.BACKGROUND);
		setLayout(null);

		JLabel brandLabel = new JLabel("알고먹자", SwingConstants.CENTER);
		brandLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 42));
		brandLabel.setForeground(AppTheme.ACCENT);
		brandLabel.setBounds(30, 145, AppTheme.CARD_WIDTH, 58);
		add(brandLabel);

		JLabel titleLabel = new JLabel("건강한 식단 관리, 함께 시작해요", SwingConstants.CENTER);
		AppTheme.styleScreenTitle(titleLabel);
		titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 21));
		titleLabel.setBounds(30, 218, AppTheme.CARD_WIDTH, 36);
		add(titleLabel);

		// 로그인 필드 레이블
		add(UIUtils.createRequiredLabel("아이디", AppTheme.TEXT, 62, 300,
				Font.SANS_SERIF, Font.BOLD, 14));
		// 로그인 입력 필드 (둥근 입력 필드 사용)
		loginField = new RoundedComponent(315, AppTheme.INPUT_HEIGHT, 14, "textfield", "",
				AppTheme.BORDER, AppTheme.INPUT_BACKGROUND, AppTheme.TEXT,
				Font.SANS_SERIF, Font.PLAIN, 14);
		loginField.setBounds(62, 326, 315, AppTheme.INPUT_HEIGHT);
		add(loginField);

		
		// 비밀번호 필드 레이블
		add(UIUtils.createRequiredLabel("비밀번호", AppTheme.TEXT, 62, 390,
				Font.SANS_SERIF, Font.BOLD, 14));
		// 비밀번호 입력 필드 (둥근 입력 필드 사용)
		passwordField = new RoundedComponent(253, AppTheme.INPUT_HEIGHT, 14, "password", "",
				AppTheme.BORDER, AppTheme.INPUT_BACKGROUND, AppTheme.TEXT,
				Font.SANS_SERIF, Font.PLAIN, 14);
		passwordField.setBounds(62, 416, 253, AppTheme.INPUT_HEIGHT);
		add(passwordField);

		passwordInput = (JPasswordField) passwordField.getComponent();
		passwordInput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		passwordVisibilityToggle = PasswordVisibilityToggle.attach(passwordInput);
		JButton passwordVisibilityButton = passwordVisibilityToggle.getButton();
		AppTheme.styleSecondaryButton(passwordVisibilityButton);
		passwordVisibilityButton.setBounds(323, 416, 54, AppTheme.INPUT_HEIGHT);
		add(passwordVisibilityButton);

		JLabel passwordRuleLabel = new JLabel("6~20자, 영문과 특수문자를 함께 사용하세요.");
		AppTheme.styleCaption(passwordRuleLabel);
		passwordRuleLabel.setBounds(62, 461, 315, 18);
		add(passwordRuleLabel);

		// 엔터키 입력 시 로그인 시도 기능 추가
		// 아이디 필드에 KeyListener 추가
		if (loginField.getTextField() != null) {
			loginField.getTextField().addKeyListener(new java.awt.event.KeyAdapter() {
				@Override
				public void keyPressed(java.awt.event.KeyEvent e) {
					if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
						attemptLogin();
					}
				}
			});
		}
		
		// 비밀번호 필드에 KeyListener 추가
		if (passwordField.getTextField() != null) {
			passwordField.getTextField().addKeyListener(new java.awt.event.KeyAdapter() {
				@Override
				public void keyPressed(java.awt.event.KeyEvent e) {
					if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
						attemptLogin();
					}
				}
			});
		}

		// Remember me & Forgot password
		rememberMe = new JCheckBox("아이디 저장");
		rememberMe.setFont(AppTheme.CAPTION_FONT);
		rememberMe.setForeground(AppTheme.TEXT_SECONDARY);
		rememberMe.setBounds(62, 491, 120, 22);
		rememberMe.setBackground(AppTheme.BACKGROUND);
		rememberMe.setFocusPainted(false);
		add(rememberMe);

		JLabel forgotPass = new JLabel("아이디·비밀번호 찾기", SwingConstants.RIGHT);
		forgotPass.setFont(AppTheme.CAPTION_FONT);
		forgotPass.setForeground(AppTheme.PRIMARY_DARK);
		forgotPass.setBounds(225, 491, 152, 22);
		 // 🔹 클릭 가능하도록 커서 변경
		// 🔹 기본 HTML 스타일 적용 (마우스 오버 시 효과 주기)
		forgotPass.addMouseListener(new MouseAdapter() {
			@Override
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
				forgotPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
				forgotPass.setText("<html><u>아이디·비밀번호 찾기</u></html>");
		    }
		    @Override
		    public void mouseExited(java.awt.event.MouseEvent evt) {
		        forgotPass.setText("아이디·비밀번호 찾기");
		    }
			@Override
		    public void mouseClicked(java.awt.event.MouseEvent evt) {
		        mainFrame.showFindIdPwPanel(); // 🔹 입력 상태 초기화 후 ID/PW 찾기 패널로 이동
		    }
		});
		add(forgotPass);
		
		// 저장된 아이디 불러오기
        loadUserId();
		
		// 로그인 버튼 (둥근 버튼 사용)
		loginButton = new RoundedComponent(315, AppTheme.PRIMARY_BUTTON_HEIGHT, 14,
				"button", "로그인", AppTheme.PRIMARY_DARK, AppTheme.PRIMARY_DARK,
				Color.WHITE, Font.SANS_SERIF, Font.BOLD, 14);
		loginButton.setBounds(62, 535, 315, AppTheme.PRIMARY_BUTTON_HEIGHT);
		loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		loginButton.getButton().addActionListener(this);
		add(loginButton);

		// 회원가입 버튼 (둥근 버튼 사용)
		signUpButton = new RoundedComponent(315, AppTheme.PRIMARY_BUTTON_HEIGHT, 14,
				"button", "회원가입", AppTheme.PRIMARY, AppTheme.CARD,
				AppTheme.PRIMARY_DARK, Font.SANS_SERIF, Font.BOLD, 14);

		signUpButton.setBounds(62, 591, 315, AppTheme.PRIMARY_BUTTON_HEIGHT);
		signUpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		signUpButton.getButton().addActionListener(this);
		add(signUpButton);
	}

	// 로그인 시도 메서드 추가 (코드 중복 방지)
	private void attemptLogin() {
		String userId = loginField.getText();
		char[] userPassword = passwordInput.getPassword();

		try {
			if (userId.isEmpty() || userPassword.length == 0) {
				CustomDialog.showDialog(mainFrame, "아이디와 비밀번호를 입력하세요.", "로그인 오류");
				resetAfterFailure();
			} else {
				// 사용자 또는 관리자 정보를 가져옵니다
				UserBean user = loginDAO.getUserInfo(userId, userPassword);
				if (user != null) {
					// 로그인 성공 시 즉시 LoginManager에 사용자 정보를 저장
					LoginManager.getInstance().setCurrentUser(user);
					
					if (rememberMe.isSelected()) {
						saveUserId(userId);
					} else {
						clearUserId();
					}
					resetAfterSuccess();
					
					// 관리자 여부에 따라 다른 패널 표시
					if (user.isAdmin() || LoginManager.getInstance().isAdmin()) {
						mainFrame.showPanel("mainAdmin");
					} else {
						// 일반 사용자 패널로 이동
						mainFrame.showPanel("mainUser");
						
						// 일일현황 패널이 표시되도록 설정 (약간의 지연 후 실행)
						SwingUtilities.invokeLater(() -> {
							// MainFrame의 메서드를 사용하여 일일현황 패널로 이동
							mainFrame.showMainUserPanel("HomeDaily");
							// 네비게이션 바의 홈 버튼도 선택 상태로 설정
							ui_n_utils.NavUtil.selectHomeButton();
						});
					}
				} else {
					CustomDialog.showDialog(mainFrame, "아이디 혹은 비밀번호가 틀렸습니다.", "로그인 실패");
					resetAfterFailure();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			CustomDialog.showDialog(mainFrame, "로그인 처리 중 오류가 발생했습니다.", "로그인 오류");
			resetAfterFailure();
		} finally {
			Arrays.fill(userPassword, '\0');
		}
	}

	public void resetAfterFailure() {
		clearPasswordInput();
		resetPasswordVisibility();
	}

	public void resetAfterSuccess() {
		clearPasswordInput();
		resetPasswordVisibility();
	}

	public void resetForLogout() {
		clearPasswordInput();
		resetPasswordVisibility();

		if (rememberMe.isSelected()) {
			loadUserId();
		} else if (loginField.getTextField() != null) {
			loginField.getTextField().setText("");
		}
	}

	private void clearPasswordInput() {
		if (passwordInput != null) {
			passwordInput.setText("");
		}
	}

	private void resetPasswordVisibility() {
		if (passwordVisibilityToggle != null) {
			passwordVisibilityToggle.reset();
		}
	}

	@Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton.getButton()) {
            attemptLogin();
        } else if (e.getSource() == signUpButton.getButton()) {
            mainFrame.showPanel("join");
        }
    }
	
	
	private void saveUserId(String userId) {
        try (FileOutputStream out = new FileOutputStream(PROPERTIES_FILE)) {
            properties.setProperty("rememberedUserId", userId);
            properties.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserId() {
        try (FileInputStream in = new FileInputStream(PROPERTIES_FILE)) {
            properties.load(in);
            String savedUserId = properties.getProperty("rememberedUserId", "");
            
            if (loginField.getTextField() != null) { // 🔹 getTextField()가 null이 아닐 때만 설정
                loginField.getTextField().setText(savedUserId);
            }
            
            rememberMe.setSelected(!savedUserId.isEmpty());
        } catch (IOException e) {
            // 파일이 없을 경우 예외 발생 가능, 무시해도 됨
        }
    }

    private void clearUserId() {
        try (FileOutputStream out = new FileOutputStream(PROPERTIES_FILE)) {
            properties.remove("rememberedUserId");
            properties.store(out, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
