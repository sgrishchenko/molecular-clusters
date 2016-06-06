package vsu.sc.grishchenko.molecularclusters.experiment;

import vsu.sc.grishchenko.molecularclusters.math.Trajectory;
import vsu.sc.grishchenko.molecularclusters.settings.CurrentSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * <p>Класс с набором статических методов для получения макроскопических параметров моделей.</p>
 *
 * @author Грищенко Сергей
 */
public final class Analyzer {
    /**
     * <p>Получения макроскопических параметров на основе рассчитанных траекторий движения частиц.</p>
     *
     * @param solvingSystemResult список рассчитанных траекторий движения частиц
     * @return объект с макроскопическими параметрами.
     * @see AnalyzeResult
     * @see Trajectory
     */
    public static AnalyzeResult getParams(List<Trajectory> solvingSystemResult) {

        List<String> movingPoints = getMovingPoints(solvingSystemResult);
        String xMovingPoint = movingPoints.stream().filter(p -> p.startsWith("x")).findFirst().get();
        String yMovingPoint = movingPoints.stream().filter(p -> p.startsWith("y")).findFirst().get();
        String zMovingPoint = movingPoints.stream().filter(p -> p.startsWith("z")).findFirst().get();
        Integer countSteps = solvingSystemResult.get(0).getPath().size();


        List<Trajectory> tubeOnly = new ArrayList<>(solvingSystemResult);

        movingPoints.forEach(tubeOnly::remove);

        Map<String, Trajectory> resultMap = solvingSystemResult.stream()
                .collect(Collectors.toMap(Trajectory::getLabel, Function.identity()));

        double length = getTubeLength(tubeOnly);
        //double radius = getTubeRadius(tubeOnly);

        Double initRadius = Math.sqrt(
                Math.pow(resultMap.get(xMovingPoint).getPath().get(0), 2) +
                        Math.pow(resultMap.get(yMovingPoint).getPath().get(0), 2)
        );

        AnalyzeResult result = new AnalyzeResult(
                initRadius,
                getFi(xMovingPoint, yMovingPoint, 0, resultMap),
                getTeta(xMovingPoint, yMovingPoint, zMovingPoint, 0, resultMap)
        );

        if (resultMap.get(zMovingPoint).getPath()
                .stream()
                .noneMatch(p -> p > length)) return result;

        List<Double> L = new ArrayList<>();

        double r;
        double S = 0;
        double time = 0;
        double l = 0;

        boolean dx, dy, dz;
        boolean pdx = true, pdy = true, pdz = true;

        for (int i = 0; i < countSteps - 1; i++) {

            if (resultMap.get(zMovingPoint).getPath().get(i) > 0
                    && resultMap.get(zMovingPoint).getPath().get(i) < length) {

                final int fI = i;
                r = Math.sqrt(movingPoints
                        .stream()
                        .mapToDouble(p -> Math.pow(resultMap.get(p).getPath().get(fI + 1) - resultMap.get(p).getPath().get(fI), 2))
                        .sum());

                l += r;

                dx = isDerivativeChangedSign(i, xMovingPoint, resultMap);
                dy = isDerivativeChangedSign(i, yMovingPoint, resultMap);
                dz = isDerivativeChangedSign(i, zMovingPoint, resultMap);

                if ((i > 1 && ((dx && !pdx && !pdy && !pdz)
                        || (dy && !pdx && !pdy && !pdz)
                        || (dz && !pdx && !pdy && !pdz)))
                        || resultMap.get(zMovingPoint).getPath().get(i + 1) > length) {

                    L.add(l);
                    l = 0;
                    //System.out.print("change v ");
                }
                pdx = dx;
                pdy = dy;
                pdz = dz;
                time += CurrentSettings.getInstance().getStepSize();
                S += r;
                //System.out.println(r);
            }

            if (resultMap.get(zMovingPoint).getPath().get(i) > length
                    && resultMap.get(zMovingPoint).getPath().get(i - 1) < length) {

                result.setFinalFi(getFi(xMovingPoint, yMovingPoint, i - 1, resultMap));
                result.setFinalTeta(getTeta(xMovingPoint, yMovingPoint, zMovingPoint, i - 1, resultMap));
            }
        }

        result.setPathLength(S);
        result.setPathLengthToTubeLength(S / length);
        result.setAvgSpeed(S / time);
        result.setAvgFreePath(L.stream().collect(Collectors.averagingDouble(l1 -> l1)));
        result.setDiffusionCoeff(S / time * result.getAvgFreePath() / 6);

        return result;
    }

