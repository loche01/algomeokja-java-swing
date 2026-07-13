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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import main.MainAdminPanel;
import model.NoticeBean;
import ui_n_utils.AppTheme;

public class NoticeAdminPanel extends JPanel {
    private static final int CARD_WIDTH = 380;
    private static final int NOTICE_CARD_HEIGHT = 118;

    private final NoticeDAO noticeDAO;
    private final MainAdminPanel mainAdminPanel;
    private final NoticeListPanel noticeListPanel;
    private final JScrollPane scrollPane;
    private final Map<JCheckBox, Integer> noticeSelections = new LinkedHashMap<>();
    private final JButton deleteButton;
    private int reloadGeneration;

    public NoticeAdminPanel(MainAdminPanel mainAdminPanel) {
        this.mainAdminPanel = mainAdminPanel;
        this.noticeDAO = new NoticeDAO();

        setLayout(null);
        setBackground(AppTheme.BACKGROUND);
        setBounds(0, 0, 440, 856);

        JLabel titleLabel = new JLabel("공지사항 관리");
        titleLabel.setFont(AppTheme.TITLE_FONT);
        titleLabel.setForeground(AppTheme.TEXT);
        titleLabel.setBounds(30, 22, 220, 32);
        add(titleLabel);

        JLabel descriptionLabel = new JLabel("공지사항을 등록하고 관리합니다.");
        descriptionLabel.setFont(AppTheme.CAPTION_FONT);
        descriptionLabel.setForeground(AppTheme.TEXT_SECONDARY);
        descriptionLabel.setBounds(30, 55, 250, 22);
        add(descriptionLabel);

        JButton writeButton = new JButton("공지 작성");
        AppTheme.stylePrimaryButton(writeButton);
        writeButton.setBounds(288, 25, 122, 38);
        writeButton.addActionListener(e -> mainAdminPanel.showPanel("NoticeWrite"));
        add(writeButton);

        noticeListPanel = new NoticeListPanel();
        noticeListPanel.setBackground(AppTheme.BACKGROUND);
        noticeListPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 12, 1));

        scrollPane = new JScrollPane(noticeListPanel);
        scrollPane.setBounds(30, 94, CARD_WIDTH, 610);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(AppTheme.BACKGROUND);
        add(scrollPane);

        deleteButton = new JButton("선택 삭제");
        AppTheme.styleDangerButton(deleteButton);
        deleteButton.setBounds(30, 720, CARD_WIDTH, 42);
        deleteButton.addActionListener(e -> deleteSelectedNotices());
        add(deleteButton);
    }

    public void loadNotices() {
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
                    refreshNotices(get());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(getDialogParent(),
                            "공지사항 목록을 불러오지 못했습니다.",
                            "공지사항 관리", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    public void refreshNotices(List<NoticeBean> notices) {
        noticeListPanel.removeAll();
        noticeSelections.clear();

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
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }

    private JPanel createNoticeCard(NoticeBean notice) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        AppTheme.styleCard(card);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, NOTICE_CARD_HEIGHT));
        card.setPreferredSize(new Dimension(CARD_WIDTH, NOTICE_CARD_HEIGHT));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 12)));

        JCheckBox checkBox = new JCheckBox();
        checkBox.setOpaque(false);
        checkBox.setToolTipText("삭제할 공지사항 선택");
        checkBox.setPreferredSize(new Dimension(28, 28));
        noticeSelections.put(checkBox, notice.getNotice_num());

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        contentPanel.setToolTipText("공지사항 상세 보기");

        String fullTitle = safeText(notice.getNotice_title(), "제목 없음");
        JLabel titleLabel = new JLabel();
        titleLabel.setFont(AppTheme.BODY_BOLD_FONT);
        titleLabel.setForeground(AppTheme.TEXT);
        titleLabel.setText(ellipsize(fullTitle, titleLabel.getFontMetrics(titleLabel.getFont()), 292));
        titleLabel.setToolTipText(fullTitle);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel authorLabel = new JLabel("작성자  " + safeText(notice.getAdmin_id(), "정보 없음"));
        authorLabel.setFont(AppTheme.CAPTION_FONT);
        authorLabel.setForeground(AppTheme.TEXT_SECONDARY);
        authorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel("작성일  " + formatDate(notice));
        dateLabel.setFont(AppTheme.CAPTION_FONT);
        dateLabel.setForeground(AppTheme.TEXT_SECONDARY);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(7));
        contentPanel.add(authorLabel);
        contentPanel.add(Box.createVerticalStrut(3));
        contentPanel.add(dateLabel);

        MouseAdapter openListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openNoticeDetail(notice.getNotice_num());
            }
        };
        addOpenListener(contentPanel, openListener);

        card.add(checkBox, BorderLayout.WEST);
        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }

    private void openNoticeDetail(int noticeId) {
        AdminNoticeDetailPanel detailPanel = mainAdminPanel.getAdminNoticeDetailPanel();
        detailPanel.loadNoticeDetail(noticeId);
        mainAdminPanel.showPanel("AdminNoticeDetail");
    }

    private void deleteSelectedNotices() {
        List<Integer> selectedIds = noticeSelections.entrySet().stream()
                .filter(entry -> entry.getKey().isSelected())
                .map(Map.Entry::getValue)
                .toList();

        if (selectedIds.isEmpty()) {
            JOptionPane.showMessageDialog(getDialogParent(), "삭제할 공지사항을 선택하세요.",
                    "선택 삭제", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(getDialogParent(),
                "선택한 공지사항을 삭제하시겠습니까?", "삭제 확인",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        deleteButton.setEnabled(false);
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                boolean allDeleted = true;
                for (int noticeId : selectedIds) {
                    if (!noticeDAO.deleteNotice(noticeId)) {
                        allDeleted = false;
                    }
                }
                return allDeleted;
            }

            @Override
            protected void done() {
                try {
                    boolean allDeleted = get();
                    loadNotices();
                    if (allDeleted) {
                        JOptionPane.showMessageDialog(getDialogParent(),
                                "선택한 공지사항을 삭제했습니다.",
                                "삭제 완료", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(getDialogParent(),
                                "일부 공지사항을 삭제하지 못했습니다.",
                                "삭제 실패", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(getDialogParent(),
                            "공지사항 삭제 중 오류가 발생했습니다.",
                            "삭제 실패", JOptionPane.ERROR_MESSAGE);
                } finally {
                    deleteButton.setEnabled(true);
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
