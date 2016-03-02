package vsu.sc.grishchenko.molecularclusters.math;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;
import java.util.stream.Collectors;

public final class Solver {
    private final static String separatorGroup= "(\\s|\\+|\\-|\\*|/|\\(|\\))";

    public static Map<String, List<Double>> solveSystem(List<DifferentialEquation> equations,
                                                        SolveMethod method,
                                                        Double initialCondition,
                                                        Integer countSteps,
                                                        Double stepSize) {

        ExpressionBuilder builder;
        Double newValue;
        List<Double> resultFunction;

        //init function lefts and result
        Map<String, List<Double>> result = new HashMap<>(equations.size());
        for (DifferentialEquation equation : equations) {
            builder = new ExpressionBuilder(equation.getLhsFunction());
            builder.variable("x");
            builder.variables((Set<String>) equations.stream().map(e -> e.getLabel()).collect(Collectors.toCollection(HashSet::new)));
            equation.setLhsFunctionExpression(builder.build());

            resultFunction = new ArrayList<>(countSteps);
            resultFunction.add(equation.getInitialCondition());
            result.put(equation.getLabel(), resultFunction);
        }

        List<List<Double>> k = new ArrayList<>(4);
        if (SolveMethod.RUNGE_KUTTA_4.equals(method)) {
            //init coefficients
            for (int i = 0; i < 4; i++) {
                k.add(new ArrayList<>(equations.size()));
                for (DifferentialEquation equation : equations)
                    k.get(i).add(equation.getInitialCondition());
            }
        }

        //body
        for (int i = 1; i < countSteps; i++, initialCondition += stepSize) {
            switch (method) {
                case EULER:
                    for (DifferentialEquation equation : equations) {
                        equation.setLhsValue(equation
                                .getLhsFunctionExpression()
                                .setVariables(equations.stream().collect(Collectors.toMap(e -> e.getLabel(), e -> e.getInitialCondition())))
                                .setVariable("x", initialCondition)
                                .evaluate());
                    }
                    for (DifferentialEquation equation : equations) {
                        newValue = equation.getInitialCondition() + stepSize * equation.getLhsValue();

                        result.get(equation.getLabel()).add(newValue);
                        equation.setInitialCondition(newValue);
                    }
                    break;
                case RUNGE_KUTTA_4:
                    //k1
                    evoluteCoeff(result, null, k.get(0),
                            equations.stream().map(e -> e.getLhsFunctionExpression()).collect(Collectors.toList()),
                            initialCondition, stepSize, 1, i, equations.size());
                    //k2
                    evoluteCoeff(result, k.get(0), k.get(1),
                            equations.stream().map(e -> e.getLhsFunctionExpression()).collect(Collectors.toList()),
                            initialCondition, stepSize, 2, i, equations.size());
                    //k3
                    evoluteCoeff(result, k.get(1), k.get(2),
                            equations.stream().map(e -> e.getLhsFunctionExpression()).collect(Collectors.toList()),
                            initialCondition, stepSize, 2, i, equations.size());
                    //k4
                    evoluteCoeff(result, k.get(2), k.get(3),
                            equations.stream().map(e -> e.getLhsFunctionExpression()).collect(Collectors.toList()),
                            initialCondition, stepSize, 1, i, equations.size());

                    for (int j = 0; j < equations.size(); j++) {
                        String variableName = "y" + (j + 1);
                        result.get(variableName).add(result.get(variableName).get(i - 1)
                                + (k.get(0).get(j) + 2 * k.get(1).get(j) + 2 * k.get(2).get(j) + k.get(3).get(j)) / 6);
                    }
                    break;
                default:
                    break;
            }
        }

        return result;
    }

    private static void evoluteCoeff(Map<String, List<Double>> result,
                                     List<Double> baseCoeff,
                                     List<Double> coeff,
                                     List<Expression> expressions,
                                     Double x,
                                     Double h,
                                     Integer denom,
                                     Integer index,
                                     Integer size) {
        Expression tmp;
        for (int j = 0; j < size; j++) {
            tmp = expressions.get(j);
            tmp.setVariable("x", x);
            for (int l = 0; l < size; l++) {
                String variableName = "y" + (l + 1);
                tmp.setVariable(variableName,
                        result.get(variableName).get(index - 1)
                                + (baseCoeff == null ? 0 : baseCoeff.get(l) / denom));
            }
            coeff.set(j, h * tmp.evaluate());
        }
    }

