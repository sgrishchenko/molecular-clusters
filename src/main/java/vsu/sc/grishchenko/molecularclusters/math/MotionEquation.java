package vsu.sc.grishchenko.molecularclusters.math;

import net.objecthunter.exp4j.Expression;

/**
 * <p>Класс для описания уравнения движения частицы моделируемой системы.
 * Речь идет об описание определнного измерения при движении в трехмерном пространстве.</p>
 *
 * @author Грищенко Сергей
 */
public class MotionEquation {
    /**
     * <p>Текстовая метка частицы.</p>
     */
    private String label;
    /**
     * <p>Предпоследнее значение положения частицы.</p>
     */
    private double beforeLastPosition;
    /**
     * <p>Последнее значенияе положения частицы.</p>
     */
    private double pastPosition;
    /**
     * <p>Значение начальной скорости частицы.</p>
     */
    private double initialVelocity;
    /**
     * <p>Модуль суммы сил, действующих на чатицу, описанный аналитическим выражением.</p>
     */
    private String accelerationEquation;
    /**
     * <p>Объект {@link Expression}, созданный на основе {@link MotionEquation#accelerationEquation}.</p>
     *
     * @see Exception
     */
    private Expression accelerationExpression;
    /**
     * <p>Значение ускорения частицы.</p>
     */
    private double acceleration;
    /**
     * <p>Цвет частицы.</p>
     */
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
