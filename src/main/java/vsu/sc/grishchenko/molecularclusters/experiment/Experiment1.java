package vsu.sc.grishchenko.molecularclusters.experiment;

import vsu.sc.grishchenko.molecularclusters.GlobalSettings;
import vsu.sc.grishchenko.molecularclusters.math.MotionEquationData;
import vsu.sc.grishchenko.molecularclusters.math.Trajectory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static vsu.sc.grishchenko.molecularclusters.experiment.Analyzer.getParams;
import static vsu.sc.grishchenko.molecularclusters.experiment.Analyzer.getTubeRadius;

public class Experiment1 extends AbstractExperiment {
    public Experiment1(String filePath,
                       List<MotionEquationData> dataList,
                       Function<List<MotionEquationData>, List<Trajectory>> source) {
        super(filePath, dataList, source);
    }

    @Override
    protected List<List<AnalyzeResult>> call() throws Exception {
        GlobalSettings.ExperimentSettings settings = GlobalSettings.getInstance().experimentSettings;

        List<List<AnalyzeResult>> results = new ArrayList<>();

        double radius = getTubeRadius(settings.getChirality()[0], settings.getChirality()[1]);
        double v = settings.getInitialVelocity();

        double fi;
        double r;
        int experimentStageCount = 10;
        int experimentStageLength = 10;
        experimentLength = experimentStageLength * experimentStageCount;

        for (int i = 0; i < experimentStageCount; i++) {
            results.add(new ArrayList<>());
            for (int j = 0; j < experimentStageLength; j++) {
                if (dataList != null) {
                    MotionEquationData lastData = dataList.get(dataList.size() - 1);

                    r = radius * i / 10;
                    fi = Math.toRadians(90 * j / 10);

                    lastData.setInitialPosition(new Double[]{
                            settings.getInitialPosition()[0] - r,
                            settings.getInitialPosition()[1],
                            settings.getInitialPosition()[2]
                    });
                    lastData.setInitialVelocity(new Double[]{Math.sin(fi) * v, Math.cos(fi) * v, 0.});
                }

                List<Trajectory> solveResult = solve(i * experimentStageLength + j + 1);
                results.get(i).add(getParams(solveResult));
                if (isCancelled()) return results;
            }
        }

        return results;
    }
}
