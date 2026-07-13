package panel;

import DB.NoticeDAO;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import main.MainUserPanel;
import model.NoticeBean;
import ui_n_utils.AppTheme;

public class NoticePanel extends JPanel {
    private static final int CARD_WIDTH = 380;
    private static final int NOTICE_CARD_HEIGHT = 112;

    private final MainUserPanel mainUserPanel;
    private final NoticeDAO noticeDAO;
    private final NoticeListPanel noticeListPanel;
    private final JScrollPane noticeScrollPane;
    private int reloadGeneration;
    private int detailOpenGeneration;

    public NoticePanel(MainUserPanel mainUserPanel) {
        this.mainUserPanel = mainUserPanel;
        this.noticeDAO = new NoticeDAO();

        setLayout(null);
        setBackground(AppTheme.BACKGROUND);
        setBounds(0, 0, 440, 736);

        JLabel titleLabel = new JLabel("공지사항");
        titleLabel.setFont(AppTheme.TITLE_FONT);
        titleLabel.setForeground(AppTheme.TEXT);
        titleLabel.setBounds(30, 22, 220, 32);
        add(titleLabel);

        JLabel descriptionLabel = new JLabel("새로운 소식과 안내를 확인합니다.");
        descriptionLabel.setFont(AppTheme.CAPTION_FONT);
        descriptionLabel.setForeground(AppTheme.TEXT_SECONDARY);
        descriptionLabel.setBounds(30, 55, 260, 22);
        add(descriptionLabel);

        JButton backButton = new JButton("이전 화면");
        AppTheme.styleSecondaryButton(backButton);
        backButton.setBounds(300, 25, 110, 36);
        backButton.addActionListener(e -> mainUserPanel.goToPreviousPanel());
        add(backButton);

        noticeListPanel = new NoticeListPanel();
        noticeListPanel.setBackground(AppTheme.BACKGROUND);
        noticeListPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 12, 1));

        noticeScrollPane = new JScrollPane(noticeListPanel);
        noticeScrollPane.setBounds(30, 94, CARD_WIDTH, 610);
        noticeScrollPane.setBorder(BorderFactory.createEmptyBorder());
        noticeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        noticeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        noticeScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        noticeScrollPane.getViewport().setBackground(AppTheme.BACKGROUND);
        add(noticeScrollPane);
    }

    public void reloadNotices() {
        int requestedGeneration = ++reloadGeneration;
        new SwingWorker<List<NoticeBean>, Void>() {
            @Override
            protected List<NoticeBean> doInBackground() {
                return noticeDAO.getAllNotices();
            }

            @Override
            protected void done() {
                if (requestedGeneration != reloadGeneration) {
                    return;
                }
                try {
                    refreshNoticeList(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(getDialogParent(),
                            "공지사항 목록을 불러오지 못했습니다.",
                            "공지사항", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void refreshNoticeList(List<NoticeBean> notices) {
        noticeListPanel.removeAll();

        if (notices.isEmpty()) {
            JPanel emptyCard = new JPanel(new BorderLayout());
            AppTheme.styleCard(emptyCard);
            emptyCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            emptyCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            emptyCard.setPreferredSize(new Dimension(CARD_WIDTH, 120));

            JLabel emptyLabel = new JLabel("등록된 공지사항이 없습니다.", SwingConstants.CENTER);
            emptyLabel.setFont(AppTheme.BODY_FONT);
            emptyLabel.setForeground(AppTheme.TEXT_SECONDARY);
            emptyCard.add(emptyLabel, BorderLayout.CENTER);
            noticeListPanel.add(emptyCard);
        } else {
            for (NoticeBean notice : notices) {
                noticeListPanel.add(createNoticeCard(notice));
                noticeListPanel.add(Box.createVerticalStrut(14));
            }
        }

        noticeListPanel.revalidate();
        noticeListPanel.repaint();
        SwingUtilities.invokeLater(() -> noticeScrollPane.getVerticalScrollBar().setValue(0));
    }

    private JPanel createNoticeCard(NoticeBean notice) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        AppTheme.styleCard(card);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, NOTICE_CARD_HEIGHT));
        card.setPreferredSize(new Dimension(CARD_WIDTH, NOTICE_CARD_HEIGHT));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        String fullTitle = safeText(notice.getNotice_title(), "제목 없음");
        JLabel titleLabel = new JLabel();
        titleLabel.setFont(AppTheme.BODY_BOLD_FONT);
        titleLabel.setForeground(AppTheme.TEXT);
        titleLabel.setText(ellipsize(fullTitle, titleLabel.getFontMetrics(titleLabel.getFont()), 332));
        titleLabel.setToolTipText(fullTitle);

        JPanel metadataPanel = new JPanel();
        metadataPanel.setOpaque(false);
        metadataPanel.setLayout(new BoxLayout(metadataPanel, BoxLayout.Y_AXIS));

        JLabel authorLabel = new JLabel("작성자  " + safeText(notice.getAdmin_id(), "정보 없음"));
        authorLabel.setFont(AppTheme.CAPTION_FONT);
        authorLabel.setForeground(AppTheme.TEXT_SECONDARY);

        JLabel dateLabel = new JLabel("작성일  " + formatDate(notice));
        dateLabel.setFont(AppTheme.CAPTION_FONT);
        dateLabel.setForeground(AppTheme.TEXT_SECONDARY);

        metadataPanel.add(authorLabel);
        metadataPanel.add(Box.createVerticalStrut(3));
        metadataPanel.add(dateLabel);

        MouseAdapter openListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openNotice(notice.getNotice_num());
            }
        };

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(metadataPanel, BorderLayout.CENTER);
        addOpenListener(card, openListener);
        card.setToolTipText("공지사항 상세 보기");
        return card;
    }

    private void openNotice(int noticeId) {
        int requestedGeneration = ++detailOpenGeneration;
        new SwingWorker<NoticeBean, Void>() {
            @Override
            protected NoticeBean doInBackground() {
                return noticeDAO.getNoticeById(noticeId);
            }

            @Override
            protected void done() {
                if (requestedGeneration != detailOpenGeneration) {
                    return;
                }
                try {
                    NoticeBean selectedNotice = get();
                    if (selectedNotice == null) {
                        JOptionPane.showMessageDialog(getDialogParent(),
                                "공지사항을 불러오지 못했습니다.",
                                "공지사항", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    mainUserPanel.getNoticeDetailPanel().updateNoticeDetail(selectedNotice);
                    mainUserPanel.showPanel("noticeDetailPanel");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(getDialogParent(),
                            "공지사항을 불러오지 못했습니다.",
                            "공지사항", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void addOpenListener(Component component, MouseAdapter listener) {
        component.addMouseListener(listener);
        component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (component instanceof java.awt.Container) {
            for (Component child : ((java.awt.Container) component).getComponents()) {
                addOpenListener(child, listener);
            }
        }
    }

    private String formatDate(NoticeBean notice) {
        if (notice.getNotice_time() == null) {
            return "정보 없음";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(notice.getNotice_time());
    }

    private String safeText(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String ellipsize(String text, FontMetrics metrics, int maxWidth) {
        if (metrics.stringWidth(text) <= maxWidth) {
            return text;
        }
        String ellipsis = "…";
        int end = text.length();
        while (end > 0 && metrics.stringWidth(text.substring(0, end) + ellipsis) > maxWidth) {
            end--;
        }
        return text.substring(0, end) + ellipsis;
    }

    private Component getDialogParent() {
        Window window = SwingUtilities.getWindowAncestor(this);
        return window != null ? window : this;
    }

    private static class NoticeListPanel extends JPanel implements Scrollable {
        NoticeListPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return Math.max(visibleRect.height - 32, 16);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
