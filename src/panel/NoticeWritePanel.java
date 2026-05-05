package panel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import DB.NoticeDAO;
import DB.NoticeFileDAO;
import main.MainAdminPanel;
import model.NoticeBean;
import ui_n_utils.RoundedComponent;
import ui_n_utils.UserSessionManager;

public class NoticeWritePanel extends JPanel {
    private RoundedComponent noticePanel, saveButton, fileButton, removeFileButton;
    private JTextField titleField;
    private JTextArea contentArea;
    private JButton closeButton;
    private File selectedFile; // 🔹 선택한 파일 저장 변수
    private List<File> attachedFiles = new ArrayList<>(); // 🔹 첨부 파일 리스트 추가
    private DefaultListModel<String> fileListModel; // 🔹 파일 목록 모델 추가
    private JList<String> fileList; // 🔹 파일 목록 리스트 추가
    private MainAdminPanel mainAdminPanel;
    private NoticeDAO noticeDAO; // 🔹 NoticeDAO 추가
    private NoticeFileDAO noticeFileDAO;
    
    public NoticeWritePanel(MainAdminPanel mainAdminPanel) {
        this.mainAdminPanel = mainAdminPanel; // 🔹 메인 프레임 참조
        this.noticeDAO = new NoticeDAO(); // 🔹 NoticeDAO 객체 생성
        this.noticeFileDAO = new NoticeFileDAO();
        
        setLayout(null);
        setBackground(new Color(0xC0E993));
        setBounds(0, 0, 440, 956); // 🔹 패널 크기 지정

        // 🔹 공지사항 패널
        noticePanel = new RoundedComponent(380, 670, 30, "panel", "", 
                                           new Color(192, 233, 147), Color.white, Color.black, " ", 0, 0);
        noticePanel.setBounds(21, 40, 380, 670);
        noticePanel.setBackground(Color.WHITE);
        noticePanel.setLayout(null);
        add(noticePanel);

        // 🔹 공지사항 제목
        JLabel titleLabel = new JLabel("공지사항 작성");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 30));
        titleLabel.setBounds(20, 10, 300, 40);
        noticePanel.add(titleLabel);

        // 🔹 닫기 버튼
        closeButton = new JButton("X");
        closeButton.setBounds(330, 10, 50, 50);
        closeButton.setFont(new Font("Inter", Font.BOLD, 14));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        noticePanel.add(closeButton);

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainAdminPanel.showPanel("NoticeAdmin"); // 🔹 관리자 패널로 돌아가기
            }
        });

        JSeparator separator = new JSeparator();
        separator.setBounds(0, 115, 380, 1);
        separator.setForeground(Color.BLACK);
        noticePanel.add(separator);

        // 🔹 제목 입력 필드
        JLabel smallTitleLabel = new JLabel("제목:");
        smallTitleLabel.setFont(new Font("Inter", Font.BOLD, 14));
        smallTitleLabel.setBounds(10, 120, 50, 25);
        noticePanel.add(smallTitleLabel);

        titleField = new JTextField();
        titleField.setFont(new Font("Inter", Font.BOLD, 14));
        titleField.setBounds(65, 122, 250, 20);
        titleField.setBorder(null);
        noticePanel.add(titleField);

        // 🔹 본문 입력 필드
        contentArea = new JTextArea();
        contentArea.setFont(new Font("Inter", Font.BOLD, 14));
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBounds(10, 290, 360, 300);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        noticePanel.add(scrollPane);

        JSeparator separator4 = new JSeparator();
        separator4.setBounds(0, 600, 380, 1);
        separator4.setForeground(Color.BLACK);
        noticePanel.add(separator4);

        // 🔹 구분선 추가
        JSeparator separator1 = new JSeparator();
        separator1.setBounds(0, 150, 380, 1);
        separator1.setForeground(Color.BLACK);
        noticePanel.add(separator1);

        // 🔹 파일 첨부 라벨
        JLabel authorLabel = new JLabel("파일 첨부:");
        authorLabel.setFont(new Font("Inter", Font.BOLD, 13));
        authorLabel.setForeground(Color.black);
        authorLabel.setBounds(10, 155, 200, 20);
        noticePanel.add(authorLabel);

        // 🔹 파일 선택 버튼
        fileButton = new RoundedComponent(100, 30, 10, "button", "파일 첨부", 
                Color.BLACK, Color.BLACK, Color.WHITE, "맑은 고딕", Font.BOLD, 14);
        fileButton.setBounds(80, 155, 100, 30);
        noticePanel.add(fileButton);

        // 🔹 파일 삭제 버튼 (첨부 버튼과 디자인 동일 & 10px 간격 유지)
        removeFileButton = new RoundedComponent(100, 30, 10, "button", "삭제", 
                Color.BLACK, Color.BLACK, Color.WHITE, "맑은 고딕", Font.BOLD, 14);
        removeFileButton.setBounds(190, 155, 100, 30);
        removeFileButton.getButton().setEnabled(false);
        noticePanel.add(removeFileButton);

        // 🔹 파일 목록 모델 & 리스트 추가
        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        fileList.setFont(new Font("Inter", Font.PLAIN, 12));
        fileList.setBounds(10, 190, 360, 80);
        fileList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        noticePanel.add(fileList);

        // 🔹 파일 선택 이벤트 추가
        fileButton.getButton().addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            int returnValue = fileChooser.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File[] selectedFiles = fileChooser.getSelectedFiles();
                for (File file : selectedFiles) {
                    attachedFiles.add(file);
                    fileListModel.addElement(file.getName());
                }
                removeFileButton.getButton().setEnabled(true);
            }
        });

        // 🔹 파일 삭제 이벤트 추가
        removeFileButton.getButton().addActionListener(e -> {
            int selectedIndex = fileList.getSelectedIndex();
            if (selectedIndex != -1) {
                attachedFiles.remove(selectedIndex);
                fileListModel.remove(selectedIndex);
                if (fileListModel.isEmpty()) {
                    removeFileButton.getButton().setEnabled(false);
                }
            }
        });
        
    

        // 🔹 저장 버튼 추가
        saveButton = new RoundedComponent(100, 40, 10, "button", "저장", 
                Color.BLACK, Color.BLACK, Color.WHITE, "맑은 고딕", Font.BOLD, 16);
        saveButton.setBounds(140, 615, 100, 40);
        noticePanel.add(saveButton);

        // 🔹 저장 버튼 클릭 시
        saveButton.getButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String content = contentArea.getText();
                String adminId = UserSessionManager.getInstance().getCurrentUser().getUser_id();

                if (adminId == null || adminId.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "로그인한 관리자의 ID를 찾을 수 없습니다!", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 🔹 공지사항 저장 후 ID 받아오기
                boolean success = noticeDAO.addNotice(adminId, title, content);
                int noticeId = noticeDAO.getLastNoticeId();  // ✅ 방금 저장된 공지사항 ID 가져오기

                if (!success || noticeId <= 0) {
                    JOptionPane.showMessageDialog(null, "공지사항 저장에 실패했습니다!", "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 🔹 파일이 선택된 경우, DB에 파일 저장
                if (!attachedFiles.isEmpty()) {
                    boolean allUploaded = true;
                    for (File file : attachedFiles) {
                        boolean fileUploaded = noticeFileDAO.uploadFile(noticeId, adminId, file);
                        if (!fileUploaded) {
                            allUploaded = false;
                        }
                    }

                    if (allUploaded) {
                        JOptionPane.showMessageDialog(null, "파일 업로드 성공!", "파일 저장 완료", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "일부 파일 업로드 실패!", "파일 저장 오류", JOptionPane.ERROR_MESSAGE);
                    }
                }

                if (success) {
                    JOptionPane.showMessageDialog(null, "공지사항이 저장되었습니다!", "저장 완료", JOptionPane.INFORMATION_MESSAGE);
                    
                    // 🔹 공지사항 목록 새로고침
                    NoticeAdminPanel noticeAdmin = mainAdminPanel.getNoticeAdminPanel();
                    if (noticeAdmin != null) {
                        noticeAdmin.loadNotices(); // ✅ 공지사항 즉시 갱신
                    }
                    clearFields();

                    mainAdminPanel.showPanel("NoticeAdmin"); // 🔹 공지사항 패널로 이동
                }
            }
        });


    }
    
    
    /**
     * 🧹 **입력 필드 초기화 함수**
     */
    private void clearFields() {
        titleField.setText("");         // 🔹 제목 필드 초기화
        contentArea.setText("");        // 🔹 본문 필드 초기화
        attachedFiles.clear();          // 🔹 파일 리스트 초기화
        fileListModel.clear();          // 🔹 UI 리스트 초기화
        removeFileButton.getButton().setEnabled(false); // 🔹 파일 삭제 버튼 비활성화
    }
}
