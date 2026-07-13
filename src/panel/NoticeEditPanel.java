package panel;

import DB.NoticeDAO;
import DB.NoticeFileDAO;
import java.awt.Component;
import java.awt.Cursor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import main.MainAdminPanel;
import model.NoticeBean;
import model.UserBean;
import ui_n_utils.AppTheme;
import ui_n_utils.UserSessionManager;

public class NoticeEditPanel extends JPanel {
    private final MainAdminPanel mainAdminPanel;
    private final NoticeDAO noticeDAO;
    private final NoticeFileDAO noticeFileDAO;
    private final JTextField titleField;
    private final JTextArea contentArea;
    private final JScrollPane contentScrollPane;
    private final DefaultListModel<String> fileListModel = new DefaultListModel<>();
    private final JList<String> fileList;
    private final JScrollPane fileScrollPane;
    private final JButton removeFileButton;
    private final List<File> attachedFiles = new ArrayList<>();
    private final List<String> existingFiles = new ArrayList<>();
    private int noticeId;

    public NoticeEditPanel(MainAdminPanel mainAdminPanel) {
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

        JLabel screenTitle = new JLabel("공지 수정");
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

        fileList = new JList<>(fileListModel);
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

        JButton saveButton = new JButton("변경사항 저장");
        AppTheme.stylePrimaryButton(saveButton);
        saveButton.setBounds(20, 730, 340, 44);
        saveButton.addActionListener(e -> updateNotice());
        card.add(saveButton);
    }

    public void loadNoticeForEdit(int noticeId) {
        clearFields();
        this.noticeId = noticeId;

        NoticeBean notice = noticeDAO.getNoticeById(noticeId);
        if (notice == null) {
            this.noticeId = 0;
            JOptionPane.showMessageDialog(this, "공지사항을 불러오지 못했습니다.",
                    "공지 수정", JOptionPane.ERROR_MESSAGE);
            return;
        }

        titleField.setText(notice.getNotice_title());
        contentArea.setText(notice.getNotice_content());
        contentArea.setCaretPosition(0);

        List<Map<String, Object>> files = noticeFileDAO.getFilesByNoticeId(noticeId);
        for (Map<String, Object> fileData : files) {
            String fileName = (String) fileData.get("fileName");
            if (fileName != null) {
                existingFiles.add(fileName);
                fileListModel.addElement(fileName);
            }
        }
        removeFileButton.setEnabled(!fileListModel.isEmpty());
        resetScrollPositions();
    }

    public void resetScrollPositions() {
        contentArea.setCaretPosition(0);
        javax.swing.SwingUtilities.invokeLater(() -> {
            contentScrollPane.getVerticalScrollBar().setValue(0);
            fileScrollPane.getVerticalScrollBar().setValue(0);
        });
    }

    private void updateNotice() {
        String newTitle = titleField.getText().trim();
        String newContent = contentArea.getText().trim();
        if (newTitle.isEmpty() || newContent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "제목과 본문을 입력해주세요.",
                    "입력 확인", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (noticeId <= 0) {
            JOptionPane.showMessageDialog(this, "수정할 공지사항을 확인할 수 없습니다.",
                    "수정 실패", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String adminId = null;
        if (!attachedFiles.isEmpty()) {
            UserBean currentUser = UserSessionManager.getInstance().getCurrentUser();
            adminId = currentUser == null ? null : currentUser.getUser_id();
            if (adminId == null || adminId.isBlank()) {
                JOptionPane.showMessageDialog(this, "로그인한 관리자 정보를 확인할 수 없습니다.",
                        "공지 수정", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (!noticeDAO.updateNotice(noticeId, newTitle, newContent)) {
            JOptionPane.showMessageDialog(this, "공지사항을 수정하지 못했습니다.",
                    "수정 실패", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean allFilesUploaded = true;
        for (File file : attachedFiles) {
            if (!noticeFileDAO.uploadFile(noticeId, adminId, file)) {
                allFilesUploaded = false;
            }
        }

        if (allFilesUploaded) {
            JOptionPane.showMessageDialog(this, "공지사항을 수정했습니다.",
                    "수정 완료", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "공지사항은 수정되었지만 일부 첨부파일을 저장하지 못했습니다.",
                    "첨부파일 오류", JOptionPane.ERROR_MESSAGE);
        }
        mainAdminPanel.showPanel("NoticeAdmin");
    }

    private void selectFiles() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "이미지 및 문서 파일", "jpg", "png", "pdf", "docx", "txt"));

        int returnValue = fileChooser.showOpenDialog(this);
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
            JOptionPane.showMessageDialog(this, "제거할 첨부파일을 선택하세요.",
                    "첨부파일", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedIndex < existingFiles.size()) {
            JOptionPane.showMessageDialog(this,
                    "기존 첨부파일 삭제는 현재 기능에서 지원하지 않습니다.",
                    "첨부파일", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int newFileIndex = selectedIndex - existingFiles.size();
        attachedFiles.remove(newFileIndex);
        fileListModel.remove(selectedIndex);
        removeFileButton.setEnabled(!fileListModel.isEmpty());
    }

    private void returnToList() {
        clearFields();
        mainAdminPanel.showPanel("NoticeAdmin");
    }

    private void clearFields() {
        noticeId = 0;
        titleField.setText("");
        contentArea.setText("");
        existingFiles.clear();
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
}
