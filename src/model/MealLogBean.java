package model;

public class MealLogBean {
    private int meal_log_code;    // meal_log_code (PK)
    private int meal_code;        // meal_code (FK)
    private int food_code;        // food_code (FK)
    private double weight_g;      // weight_g
    private double meal_kcal;     // meal_kcal
    private double meal_carb;     // meal_carb
    private double meal_protein;  // meal_protein
    private double meal_fat;      // meal_fat
    
    // 추가 필드 (JOIN 결과 저장용)
    private String food_name;     // food_name (from food table)
    
    // Getters and Setters
    public int getMeal_log_code() {
        return meal_log_code;
    }
    public void setMeal_log_code(int meal_log_code) {
        this.meal_log_code = meal_log_code;
    }
    
    public int getMeal_code() {
        return meal_code;
    }
    public void setMeal_code(int meal_code) {
        this.meal_code = meal_code;
    }
    
    public int getFood_code() {
        return food_code;
    }
    public void setFood_code(int food_code) {
        this.food_code = food_code;
    }
    
    public double getWeight_g() {
        return weight_g;
    }
    public void setWeight_g(double weight_g) {
        this.weight_g = weight_g;
    }
    
    public double getMeal_kcal() {
        return meal_kcal;
    }
    public void setMeal_kcal(double meal_kcal) {
        this.meal_kcal = meal_kcal;
    }
    
    public double getMeal_carb() {
        return meal_carb;
    }
    public void setMeal_carb(double meal_carb) {
        this.meal_carb = meal_carb;
    }
    
    public double getMeal_protein() {
        return meal_protein;
    }
    public void setMeal_protein(double meal_protein) {
        this.meal_protein = meal_protein;
    }
    
    public double getMeal_fat() {
        return meal_fat;
    }
    public void setMeal_fat(double meal_fat) {
        this.meal_fat = meal_fat;
    }
    
    public String getFood_name() {
        return food_name;
    }
    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }
}
