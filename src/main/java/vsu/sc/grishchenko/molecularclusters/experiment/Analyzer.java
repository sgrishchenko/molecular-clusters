package vsu.sc.grishchenko.molecularclusters.experiment;

import com.google.gson.Gson;
import vsu.sc.grishchenko.molecularclusters.GlobalSettings;
import vsu.sc.grishchenko.molecularclusters.math.MotionEquationData;
import vsu.sc.grishchenko.molecularclusters.math.Solver;
import vsu.sc.grishchenko.molecularclusters.math.Trajectory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public final class Analyzer {
    public static Gson gson = new Gson();

    public static String createExperimentDirectory(String filePath) {
        if (Files.notExists(Paths.get(filePath))) {
            new File(filePath).mkdirs();
            return filePath;
        }

        int i = 1;
        String oldFilePath = filePath;
        do {
            filePath = oldFilePath.replaceAll("/$", "(" + i + ")/");
            i++;
        } while (Files.exists(Paths.get(filePath)));
        new File(filePath).mkdirs();
        return filePath;
    }

    public static List<List<AnalyzeResult>> experiment1(String filePath, List<MotionEquationData> dataList) {
        GlobalSettings.ExperimentSettings settings = GlobalSettings.getInstance().experimentSettings;

        List<List<AnalyzeResult>> results = new ArrayList<>();

        double radius = getTubeRadius(settings.getChirality()[0], settings.getChirality()[1]);
        double v = settings.getInitialVelocity();

        double fi;
        double r;
        for (int i = 0; i < 10; i++) {
            results.add(new ArrayList<>());
            for (int j = 0; j < 10; j++) {
                MotionEquationData lastData = dataList.get(dataList.size() - 1);

                r = radius * i / 10;
                fi = Math.toRadians(90 * j / 10);

                lastData.setInitialPosition(new Double[]{
                        settings.getInitialPosition()[0] - r,
                        settings.getInitialPosition()[1],
                        settings.getInitialPosition()[2]
                });
                lastData.setInitialVelocity(new Double[]{Math.sin(fi) * v, Math.cos(fi) * v, 0.});

                List<Trajectory> solveResult = Solver.solveVerlet(dataList, 0, settings.getCountSteps(), settings.getStepSize());
                File out = new File(filePath + "exp_" + i + "_" + j + ".txt");
                try (FileWriter writer = new FileWriter(out)) {
                    writer.write(gson.toJson(solveResult));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                results.get(i).add(getParams(solveResult));
            }
        }

        return results;
    }

    public static List<List<AnalyzeResult>> experiment2(String filePath,
                                                        List<MotionEquationData> dataList,
                                                        List<Trajectory> solvingSystemResult) {

        GlobalSettings.ExperimentSettings settings = GlobalSettings.getInstance().experimentSettings;

        List<List<AnalyzeResult>> results = new ArrayList<>();

        AnalyzeResult params = getParams(solvingSystemResult);
        double v = 75;
        double r = params.getRadius();
        double fi = Math.toRadians(params.getFi());
        double teta;

        results.add(new ArrayList<>());
        for (int j = 0; j < 20; j++) {
            MotionEquationData lastData = dataList.get(dataList.size() - 1);
            teta = Math.toRadians(90 - 90 * j / 9);

            lastData.setInitialPosition(new Double[]{
                    settings.getInitialPosition()[0] - r * Math.sin(teta),
                    settings.getInitialPosition()[1],
                    settings.getInitialPosition()[2] + r * Math.cos(teta)});
            lastData.setInitialVelocity(new Double[]{
                    Math.sin(fi) * Math.sin(teta) * v,
                    Math.cos(fi) * v,
                    -Math.cos(teta) * v
            });

            List<Trajectory> solveResult = Solver.solveVerlet(dataList, 0, settings.getCountSteps(), settings.getStepSize());
            File out = new File(filePath + "exp_" + j + ".txt");
            try (FileWriter writer = new FileWriter(out)) {
                writer.write(gson.toJson(solveResult));
            } catch (IOException e) {
                e.printStackTrace();
            }

            results.get(0).add(getParams(solveResult));
        }

        return results;
    }

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
        double radius = getTubeRadius(tubeOnly);

        Double initRadius = Math.sqrt(
                Math.pow(resultMap.get(xMovingPoint).getPath().get(0), 2) +
                Math.pow(resultMap.get(zMovingPoint).getPath().get(0), 2)
        );

        AnalyzeResult result = new AnalyzeResult(
                initRadius,
                getAngleWithTubeAxis(xMovingPoint, yMovingPoint, zMovingPoint, 0, resultMap),
                Math.toDegrees(Math.acos(resultMap.get(zMovingPoint).getPath().get(0) / initRadius))
        );

        if (resultMap.get(yMovingPoint).getPath()
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

            if (resultMap.get(yMovingPoint).getPath().get(i) > 0
                    && resultMap.get(yMovingPoint).getPath().get(i) < length) {

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
                        || resultMap.get(yMovingPoint).getPath().get(i + 1) > length) {

                    L.add(l);
                    l = 0;
                    System.out.print("change v ");
                }
                pdx = dx;
                pdy = dy;
                pdz = dz;
                time += GlobalSettings.getInstance().experimentSettings.getStepSize();
                S += r;
                System.out.println(r);
            }

            if (resultMap.get(yMovingPoint).getPath().get(i) > length
                    && resultMap.get(yMovingPoint).getPath().get(i - 1) < length) {

                result.setFinalFi(getAngleWithTubeAxis(xMovingPoint, yMovingPoint, zMovingPoint, i - 1, resultMap));
            }
        }

        result.setPathLength(S);
        result.setPathLengthToTubeLength(S / length);
        result.setAvgSpeed(S / time);
        result.setAvgFreePath(L.stream().collect(Collectors.averagingDouble(l1 -> l1)));
        result.setDiffusionCoeff(S / time * result.getAvgFreePath() / 6);

        return result;
    }

    private static boolean isDerivativeChangedSign(Integer index,
                                                   String label,
                                                   Map<String, Trajectory> solvingSystemResult) {

        double difference1 = solvingSystemResult.get(label).getPath().get(index + 1) - solvingSystemResult.get(label).getPath().get(index);
        double difference2 = solvingSystemResult.get(label).getPath().get(index) - solvingSystemResult.get(label).getPath().get(index - 1);

        return Math.signum(difference1) != Math.signum(difference2);
    }

    private static double getAngleWithTubeAxis(String xLabel, String yLabel, String zLabel,
                                               int index, Map<String, Trajectory> solvingSystemResult) {

        Double rx = Math.abs(solvingSystemResult.get(xLabel).getPath().get(index + 1) - solvingSystemResult.get(xLabel).getPath().get(index));
        Double ry = Math.abs(solvingSystemResult.get(yLabel).getPath().get(index + 1) - solvingSystemResult.get(yLabel).getPath().get(index));
        Double rz = Math.abs(solvingSystemResult.get(zLabel).getPath().get(index + 1) - solvingSystemResult.get(zLabel).getPath().get(index));

        Double initShift = Math.sqrt(rx * rx + ry * ry + rz * rz);

        return Math.toDegrees(Math.acos(ry / initShift));
    }

    private static Double getTubeRadius(Integer m, Integer n) {
        return 1. / 2 * Math.sqrt(3) / Math.PI * 1.42 * Math.sqrt(Math.pow(m, 2) + Math.pow(n, 2) + m * n);
    }

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

    private static Double getTubeLength(List<Trajectory> tubeOnly) {

        double[] yPoints = tubeOnly.stream()
                .filter(t -> t.getLabel().startsWith("y"))
                .mapToDouble(t -> t.getPath().get(0))
                .toArray();

        return Math.abs(DoubleStream.of(yPoints).max().getAsDouble() - DoubleStream.of(yPoints).min().getAsDouble());
    }

    private static List<String> getMovingPoints(List<Trajectory> solvingSystemResult) {
        List<String> movingPoints = new ArrayList<>();
        movingPoints.addAll(solvingSystemResult.stream()
                .filter(point -> point.getPath() != null && point.getPath().size() > 1
                        && Math.abs(point.getPath().get(0) - point.getPath().get(1)) > 0)
                .map(Trajectory::getLabel)
                .collect(Collectors.toList()));
        return movingPoints;
    }
}
