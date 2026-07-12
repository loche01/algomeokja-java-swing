package panel;

import DB.NoticeDAO;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import main.MainUserPanel;
import model.NoticeBean;
import ui_n_utils.RoundedComponent;


public class NoticePanel extends JPanel {
    private RoundedComponent noticePanel;
    private NoticeDAO noticeDAO;

    private JPanel headerPanel; // ✅ 헤더 패널을 따로 관리
    private JPanel noticeListPanel;
    private JScrollPane noticeScrollPane;

    public NoticePanel(MainUserPanel mainUserPanel) {
        this.noticeDAO = new NoticeDAO(); // NoticeDAO 객체 생성

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadNotices(mainUserPanel); // 자동 갱신
            }
        });

        setLayout(null);
        setBackground(new Color(0xC0E993));
        setBounds(0, 0, 440, 956);

        // 🔹 배경 패널
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBounds(0, 0, 440, 956);
        backgroundPanel.setBackground(new Color(0xC0E993));
        backgroundPanel.setLayout(null);
        add(backgroundPanel);

        // 🔹 공지사항 패널
        noticePanel = new RoundedComponent(400, 670, 30, "panel", "",
                new Color(192, 233, 147), Color.white, Color.black, " ", 0, 0);
        noticePanel.setBounds(12, 40, 400, 670);
        noticePanel.setBackground(Color.WHITE);
        noticePanel.setLayout(null);
        backgroundPanel.add(noticePanel);

        // ✅ 헤더 패널 생성 (removeAll 방지)
        headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBounds(0, 0, 400, 90);
        headerPanel.setBackground(Color.WHITE);
        noticePanel.add(headerPanel);

        JLabel titleLabel = new JLabel("공지사항");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 30));
        titleLabel.setBounds(20, 10, 200, 40);
        headerPanel.add(titleLabel);
        
        // 뒤로가기 버튼 추가
        JButton backButton = new JButton("X");
        backButton.setBounds(350, 10, 50, 50);
        backButton.setFont(new Font("Inter", Font.BOLD, 24));
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        headerPanel.add(backButton);
        
        // 뒤로가기 버튼 클릭 시 이전 패널로 돌아가기
        backButton.addActionListener(e -> {
            if (mainUserPanel != null) {
                // 이전 패널로 돌아가기
                mainUserPanel.goToPreviousPanel();
            } else {
                System.out.println("❌ mainUserPanel이 null입니다!");
            }
        });

        JSeparator separator = new JSeparator();
        separator.setBounds(10, 70, 380, 1);
        separator.setForeground(Color.BLACK);
        headerPanel.add(separator);

        noticeListPanel = new JPanel();
        noticeListPanel.setLayout(new BoxLayout(noticeListPanel, BoxLayout.Y_AXIS));
        noticeListPanel.setBackground(Color.WHITE);

        noticeScrollPane = new JScrollPane(noticeListPanel);
        noticeScrollPane.setBounds(10, 90, 380, 550);
        noticeScrollPane.setBorder(BorderFactory.createEmptyBorder());
        noticeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        noticeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        noticeScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        noticePanel.add(noticeScrollPane);

        // 🔹 공지사항 목록 불러오기 (DB 연동)
        loadNotices(mainUserPanel);
    }

    // 🔹 공지사항 목록 불러오기 (DB 연동)
    private void loadNotices(MainUserPanel mainUserPanel) {
        int scrollPosition = noticeScrollPane.getVerticalScrollBar().getValue();
        List<NoticeBean> notices = noticeDAO.getAllNotices();
        noticeListPanel.removeAll();

        if (notices.isEmpty()) {
            JLabel noDataLabel = new JLabel("📢 등록된 공지사항이 없습니다.");
            noDataLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            noDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noDataLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            noDataLabel.setMaximumSize(new Dimension(360, 100));
            noDataLabel.setPreferredSize(new Dimension(360, 100));
            noticeListPanel.add(noDataLabel);
        } else {
            for (NoticeBean notice : notices) {
                noticeListPanel.add(createNoticeCard(notice, mainUserPanel));
                noticeListPanel.add(Box.createVerticalStrut(10));
            }
        }

        noticeListPanel.revalidate();
        noticeListPanel.repaint();
        SwingUtilities.invokeLater(() -> noticeScrollPane.getVerticalScrollBar().setValue(scrollPosition));
    }

    private JPanel createNoticeCard(NoticeBean notice, MainUserPanel mainUserPanel) {
        JPanel card = new JPanel(null);
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setMaximumSize(new Dimension(360, 82));
        card.setPreferredSize(new Dimension(360, 82));
        card.setBackground(new Color(0xF8FAF6));
        card.setBorder(BorderFactory.createLineBorder(new Color(0xD7E2D0)));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel numberLabel = new JLabel("#" + notice.getNotice_num());
        numberLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 12));
        numberLabel.setForeground(new Color(0x609056));
        numberLabel.setBounds(12, 8, 45, 20);

        JLabel titleLabel = new JLabel(notice.getNotice_title());
        titleLabel.setFont(new Font("Malgun Gothic", Font.BOLD, 16));
        titleLabel.setBounds(55, 8, 290, 24);

        JLabel authorLabel = new JLabel("작성자: " + notice.getAdmin_id());
        authorLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 12));
        authorLabel.setForeground(Color.DARK_GRAY);
        authorLabel.setBounds(55, 42, 135, 20);

        JLabel dateLabel = new JLabel(new SimpleDateFormat("yyyy-MM-dd").format(new Date(notice.getNotice_time().getTime())));
        dateLabel.setFont(new Font("Malgun Gothic", Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        dateLabel.setBounds(220, 42, 125, 20);

        java.awt.event.MouseAdapter openListener = new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                NoticeBean selectedNotice = noticeDAO.getNoticeById(notice.getNotice_num());
                if (selectedNotice == null) {
                    JOptionPane.showMessageDialog(NoticePanel.this,
                            "공지사항을 불러오지 못했습니다.", "공지사항", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                mainUserPanel.getNoticeDetailPanel().updateNoticeDetail(selectedNotice);
                mainUserPanel.showPanel("noticeDetailPanel");
            }
        };

        card.addMouseListener(openListener);
        numberLabel.addMouseListener(openListener);
        titleLabel.addMouseListener(openListener);
        authorLabel.addMouseListener(openListener);
        dateLabel.addMouseListener(openListener);
        card.add(numberLabel);
        card.add(titleLabel);
        card.add(authorLabel);
        card.add(dateLabel);
        return card;
    }
}
