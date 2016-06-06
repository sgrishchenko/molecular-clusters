package vsu.sc.grishchenko.molecularclusters.settings;

import vsu.sc.grishchenko.molecularclusters.animation.RunAnimate;

/**
 * <p>Класс, написанный с использованием шаблона проектирования "Одиночка"
 * и хранящий текущие настройки приложения.</p>
 *
 * @author Грищенко Сергей
 */
public class CurrentSettings {
    private static CurrentSettings ourInstance = new CurrentSettings();

    /**
     * <p>Количество шагов при выполнении алгоритма численного интергирования.</p>
     */
    private Integer countSteps = 1000;
    /**
     * <p>Величина сдвига по времени при выполнении алгоритма численного интергирования.</p>
     */
    private Double stepSize = 0.001;
    /**
     * <p>Время в миллисекундах между шагами анимации при демонстрации трехмерной анимированной модели.</p>
     *
     * @see RunAnimate#timeStep
     */
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
