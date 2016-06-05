package vsu.sc.grishchenko.molecularclusters.experiment;

import vsu.sc.grishchenko.molecularclusters.math.MotionEquationData;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Класс для хранения параметров конфигурации запуска задания
 * на выполнение серии экспериментов.</p>
 *
 * @author Грищенко Сергей
 */
public class ExperimentConfig {
    /**
     * <p>Метка частицы, начальное положение и/или начальная скорость которой
     * будет варьироваться между запусками экспериментов.</p>
     */
    private String movingPointLabel;
    /**
     * <p>Начальное положение частицы, начальное положение и/или начальная скорость которой
     * будет варьироваться между запусками экспериментов.</p>
     */
    private Double[] initialPosition;
    /**
     * <p>Начальная скорость частицы, начальное положение и/или начальная скорость которой
     * будет варьироваться между запусками экспериментов.</p>
     */
    private Double[] initialVelocity;
    /**
     * <p>Список итераций, которые необходимо выполнить в рамках запуска задания.</p>
     *
     * @see Iteration
     * @see MotionEquationData
     */
    private List<Iteration<MotionEquationData>> iterations = new ArrayList<>();

    public List<Iteration<MotionEquationData>> getIterations() {
        return iterations;
    }

    public void setIterations(List<Iteration<MotionEquationData>> iterations) {
        this.iterations = iterations;
    }

    public String getMovingPointLabel() {
        return movingPointLabel;
    }

    public void setMovingPointLabel(String movingPointLabel) {
        this.movingPointLabel = movingPointLabel;
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
