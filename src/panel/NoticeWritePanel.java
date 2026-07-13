package panel;

import DB.NoticeDAO;
import DB.NoticeFileDAO;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import main.MainAdminPanel;
import model.UserBean;
import ui_n_utils.AppTheme;
import ui_n_utils.UserSessionManager;

public class NoticeWritePanel extends JPanel {
    private final MainAdminPanel mainAdminPanel;
    private final NoticeDAO noticeDAO;
    private final NoticeFileDAO noticeFileDAO;
    private final JTextField titleField;
    private final JTextArea contentArea;
    private final JScrollPane contentScrollPane;
    private final List<File> attachedFiles = new ArrayList<>();
    private final DefaultListModel<String> fileListModel = new DefaultListModel<>();
    private final JList<String> fileList;
    private final JScrollPane fileScrollPane;
    private final JButton removeFileButton;

    public NoticeWritePanel(MainAdminPanel mainAdminPanel) {
        this.mainAdminPanel = mainAdminPanel;
        this.noticeDAO = new NoticeDAO();
        this.noticeFileDAO = new NoticeFileDAO();

        setLayout(null);
        setBackground(AppTheme.BACKGROUND);
        setBounds(0, 0, 440, 856);

        JPanel card = new JPanel(null);
        AppTheme.styleCard(card);
        card.setBounds(30, 18, 380, 792);
        add(card);

        JLabel screenTitle = new JLabel("공지 작성");
        screenTitle.setFont(AppTheme.TITLE_FONT);
        screenTitle.setForeground(AppTheme.TEXT);
        screenTitle.setBounds(20, 16, 220, 34);
        card.add(screenTitle);

        JButton listButton = new JButton("목록으로");
        AppTheme.styleSecondaryButton(listButton);
        listButton.setBounds(268, 16, 92, 36);
        listButton.addActionListener(e -> returnToList());
        card.add(listButton);

        JLabel titleLabel = createFieldLabel("제목");
        titleLabel.setBounds(20, 72, 120, 22);
        card.add(titleLabel);

        titleField = new JTextField();
        AppTheme.styleInputField(titleField);
        titleField.setBounds(20, 99, 340, AppTheme.INPUT_HEIGHT);
        card.add(titleField);

        JLabel contentLabel = createFieldLabel("본문");
        contentLabel.setBounds(20, 151, 120, 22);
        card.add(contentLabel);

        contentArea = new JTextArea();
        contentArea.setFont(AppTheme.BODY_FONT);
        contentArea.setForeground(AppTheme.TEXT);
        contentArea.setBackground(AppTheme.INPUT_BACKGROUND);
        contentArea.setCaretColor(AppTheme.TEXT);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setMargin(new java.awt.Insets(10, 10, 10, 10));

        contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBounds(20, 177, 340, 300);
        contentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentScrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        card.add(contentScrollPane);

        JLabel fileLabel = createFieldLabel("첨부파일");
        fileLabel.setBounds(20, 493, 120, 22);
        card.add(fileLabel);

        JButton fileButton = new JButton("파일 추가");
        AppTheme.styleSecondaryButton(fileButton);
        fileButton.setBounds(20, 520, 160, 36);
        fileButton.addActionListener(e -> selectFiles());
        card.add(fileButton);

        removeFileButton = new JButton("선택 제거");
        AppTheme.styleSecondaryButton(removeFileButton);
        removeFileButton.setBounds(200, 520, 160, 36);
        removeFileButton.setEnabled(false);
        removeFileButton.addActionListener(e -> removeSelectedFile());
        card.add(removeFileButton);

        fileList = new FileNameList(fileListModel);
        styleFileList(fileList);

        fileScrollPane = new JScrollPane(fileList);
        fileScrollPane.setBounds(20, 568, 340, 120);
        fileScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        fileScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        fileScrollPane.getVerticalScrollBar().setUnitIncrement(14);
        fileScrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        card.add(fileScrollPane);

        JLabel requiredLabel = new JLabel("제목과 본문은 필수 입력 항목입니다.");
        requiredLabel.setFont(AppTheme.CAPTION_FONT);
        requiredLabel.setForeground(AppTheme.TEXT_SECONDARY);
        requiredLabel.setBounds(20, 698, 340, 20);
        card.add(requiredLabel);

        JButton saveButton = new JButton("공지 등록");
        AppTheme.stylePrimaryButton(saveButton);
        saveButton.setBounds(20, 730, 340, 44);
        saveButton.addActionListener(e -> saveNotice());
        card.add(saveButton);
    }

