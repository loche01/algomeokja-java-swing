package panel;

import java.awt.*;
import javax.swing.*;

public class CalendarPanel extends JPanel {
    public CalendarPanel() {
    	setBounds(0, 155, 440, 700);
        setBackground(Color.red);
        setLayout(null);

        JLabel label = new JLabel(" 캘린더");
        label.setFont(new Font("Inter", Font.BOLD, 20));
        label.setBounds(124, 25, 192, 60);
        add(label);
    }
}

