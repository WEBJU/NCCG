package model;

public class Parking {
    private String carType;
    private String model;
    private String numberPlate;
    private String color;
    private String other;
    private String uid;

    public Parking() {
    }

    public Parking(String carType, String model, String numberPlate, String color, String other, String uid) {
        this.carType = carType;
        this.model = model;
        this.numberPlate = numberPlate;
        this.color = color;
        this.other = other;
        this.uid = uid;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

}
