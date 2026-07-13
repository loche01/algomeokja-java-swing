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
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.ToolTipManager;
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
    private final JButton listButton;
    private final JButton fileButton;
    private final JButton removeFileButton;
    private final JButton saveButton;
    private final List<File> attachedFiles = new ArrayList<>();
    private final List<String> existingFiles = new ArrayList<>();
    private int noticeId;
    private int editLoadGeneration;

    public NoticeEditPanel(MainAdminPanel mainAdminPanel) {
        this.mainAdminPanel = mainAdminPanel;
        this.noticeDAO = new NoticeDAO();
        this.noticeFileDAO = new NoticeFileDAO();

        setLayout(null);
        setBackground(AppTheme.BACKGROUND);
        setBounds(0, 0, 440, 856);

        JPanel card = new JPanel(null);
        AppTheme.styleCard(card);
        card.setBounds(AppTheme.HORIZONTAL_MARGIN, 18, AppTheme.CARD_WIDTH, 792);
        add(card);

        JLabel screenTitle = new JLabel("공지 수정");
        AppTheme.styleScreenTitle(screenTitle);
        screenTitle.setBounds(20, 16, 220, 34);
        card.add(screenTitle);

        listButton = new JButton("목록으로");
        AppTheme.styleSecondaryButton(listButton);
        listButton.setBounds(268, 16, 92, 36);
        listButton.addActionListener(e -> returnToList());
        card.add(listButton);

        JLabel descriptionLabel = new JLabel("공지 내용과 첨부파일을 수정합니다.");
        AppTheme.styleScreenDescription(descriptionLabel);
        descriptionLabel.setBounds(20, 52, 240, 22);
        card.add(descriptionLabel);

        JLabel titleLabel = createFieldLabel("제목");
        titleLabel.setBounds(20, 84, 120, 22);
        card.add(titleLabel);

        titleField = new JTextField();
        AppTheme.styleInputField(titleField);
        titleField.setBounds(20, 111, 340, AppTheme.INPUT_HEIGHT);
        card.add(titleField);

        JLabel contentLabel = createFieldLabel("본문");
        contentLabel.setBounds(20, 163, 120, 22);
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
        contentScrollPane.setBounds(20, 189, 340, 270);
        contentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentScrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        card.add(contentScrollPane);

        JLabel fileLabel = createFieldLabel("첨부파일");
        fileLabel.setBounds(20, 475, 120, 22);
        card.add(fileLabel);

        fileButton = new JButton("파일 추가");
        AppTheme.styleSecondaryButton(fileButton);
        fileButton.setBounds(20, 502, 160, 36);
        fileButton.addActionListener(e -> selectFiles());
        card.add(fileButton);

        removeFileButton = new JButton("선택 제거");
        AppTheme.styleSecondaryButton(removeFileButton);
        removeFileButton.setBounds(200, 502, 160, 36);
        removeFileButton.setEnabled(false);
        removeFileButton.addActionListener(e -> removeSelectedFile());
        card.add(removeFileButton);

        fileList = new FileNameList(fileListModel);
        styleFileList(fileList);

        fileScrollPane = new JScrollPane(fileList);
        fileScrollPane.setBounds(20, 550, 340, 112);
        fileScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        fileScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        fileScrollPane.getVerticalScrollBar().setUnitIncrement(14);
        fileScrollPane.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        card.add(fileScrollPane);

        JLabel requiredLabel = new JLabel("제목과 본문은 필수 입력 항목입니다.");
        requiredLabel.setFont(AppTheme.CAPTION_FONT);
        requiredLabel.setForeground(AppTheme.TEXT_SECONDARY);
        requiredLabel.setBounds(20, 674, 340, 20);
        card.add(requiredLabel);

        saveButton = new JButton("변경사항 저장");
        AppTheme.stylePrimaryButton(saveButton);
        saveButton.setBounds(20, 708, 340, 44);
        saveButton.addActionListener(e -> updateNotice());
        card.add(saveButton);
    }

    public void loadNoticeForEdit(int noticeId) {
        clearFields();
        setFormBusy(true);
        int requestedGeneration = ++editLoadGeneration;
        new SwingWorker<EditData, Void>() {
            @Override
            protected EditData doInBackground() {
                NoticeBean notice = noticeDAO.getNoticeById(noticeId);
                if (notice == null) {
                    return null;
                }
                List<Map<String, Object>> files = noticeFileDAO.getFilesByNoticeId(noticeId);
                return new EditData(notice, files);
            }

            @Override
            protected void done() {
                if (requestedGeneration != editLoadGeneration) {
                    return;
                }
                try {
                    EditData editData = get();
                    if (editData == null) {
                        JOptionPane.showMessageDialog(getDialogParent(),
                                "공지사항을 불러오지 못했습니다.",
                                "공지 수정", JOptionPane.ERROR_MESSAGE);
                        mainAdminPanel.showPanel("NoticeAdmin");
                        return;
                    }
                    applyEditData(editData);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(getDialogParent(),
                            "공지사항을 불러오지 못했습니다.",
                            "공지 수정", JOptionPane.ERROR_MESSAGE);
                    mainAdminPanel.showPanel("NoticeAdmin");
                } finally {
                    setFormBusy(false);
                }
            }
        }.execute();
    }

    private void applyEditData(EditData editData) {
        this.noticeId = editData.notice.getNotice_num();
        titleField.setText(editData.notice.getNotice_title());
        contentArea.setText(editData.notice.getNotice_content());
        contentArea.setCaretPosition(0);

        for (Map<String, Object> fileData : editData.files) {
            String fileName = (String) fileData.get("fileName");
            if (fileName != null) {
                existingFiles.add(fileName);
                fileListModel.addElement(fileName);
            }
        }
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
            JOptionPane.showMessageDialog(getDialogParent(), "제목과 본문을 입력해주세요.",
                    "입력 확인", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (noticeId <= 0) {
            JOptionPane.showMessageDialog(getDialogParent(), "수정할 공지사항을 확인할 수 없습니다.",
                    "수정 실패", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String adminId = null;
        if (!attachedFiles.isEmpty()) {
            UserBean currentUser = UserSessionManager.getInstance().getCurrentUser();
            adminId = currentUser == null ? null : currentUser.getUser_id();
            if (adminId == null || adminId.isBlank()) {
                JOptionPane.showMessageDialog(getDialogParent(), "로그인한 관리자 정보를 확인할 수 없습니다.",
                        "공지 수정", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        List<File> filesToUpload = List.copyOf(attachedFiles);
        int noticeToUpdate = noticeId;
        String uploadAdminId = adminId;
        setFormBusy(true);
        new SwingWorker<SaveResult, Void>() {
            @Override
            protected SaveResult doInBackground() {
                if (!noticeDAO.updateNotice(noticeToUpdate, newTitle, newContent)) {
                    return SaveResult.noticeFailure();
                }

                int failedFileCount = 0;
                for (File file : filesToUpload) {
                    if (!noticeFileDAO.uploadFile(noticeToUpdate, uploadAdminId, file)) {
                        failedFileCount++;
                    }
                }
                return SaveResult.success(filesToUpload.size(), failedFileCount);
            }

            @Override
            protected void done() {
                try {
                    SaveResult result = get();
                    if (!result.noticeSaved) {
                        JOptionPane.showMessageDialog(getDialogParent(),
                                "공지사항을 수정하지 못했습니다.",
                                "수정 실패", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (result.failedFileCount == 0) {
                        JOptionPane.showMessageDialog(getDialogParent(),
                                "공지사항을 수정했습니다.",
                                "수정 완료", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(getDialogParent(),
                                "공지사항은 수정되었지만 첨부파일 "
                                        + result.totalFileCount + "개 중 "
                                        + result.failedFileCount + "개를 저장하지 못했습니다.",
                                "첨부파일 오류", JOptionPane.ERROR_MESSAGE);
                    }
                    mainAdminPanel.showPanel("NoticeAdmin");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(getDialogParent(),
                            "공지사항 수정 중 오류가 발생했습니다.",
                            "수정 실패", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setFormBusy(false);
                }
            }
        }.execute();
    }

    private void selectFiles() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "이미지 및 문서 파일", "jpg", "png", "pdf", "docx", "txt"));

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
        if (selectedIndex < existingFiles.size()) {
            JOptionPane.showMessageDialog(getDialogParent(),
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

    private void setFormBusy(boolean busy) {
        listButton.setEnabled(!busy);
        fileButton.setEnabled(!busy);
        saveButton.setEnabled(!busy);
        titleField.setEnabled(!busy);
        contentArea.setEnabled(!busy);
        fileList.setEnabled(!busy);
        removeFileButton.setEnabled(!busy && !fileListModel.isEmpty());
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

    private static class SaveResult {
        private final boolean noticeSaved;
        private final int totalFileCount;
        private final int failedFileCount;

        private SaveResult(boolean noticeSaved, int totalFileCount, int failedFileCount) {
            this.noticeSaved = noticeSaved;
            this.totalFileCount = totalFileCount;
            this.failedFileCount = failedFileCount;
        }

        private static SaveResult noticeFailure() {
            return new SaveResult(false, 0, 0);
        }

        private static SaveResult success(int totalFileCount, int failedFileCount) {
            return new SaveResult(true, totalFileCount, failedFileCount);
        }
    }

    private static class EditData {
        private final NoticeBean notice;
        private final List<Map<String, Object>> files;

        private EditData(NoticeBean notice, List<Map<String, Object>> files) {
            this.notice = notice;
            this.files = files;
        }
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
