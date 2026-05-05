package panel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.swing.*;

import DB.NoticeDAO;
import DB.NoticeFileDAO;
import model.NoticeBean;
import main.MainAdminPanel;
import ui_n_utils.RoundedComponent;

public class AdminNoticeDetailPanel extends JPanel {

    private RoundedComponent noticePanel,editButton;
    private JLabel titleValue, authorLabel, dateLabel;
    private JTextArea contentArea;
    private JScrollPane contentScrollPane;
    private JButton closeButton;
    private JPanel fileListPanel;
    private NoticeDAO noticeDAO;
    private NoticeFileDAO noticeFileDAO;
    private MainAdminPanel mainAdminPanel;
    private int currentNoticeId;

    public AdminNoticeDetailPanel(MainAdminPanel mainAdminPanel) {
        this.mainAdminPanel = mainAdminPanel;
        this.noticeDAO = new NoticeDAO();
        this.noticeFileDAO = new NoticeFileDAO();

        setLayout(null);
        setBackground(new Color(0xC0E993));
        setBounds(0, 0, 440, 956);

        // 🔹 배경 패널
        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBounds(0, 0, 440, 956);
        backgroundPanel.setBackground(new Color(0xC0E993));
        backgroundPanel.setLayout(null);
        add(backgroundPanel);

        // 🔹 공지사항 패널 (NoticeDetailPanel 디자인 반영)
        noticePanel = new RoundedComponent(380, 670, 30, "panel", "",
                new Color(192, 233, 147), Color.white, Color.black, " ", 0, 0);
        noticePanel.setBounds(21, 40, 380, 670);
        noticePanel.setBackground(Color.WHITE);
        noticePanel.setLayout(null);
        backgroundPanel.add(noticePanel);

        // 🔹 공지사항 제목
        JLabel titleLabel = new JLabel("공지사항 ");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 30));
        titleLabel.setBounds(20, 10, 300, 40);
        noticePanel.add(titleLabel);

        // 🔹 닫기 버튼 (위치 및 크기 수정)
        closeButton = new JButton("X");
        closeButton.setBounds(330, 10, 50, 50);
        closeButton.setFont(new Font("Inter", Font.BOLD, 14));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        noticePanel.add(closeButton);
        closeButton.addActionListener(e -> mainAdminPanel.showPanel("NoticeAdmin"));

        // 🔹 제목 표시줄
        JLabel smallTitleLabel = new JLabel("제목:");
        smallTitleLabel.setFont(new Font("Inter", Font.BOLD, 14));
        smallTitleLabel.setBounds(10, 60, 50, 20);
        noticePanel.add(smallTitleLabel);

        titleValue = new JLabel();
        titleValue.setFont(new Font("Inter", Font.BOLD, 14));
        titleValue.setBounds(70, 60, 300, 20);
        noticePanel.add(titleValue);

        JSeparator separator1 = new JSeparator();
        separator1.setBounds(10, 85, 360, 1);
        separator1.setForeground(Color.BLACK);
        noticePanel.add(separator1);

        // 🔹 작성자 및 날짜 정보 (위치 및 크기 조정)
        authorLabel = new JLabel();
        authorLabel.setFont(new Font("Inter", Font.BOLD, 12));
        authorLabel.setForeground(Color.GRAY);
        authorLabel.setBounds(10, 95, 250, 20);
        noticePanel.add(authorLabel);

        dateLabel = new JLabel();
        dateLabel.setFont(new Font("Inter", Font.BOLD, 12));
        dateLabel.setForeground(Color.GRAY);
        dateLabel.setBounds(200, 95, 180, 20);
        noticePanel.add(dateLabel);

        JSeparator separator2 = new JSeparator();
        separator2.setBounds(10, 120, 360, 1);
        separator2.setForeground(Color.BLACK);
        noticePanel.add(separator2);

        // 🔹 본문 내용 (JTextArea + JScrollPane)
        contentArea = new JTextArea();
        contentArea.setFont(new Font("Inter", Font.PLAIN, 14));
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        contentArea.setCursor(null);
        contentArea.setBackground(Color.WHITE);
        contentArea.setMargin(new Insets(5, 5, 5, 5));

        contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBounds(10, 130, 360, 300);
        contentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentScrollPane.setBorder(null);
        noticePanel.add(contentScrollPane);

        // 🔹 파일 목록 패널 추가 (위치 조정)
        fileListPanel = new JPanel();
        fileListPanel.setLayout(new BoxLayout(fileListPanel, BoxLayout.Y_AXIS));
        fileListPanel.setBounds(10, 450, 360, 150);
        fileListPanel.setBackground(Color.WHITE);
        noticePanel.add(fileListPanel);

        // 🔹 구분선 추가 (본문 아래)
        JSeparator separator3 = new JSeparator();
        separator3.setBounds(10, 600, 360, 1);
        separator3.setForeground(Color.BLACK);
        noticePanel.add(separator3);
        
     // 🔹 수정 버튼 추가 (이 부분이 빠져 있어서 NullPointerException 발생)
        editButton = new RoundedComponent(100, 40, 10, "button", "수정", 
                        Color.GRAY, Color.GRAY, Color.WHITE, "맑은 고딕", Font.BOLD, 16);
        editButton.setBounds(150, 600, 100, 40);
        noticePanel.add(editButton);

        
  
 
        editButton.getButton().addActionListener(e -> {
            if (currentNoticeId <= 0) {
                JOptionPane.showMessageDialog(null, "수정할 공지사항이 없습니다!", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("🔹 수정할 공지사항 ID: " + currentNoticeId);
            mainAdminPanel.showPanel("NoticeEditPanel");  
            mainAdminPanel.getNoticeEditPanel().loadNoticeForEdit(currentNoticeId);
        });





        
    }

    /**
     * ✅ 공지사항 ID를 받아서 데이터베이스에서 불러와 적용하는 메서드
     */
    public void loadNoticeDetail(int noticeId) {
        NoticeBean notice = noticeDAO.getNoticeById(noticeId);

        if (notice == null) {
            System.err.println("❌ 공지사항 데이터 없음");
            return;
        }

        this.currentNoticeId = notice.getNotice_num();  // ✅ 현재 공지사항 ID 저장

        titleValue.setText(notice.getNotice_title());
        authorLabel.setText("작성자: " + notice.getAdmin_id());
        dateLabel.setText("작성날짜: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(notice.getNotice_time()));
        contentArea.setText(notice.getNotice_content());
        contentArea.setCaretPosition(0);
        contentArea.revalidate();
        contentArea.repaint();

        
        // 🔹 파일 목록 불러오기
        List<Map<String, Object>> fileList = noticeFileDAO.getFilesByNoticeId(notice.getNotice_num());
        fileListPanel.removeAll();

        if (fileList.isEmpty()) {
            fileListPanel.add(new JLabel("📂 첨부 파일 없음"));
        } else {
            for (Map<String, Object> file : fileList) {
                String fileName = (String) file.get("fileName");
                int fileId = (int) file.get("fileId");

                JLabel fileLabel = new JLabel("📄 " + fileName);
                fileLabel.setFont(new Font("Inter", Font.PLAIN, 12));
                fileLabel.setForeground(Color.BLUE);
                fileLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                fileLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        noticeFileDAO.downloadFile(fileId);
                    }
                });
                fileListPanel.add(fileLabel);
            }
        }

        fileListPanel.revalidate();
        fileListPanel.repaint();
    }
   
}
