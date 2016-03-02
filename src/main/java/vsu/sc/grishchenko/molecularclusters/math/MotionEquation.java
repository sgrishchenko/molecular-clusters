package vsu.sc.grishchenko.molecularclusters.math;

import net.objecthunter.exp4j.Expression;

public class MotionEquation {
    private String label;

    private double beforeLastPosition;
    private double pastPosition;

    private double initialVelocity;

    private String accelerationEquation;
    private Expression accelerationExpression;
    private double acceleration;
    private Color color = new Color(0.6, 0, 0, 1);

    public MotionEquation(String label,
                          String accelerationEquation,
                          double initialPosition,
                          double initialVelocity,
                          Color color) {
        this.label = label;
        this.beforeLastPosition = initialPosition;
        this.initialVelocity = initialVelocity;
        this.accelerationEquation = accelerationEquation;
        this.color = color;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getPastPosition() {
        return pastPosition;
    }

    public void setPastPosition(double pastPosition) {
        this.pastPosition = pastPosition;
    }

    public Expression getAccelerationExpression() {
        return accelerationExpression;
    }

    public void setAccelerationExpression(Expression accelerationExpression) {
        this.accelerationExpression = accelerationExpression;
    }

    public double getBeforeLastPosition() {
        return beforeLastPosition;
    }

    public void setBeforeLastPosition(double beforeLastPosition) {
        this.beforeLastPosition = beforeLastPosition;
    }

    public double getInitialVelocity() {
        return initialVelocity;
    }

    public void setInitialVelocity(double initialVelocity) {
        this.initialVelocity = initialVelocity;
    }

    public String getAccelerationEquation() {
        return accelerationEquation;
    }

    public void setAccelerationEquation(String accelerationEquation) {
        this.accelerationEquation = accelerationEquation;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
