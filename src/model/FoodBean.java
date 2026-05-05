package model;

public class FoodBean {
    private int foodCode;  // 음식 코드
    private String foodName;  // 음식 이름
    private double foodKcal;  // 칼로리
    private double carb;  // 탄수화물
    private double protein;  // 단백질
    private double fat;  // 지방
    
 // model.FoodBean.java
    private int weight = 100; // 기본 100g
    public int getWeight() {
        return weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }

    
    public int getFoodCode() {
        return foodCode;
    }

    public void setFoodCode(int foodCode) {
        this.foodCode = foodCode;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getFoodKcal() {
        return foodKcal;
    }

    public void setFoodKcal(double foodKcal) {
        this.foodKcal = foodKcal;
    }

    public double getCarb() {
        return carb;
    }

    public void setCarb(double carb) {
        this.carb = carb;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }
}
