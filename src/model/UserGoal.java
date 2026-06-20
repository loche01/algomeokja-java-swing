package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UserGoal {
    private String userId;
    private BigDecimal startWeight;
    private BigDecimal targetWeight;
    private int targetDuration; // 일수로 저장
    private LocalDate createdAt;

    // 생성자
    public UserGoal(String userId, BigDecimal startWeight, BigDecimal targetWeight, int targetDuration) {
        this(userId, startWeight, targetWeight, targetDuration, null);
    }

    public UserGoal(String userId, BigDecimal startWeight, BigDecimal targetWeight, int targetDuration, LocalDate createdAt) {
        this.userId = userId;
        this.startWeight = startWeight;
        this.targetWeight = targetWeight;
        this.targetDuration = targetDuration;
        this.createdAt = createdAt;
    }

    // Getter & Setter
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public BigDecimal getStartWeight() { return startWeight; }
    public void setStartWeight(BigDecimal startWeight) { this.startWeight = startWeight; }

    public BigDecimal getTargetWeight() { return targetWeight; }
    public void setTargetWeight(BigDecimal targetWeight) { this.targetWeight = targetWeight; }

    public int getTargetDuration() { return targetDuration; }
    public void setTargetDuration(int targetDuration) { this.targetDuration = targetDuration; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}
