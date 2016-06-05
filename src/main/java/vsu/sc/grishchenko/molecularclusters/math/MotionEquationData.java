package vsu.sc.grishchenko.molecularclusters.math;

/**
 * <p>Класс для описания информации о частице моделируемой системы.</p>
 *
 * @author Грищенко Сергей
 */
public class MotionEquationData {
    /**
     * <p>Текстовая метка частицы.</p>
     */
    private String label;
    /**
     * <p>Модуль суммы сил, действующих на чатицу, описанный аналитическим выражением.</p>
     */
    private String accelerationEquation = "0";
    /**
     * <p>Вектор начального положения частицы.</p>
     */
    private Double[] initialPosition = {0., 0., 0.};
    /**
     * <p>Вектор начальной скорости частицы.</p>
     */
    private Double[] initialVelocity = {0., 0., 0.};
    /**
     * <p>Цвет частицы.</p>
     */
    private Color color = new Color(0.6, 0, 0, 1);

    public MotionEquationData(String label) {
        this.label = label;
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
