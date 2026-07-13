package panel;

import DB.NoticeFileDAO;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Window;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import main.MainUserPanel;
import model.NoticeBean;
import ui_n_utils.AppTheme;

public class NoticeDetailPanel extends JPanel {
    private final MainUserPanel mainUserPanel;
    private final NoticeFileDAO noticeFileDAO;
    private final JTextArea titleArea;
    private final JScrollPane titleScrollPane;
    private final JLabel authorLabel;
    private final JLabel dateLabel;
    private final JTextArea contentArea;
    private final JScrollPane contentScrollPane;
    private final JPanel fileListPanel;
    private final JScrollPane fileScrollPane;
    private int fileLoadGeneration;

    public NoticeDetailPanel(MainUserPanel mainUserPanel) {
        this.mainUserPanel = mainUserPanel;
        this.noticeFileDAO = new NoticeFileDAO();

        setLayout(null);
        setBackground(AppTheme.BACKGROUND);
        setBounds(0, 0, 440, 736);

        JPanel card = new JPanel(null);
        AppTheme.styleCard(card);
        card.setBounds(AppTheme.HORIZONTAL_MARGIN, 18, AppTheme.CARD_WIDTH, 690);
        add(card);

        JLabel screenTitle = new JLabel("공지사항 상세");
        AppTheme.styleScreenTitle(screenTitle);
        screenTitle.setBounds(20, 16, 220, 34);
        card.add(screenTitle);

        JButton listButton = new JButton("목록으로");
        AppTheme.styleSecondaryButton(listButton);
        listButton.setBounds(268, 16, 92, 36);
        listButton.addActionListener(e -> mainUserPanel.showPanel("Notice"));
        card.add(listButton);

        JLabel descriptionLabel = new JLabel("공지 내용과 첨부파일을 확인합니다.");
        AppTheme.styleScreenDescription(descriptionLabel);
        descriptionLabel.setBounds(20, 52, 235, 22);
        card.add(descriptionLabel);

        titleArea = new JTextArea();
        titleArea.setFont(AppTheme.SECTION_TITLE_FONT);
        titleArea.setForeground(AppTheme.TEXT);
        titleArea.setBackground(AppTheme.CARD);
        titleArea.setEditable(false);
        titleArea.setFocusable(false);
        titleArea.setLineWrap(true);
        titleArea.setWrapStyleWord(true);
        titleArea.setBorder(BorderFactory.createEmptyBorder());

        titleScrollPane = new JScrollPane(titleArea);
        titleScrollPane.setBounds(20, 82, 340, 66);
        titleScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        titleScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        titleScrollPane.setBorder(BorderFactory.createEmptyBorder());
        titleScrollPane.getViewport().setBackground(AppTheme.CARD);
        card.add(titleScrollPane);

        authorLabel = createMetadataLabel();
        authorLabel.setBounds(20, 153, 340, 20);
        card.add(authorLabel);

        dateLabel = createMetadataLabel();
        dateLabel.setBounds(20, 176, 340, 20);
        card.add(dateLabel);

        JLabel contentLabel = new JLabel("본문");
        contentLabel.setFont(AppTheme.BODY_BOLD_FONT);
        contentLabel.setForeground(AppTheme.TEXT);
        contentLabel.setBounds(20, 209, 100, 22);
        card.add(contentLabel);

        contentArea = new JTextArea();
        contentArea.setFont(AppTheme.BODY_FONT);
        contentArea.setForeground(AppTheme.TEXT);
        contentArea.setBackground(AppTheme.INPUT_BACKGROUND);
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        contentArea.setEditable(false);
        contentArea.setMargin(new java.awt.Insets(10, 10, 10, 10));

        contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBounds(20, 235, 340, 280);
        contentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentScrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        card.add(contentScrollPane);

        JLabel fileTitleLabel = new JLabel("첨부파일");
        fileTitleLabel.setFont(AppTheme.BODY_BOLD_FONT);
        fileTitleLabel.setForeground(AppTheme.TEXT);
        fileTitleLabel.setBounds(20, 530, 120, 22);
        card.add(fileTitleLabel);

        fileListPanel = new JPanel();
        fileListPanel.setLayout(new BoxLayout(fileListPanel, BoxLayout.Y_AXIS));
        fileListPanel.setBackground(AppTheme.CARD);
        fileListPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        fileScrollPane = new JScrollPane(fileListPanel);
        fileScrollPane.setBounds(20, 556, 340, 108);
        fileScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        fileScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        fileScrollPane.getVerticalScrollBar().setUnitIncrement(14);
        fileScrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        card.add(fileScrollPane);

        resetNoticeDetail();
    }

