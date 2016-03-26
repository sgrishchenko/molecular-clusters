package vsu.sc.grishchenko.molecularclusters.experiment;

import com.google.gson.Gson;
import javafx.concurrent.Task;
import vsu.sc.grishchenko.molecularclusters.math.MotionEquationData;
import vsu.sc.grishchenko.molecularclusters.math.Trajectory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static vsu.sc.grishchenko.molecularclusters.experiment.Analyzer.getParams;

public class ExperimentTask extends Task<List<AnalyzeResult>> {
    private String filePath;
    protected List<MotionEquationData> dataList;
    private Function<List<MotionEquationData>, List<Trajectory>> source;
    private ExperimentConfig config;

    protected int experimentIndex = 1;
    protected int experimentLength;

    protected Gson gson = new Gson();

    public ExperimentTask(String filePath,
                          List<MotionEquationData> dataList,
                          Function<List<MotionEquationData>, List<Trajectory>> source,
                          ExperimentConfig config) {
        this.filePath = filePath;
        this.dataList = dataList;
        this.source = source;
        this.config = config;
    }

    protected List<Trajectory> solve() {
        updateMessage(String.format("Выплняется расчет № %d...", experimentIndex));


        List<Trajectory> solveResult = source.apply(dataList);

        if (filePath != null) {
            File out = new File(filePath + "exp_" + experimentIndex + ".txt");
            try (FileWriter writer = new FileWriter(out)) {
                writer.write(gson.toJson(solveResult));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
        MotionEquationData variableEquation = dataList
                .stream()
                .filter(data -> data.getLabel().trim().equalsIgnoreCase(config.getMovingPointLabel().trim()))
                .findFirst().get();


        List<AnalyzeResult> results = new ArrayList<>();

        variableEquation.setInitialPosition(config.getInitialPosition());
        variableEquation.setInitialVelocity(config.getInitialVelocity());

        //TODO: делегировать расчет iteration, сейчас не правильно считается
        experimentLength = config.getIterations()
                .stream()
                .mapToInt(i -> new Double(Math.floor((i.getTo() - i.getFrom()) / i.getStep())).intValue() + 1)
                .sum();

        iterationsHandle(variableEquation, config.getIterations(), results);

        return results;
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        updateMessage("Все расчеты эксперимента успешно выполнены.");
    }
}
