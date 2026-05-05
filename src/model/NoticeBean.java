package model;

import java.util.Date;

public class NoticeBean {
    private int notice_num;       // 공지사항 번호 (PK)
    private String admin_id;      // 관리자 ID
    private String notice_title;  // 공지사항 제목
    private String notice_content; // 공지사항 내용
    private Date notice_time;     // 작성 시간

    public int getNotice_num() { return notice_num; }
    public void setNotice_num(int notice_num) { this.notice_num = notice_num; }

    public String getAdmin_id() { return admin_id; }
    public void setAdmin_id(String admin_id) { this.admin_id = admin_id; }

    public String getNotice_title() { return notice_title; }
    public void setNotice_title(String notice_title) { this.notice_title = notice_title; }

    public String getNotice_content() { return notice_content; }
    public void setNotice_content(String notice_content) { this.notice_content = notice_content; }

    public Date getNotice_time() { return notice_time; }
    public void setNotice_time(Date notice_time) { this.notice_time = notice_time; }
}