    /**
     * <p>Служебный метод, определяющий, изменила ли частица направление движения по определенной координате.</p>
     *
     * @param index               номер временной метки
     * @param label               метка рассматриваемой частицы
     * @param solvingSystemResult карта меток всех частиц системы и траекторий их движения
     * @return <code>true</code>, если частица изменила направление движения, иначе <code>false</code>
     * @see Trajectory
     */
    private static boolean isDerivativeChangedSign(Integer index,
                                                   String label,
                                                   Map<String, Trajectory> solvingSystemResult) {

        double difference1 = solvingSystemResult.get(label).getPath().get(index + 1) - solvingSystemResult.get(label).getPath().get(index);
        double difference2 = solvingSystemResult.get(label).getPath().get(index) - solvingSystemResult.get(label).getPath().get(index - 1);

        return Math.signum(difference1) != Math.signum(difference2);
    }

    /**
     * <p>Служебный метод для получения азимутального угла вектора скорости.</p>
     *
     * @param xLabel              метка рассматриваемой частицы по координате X
     * @param yLabel              метка рассматриваемой частицы по координате Y
     * @param index               номер временной метки
     * @param solvingSystemResult карта меток всех частиц системы и траекторий их движения
     * @return значение азимутального угла в градусах.
     * @see Trajectory
     */
    private static double getFi(String xLabel, String yLabel,
                                int index, Map<String, Trajectory> solvingSystemResult) {

        return Math.toDegrees(Math.atan((
                solvingSystemResult.get(yLabel).getPath().get(index + 1) - solvingSystemResult.get(yLabel).getPath().get(index))
                / (solvingSystemResult.get(xLabel).getPath().get(index + 1) - solvingSystemResult.get(xLabel).getPath().get(index))
        ));
    }

    /**
     * <p>Служебный метод для получения зенитного угла вектора скорости.</p>
     *
     * @param xLabel              метка рассматриваемой частицы по координате X
     * @param yLabel              метка рассматриваемой частицы по координате Y
     * @param zLabel              метка рассматриваемой частицы по координате Z
     * @param index               номер временной метки
     * @param solvingSystemResult карта меток всех частиц системы и траекторий их движения
     * @return значение зенитного угла в градусах.
     * @see Trajectory
     */
    private static double getTeta(String xLabel, String yLabel, String zLabel,
                                  int index, Map<String, Trajectory> solvingSystemResult) {

        Double radius = Math.sqrt(
                Math.pow(solvingSystemResult.get(xLabel).getPath().get(index + 1) - solvingSystemResult.get(xLabel).getPath().get(index), 2) +
                        Math.pow(solvingSystemResult.get(yLabel).getPath().get(index + 1) - solvingSystemResult.get(yLabel).getPath().get(index), 2) +
                        Math.pow(solvingSystemResult.get(zLabel).getPath().get(index + 1) - solvingSystemResult.get(zLabel).getPath().get(index), 2)
        );

        return Math.toDegrees(Math.acos((solvingSystemResult.get(zLabel).getPath().get(index + 1) - solvingSystemResult.get(zLabel).getPath().get(index)) / radius));
    }

    /**
     * <p>Расчет радиуса нанотрубки на основе индексов хиральности.</p>
     *
     * @param m первый индекс хиральности
     * @param n второй индекс хиральности
     * @return значение радиуса нанотрубки.
     */
    protected static Double getTubeRadius(Integer m, Integer n) {
        return 1. / 2 * Math.sqrt(3) / Math.PI * 1.42 * Math.sqrt(Math.pow(m, 2) + Math.pow(n, 2) + m * n);
    }

