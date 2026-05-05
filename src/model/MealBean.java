package model;

import java.sql.Date;
import java.sql.Time;

public class MealBean {
    private int meal_code;          // meal_code (PK)
    private String user_id;         // user_id
    private Date meal_date;         // meal_date
    private Time meal_time;         // meal_time
    private String meal_type;       // meal_type (ENUM: '아침','점심','저녁','간식')
    private String meal_image_path; // meal_image_path
    
    // Getters and Setters
    public int getMeal_code() {
        return meal_code;
    }
    
    public void setMeal_code(int meal_code) {
        this.meal_code = meal_code;
    }
    
    public String getUser_id() {
        return user_id;
    }
    
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    
    public Date getMeal_date() {
        return meal_date;
    }
    
    public void setMeal_date(Date meal_date) {
        this.meal_date = meal_date;
    }
    
    public Time getMeal_time() {
        return meal_time;
    }
    
    public void setMeal_time(Time meal_time) {
        this.meal_time = meal_time;
    }
    
    public String getMeal_type() {
        return meal_type;
    }
    
    public void setMeal_type(String meal_type) {
        this.meal_type = meal_type;
    }
    
    public String getMeal_image_path() {
        return meal_image_path;
    }
    
    public void setMeal_image_path(String meal_image_path) {
        this.meal_image_path = meal_image_path;
    }
}