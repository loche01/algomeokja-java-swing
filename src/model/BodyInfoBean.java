package model;

public class BodyInfoBean {
    private float height;
    private float weight;
    private float fatRate;
    private float fatMass;
    private float muscleMass;

    // ✅ 기본 생성자
    public BodyInfoBean() {}

    // ✅ 모든 필드를 받는 생성자
    public BodyInfoBean(float height, float weight, float fatRate, float fatMass, float muscleMass) {
        this.height = height;
        this.weight = weight;
        this.fatRate = fatRate;
        this.fatMass = fatMass;
        this.muscleMass = muscleMass;
    }

    // ✅ Getter & Setter 메서드
    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }

    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }

    public float getFatRate() { return fatRate; }
    public void setFatRate(float fatRate) { this.fatRate = fatRate; }

    public float getFatMass() { return fatMass; }
    public void setFatMass(float fatMass) { this.fatMass = fatMass; }

    public float getMuscleMass() { return muscleMass; }
    public void setMuscleMass(float muscleMass) { this.muscleMass = muscleMass; }

    // ✅ 디버깅용 toString() 메서드 추가
    @Override
    public String toString() {
        return "BodyInfoBean [height=" + height + ", weight=" + weight + 
               ", fatRate=" + fatRate + ", fatMass=" + fatMass + 
               ", muscleMass=" + muscleMass + "]";
    }
}
