package panel;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import DB.NoticeDAO;
import DB.NoticeFileDAO;
import main.MainAdminPanel;
import model.NoticeBean;
import ui_n_utils.RoundedComponent;
import ui_n_utils.UserSessionManager;

public class NoticeEditPanel extends JPanel {
    private RoundedComponent noticePanel, saveButton, cancelButton, fileButton, removeFileButton;
    private JTextField titleField;
    private JTextArea contentArea;
    private JList<String> fileList;
    private DefaultListModel<String> fileListModel;
    private JButton closeButton;
    private MainAdminPanel mainAdminPanel;
    private NoticeDAO noticeDAO;
    private NoticeFileDAO noticeFileDAO;
    private int noticeId;
    private List<File> attachedFiles = new ArrayList<>();
    private List<String> existingFiles = new ArrayList<>();
    

    public NoticeEditPanel(MainAdminPanel mainAdminPanel) {
        this.mainAdminPanel = mainAdminPanel;
        this.noticeDAO = new NoticeDAO();
        this.noticeFileDAO = new NoticeFileDAO();

        setLayout(null);
        setBackground(new Color(0xC0E993));
        setBounds(0, 0, 440, 956);

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBounds(0, 0, 440, 956);
        backgroundPanel.setBackground(new Color(0xC0E993));
        backgroundPanel.setLayout(null);
        add(backgroundPanel);

        noticePanel = new RoundedComponent(380, 670, 30, "panel", "",
                new Color(192, 233, 147), Color.white, Color.black, " ", 0, 0);
        noticePanel.setBounds(21, 40, 380, 670);
        noticePanel.setBackground(Color.WHITE);
        noticePanel.setLayout(null);
        backgroundPanel.add(noticePanel);

        JLabel titleLabel = new JLabel("공지사항 수정");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 30));
        titleLabel.setBounds(20, 10, 300, 40);
        noticePanel.add(titleLabel);

        closeButton = new JButton("X");
        closeButton.setBounds(330, 10, 50, 50);
        closeButton.setFont(new Font("Inter", Font.BOLD, 14));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        noticePanel.add(closeButton);
        closeButton.addActionListener(e -> mainAdminPanel.showPanel("NoticeAdmin"));

        JLabel smallTitleLabel = new JLabel("제목:");
        smallTitleLabel.setFont(new Font("Inter", Font.BOLD, 14));
        smallTitleLabel.setBounds(10, 60, 50, 25);
        noticePanel.add(smallTitleLabel);

        titleField = new JTextField();
        titleField.setFont(new Font("Inter", Font.BOLD, 14));
        titleField.setBounds(70, 60, 300, 25);
        titleField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        noticePanel.add(titleField);

        JSeparator separator1 = new JSeparator();
        separator1.setBounds(10, 90, 360, 1);
        separator1.setForeground(Color.BLACK);
        noticePanel.add(separator1);

        JLabel contentLabel = new JLabel("내용:");
        contentLabel.setFont(new Font("Inter", Font.BOLD, 14));
        contentLabel.setBounds(10, 100, 50, 25);
        noticePanel.add(contentLabel);

        contentArea = new JTextArea();
        contentArea.setFont(new Font("Inter", Font.PLAIN, 14));
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBounds(10, 130, 360, 300);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        noticePanel.add(scrollPane);

        fileButton = new RoundedComponent(100, 30, 10, "button", "파일 추가",
                Color.BLACK, Color.BLACK, Color.WHITE, "맑은 고딕", Font.BOLD, 14);
        fileButton.setBounds(10, 450, 100, 30);
        noticePanel.add(fileButton);
        fileButton.getButton().addActionListener(e -> selectFiles());

        removeFileButton = new RoundedComponent(100, 30, 10, "button", "파일 삭제",
                Color.GRAY, Color.GRAY, Color.WHITE, "맑은 고딕", Font.BOLD, 14);
        removeFileButton.setBounds(120, 450, 100, 30);
        noticePanel.add(removeFileButton);
        removeFileButton.getButton().addActionListener(e -> removeSelectedFile());

        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        fileList.setFont(new Font("Inter", Font.PLAIN, 12));
        fileList.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JScrollPane fileScrollPane = new JScrollPane(fileList);
        fileScrollPane.setBounds(10, 490, 360, 100);
        noticePanel.add(fileScrollPane);
        
        

        saveButton = new RoundedComponent(100, 40, 10, "button", "수정 완료",
                Color.BLACK, Color.BLACK, Color.WHITE, "맑은 고딕", Font.BOLD, 16);
        saveButton.setBounds(80, 600, 100, 40);
        noticePanel.add(saveButton);
        saveButton.getButton().addActionListener(e -> {
            updateNotice();
        });

        
        cancelButton = new RoundedComponent(100, 40, 10, "button", "취소",
                Color.GRAY, Color.GRAY, Color.WHITE, "맑은 고딕", Font.BOLD, 16);
        cancelButton.setBounds(200, 600, 100, 40);
        noticePanel.add(cancelButton);
        cancelButton.getButton().addActionListener(e -> mainAdminPanel.showPanel("NoticeAdmin"));
    
    }
    
    private void updateNotice() {
        String newTitle = titleField.getText().trim();
        String newContent = contentArea.getText().trim();

        // 🔹 제목과 내용이 비어 있는지 확인
        if (newTitle.isEmpty() || newContent.isEmpty()) {
            JOptionPane.showMessageDialog(null, "제목과 내용을 입력해주세요!", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 🔹 데이터베이스에 공지사항 업데이트
        boolean success = noticeDAO.updateNotice(noticeId, newTitle, newContent); // ✅ noticeId → noticeNum
        if (success) {
            // 🔹 현재 로그인한 관리자 ID 가져오기
            String adminId = UserSessionManager.getInstance().getCurrentUser().getUser_id();

            // 🔹 새로 추가된 파일 업로드
            for (File file : attachedFiles) {
                noticeFileDAO.uploadFile(noticeId, adminId, file); // ✅ noticeId → noticeNum
            }

            // 🔹 성공 메시지 표시 후, 공지사항 목록 패널로 이동
            JOptionPane.showMessageDialog(null, "공지사항이 수정되었습니다!", "수정 완료", JOptionPane.INFORMATION_MESSAGE);

            // 🔹 공지사항 목록 패널로 이동
            mainAdminPanel.showPanel("NoticeAdmin");

            // 🔹 공지사항 목록 새로고침
            NoticeAdminPanel noticeAdminPanel = mainAdminPanel.getNoticeAdminPanel();
            if (noticeAdminPanel != null) {
                noticeAdminPanel.loadNotices();
            }

        } else {
            JOptionPane.showMessageDialog(null, "공지사항 수정에 실패했습니다!", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadNoticeForEdit(int noticeId) {  // ✅ noticeId 사용
        clearFields();

        this.noticeId = noticeId; // ✅ 올바른 ID 저장

        NoticeBean notice = noticeDAO.getNoticeById(noticeId); // ✅ 올바른 메서드 사용
        if (notice == null) {
            JOptionPane.showMessageDialog(null, "공지사항 데이터를 불러올 수 없습니다!", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        titleField.setText(notice.getNotice_title());
        contentArea.setText(notice.getNotice_content());

        List<Map<String, Object>> fileList = noticeFileDAO.getFilesByNoticeId(noticeId); // ✅ 올바른 ID 사용
        for (Map<String, Object> fileData : fileList) {
            String fileName = (String) fileData.get("fileName");
            existingFiles.add(fileName);
            fileListModel.addElement(fileName);
        }

    }


    private void clearFields() {
        titleField.setText("");
        contentArea.setText("");
        existingFiles.clear();
        attachedFiles.clear();
        fileListModel.clear();
    }

    private void selectFiles() {
    	JFileChooser fileChooser = new JFileChooser();
    	fileChooser.setMultiSelectionEnabled(true); // 여러 개의 파일 선택 가능
    	fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // ✅ 폴더가 아니라 파일만 선택 가능하도록 설정
    	fileChooser.setAcceptAllFileFilterUsed(true); // ✅ 모든 파일을 허용

    	// 🔹 특정 확장자만 보이도록 필터 추가 (선택 사항)
    	FileNameExtensionFilter filter = new FileNameExtensionFilter("이미지 및 문서 파일", "jpg", "png", "pdf", "docx", "txt");
    	fileChooser.setFileFilter(filter);

    	int returnValue = fileChooser.showOpenDialog(null);

    	if (returnValue == JFileChooser.APPROVE_OPTION) {
    	    File[] selectedFiles = fileChooser.getSelectedFiles();
    	    for (File file : selectedFiles) {
    	        attachedFiles.add(file);
    	        fileListModel.addElement(file.getName());
    	    }
    	}

    }

    private void removeSelectedFile() {
        int selectedIndex = fileList.getSelectedIndex();
        if (selectedIndex != -1) {
            fileListModel.remove(selectedIndex);
            attachedFiles.remove(selectedIndex);
        }
    }
    
}
