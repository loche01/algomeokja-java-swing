package model;

public class ExerciseBean {
    private int exerciseCode;
    private String exerciseName;
    private String exerciseCategory;
    private String exerciseType;
    private float exerciseMET;
    
    // 기본 생성자
    public ExerciseBean() {}
    
    // 모든 필드를 초기화하는 생성자
    public ExerciseBean(int exerciseCode, String exerciseName, String exerciseCategory, 
                        String exerciseType, float exerciseMET) {
        this.exerciseCode = exerciseCode;
        this.exerciseName = exerciseName;
        this.exerciseCategory = exerciseCategory;
        this.exerciseType = exerciseType;
        this.exerciseMET = exerciseMET;
    }
    
    // Getter와 Setter 메서드
    public int getExerciseCode() {
        return exerciseCode;
    }
    
    public void setExerciseCode(int exerciseCode) {
        this.exerciseCode = exerciseCode;
    }
    
    public String getExerciseName() {
        return exerciseName;
    }
    
    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }
    
    public String getExerciseCategory() {
        return exerciseCategory;
    }
    
    public void setExerciseCategory(String exerciseCategory) {
        this.exerciseCategory = exerciseCategory;
    }
    
    public String getExerciseType() {
        return exerciseType;
    }
    
    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }
    
    public float getExerciseMET() {
        return exerciseMET;
    }
    
    public void setExerciseMET(float exerciseMET) {
        this.exerciseMET = exerciseMET;
    }
    
    @Override
    public String toString() {
        return "ExerciseBean [exerciseCode=" + exerciseCode + 
               ", exerciseName=" + exerciseName + 
               ", exerciseCategory=" + exerciseCategory + 
               ", exerciseType=" + exerciseType + 
               ", exerciseMET=" + exerciseMET + "]";
    }
}