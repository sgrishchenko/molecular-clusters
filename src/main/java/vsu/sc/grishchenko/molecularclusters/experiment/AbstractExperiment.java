package vsu.sc.grishchenko.molecularclusters.experiment;

import com.google.gson.Gson;
import javafx.concurrent.Task;
import vsu.sc.grishchenko.molecularclusters.math.MotionEquationData;
import vsu.sc.grishchenko.molecularclusters.math.Trajectory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractExperiment extends Task<List<List<AnalyzeResult>>> {
    private String filePath;
    protected List<MotionEquationData> dataList;
    private Function<List<MotionEquationData>, List<Trajectory>> source;
    protected int experimentLength;

    protected Gson gson = new Gson();

    public AbstractExperiment(String filePath,
                              List<MotionEquationData> dataList,
                              Function<List<MotionEquationData>, List<Trajectory>> source) {
        this.filePath = filePath;
        this.dataList = dataList;
        this.source = source;
    }

    protected List<Trajectory> solve(int experimentIndex) {
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

    @Override
    protected void succeeded() {
        super.succeeded();
        updateMessage("Все расчеты эксперимента успешно выполнены.");
    }
}
