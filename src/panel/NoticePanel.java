package panel;

import DB.NoticeDAO;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import main.MainUserPanel;
import model.NoticeBean;
import ui_n_utils.RoundedComponent;


public class NoticePanel extends JPanel {
    private RoundedComponent prevPageButton, nextPageButton, noticePanel;
    private NoticeDAO noticeDAO;

    private JPanel headerPanel; // ✅ 헤더 패널을 따로 관리
    private NoticeDetailPanel noticeDetailPanel;

    public NoticePanel(MainUserPanel mainUserPanel) {
        this.noticeDAO = new NoticeDAO(); // NoticeDAO 객체 생성

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                System.out.println("🔄 공지사항 패널이 보임 → DB에서 공지사항 새로 불러옴");
                loadNotices(mainUserPanel); // 자동 갱신
            }
        });

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
        noticePanel = new RoundedComponent(400, 670, 30, "panel", "",
                new Color(192, 233, 147), Color.white, Color.black, " ", 0, 0);
        noticePanel.setBounds(12, 40, 400, 670);
        noticePanel.setBackground(Color.WHITE);
        noticePanel.setLayout(null);
        backgroundPanel.add(noticePanel);

        // ✅ 헤더 패널 생성 (removeAll 방지)
        headerPanel = new JPanel();
        headerPanel.setLayout(null);
        headerPanel.setBounds(0, 0, 400, 90);
        headerPanel.setBackground(Color.WHITE);
        noticePanel.add(headerPanel);

        JLabel titleLabel = new JLabel("공지사항");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 30));
        titleLabel.setBounds(20, 10, 200, 40);
        headerPanel.add(titleLabel);
        
        // 뒤로가기 버튼 추가
        JButton backButton = new JButton("X");
        backButton.setBounds(350, 10, 50, 50);
        backButton.setFont(new Font("Inter", Font.BOLD, 24));
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        headerPanel.add(backButton);
        
        // 뒤로가기 버튼 클릭 시 이전 패널로 돌아가기
        backButton.addActionListener(e -> {
            if (mainUserPanel != null) {
                // 이전 패널로 돌아가기
                String previousPanel = mainUserPanel.getPreviousPanel();
                System.out.println("🔙 공지사항 닫기: 이전 패널(" + previousPanel + ")로 돌아갑니다.");
                mainUserPanel.goToPreviousPanel();
            } else {
                System.out.println("❌ mainUserPanel이 null입니다!");
            }
        });

        // 🔹 목록 헤더
        String[] headers = {"번호", "제목", "작성자", "작성날짜"};
        int[] headerX = {10, 60, 190, 280};  // 📌 위치 조정

        for (int i = 0; i < headers.length; i++) {
            JLabel label = new JLabel(headers[i]);
            label.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            label.setBounds(headerX[i], 60, 100, 20);
            headerPanel.add(label);
        }

        JSeparator separator = new JSeparator();
        separator.setBounds(0, 85, 400, 3);
        separator.setForeground(Color.BLACK);
        headerPanel.add(separator);

        // 🔹 공지사항 목록 불러오기 (DB 연동)
        loadNotices(mainUserPanel);
    }

    // 🔹 공지사항 목록 불러오기 (DB 연동)
    private void loadNotices(MainUserPanel mainUserPanel) {
        System.out.println("🔄 공지사항 UI 업데이트 시작...");

        List<NoticeBean> notices = noticeDAO.getAllNotices();
        System.out.println("✅ 불러온 공지사항 개수: " + notices.size());

        // ✅ 기존 공지사항 목록만 삭제 (헤더 유지)
        Component[] components = noticePanel.getComponents();
        for (Component c : components) {
            if (c != headerPanel) { // 헤더 패널은 삭제하지 않음
                noticePanel.remove(c);
            }
        }

        if (notices.isEmpty()) {
            JLabel noDataLabel = new JLabel("📢 등록된 공지사항이 없습니다.");
            noDataLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            noDataLabel.setBounds(100, 200, 300, 30);
            noticePanel.add(noDataLabel);
        } else {
            for (int i = 0; i < notices.size(); i++) {
                NoticeBean notice = notices.get(i);

                JLabel number = new JLabel(String.valueOf(notice.getNotice_num()));
                JLabel title = new JLabel(notice.getNotice_title());
                JLabel author = new JLabel(notice.getAdmin_id());
                JLabel date = new JLabel(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new Date(notice.getNotice_time().getTime())));

                number.setBounds(10, 100 + (i * 40), 50, 30);
                title.setBounds(50, 100 + (i * 40), 120, 30);  // 제목 너비 조정
                author.setBounds(190, 100 + (i * 40), 80, 30); // 작성자 왼쪽 이동
                date.setBounds(270, 100 + (i * 40), 140, 30);  // 날짜도 왼쪽 이동

                title.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        System.out.println("🔍 공지사항 제목 클릭됨: " + notice.getNotice_title());

                        int noticeNum = notice.getNotice_num();
                        NoticeBean selectedNotice = new NoticeDAO().getNoticeById(noticeNum);

                        if (selectedNotice != null) {
                            System.out.println("✅ 공지사항 상세 데이터 정상 조회됨! -> updateNoticeDetail 호출");
                            mainUserPanel.getNoticeDetailPanel().updateNoticeDetail(selectedNotice);
                        } else {
                            System.err.println("❌ 공지사항 상세 데이터 없음!");
                        }

                        mainUserPanel.showPanel("noticeDetailPanel");
                    }
                });


                noticePanel.add(number);
                noticePanel.add(title);
                noticePanel.add(author);
                noticePanel.add(date);
                System.out.println("📌 공지사항 UI 추가: " + notice.getNotice_title() + " | 날짜: " + notice.getNotice_time());
            }
            
        }

        // ✅ UI 강제 갱신
        noticePanel.revalidate();
        noticePanel.repaint();
        System.out.println("✅ 공지사항 UI 업데이트 완료");
    }
}
