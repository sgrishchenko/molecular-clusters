package vsu.sc.grishchenko.molecularclusters.experiment;

import vsu.sc.grishchenko.molecularclusters.GlobalSettings;
import vsu.sc.grishchenko.molecularclusters.math.MotionEquationData;
import vsu.sc.grishchenko.molecularclusters.math.Trajectory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static vsu.sc.grishchenko.molecularclusters.experiment.Analyzer.getParams;

public class Experiment2 extends AbstractExperiment {
    private List<Trajectory> solvingSystemResult;

    public Experiment2(String filePath,
                       List<MotionEquationData> dataList,
                       Function<List<MotionEquationData>, List<Trajectory>> source,
                       List<Trajectory> solvingSystemResult) {
        super(filePath, dataList, source);
        this.solvingSystemResult = solvingSystemResult;
    }

    @Override
    protected List<List<AnalyzeResult>> call() throws Exception {
        GlobalSettings.ExperimentSettings settings = GlobalSettings.getInstance().experimentSettings;

        List<List<AnalyzeResult>> results = new ArrayList<>();

        AnalyzeResult params = getParams(solvingSystemResult);
        double v = settings.getInitialVelocity();
        double r = params.getRadius();
        double fi = Math.toRadians(params.getFi());
        double teta;

        experimentLength = 20;
        results.add(new ArrayList<>());
        for (int j = 0; j < experimentLength; j++) {
            if (dataList != null) {
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
            }

            List<Trajectory> solveResult = solve(j + 1);
            results.get(0).add(getParams(solveResult));
            if (isCancelled()) return results;
        }

        return results;
    }
}
