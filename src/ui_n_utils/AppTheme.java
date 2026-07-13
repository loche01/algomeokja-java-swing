package ui_n_utils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 인증 및 회원정보 화면에서 사용하는 작은 공통 스타일 모음입니다.
 */
public final class AppTheme {
    public static final int HORIZONTAL_MARGIN = 30;
    public static final int INPUT_HEIGHT = 38;

    public static final Color BACKGROUND = new Color(0xEEF6E9);
    public static final Color CARD = Color.WHITE;
    public static final Color PRIMARY = new Color(0x609056);
    public static final Color PRIMARY_DARK = new Color(0x3F6538);
    public static final Color TEXT = new Color(0x253024);
    public static final Color TEXT_SECONDARY = new Color(0x6B7469);
    public static final Color BORDER = new Color(0xD7E1D3);
    public static final Color INPUT_BACKGROUND = new Color(0xF5F7F4);
    public static final Color ERROR = new Color(0xB84A4A);

    public static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 24);
    public static final Font SECTION_TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 18);
    public static final Font BODY_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    public static final Font BODY_BOLD_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    public static final Font CAPTION_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    public static final Font BUTTON_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 13);

    private AppTheme() {
    }

    public static void styleCard(JPanel panel) {
        panel.setOpaque(true);
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createLineBorder(BORDER));
    }

    public static void stylePrimaryButton(AbstractButton button) {
        styleButtonBase(button);
        button.setBackground(PRIMARY_DARK);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(PRIMARY_DARK));
    }

    public static void styleSecondaryButton(AbstractButton button) {
        styleButtonBase(button);
        button.setBackground(CARD);
        button.setForeground(PRIMARY_DARK);
        button.setBorder(BorderFactory.createLineBorder(PRIMARY));
    }

    public static void styleDangerButton(AbstractButton button) {
        styleButtonBase(button);
        button.setBackground(CARD);
        button.setForeground(ERROR);
        button.setBorder(BorderFactory.createLineBorder(ERROR));
    }

    public static void styleInputField(JTextField field) {
        field.setOpaque(true);
        field.setFont(BODY_FONT);
        field.setForeground(TEXT);
        field.setBackground(INPUT_BACKGROUND);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
    }

    public static void styleReadOnlyField(JTextField field) {
        styleInputField(field);
        field.setBackground(new Color(0xE8ECE6));
        field.setForeground(TEXT_SECONDARY);
    }

    private static void styleButtonBase(AbstractButton button) {
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setFont(BUTTON_FONT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(0, 10, 0, 10));
    }
}
