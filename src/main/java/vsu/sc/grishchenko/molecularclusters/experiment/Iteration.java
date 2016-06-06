package vsu.sc.grishchenko.molecularclusters.experiment;

import java.util.function.BiConsumer;

/**
 * <p>Класс, описывающий итерацию и её параметры в рамках задания
 * на выполнение расчета серии экспериментов.</p>
 *
 * @param <T> тип элемента, с которым будут выполняться действия в процессе итерации
 * @author Грищенко Сергей
 */
public class Iteration<T> {
    /**
     * <p>Название итерации.</p>
     */
    private String name;
    /**
     * <p>Начальное значение итерации.</p>
     */
    private double from;
    /**
     * <p>Конечное значение итерации.</p>
     */
    private double to;
    /**
     * <p>Шаг итерации.</p>
     */
    private double step;
    /**
     * <p>Текущее значение в процессе выполнения итерации.</p>
     */
    private double currentValue;
    /**
     * <p>Действие, которое необходимо выполнить с объектом, для которого проводится итерация
     * и текущим значением в процессе итерации.</p>
     */
    private BiConsumer<T, Double> action;
    /**
     * <p>Направление итерации (<code>1</code> - положительное, <code>-1</code> - отрицательное).</p>
     */
    private int direction = 1;

    public Iteration(String name, double from, double to, double step, BiConsumer<T, Double> action) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.step = step;
        this.action = action;
        currentValue = from;
        if (from > to) direction = -1;
    }

    /**
     * <p>Переход к следующему шагу итерации.</p>
     *
     * @return <code>true</code>, если есть возможность совершить следующий переход,
     * иначе <code>false</code>.
     */
    public boolean iterate() {
        if (direction * currentValue >= direction * to) return false;

        currentValue += direction * step;

        if (direction * currentValue > direction * to) currentValue = to;

        return true;
    }

    /**
     * <p>Применение метода {@link Iteration#action} к переданному объекту.</p>
     *
     * @param obj объект, с которым необходимо выполнить действие
     * @see Iteration#action
     */
    public void handle(T obj) {
        getAction().accept(obj, currentValue);
    }

    /**
     * <p>Возвращает итерацию в исходное положение.</p>
     */
    public void restart() {
        currentValue = from;
    }

    /**
     * <p>Рассчитывает количество шагов в итерации.</p>
     *
     * @return количество шагов.
     */
    public int getStepCount() {
        return (int) Math.ceil((to * direction - from * direction) / step);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getFrom() {
        return from;
    }

    public void setFrom(double from) {
        this.from = from;
    }

    public double getTo() {
        return to;
    }

    public void setTo(double to) {
        this.to = to;
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public BiConsumer<T, Double> getAction() {
        return action;
    }

    public void setAction(BiConsumer<T, Double> action) {
        this.action = action;
    }

    public double getCurrentValue() {
        return currentValue;
    }
}
