package model;

import java.util.Date;

public class UserBean {
	
	
	private String user_id;        // 회원아이디
    private String user_name;      // 이름
    private String user_phone;     // 전화번호
    private String user_email;     // 이메일
    private Date user_createdtime; // 생성시간
	private String user_role; 
    private String user_birthdate; // 생년월일
    private String user_gender;    // 성별

    public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_phone() {
		return user_phone;
	}
	public void setUser_phone(String user_phone) {
		this.user_phone = user_phone;
	}
	public String getUser_email() {
		return user_email;
	}
	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}
	public Date getUser_createdtime() {
		return user_createdtime;
	}
	public void setUser_createdtime(Date user_createdtime) {
		this.user_createdtime = user_createdtime;
	}
    
    // 🔹 사용자 역할 (ADMIN or USER)
    public String getUser_role() { return user_role; }
    public void setUser_role(String user_role) { this.user_role = user_role; }
    
    // 🔹 생년월일
    public String getUser_birthdate() { return user_birthdate; }
    public void setUser_birthdate(String user_birthdate) { this.user_birthdate = user_birthdate; }
    
    // 🔹 성별
    public String getUser_gender() { return user_gender; }
    public void setUser_gender(String user_gender) { this.user_gender = user_gender; }

    // 🔹 관리자 여부 확인 (ADMIN인지 체크)
    public boolean isAdmin() {
        return "ADMIN".equals(this.user_role);
    }
}
