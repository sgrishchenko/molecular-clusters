package vsu.sc.grishchenko.molecularclusters.experiment;

import java.util.function.BiConsumer;

public class Iteration<T> {
    private String name;
    private double from;
    private double to;
    private double step;
    private double currentValue;
    private BiConsumer<T, Double> action;
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

    public boolean iterate() {
        if (direction * currentValue >= direction * to) return false;

        currentValue += direction * step;

        if (direction * currentValue > direction * to) currentValue = to;

        return true;
    }

    public void handle(T obj) {
        getAction().accept(obj, currentValue);
    }

    public void restart() {
        currentValue = from;
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