    public void updateNoticeDetail(NoticeBean notice) {
        resetNoticeDetail();
        if (notice == null) {
            JOptionPane.showMessageDialog(getDialogParent(),
                    "공지사항을 불러오지 못했습니다.", "공지사항", JOptionPane.ERROR_MESSAGE);
            return;
        }

        titleArea.setText(safeText(notice.getNotice_title(), "제목 없음"));
        titleArea.setToolTipText(safeText(notice.getNotice_title(), "제목 없음"));
        authorLabel.setText("작성자  " + safeText(notice.getAdmin_id(), "정보 없음"));
        dateLabel.setText("작성일  " + formatDate(notice));
        contentArea.setText(safeText(notice.getNotice_content(), "내용이 없습니다."));
        contentArea.setCaretPosition(0);

        int requestedGeneration = ++fileLoadGeneration;
        new SwingWorker<List<Map<String, Object>>, Void>() {
            @Override
            protected List<Map<String, Object>> doInBackground() {
                return noticeFileDAO.getFilesByNoticeId(notice.getNotice_num());
            }

            @Override
            protected void done() {
                if (requestedGeneration != fileLoadGeneration) {
                    return;
                }
                try {
                    showFiles(get());
                } catch (Exception e) {
                    showFiles(List.of());
                    JOptionPane.showMessageDialog(getDialogParent(),
                            "첨부파일 목록을 불러오지 못했습니다.",
                            "첨부파일", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
        resetScrollPositions();
    }

    public void resetScrollPositions() {
        contentArea.setCaretPosition(0);
        SwingUtilities.invokeLater(() -> {
            titleScrollPane.getVerticalScrollBar().setValue(0);
            contentScrollPane.getVerticalScrollBar().setValue(0);
            fileScrollPane.getVerticalScrollBar().setValue(0);
        });
    }

    private void resetNoticeDetail() {
        titleArea.setText("");
        titleArea.setToolTipText(null);
        authorLabel.setText("");
        dateLabel.setText("");
        contentArea.setText("");
        fileListPanel.removeAll();
        fileListPanel.revalidate();
        fileListPanel.repaint();
        resetScrollPositions();
    }

    private void showFiles(List<Map<String, Object>> files) {
        fileListPanel.removeAll();
        if (files.isEmpty()) {
            JLabel emptyLabel = new JLabel("첨부파일 없음");
            emptyLabel.setFont(AppTheme.CAPTION_FONT);
            emptyLabel.setForeground(AppTheme.TEXT_SECONDARY);
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            fileListPanel.add(emptyLabel);
        } else {
            for (Map<String, Object> file : files) {
                String fileName = safeText((String) file.get("fileName"), "이름 없는 파일");
                Object fileIdValue = file.get("fileId");
                if (!(fileIdValue instanceof Integer)) {
                    continue;
                }
                int fileId = (Integer) fileIdValue;
                JButton fileButton = createFileButton(fileName, fileId);
                fileListPanel.add(fileButton);
                fileListPanel.add(Box.createVerticalStrut(6));
            }
        }
        fileListPanel.revalidate();
        fileListPanel.repaint();
    }

    private JButton createFileButton(String fileName, int fileId) {
        JButton fileButton = new JButton();
        AppTheme.styleSecondaryButton(fileButton);
        fileButton.setHorizontalAlignment(JButton.LEFT);
        fileButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        fileButton.setPreferredSize(new Dimension(310, 32));
        fileButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        FontMetrics metrics = fileButton.getFontMetrics(fileButton.getFont());
        fileButton.setText(ellipsize(fileName, metrics, 270));
        fileButton.setToolTipText(fileName);
        fileButton.addActionListener(e -> {
            fileButton.setEnabled(false);
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    return noticeFileDAO.downloadFile(fileId);
                }

                @Override
                protected void done() {
                    try {
                        if (!get()) {
                            JOptionPane.showMessageDialog(getDialogParent(),
                                    "첨부파일을 열 수 없습니다.",
                                    "첨부파일", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception exception) {
                        JOptionPane.showMessageDialog(getDialogParent(),
                                "첨부파일을 열 수 없습니다.",
                                "첨부파일", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        fileButton.setEnabled(true);
                    }
                }
            }.execute();
        });
        return fileButton;
    }

    private JLabel createMetadataLabel() {
        JLabel label = new JLabel();
        label.setFont(AppTheme.CAPTION_FONT);
        label.setForeground(AppTheme.TEXT_SECONDARY);
        return label;
    }

    private String formatDate(NoticeBean notice) {
        if (notice.getNotice_time() == null) {
            return "정보 없음";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(notice.getNotice_time());
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
}
