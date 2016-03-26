package vsu.sc.grishchenko.molecularclusters.experiment;

import vsu.sc.grishchenko.molecularclusters.math.MotionEquationData;

import java.util.ArrayList;
import java.util.List;

public class ExperimentConfig {
    private String movingPointLabel;

    private Double[] initialPosition;
    private Double[] initialVelocity;

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