    /**
     * <p>Расчет радиуса нанотрубки на основе координат атомов, которые её образуют.</p>
     *
     * @param tubeOnly список траекторий движения атомов, образующих нанотрубку
     * @return значение радиуса нанотрубки.
     * @see Trajectory
     */
    private static Double getTubeRadius(List<Trajectory> tubeOnly) {
        double[] xPoints = tubeOnly.stream()
                .filter(entry -> entry.getLabel().startsWith("x"))
                .mapToDouble(entry -> entry.getPath().get(0))
                .toArray();

        double xR = Math.abs(DoubleStream.of(xPoints).max().getAsDouble() - DoubleStream.of(xPoints).min().getAsDouble());

        double[] zPoints = tubeOnly.stream()
                .filter(entry -> entry.getLabel().startsWith("z"))
                .mapToDouble(entry -> entry.getPath().get(0))
                .toArray();

        double zR = Math.abs(DoubleStream.of(zPoints).max().getAsDouble() - DoubleStream.of(zPoints).min().getAsDouble());

        return Math.max(xR, zR) / 2;
    }

    /**
     * <p>Расчет длины нанотрубки на основе координат атомов, которые её образуют.</p>
     *
     * @param tubeOnly список траекторий движения атомов, образующих нанотрубку
     * @return значение длины нанотрубки.
     * @see Trajectory
     */
    private static Double getTubeLength(List<Trajectory> tubeOnly) {

        double[] yPoints = tubeOnly.stream()
                .filter(t -> t.getLabel().startsWith("z"))
                .mapToDouble(t -> t.getPath().get(0))
                .toArray();

        return Math.abs(DoubleStream.of(yPoints).max().getAsDouble() - DoubleStream.of(yPoints).min().getAsDouble());
    }

    /**
     * <p>Получение списка меток частиц, которые в процессе моделирования меняли свое местоположение.</p>
     *
     * @param solvingSystemResult карта меток всех частиц системы и траекторий их движения
     * @return список меток движущихся частиц.
     * @see Trajectory
     */
    private static List<String> getMovingPoints(List<Trajectory> solvingSystemResult) {
        List<String> movingPoints = new ArrayList<>();
        movingPoints.addAll(solvingSystemResult.stream()
                .filter(point -> point.getPath() != null && point.getPath().size() > 1
                        && Math.abs(point.getPath().get(0) - point.getPath().get(1)) > 0)
                .map(Trajectory::getLabel)
                .collect(Collectors.toList()));
        return movingPoints;
    }

    /**
     * <p>Преобразование сферических координат в декартовы.</p>
     *
     * @param spherical сферические координаты
     * @return декартовы координаты.
     */
    public static Double[] toCartesian(Double[] spherical) {
        return new Double[]{
                spherical[0] * Math.sin(Math.toRadians(spherical[2])) * Math.cos(Math.toRadians(spherical[1])),
                spherical[0] * Math.sin(Math.toRadians(spherical[2])) * Math.sin(Math.toRadians(spherical[1])),
                spherical[0] * Math.cos(Math.toRadians(spherical[2]))
        };
    }

    /**
     * <p>Преобразование декартовых координат в сферические.</p>
     *
     * @param cartesian декартовы координаты
     * @return сферические координаты.
     */
    public static Double[] toSpherical(Double[] cartesian) {
        Double r = Math.sqrt(Math.pow(cartesian[0], 2) + Math.pow(cartesian[1], 2) + Math.pow(cartesian[2], 2));
        if (r.equals(0.0)) return new Double[]{0., 0., 0.};
        return new Double[]{
                r,
                cartesian[0].equals(0.0) ? 0.0 : Math.toDegrees(Math.atan(cartesian[1] / cartesian[0])),
                Math.toDegrees(Math.acos(cartesian[2] / r))
        };
    }

}
