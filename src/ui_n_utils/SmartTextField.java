package ui_n_utils;

import java.awt.event.FocusListener;

import javax.swing.JTextField;
import java.awt.event.FocusEvent;

public class SmartTextField extends JTextField {
	private String placeholder;
	private boolean showingPlaceholder;

	public SmartTextField(String placeholder, int columns) {
		super(columns);
		this.placeholder = placeholder;
		this.showingPlaceholder = true;

		AppTheme.styleInputField(this);
		setForeground(AppTheme.TEXT_SECONDARY);
		setText(placeholder);
		addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (showingPlaceholder) {
					setText(""); // 클릭 시 기본 텍스트 제거
					setForeground(AppTheme.TEXT);
					showingPlaceholder = false;
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (getText().isEmpty()) {
					setForeground(AppTheme.TEXT_SECONDARY);
					setText(placeholder); // 포커스를 잃으면 기본 텍스트 복원
					showingPlaceholder = true;
				}
			}
		});
	}

	public String getRealText() {
		return showingPlaceholder ? "" : getText();
	}

	public void resetToPlaceholder() {
		showingPlaceholder = true;
		setForeground(AppTheme.TEXT_SECONDARY);
		setText(placeholder);
		setCaretPosition(0);
	}

}
