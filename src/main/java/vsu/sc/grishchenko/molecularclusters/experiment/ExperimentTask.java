package vsu.sc.grishchenko.molecularclusters.experiment;

import com.google.gson.Gson;
import javafx.concurrent.Task;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import vsu.sc.grishchenko.molecularclusters.database.EntityManager;
import vsu.sc.grishchenko.molecularclusters.entity.ExperimentEntity;
import vsu.sc.grishchenko.molecularclusters.entity.IterationEntity;
import vsu.sc.grishchenko.molecularclusters.entity.TrajectoryListEntity;
import vsu.sc.grishchenko.molecularclusters.math.MotionEquationData;
import vsu.sc.grishchenko.molecularclusters.math.Trajectory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static vsu.sc.grishchenko.molecularclusters.experiment.Analyzer.getParams;

public class ExperimentTask extends Task<List<AnalyzeResult>> {
    protected List<MotionEquationData> dataList;
    private Function<List<MotionEquationData>, List<Trajectory>> source;
    private ExperimentConfig config;
    private ExperimentEntity experimentEntity;

    protected int experimentIndex = 1;
    protected int experimentLength;

    protected Gson gson = new Gson();

    public ExperimentTask(List<MotionEquationData> dataList,
                          Function<List<MotionEquationData>, List<Trajectory>> source,
                          ExperimentConfig config) {
        this.dataList = dataList;
        this.source = source;
        this.config = config;
    }

    protected List<Trajectory> solve() {
        updateMessage(String.format("Выплняется расчет № %d из %d...",
                experimentIndex, experimentLength));


        List<Trajectory> solveResult = source.apply(dataList);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");
        String experimentName = String.format("exp_%d(%s)",
                experimentIndex,
                new DateTime(experimentEntity.getDate().getTime()).toString(formatter));

        EntityManager.save(new TrajectoryListEntity(experimentEntity,
                experimentName,
                gson.toJson(solveResult),
                Analyzer.getParams(solveResult)));

        if (isCancelled()) {
            updateMessage(String.format("Эксперимент прерван, выполненые расчеты - %d из %d.",
                    experimentIndex, experimentLength));
        }

        return solveResult;
    }

    private void iterationsHandle(MotionEquationData variableEquation,
                                  List<Iteration<MotionEquationData>> iterations,
                                  List<AnalyzeResult> results) {
        if (iterations.isEmpty()) return;
        boolean lastIteration = iterations.size() == 1;

        Iteration<MotionEquationData> iteration = iterations.get(0);
        iteration.restart();
        do {
            iteration.handle(variableEquation);

            if (lastIteration) {
                results.add(getParams(solve()));
                experimentIndex++;
            } else {
                iterationsHandle(variableEquation, iterations.subList(1, iterations.size()), results);
            }
        } while (iteration.iterate());
    }

    @Override
    protected List<AnalyzeResult> call() throws Exception {
        experimentEntity = new ExperimentEntity(config);
        EntityManager.save(experimentEntity);

        config.getIterations()
                .stream()
                .map(iteration -> new IterationEntity(experimentEntity, iteration))
                .forEach(EntityManager::save);

        MotionEquationData variableEquation = dataList
                .stream()
                .filter(data -> data.getLabel().trim().equalsIgnoreCase(config.getMovingPointLabel().trim()))
                .findFirst().get();


        List<AnalyzeResult> results = new ArrayList<>();

        variableEquation.setInitialPosition(config.getInitialPosition());
        variableEquation.setInitialVelocity(config.getInitialVelocity());

        experimentLength = config.getIterations().stream().mapToInt(Iteration::getStepCount).sum();

        iterationsHandle(variableEquation, config.getIterations(), results);

        return results;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        updateMessage("Все расчеты эксперимента успешно выполнены.");
    }
}
