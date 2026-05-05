package ui_n_utils;

import java.awt.Color;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import java.awt.event.FocusEvent;

public class SmartTextField extends JTextField {
	private String placeholder;
	private boolean showingPlaceholder;

	public SmartTextField(String placeholder, int columns) {
		super(columns);
		this.placeholder = placeholder;
		this.showingPlaceholder = true;

		setForeground(Color.lightGray); // 기본 텍스트 색상 (회색)
		setText(placeholder);
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK)); // 검은색 밑줄 적용
		
		setOpaque(false);
		
		addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				if (showingPlaceholder) {
					setText(""); // 클릭 시 기본 텍스트 제거
					setForeground(Color.BLACK); // 입력 텍스트 색상 (검정)
					showingPlaceholder = false;
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (getText().isEmpty()) {
					setForeground(Color.LIGHT_GRAY); // 기본 텍스트 색상
					setText(placeholder); // 포커스를 잃으면 기본 텍스트 복원
					showingPlaceholder = true;
				}
			}
		});
	}

	public String getRealText() {
		return showingPlaceholder ? "" : getText();
	}

}
