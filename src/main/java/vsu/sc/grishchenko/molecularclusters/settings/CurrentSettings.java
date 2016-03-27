package vsu.sc.grishchenko.molecularclusters.settings;

public class CurrentSettings {
    private static CurrentSettings ourInstance = new CurrentSettings();

    private Integer countSteps = 1000;
    private Double stepSize = 0.001;
    private Integer animateStepSize = 15;

    public static CurrentSettings getInstance() {
        return ourInstance;
    }

    private CurrentSettings() {
    }

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

    public Integer getAnimateStepSize() {
        return animateStepSize;
    }

    public void setAnimateStepSize(Integer animateStepSize) {
        this.animateStepSize = animateStepSize;
    }
}
