package panel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;

import DB.NoticeDAO;
import main.MainAdminPanel;
import model.NoticeBean;
import ui_n_utils.RoundedComponent;

public class NoticeAdminPanel extends JPanel implements ActionListener {
    private RoundedComponent noticePanel, aButton, deleteButton;
    private NoticeDAO noticeDAO;
    private MainAdminPanel mainAdminPanel;
    private JPanel noticeListPanel;
    private JScrollPane scrollPane;
    private List<JCheckBox> checkBoxes = new ArrayList<>();

    public NoticeAdminPanel(MainAdminPanel mainAdminPanel) {
        this.mainAdminPanel = mainAdminPanel;
        setLayout(null);
        setBackground(new Color(0xC0E993));
        setBounds(0, 0, 440, 956);

        noticeDAO = new NoticeDAO();

        // 공지사항 패널
        noticePanel = new RoundedComponent(400, 670, 30, "panel", "",
                new Color(192, 150, 147), Color.white, Color.black, " ", 0, 0);
        noticePanel.setBounds(12, 50, 400, 670);
        noticePanel.setLayout(null);
        add(noticePanel);

        JLabel titleLabel = new JLabel("공지사항 목록");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 30));
        titleLabel.setBounds(20, 10, 200, 40);
        noticePanel.add(titleLabel);

        // 공지사항 목록 헤더
        String[] headers = {"번호", "제목", "작성자", "작성날짜"};
        int[] headerX = {30, 70, 210, 275};

        for (int i = 0; i < headers.length; i++) {
            JLabel label = new JLabel(headers[i]);
            label.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            label.setBounds(headerX[i], 60, 100, 20);
            noticePanel.add(label);
        }

        JSeparator separator = new JSeparator();
        separator.setBounds(0, 85, 400, 3);
        separator.setForeground(Color.BLACK);
        noticePanel.add(separator);

        // 🔹 공지사항 리스트 패널 (스크롤 적용)
        noticeListPanel = new JPanel();
        noticeListPanel.setLayout(null);
        noticeListPanel.setBackground(Color.WHITE);
        noticeListPanel.setBorder(null);

        scrollPane = new JScrollPane(noticeListPanel);
        scrollPane.setBounds(10, 100, 380, 430);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(Color.WHITE);
        noticePanel.add(scrollPane);

        // 🔹 글쓰기 버튼
        aButton = new RoundedComponent(50, 25, 0, "button", "글쓰기 |", Color.WHITE,
                Color.WHITE, Color.BLACK, "Inter", Font.BOLD, 13);
        aButton.setBounds(20, 550, 70, 25);
        aButton.getButton().addActionListener(this);
        noticePanel.add(aButton);

        // 🔹 삭제 버튼 (글쓰기 버튼과 같은 디자인 적용)
        deleteButton = new RoundedComponent(50, 25, 0, "button", "삭제", Color.WHITE,
                Color.WHITE, Color.BLACK, "Inter", Font.BOLD, 13);
        deleteButton.setBounds(100, 550, 70, 25);
        deleteButton.getButton().addActionListener(e -> deleteSelectedNotices());
        noticePanel.add(deleteButton);

        // 공지사항 목록 불러오기
        loadNotices();
    }

    /**
     * 선택된 공지사항 삭제 메서드
     * 체크된 항목을 찾아 삭제하고 UI를 갱신함
     */
    private void deleteSelectedNotices() {
        List<Integer> selectedIds = new ArrayList<>();

        // 🔹 체크된 공지사항 ID 수집
        for (int i = 0; i < checkBoxes.size(); i++) {
            if (checkBoxes.get(i).isSelected()) {
                selectedIds.add(Integer.parseInt(((JLabel) noticeListPanel.getComponent(i * 5 + 1)).getText())); // 공지사항 ID
            }
        }

        if (selectedIds.isEmpty()) {
            JOptionPane.showMessageDialog(null, "삭제할 공지사항을 선택하세요!", "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "선택한 공지사항을 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            for (int noticeId : selectedIds) {
                noticeDAO.deleteNotice(noticeId);
            }

            // ✅ UI 갱신
            loadNotices();
        }
    }

    // 🔹 공지사항 목록 불러오기 (DB 연동)
    public void loadNotices() {
        List<NoticeBean> notices = noticeDAO.getAllNotices();
        refreshNotices(notices);
    }

    // 🔹 공지사항 목록 새로고침
    public void refreshNotices(List<NoticeBean> notices) {
        noticeListPanel.removeAll();
        checkBoxes.clear();

        // ✅ 패널 크기 설정 (공지 개수에 따라 동적 조정)
        noticeListPanel.setPreferredSize(new Dimension(360, Math.max(430, notices.size() * 52)));

        if (notices.isEmpty()) {
            JLabel noDataLabel = new JLabel("📢 등록된 공지사항이 없습니다.");
            noDataLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            noDataLabel.setBounds(100, 200, 300, 30);
            noticeListPanel.add(noDataLabel);
        } else {
            for (int i = 0; i < notices.size(); i++) {
                NoticeBean notice = notices.get(i);
                int rowY = i * 52;

                JCheckBox checkBox = new JCheckBox();
                checkBox.setBounds(5, rowY + 7, 24, 30);
                checkBox.setBackground(new Color(0xF8FAF6));
                checkBoxes.add(checkBox);

                JLabel number = new JLabel(String.valueOf(notice.getNotice_num()));
                JLabel title = new JLabel(notice.getNotice_title());
                JLabel author = new JLabel(notice.getAdmin_id());
                JLabel date = new JLabel(new SimpleDateFormat("yyyy-MM-dd")
                        .format(new Date(notice.getNotice_time().getTime())));

                number.setFont(new Font("Malgun Gothic", Font.PLAIN, 13));
                title.setFont(new Font("Malgun Gothic", Font.BOLD, 14));
                author.setFont(new Font("Malgun Gothic", Font.PLAIN, 12));
                date.setFont(new Font("Malgun Gothic", Font.PLAIN, 12));
                author.setForeground(Color.DARK_GRAY);
                date.setForeground(Color.GRAY);
                title.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                JLabel[] rowLabels = {number, title, author, date};
                for (JLabel label : rowLabels) {
                    label.setOpaque(true);
                    label.setBackground(new Color(0xF8FAF6));
                    label.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(0xE1E7DD)));
                }

                // 📌 제목 클릭 시 상세 페이지 이동
                title.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        int noticeNum = notice.getNotice_num();
                        NoticeBean selectedNotice = new NoticeDAO().getNoticeById(noticeNum);

                        if (selectedNotice != null) {
                        	 AdminNoticeDetailPanel detailPanel = mainAdminPanel.getAdminNoticeDetailPanel(); 
                        	 detailPanel.loadNoticeDetail(noticeNum); // ✅ 공지사항 데이터 적용
                             mainAdminPanel.showPanel("AdminNoticeDetail"); // ✅ 패널 전환 수정
                        }
                    }
                });

                number.setBounds(30, rowY, 40, 44);
                title.setBounds(70, rowY, 140, 44);
                author.setBounds(210, rowY, 65, 44);
                date.setBounds(275, rowY, 85, 44);

                noticeListPanel.add(checkBox);
                noticeListPanel.add(number);
                noticeListPanel.add(title);
                noticeListPanel.add(author);
                noticeListPanel.add(date);
            }
        }

        noticeListPanel.revalidate();
        noticeListPanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == aButton.getButton()) {
            mainAdminPanel.showPanel("NoticeWrite");
        }
    }
}
