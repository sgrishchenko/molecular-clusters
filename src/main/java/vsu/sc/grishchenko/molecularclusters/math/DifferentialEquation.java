package vsu.sc.grishchenko.molecularclusters.math;

import net.objecthunter.exp4j.Expression;

public class DifferentialEquation {
    private String label;
    private double initialCondition;

    private String lhsFunction;
    private Expression lhsFunctionExpression;
    private double lhsValue;

    public DifferentialEquation(String label, String lhsFunction, double initialCondition) {
        this.label = label;
        this.lhsFunction = lhsFunction;
        this.initialCondition = initialCondition;
    }

    public double getLhsValue() {
        return lhsValue;
    }

    public void setLhsValue(double lhsValue) {
        this.lhsValue = lhsValue;
    }

    public Expression getLhsFunctionExpression() {
        return lhsFunctionExpression;
    }

    public void setLhsFunctionExpression(Expression lhsFunctionExpression) {
        this.lhsFunctionExpression = lhsFunctionExpression;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLhsFunction() {
        return lhsFunction;
    }

    public void setLhsFunction(String lhsFunction) {
        this.lhsFunction = lhsFunction;
    }

    public double getInitialCondition() {
        return initialCondition;
    }

    public void setInitialCondition(double initialCondition) {
        this.initialCondition = initialCondition;
    }
}
