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
		
		setBackground(Color.WHITE);
		setLayout(null);

		// 상단 문구
		JLabel titleLabel = new JLabel("\"건강한 식단 관리, 함께 시작해요!\"", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Inter", Font.BOLD, 22));
		titleLabel.setBounds(48, 263, 345, 59);
		add(titleLabel);

		// 로그인 필드 레이블
		add(UIUtils.createRequiredLabel("아이디", Color.black,64, 360, "Inter",Font.BOLD, 15));
		// 로그인 입력 필드 (둥근 입력 필드 사용)
		loginField = new RoundedComponent(315, 40, 20, "textfield", ""
				, new Color(217, 217, 217), new Color(217, 217, 217), Color.black, 
				"Inter",Font.PLAIN, 14);
		loginField.setBounds(62, 398, 315, 40);
		add(loginField);

		
		// 비밀번호 필드 레이블
		add(UIUtils.createRequiredLabel("비밀번호", Color.black, 62, 459, "Inter", Font.BOLD, 15));
		// 비밀번호 입력 필드 (둥근 입력 필드 사용)
		passwordField = new RoundedComponent(315, 40, 20, "password", "",
				new Color(217, 217, 217), new Color(217, 217, 217), Color.black,
				"Inter", Font.PLAIN, 14);
		passwordField.setBounds(62, 495, 315, 40);
		add(passwordField);

		passwordInput = (JPasswordField) passwordField.getComponent();
		passwordInput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		passwordVisibilityToggle = PasswordVisibilityToggle.attach(passwordInput);
		JButton passwordVisibilityButton = passwordVisibilityToggle.getButton();
		passwordVisibilityButton.setBounds(382, 495, 50, 40);
		add(passwordVisibilityButton);

		JLabel passwordRuleLabel = new JLabel("6~20자, 영문과 특수문자를 함께 사용하세요.");
		passwordRuleLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 11));
		passwordRuleLabel.setForeground(Color.DARK_GRAY);
		passwordRuleLabel.setBounds(64, 540, 313, 18);
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
		rememberMe = new JCheckBox("Remember me");
		rememberMe.setFont(new Font("Inter", Font.PLAIN, 12));
		rememberMe.setBounds(64, 570, 120, 20);
		rememberMe.setBackground(Color.WHITE);
		rememberMe.setFocusPainted(false);
		add(rememberMe);

		JLabel forgotPass = new JLabel("Forgot password/ID?");
		forgotPass.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		forgotPass.setForeground(Color.BLACK);
		forgotPass.setBounds(260, 570, 150, 20);
		 // 🔹 클릭 가능하도록 커서 변경
		// 🔹 기본 HTML 스타일 적용 (마우스 오버 시 효과 주기)
		forgotPass.addMouseListener(new MouseAdapter() {
			@Override
		    public void mouseEntered(java.awt.event.MouseEvent evt) {
				forgotPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
				forgotPass.setText("<html><u><span style='color:blue;'>Forgot password/ID?</span></u></html>"); // 🔹 마우스 올리면 색상 변경
		    }
		    @Override
		    public void mouseExited(java.awt.event.MouseEvent evt) {
		        forgotPass.setText("Forgot password/ID?"); // 🔹 원래 색상으로 복구
		    }
			@Override
		    public void mouseClicked(java.awt.event.MouseEvent evt) {
		        mainFrame.showPanel("findIdPw"); // 🔹 ID/PW 찾기 패널로 이동
		    }
		});
		add(forgotPass);
		
		// 저장된 아이디 불러오기
        loadUserId();
		
		// 로그인 버튼 (둥근 버튼 사용)
		loginButton = new RoundedComponent(132, 40, 20, "button", "Login", 
				new Color(192, 233, 147), new Color(192, 233, 147), Color.black, 
				"Inter",Font.BOLD, 14);
		loginButton.setBounds(64, 598, 132, 40);
		loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		loginButton.getButton().addActionListener(this);
		add(loginButton);

		// 회원가입 버튼 (둥근 버튼 사용)
		signUpButton = new RoundedComponent(132, 40, 20, "button", "회원가입", 
				Color.BLACK, Color.BLACK, Color.WHITE, "Inter",Font.BOLD, 14);
		
		signUpButton.setBounds(242, 598, 135, 40);
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
