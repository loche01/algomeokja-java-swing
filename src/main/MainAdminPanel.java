package main;

import java.awt.*;
import javax.swing.*;
import model.LoginManager;
import panel.*;
import ui_n_utils.HeaderUtil;

public class MainAdminPanel extends JPanel {
    private NoticeWritePanel noticeWritePanel;
    private NoticeAdminPanel noticeAdminPanel; // 📌 NoticeAdminPanel 추가
    private AdminNoticeDetailPanel adminNoticeDetailPanel;
    private NoticeEditPanel noticeEditPanel;

    public MainAdminPanel() {
        setLayout(null);
        setBackground(Color.WHITE);
        setBounds(0, 0, 440, 956); // ✅ 프레임 크기에 맞춤

        JPanel header = HeaderUtil.createAdminHeader(
                "알고먹자",
                e -> showPanel("NoticeAdmin"),
                e -> confirmLogout());
        header.setBounds(0, 0, 440, 100);
        add(header);
        
        // 🔹 공지사항 관리 패널 추가
        noticeAdminPanel = new NoticeAdminPanel(this);
        noticeWritePanel = new NoticeWritePanel(this);
        adminNoticeDetailPanel = new AdminNoticeDetailPanel(this);
        noticeEditPanel = new NoticeEditPanel(this);

        // 🔹 패널 위치 및 크기 설정
        noticeAdminPanel.setBounds(0, 100, 440, 856);
        noticeWritePanel.setBounds(0, 100, 440, 856);
        adminNoticeDetailPanel.setBounds(0, 100, 440, 856);
        noticeEditPanel.setBounds(0, 100, 440, 856);
        
        add(noticeAdminPanel);
        add(noticeWritePanel);
        add(adminNoticeDetailPanel);
        add(noticeEditPanel);
        // 🔹 기본 화면 설정
        showPanel("NoticeAdmin");
    }

    private void confirmLogout() {
        Window window = SwingUtilities.getWindowAncestor(this);
        Component dialogParent = window != null ? window : this;
        int option = JOptionPane.showConfirmDialog(
                dialogParent,
                "로그아웃 하시겠습니까?",
                "로그아웃 확인",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        if (!(window instanceof MainFrame)) {
            JOptionPane.showMessageDialog(
                    dialogParent,
                    "로그인 화면으로 이동할 수 없습니다.",
                    "로그아웃 오류",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        LoginManager.getInstance().logout();
        ((MainFrame) window).showLoginAfterLogout();
        JOptionPane.showMessageDialog(
                window,
                "로그아웃 되었습니다.",
                "로그아웃 성공",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // 📌 패널 전환 메서드 (공지사항 관리/작성 화면 전환)
    public void showPanel(String panelName) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> showPanel(panelName));
            return;
        }

        noticeAdminPanel.setVisible("NoticeAdmin".equals(panelName));
        noticeWritePanel.setVisible("NoticeWrite".equals(panelName));
        adminNoticeDetailPanel.setVisible("AdminNoticeDetail".equals(panelName)); // ✅ 수정된 부분
        noticeEditPanel.setVisible("NoticeEditPanel".equals(panelName));

        if ("NoticeAdmin".equals(panelName)) {
            noticeAdminPanel.setEnabled(true);
            noticeAdminPanel.loadNotices();
        } else if ("NoticeWrite".equals(panelName)) {
            noticeWritePanel.setEnabled(true);
            noticeWritePanel.resetForEntry();
        } else if ("AdminNoticeDetail".equals(panelName)) {
            adminNoticeDetailPanel.setEnabled(true);
            adminNoticeDetailPanel.resetScrollPositions();
        } else if ("NoticeEditPanel".equals(panelName)) {
            noticeEditPanel.setEnabled(true);
            noticeEditPanel.resetScrollPositions();
        }

        revalidate();
        repaint();
    }

    // 📌 NoticeAdminPanel을 반환하는 메서드 추가 (NoticeWritePanel에서 사용)
    public NoticeAdminPanel getNoticeAdminPanel() {
        return noticeAdminPanel;
    }
    
    // 📌 AdminNoticeDetailPanel을 반환하는 메서드 추가 ✅ (이 코드가 오류 해결의 핵심)
    public AdminNoticeDetailPanel getAdminNoticeDetailPanel() {
        return adminNoticeDetailPanel;
    }
    
    public NoticeEditPanel getNoticeEditPanel() {
        return noticeEditPanel;
    }
}
