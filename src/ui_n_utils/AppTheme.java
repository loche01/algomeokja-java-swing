package ui_n_utils;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 인증 및 회원정보 화면에서 사용하는 작은 공통 스타일 모음입니다.
 */
public final class AppTheme {
    public static final int HORIZONTAL_MARGIN = 30;
    public static final int CARD_WIDTH = 380;
    public static final int CARD_PADDING = 20;
    public static final int SECTION_GAP = 18;
    public static final int ROW_GAP = 14;
    public static final int SMALL_GAP = 8;
    public static final int INPUT_HEIGHT = 38;
    public static final int PRIMARY_BUTTON_HEIGHT = 44;
    public static final int SECONDARY_BUTTON_HEIGHT = 38;

    /**
     * Fixed-frame geometry used only by panels that explicitly opt into layout calculations.
     * Color, font, and component styling remain in the outer AppTheme class.
     */
    public static final class Layout {
        public static final int USER_HEADER_BOTTOM = 100;
        public static final int USER_CONTENT_TOP_WITHOUT_TAB = 90;
        public static final int USER_CONTENT_TOP_WITH_TAB = 140;
        public static final int USER_NAVIGATION_TOP = 826;
        public static final int USER_CONTENT_HEIGHT_WITHOUT_TAB =
                USER_NAVIGATION_TOP - USER_CONTENT_TOP_WITHOUT_TAB;
        public static final int USER_CONTENT_HEIGHT_WITH_TAB =
                USER_NAVIGATION_TOP - USER_CONTENT_TOP_WITH_TAB;

        private static final float OPTICAL_TOP_SHARE = 0.40f;
        private static final int MIN_EXTERNAL_MARGIN = 24;

        private Layout() {
        }

        public static int calculateOpticalBlockY(
                int contentTop, int contentBottom, int blockHeight) {
            int availableHeight = Math.max(0, contentBottom - contentTop);
            int remainingHeight = Math.max(0, availableHeight - blockHeight);
            int opticalY = contentTop + Math.round(remainingHeight * OPTICAL_TOP_SHARE);
            int minimumY = contentTop + MIN_EXTERNAL_MARGIN;
            int maximumY = contentBottom - MIN_EXTERNAL_MARGIN - blockHeight;

            if (maximumY < minimumY) {
                return Math.max(contentTop, contentBottom - blockHeight);
            }
            return Math.max(minimumY, Math.min(opticalY, maximumY));
        }
    }

    public static final Color BACKGROUND = new Color(0xF3F7F1);
    public static final Color CONTENT_BACKGROUND = new Color(0xF7F9F6);
    public static final Color CARD = Color.WHITE;
    public static final Color PRIMARY = new Color(0x557F4D);
    public static final Color PRIMARY_LIGHT = new Color(0xDCECD5);
    public static final Color ACCENT = new Color(0xB9DE9F);
    public static final Color PRIMARY_DARK = new Color(0x365B3B);
    public static final Color TEXT = new Color(0x26332A);
    public static final Color TEXT_SECONDARY = new Color(0x6A756C);
    public static final Color BORDER = new Color(0xD8E2D5);
    public static final Color INPUT_BACKGROUND = new Color(0xF7F9F6);
    public static final Color DISABLED = new Color(0xA8B0A9);
    public static final Color ERROR = new Color(0xB45353);
    public static final Color DANGER_BACKGROUND = new Color(0xFFF7F7);

    public static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 24);
    public static final Font SECTION_TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 18);
    public static final Font BODY_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    public static final Font BODY_BOLD_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    public static final Font CAPTION_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    public static final Font BUTTON_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 13);

    private AppTheme() {
    }

    public static void styleScreenTitle(JLabel label) {
        label.setFont(TITLE_FONT);
        label.setForeground(TEXT);
    }

    public static void styleScreenDescription(JLabel label) {
        label.setFont(BODY_FONT);
        label.setForeground(TEXT_SECONDARY);
    }

    public static void styleSectionTitle(JLabel label) {
        label.setFont(SECTION_TITLE_FONT);
        label.setForeground(PRIMARY_DARK);
    }

    public static void styleCaption(JLabel label) {
        label.setFont(CAPTION_FONT);
        label.setForeground(TEXT_SECONDARY);
    }

    public static void styleEmptyState(JLabel label) {
        label.setFont(BODY_FONT);
        label.setForeground(TEXT_SECONDARY);
        label.setHorizontalAlignment(JLabel.CENTER);
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
        button.setBackground(DANGER_BACKGROUND);
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
