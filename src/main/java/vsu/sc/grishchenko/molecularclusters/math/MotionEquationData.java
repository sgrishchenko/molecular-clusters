package vsu.sc.grishchenko.molecularclusters.math;

public class MotionEquationData {
    private String label;
    private String accelerationEquation;
    private Double[] initialPosition = {0., 0., 0.};
    private Double[] initialVelocity = {0., 0., 0.};
    private Color color = new Color(0.6, 0, 0, 1);

    public MotionEquationData(String label) {
        this.label = label;
        this.accelerationEquation = "0";
    }

    public MotionEquationData(String label,
                              String accelerationEquation,
                              Double[] initialPosition,
                              Double[] initialVelocity,
                              Color color) {
        this.label = label;
        this.accelerationEquation = accelerationEquation;
        this.initialPosition = initialPosition;
        this.initialVelocity = initialVelocity;
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAccelerationEquation() {
        return accelerationEquation;
    }

    public void setAccelerationEquation(String accelerationEquation) {
        this.accelerationEquation = accelerationEquation;
    }

    public Double[] getInitialPosition() {
        return initialPosition;
    }

    public void setInitialPosition(Double[] initialPosition) {
        this.initialPosition = initialPosition;
    }

    public Double[] getInitialVelocity() {
        return initialVelocity;
    }

    public void setInitialVelocity(Double[] initialVelocity) {
        this.initialVelocity = initialVelocity;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
