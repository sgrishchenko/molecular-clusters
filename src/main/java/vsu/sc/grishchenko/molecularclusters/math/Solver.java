package vsu.sc.grishchenko.molecularclusters.math;

import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>Класс с набором утилитных методов для решение дифференциальных уравнений.</p>
 *
 * @author Грищенко Сергей
 */
public final class Solver {
    /**
     * <p>Константа, используемая в регуляном выражения для добавления к
     * текстовой метке частицы префиксов <i>x</i>, <i>y</i> и <i>z</i>
     * при формировании из одного векторного уравнения три вещественных.</p>
     */
    private final static String separatorGroup = "(\\s|\\+|\\-|\\*|/|\\(|\\))";

    /**
     * <p>Метод преобразования объекта {@link MotionEquationData}
     * в объект {@link MotionEquation}. Из одного объекта {@link MotionEquationData}
     * потенциально можно получить три различных объекта {@link MotionEquation}
     * в зависимости от того, какую проекцию векторного уравнения
     * в {@link MotionEquationData} требуется рассмотреть.</p>
     *
     * @param data       объект {@link MotionEquationData}
     * @param projection индекс проекции, которую следует использовать для преобразования
     *                   (<code>0</code> - проекция по координате X,
     *                   <code>1</code> - проекция по координате Y,
     *                   <code>2</code> - проекция по координате Z)
     * @param dataList   общий список всех объектов {@link MotionEquationData} для моделируемой системы
     * @return результирующий объект {@link MotionEquationData}.
     * @see MotionEquationData
     * @see MotionEquation
     */
    private static MotionEquation getMotionEquation(MotionEquationData data,
                                                    int projection,
                                                    List<MotionEquationData> dataList) {
        String projectionLabel;
        switch (projection) {
            case 0:
                projectionLabel = "x";
                break;
            case 1:
                projectionLabel = "y";
                break;
            case 2:
                projectionLabel = "z";
                break;
            default:
                projectionLabel = "";
                break;
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

    /**
     * <p>Метод, реализующий решение системы дифференциальный уравнений второго порядка
     * с помощью метода численного интегрирования <i>Верле</i>.</p>
     *
     * @param dataList    список всех объектов {@link MotionEquationData} для моделируемой системы
     * @param initialTime начальное значение для переменной времени
     * @param countSteps  количество шагов при выполнении алгоритма численного интегрирования
     * @param stepSize    величина шага при выполнении алгоритма численного интегрирования
     * @return список объектов {@link Trajectory}, описывающий траектрии движения частиц моделируемой системы.
     * @see MotionEquationData
     * @see Trajectory
     */
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
                for (int i = 0; i < dataList.size(); i++) {
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
