package model;

public class BodyInfo {
    private double height;
    private double weight;
    private double muscleMass;
    private double fatMass;
    private double fatPercentage;

    public BodyInfo(double height, double weight, double muscleMass, double fatMass, double fatPercentage) {
        this.height = height;
        this.weight = weight;
        this.muscleMass = muscleMass;
        this.fatMass = fatMass;
        this.fatPercentage = fatPercentage;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public double getMuscleMass() {
        return muscleMass;
    }

    public double getFatMass() {
        return fatMass;
    }

    public double getFatPercentage() {
        return fatPercentage;
    }
}
