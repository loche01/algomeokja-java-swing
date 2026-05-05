package panel;

import DB.NoticeDAO;
import DB.NoticeFileDAO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import main.MainUserPanel;
import model.NoticeBean;
import ui_n_utils.RoundedComponent;

public class NoticeDetailPanel extends JPanel {

    private RoundedComponent noticePanel;
    private JLabel titleValue, authorLabel, dateLabel;
    private JTextArea contentArea;
    private JScrollPane contentScrollPane;
    private JButton closeButton;
    private MainUserPanel mainUserPanel;
    private JPanel fileListPanel;
    private NoticeFileDAO noticeFileDAO;
    private NoticeDAO noticeDAO;

    public NoticeDetailPanel(MainUserPanel mainUserPanel) {
        this.mainUserPanel = mainUserPanel;
        this.noticeFileDAO = new NoticeFileDAO();
        this.noticeDAO = new NoticeDAO();
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
        noticePanel = new RoundedComponent(380, 670, 30, "panel", "",
                new Color(192, 233, 147), Color.white, Color.black, " ", 0, 0);
        noticePanel.setBounds(21, 40, 380, 670);
        noticePanel.setBackground(Color.WHITE);
        noticePanel.setLayout(null);
        backgroundPanel.add(noticePanel);

        // 🔹 공지사항 제목
        JLabel titleLabel = new JLabel("공지사항");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 30));
        titleLabel.setBounds(20, 10, 200, 40);
        noticePanel.add(titleLabel);

        // 🔹 닫기 버튼
        closeButton = new JButton("X");
        closeButton.setBounds(350, 10, 50, 50);
        closeButton.setFont(new Font("Inter", Font.BOLD, 24));
        closeButton.setForeground(Color.BLACK);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        noticePanel.add(closeButton);

        closeButton.addActionListener(e -> {
            if (mainUserPanel != null) {
                // 이전 패널로 돌아가기
                String previousPanel = mainUserPanel.getPreviousPanel();
                System.out.println("🔙 공지사항 상세 닫기: 이전 패널(" + previousPanel + ")로 돌아갑니다.");
                
                // 이전 패널이 Notice인 경우 Notice로, 그 외의 경우 이전 패널로 이동
                if (previousPanel.equals("Notice")) {
                    mainUserPanel.showPanel("Notice");
                } else {
                    mainUserPanel.goToPreviousPanel();
                }
            } else {
                JOptionPane.showMessageDialog(null, "메인 패널이 설정되지 않았습니다.");
            }
        });

        // 🔹 제목 표시줄
        JLabel smallTitleLabel = new JLabel("제목:");
        smallTitleLabel.setFont(new Font("Inter", Font.BOLD, 14));
        smallTitleLabel.setBounds(10, 60, 50, 20);
        noticePanel.add(smallTitleLabel);

        titleValue = new JLabel();
        titleValue.setFont(new Font("Inter", Font.BOLD, 14));
        titleValue.setBounds(70, 60, 300, 20);
        noticePanel.add(titleValue);

        // 🔹 구분선 추가 (제목 아래)
        JSeparator separator1 = new JSeparator();
        separator1.setBounds(10, 85, 360, 1);
        separator1.setForeground(Color.BLACK);
        noticePanel.add(separator1);

        // 🔹 작성자 및 날짜 정보
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

        // 🔹 구분선 추가 (작성자/날짜 아래)
        JSeparator separator2 = new JSeparator();
        separator2.setBounds(10, 120, 360, 1);
        separator2.setForeground(Color.BLACK);
        noticePanel.add(separator2);

        // 🔹 본문 내용 (JTextArea + JScrollPane)
        contentArea = new JTextArea();
        contentArea.setFont(new Font("Inter", Font.PLAIN, 14));
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        contentArea.setEditable(false);
        contentArea.setBackground(Color.WHITE);
        contentArea.setMargin(new Insets(5, 5, 5, 5));

        contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBounds(10, 130, 360, 300);
        contentScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentScrollPane.setBorder(null);
        noticePanel.add(contentScrollPane);

        // 🔹 파일 목록 패널 추가
        fileListPanel = new JPanel();
        fileListPanel.setLayout(new BoxLayout(fileListPanel, BoxLayout.Y_AXIS));
        fileListPanel.setBounds(10, 450, 360, 150);
        fileListPanel.setBackground(Color.WHITE);
        noticePanel.add(fileListPanel);

        // 🔹 구분선 추가 (본문 아래)
        JSeparator separator3 = new JSeparator();
        separator3.setBounds(10, 620, 360, 1);
        separator3.setForeground(Color.BLACK);
        noticePanel.add(separator3);
    }

    // 🔹 공지사항 데이터를 동적으로 적용하는 메서드 추가
    public void updateNoticeDetail(NoticeBean notice) {
        if (notice == null) {
            //System.err.println("❌ 공지사항 데이터 없음");
            return;
        }

        // ✅ UI 업데이트
        titleValue.setText(notice.getNotice_title());
        authorLabel.setText(notice.getNotice_num() + "  |  작성자 : " + notice.getAdmin_id());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateLabel.setText("작성날짜: " + dateFormat.format(notice.getNotice_time()));

        contentArea.setText(notice.getNotice_content());
        contentArea.setCaretPosition(0);
        contentArea.revalidate();
        contentArea.repaint();

        // 🔹 파일 목록 불러오기
       // System.out.println("📂 Notice ID: " + notice.getNotice_num());
        List<Map<String, Object>> fileList = noticeFileDAO.getFilesByNoticeId(notice.getNotice_num());
        //System.out.println("📂 불러온 파일 개수: " + fileList.size());

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

        // 🔹 파일 목록 크기 조정
        fileListPanel.setPreferredSize(new Dimension(360, Math.max(30, fileList.size() * 30)));

        fileListPanel.revalidate();
        fileListPanel.repaint();
    }
}