    private static MotionEquation getMotionEquation(MotionEquationData data,
                                                    int projection,
                                                    List<MotionEquationData> dataList) {
        String projectionLabel;
        switch (projection) {
            case 0 : projectionLabel = "x"; break;
            case 1 : projectionLabel = "y"; break;
            case 2 : projectionLabel = "z"; break;
            default : projectionLabel = ""; break;
        }
        MotionEquation result = new MotionEquation(projectionLabel + data.getLabel(),
                data.getAccelerationEquation(),
                data.getInitialPosition()[projection],
                data.getInitialVelocity()[projection],
                data.getColor());
        dataList.stream()
                .map(MotionEquationData::getLabel)
                .forEach(label -> result.setAccelerationEquation(
                        result.getAccelerationEquation().replaceAll(separatorGroup + label + separatorGroup,
                                "$1" + projectionLabel + label + "$2")));

        return result;
    }

    public static List<Trajectory> solveVerlet(List<MotionEquationData> dataList,
                                                        double initialTime,
                                                        int countSteps,
                                                        double stepSize) {

        ExpressionBuilder builder;
        double initialAcceleration;
        Map<String, Trajectory> result = new HashMap<>(dataList.size() * 3);
        List<MotionEquation> equations = new ArrayList<>(dataList.size() * 3);
        List<Double> trajectory;
        StringBuilder compiledEquation;
        String distance;

        for (MotionEquationData data : dataList) {
            if (data.getAccelerationEquation().contains("r$")) {
                compiledEquation = new StringBuilder();
                for (int i = 0 ; i < dataList.size(); i++) {
                    if (!data.getLabel().equals(dataList.get(i).getLabel())) {
                        if (compiledEquation.length() != 0 && i != 0) compiledEquation.append("+");

                        distance = String.format("sqrt((x%2$s-x%1$s)^2 + (y%2$s-y%1$s)^2 + (z%2$s-z%1$s)^2)",
                                data.getLabel(), dataList.get(i).getLabel());

                        compiledEquation.append("(");
                        compiledEquation.append(data.getAccelerationEquation().replaceAll("r\\$", distance));
                        compiledEquation.append(String.format(")*(%1$s-%2$s)/",
                                data.getLabel(), dataList.get(i).getLabel()));
                        compiledEquation.append(distance);

                    }
                }
                data.setAccelerationEquation(compiledEquation.toString());
            }
            equations.add(getMotionEquation(data, 0, dataList));
            equations.add(getMotionEquation(data, 1, dataList));
            equations.add(getMotionEquation(data, 2, dataList));
        }

        //first step
        for (MotionEquation equation : equations) {

            builder = new ExpressionBuilder(equation.getAccelerationEquation());
            builder.variable("t");
            builder.variables(equations.stream().map(e -> e.getLabel()).collect(Collectors.toCollection(HashSet::new)));
            equation.setAccelerationExpression(builder.build());

            initialAcceleration = equation
                    .getAccelerationExpression()
                    .setVariables(equations.stream().collect(Collectors.toMap(e -> e.getLabel(), e -> e.getBeforeLastPosition())))
                    .setVariable("t", initialTime)
                    .evaluate();

            equation.setPastPosition(equation.getBeforeLastPosition()
                    + equation.getInitialVelocity() * stepSize
                    + initialAcceleration * Math.pow(stepSize, 2) / 2);

            trajectory = new ArrayList<>(countSteps);
            trajectory.add(equation.getBeforeLastPosition());
            trajectory.add(equation.getPastPosition());
            result.put(equation.getLabel(), new Trajectory(equation.getLabel(), equation.getColor(), trajectory));
        }

        //iteration
        initialTime += 2 * stepSize;
        double newPosition;
        for (int i = 2; i < countSteps; i++, initialTime += stepSize) {
            for (MotionEquation equation : equations) {
                equation.setAcceleration(equation
                        .getAccelerationExpression()
                        .setVariables(equations.stream().collect(Collectors.toMap(e -> e.getLabel(), e -> e.getPastPosition())))
                        .setVariable("t", initialTime)
                        .evaluate());
            }

            for (MotionEquation equation : equations) {
                newPosition = 2 * equation.getPastPosition()
                        - equation.getBeforeLastPosition()
                        + equation.getAcceleration() * Math.pow(stepSize, 2);

                trajectory = result.get(equation.getLabel()).getPath();
                trajectory.add(newPosition);
                equation.setBeforeLastPosition(equation.getPastPosition());
                equation.setPastPosition(newPosition);
            }
        }

        return new ArrayList<>(result.values());
    }
}
