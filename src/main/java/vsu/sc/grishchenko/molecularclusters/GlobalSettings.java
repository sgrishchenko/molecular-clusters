package vsu.sc.grishchenko.molecularclusters;

public class GlobalSettings {
    public ViewSettings viewSettings = new ViewSettings();
    public ExperimentSettings experimentSettings = new ExperimentSettings();

    private static GlobalSettings ourInstance = new GlobalSettings();

    public static GlobalSettings getInstance() {
        return ourInstance;
    }

    private GlobalSettings() {
    }

    private class MathSettings {
        private Integer countSteps = 1000;
        private Double stepSize = 0.001;

        public Integer getCountSteps() {
            return countSteps;
        }

        public void setCountSteps(Integer countSteps) {
            this.countSteps = countSteps;
        }

        public Double getStepSize() {
            return stepSize;
        }

        public void setStepSize(Double stepSize) {
            this.stepSize = stepSize;
        }
    }

    public class ViewSettings extends MathSettings {
        private Integer animateStepSize = 15;

        public Integer getAnimateStepSize() {
            return animateStepSize;
        }

        public void setAnimateStepSize(Integer animateStepSize) {
            this.animateStepSize = animateStepSize;
        }
    }

    public class ExperimentSettings extends MathSettings {
        private String movingPointLabel;
        private Integer[] chirality = {4, 4};
        private Double[] initialPosition = {0., -3., 0.};
        private Double initialVelocity = 75.;

        public String getMovingPointLabel() {
            return movingPointLabel;
        }

        public void setMovingPointLabel(String movingPointLabel) {
            this.movingPointLabel = movingPointLabel;
        }

        public Integer[] getChirality() {
            return chirality;
        }

        public void setChirality(Integer[] chirality) {
            this.chirality = chirality;
        }

        public Double[] getInitialPosition() {
            return initialPosition;
        }

        public void setInitialPosition(Double[] initialPosition) {
            this.initialPosition = initialPosition;
        }

        public Double getInitialVelocity() {
            return initialVelocity;
        }

        public void setInitialVelocity(Double initialVelocity) {
            this.initialVelocity = initialVelocity;
        }
    }
}
