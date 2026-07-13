package ui_n_utils;

import java.awt.Toolkit;
import java.util.regex.Pattern;
import javax.swing.JPasswordField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public final class PasswordDocumentFilter extends DocumentFilter {
    private static final int MAX_LENGTH = 20;
    private static final Pattern ALLOWED_CHARACTERS =
            Pattern.compile("^[A-Za-z0-9@#$%^&+=*!?]*$");

    public static void install(JPasswordField passwordField) {
        ((AbstractDocument) passwordField.getDocument())
                .setDocumentFilter(new PasswordDocumentFilter());
    }

    @Override
    public void insertString(FilterBypass filterBypass, int offset, String text,
            AttributeSet attributes) throws BadLocationException {
        if (text == null) {
            return;
        }
        replace(filterBypass, offset, 0, text, attributes);
    }

    @Override
    public void replace(FilterBypass filterBypass, int offset, int length, String text,
            AttributeSet attributes) throws BadLocationException {
        String replacement = text == null ? "" : text;
        int newLength = filterBypass.getDocument().getLength() - length + replacement.length();

        if (newLength <= MAX_LENGTH && ALLOWED_CHARACTERS.matcher(replacement).matches()) {
            filterBypass.replace(offset, length, replacement, attributes);
            return;
        }

        Toolkit.getDefaultToolkit().beep();
    }
}
