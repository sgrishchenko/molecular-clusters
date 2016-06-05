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

/**
 * <p>Класс, описывающий фоновый процесс выполнения задания на рассчет серии экспериментов.
 * Ожидается? что после завершения процесса расчета
 * будет получен список результатов {@link AnalyzeResult}.</p>
 *
 * @author Грищенко Сергей
 * @see AnalyzeResult
 * @see Task
 */
public class ExperimentTask extends Task<List<AnalyzeResult>> {
    /**
     * <p>Список объектов, описывающих уравнения движения частиц моделируемой системы.</p>
     *
     * @see MotionEquationData
     */
    private List<MotionEquationData> dataList;
    /**
     * <p>Некоторая функция, которая преобразует уравнения движения частиц моделируемой системы
     * в список траекторий движения этих частиц.</p>
     *
     * @see MotionEquationData
     * @see Trajectory
     * @see Function
     */
    private Function<List<MotionEquationData>, List<Trajectory>> source;
    /**
     * <p>Объект, описывающий конфигурацию задания на выполнение серии экспериментов.</p>
     *
     * @see ExperimentConfig
     */
    private ExperimentConfig config;
    /**
     * <p>Объект, описывающий сущность базы данных, связанную с проводимым заданием.</p>
     */
    private ExperimentEntity experimentEntity;
    /**
     * <p>Номер текущего эксперимента в задании.</p>
     */
    private int experimentIndex = 1;
    /**
     * <p>Общее число экспериментов.</p>
     */
    private int experimentLength;
    /**
     * <p>Объект для сериализации/десериализции Java-объктов
     * в фомате <a href="https://ru.wikipedia.org/wiki/JSON">JSON</a>.</p>
     *
     * @see <a href="http://static.javadoc.io/com.google.code.gson/gson/2.6.2/com/google/gson/Gson.html">Gson</a>
     */
    private Gson gson = new Gson();

    public ExperimentTask(List<MotionEquationData> dataList,
                          Function<List<MotionEquationData>, List<Trajectory>> source,
                          ExperimentConfig config) {
        this.dataList = dataList;
        this.source = source;
        this.config = config;
    }

    /**
     * <p>Метод, отвечающий за получение из уравнений движения частиц моделируемой системы
     * для текущего состояния объекта {@link ExperimentTask#dataList}
     * списока траекторий движения этих частиц, расчет макроскопических параметров
     * для этих траекторий движения и сохранения результатов в базу данных.</p>
     *
     * @return список траекторий движения частиц модели.
     * @see Trajectory
     */
    private List<Trajectory> solve() {
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

    /**
     * <p>В рамках данного метода рекурсивно обрабатываются все итерации,
     * описанные в объекте {@link ExperimentTask#config}.</p>
     *
     * @param variableEquation объект, описываещий уравнение движения варьируемой частицы
     * @param iterations       список итераций
     * @param results          список с объектами макроскописеских параметров,
     *                         полученными в результате расчетов. После каждого вызова даного метода
     *                         в этот список записывается новый объект с рассчитанными параметрами
     * @see MotionEquationData
     * @see Iteration
     * @see AnalyzeResult
     */
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
                try {
                    results.add(getParams(solve()));
                    experimentIndex++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                iterationsHandle(variableEquation, iterations.subList(1, iterations.size()), results);
            }
        } while (iteration.iterate());
    }

    /**
     * <p>Реализация абстрастного метода {@link Task#call()}.
     * Здесь в базу данный сохраняется новая сущность {@link ExperimentEntity},
     * сохраняются все сущнести {@link IterationEntity}, связанные с ней
     * и инициируется цикл по всем итерацииям задания.</p>
     *
     * @return список с объектами макроскописеских параметров, полученными в результате расчетов.
     * @see Task
     * @throws Exception исключения, которые могут возникнуть в процессе выпонения задания.
     */
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

        experimentLength = config.getIterations()
                .stream()
                .mapToInt(Iteration::getStepCount)
                .reduce((i1, i2) -> i1 * i2).getAsInt();

        iterationsHandle(variableEquation, config.getIterations(), results);

        return results;
    }

    /**
     * <p>Описание событий, которые должны быть выполнены
     * при успешном завершении всех рассчетов задания.</p>
     */
    @Override
    protected void succeeded() {
        super.succeeded();
        updateMessage("Все расчеты эксперимента успешно выполнены.");
    }
}
