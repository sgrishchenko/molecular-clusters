package vsu.sc.grishchenko.molecularclusters.math;

public class MotionEquationData {
    private String label;
    private String accelerationEquation;
    private Double[] initialPosition = {0., 0., 0.};
    private Double[] initialVelocity = {0., 0., 0.};

    public MotionEquationData(String label) {
        this.label = label;
        this.accelerationEquation = "0";
    }

    public MotionEquationData(String label,
                              String accelerationEquation,
                              Double[] initialPosition,
                              Double[] initialVelocity) {
        this.label = label;
        this.accelerationEquation = accelerationEquation;
        this.initialPosition = initialPosition;
        this.initialVelocity = initialVelocity;
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
}