    public void resetForEntry() {
        clearFields();
        titleField.requestFocusInWindow();
        contentArea.setCaretPosition(0);
        javax.swing.SwingUtilities.invokeLater(() -> {
            contentScrollPane.getVerticalScrollBar().setValue(0);
            fileScrollPane.getVerticalScrollBar().setValue(0);
        });
    }

    private void saveNotice() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();
        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(getDialogParent(), "제목과 본문을 입력해주세요.",
                    "입력 확인", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserBean currentUser = UserSessionManager.getInstance().getCurrentUser();
        String adminId = currentUser == null ? null : currentUser.getUser_id();
        if (adminId == null || adminId.isBlank()) {
            JOptionPane.showMessageDialog(getDialogParent(), "로그인한 관리자 정보를 확인할 수 없습니다.",
                    "공지 등록", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = noticeDAO.addNotice(adminId, title, content);
        int noticeId = success ? noticeDAO.getLastNoticeId() : -1;
        if (!success || noticeId <= 0) {
            JOptionPane.showMessageDialog(getDialogParent(), "공지사항을 등록하지 못했습니다.",
                    "등록 실패", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean allFilesUploaded = true;
        for (File file : attachedFiles) {
            if (!noticeFileDAO.uploadFile(noticeId, adminId, file)) {
                allFilesUploaded = false;
            }
        }

        if (allFilesUploaded) {
            JOptionPane.showMessageDialog(getDialogParent(), "공지사항을 등록했습니다.",
                    "등록 완료", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(getDialogParent(),
                    "공지사항은 등록되었지만 일부 첨부파일을 저장하지 못했습니다.",
                    "첨부파일 오류", JOptionPane.ERROR_MESSAGE);
        }

        clearFields();
        mainAdminPanel.showPanel("NoticeAdmin");
    }

    private void selectFiles() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = fileChooser.showOpenDialog(getDialogParent());
        if (returnValue != JFileChooser.APPROVE_OPTION) {
            return;
        }

        for (File file : fileChooser.getSelectedFiles()) {
            attachedFiles.add(file);
            fileListModel.addElement(file.getName());
        }
        removeFileButton.setEnabled(!fileListModel.isEmpty());
    }

    private void removeSelectedFile() {
        int selectedIndex = fileList.getSelectedIndex();
        if (selectedIndex < 0) {
            JOptionPane.showMessageDialog(getDialogParent(), "제거할 첨부파일을 선택하세요.",
                    "첨부파일", JOptionPane.WARNING_MESSAGE);
            return;
        }
        attachedFiles.remove(selectedIndex);
        fileListModel.remove(selectedIndex);
        removeFileButton.setEnabled(!fileListModel.isEmpty());
    }

    private void returnToList() {
        clearFields();
        mainAdminPanel.showPanel("NoticeAdmin");
    }

    private void clearFields() {
        titleField.setText("");
        contentArea.setText("");
        attachedFiles.clear();
        fileListModel.clear();
        removeFileButton.setEnabled(false);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(AppTheme.BODY_BOLD_FONT);
        label.setForeground(AppTheme.TEXT);
        return label;
    }

    private void styleFileList(JList<String> list) {
        list.setFont(AppTheme.BODY_FONT);
        list.setForeground(AppTheme.TEXT);
        list.setBackground(AppTheme.INPUT_BACKGROUND);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellHeight(30);
        list.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> valueList, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        valueList, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                label.setToolTipText(value == null ? null : value.toString());
                return label;
            }
        });
    }

    private Component getDialogParent() {
        Window window = SwingUtilities.getWindowAncestor(this);
        return window != null ? window : this;
    }

    private static class FileNameList extends JList<String> {
        FileNameList(DefaultListModel<String> model) {
            super(model);
            ToolTipManager.sharedInstance().registerComponent(this);
        }

        @Override
        public String getToolTipText(MouseEvent event) {
            int index = locationToIndex(event.getPoint());
            java.awt.Rectangle cellBounds = index < 0 ? null : getCellBounds(index, index);
            if (cellBounds == null || !cellBounds.contains(event.getPoint())) {
                return null;
            }
            return getModel().getElementAt(index);
        }
    }
}
