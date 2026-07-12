package ui_n_utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.nio.CharBuffer;
import java.util.regex.Pattern;

public class ValidationUtils {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Za-z])(?=.*[@#$%^&+=*!?])[A-Za-z0-9@#$%^&+=*!?]{6,20}$");

    // 아이디 양식 검사
    public static boolean isValidUserId(String userId) {
        // 영문 대소문자, 숫자, 특수문자 중 하나 이상 포함된 6~20자의 아이디
        return Pattern.matches("^[A-Za-z0-9@#$%^&+=*!?]{6,20}$", userId);
    }
    
    //비밀번호 양식검사
    public static boolean isCreateUserPw(String userPw) {
        // 영문 대소문자와 특수문자가 반드시 포함된 6~20자의 아이디
        return userPw != null && PASSWORD_PATTERN.matcher(userPw).matches();
    }

    public static boolean isCreateUserPw(char[] userPw) {
        return userPw != null && PASSWORD_PATTERN.matcher(CharBuffer.wrap(userPw)).matches();
    }
    
    //이메일 양식검사
    public static boolean isValidEmail(String email) {
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailPattern, email);
    }
    
    // 휴대폰 양식 검사
    public static boolean isValidPhone(String phone) {
    	String phonePattern = "^010-\\d{4}-\\d{4}$";
        return Pattern.matches(phonePattern, phone);
    }
    
    // ✅ 이름 유효성 검사 추가 (한글 2~10자 이내, 숫자 및 특수문자 포함 불가)
    public static boolean isValidName(String name) {
        return Pattern.matches("^[가-힣]{2,10}$", name);
    }
    
    // 생년월일 형식 검사 (YYYY-MM-DD)
    public static boolean isValidDate(String date) {
        // 정규식으로 기본 형식 검사
        if (!Pattern.matches("^\\d{4}-\\d{2}-\\d{2}$", date)) {
            return false;
        }
        
        // SimpleDateFormat을 사용하여 실제 날짜 유효성 검사
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false); // 엄격한 검사 모드 설정
        
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
