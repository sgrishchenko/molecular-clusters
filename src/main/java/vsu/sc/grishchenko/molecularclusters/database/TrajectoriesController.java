package vsu.sc.grishchenko.molecularclusters.database;

import com.google.gson.Gson;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import vsu.sc.grishchenko.molecularclusters.entity.TrajectoryListEntity;
import vsu.sc.grishchenko.molecularclusters.experiment.AnalyzeResult;
import vsu.sc.grishchenko.molecularclusters.experiment.Analyzer;
import vsu.sc.grishchenko.molecularclusters.math.Trajectory;
import vsu.sc.grishchenko.molecularclusters.view.InfoDialog;
import vsu.sc.grishchenko.molecularclusters.view.View3D;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * <p>Класс-контроллер для обработки событий, происходящих в диалоговом окне просмотра списка
 * траекторий движения частиц разлихних моделей, сохраненных в базе данных.</p>
 *
 * @author Грищенко Сергей
 * @see Trajectory
 * @see Initializable
 */
public class TrajectoriesController implements Initializable {
    /**
     * <p>Объект для сериализации/десериализции Java-объктов
     * в фомате <a href="https://ru.wikipedia.org/wiki/JSON">JSON</a>.</p>
     *
     * @see <a href="http://static.javadoc.io/com.google.code.gson/gson/2.6.2/com/google/gson/Gson.html">Gson</a>
     */
    public Gson gson = new Gson();
    /**
     * <p>Таблица для отображения списка сохраненных траекторий движения частиц.</p>
     *
     * @see GridPane
     */
    public GridPane table;

    /**
     * <p>Заполнение из базы данных списка сохраненных траекторий движениея частиц
     * сразу поле отрытия диалогового окна.</p>
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<TrajectoryListEntity> trajectories = EntityManager.findAll(TrajectoryListEntity.class);

        HBox buttons;
        Button button;
        Label name;
        RowConstraints constraints;
        for (int i = 0; i < trajectories.size(); i++) {
            TrajectoryListEntity trajectory = trajectories.get(i);

            name = new Label(trajectory.getName());
            GridPane.setHalignment(name, HPos.CENTER);

            table.add(name, 0, i + 1);

            buttons = new HBox();
            buttons.setSpacing(20);
            buttons.setAlignment(Pos.CENTER);

            button = new Button("Обновить");
            buttons.getChildren().add(button);
            button.setOnAction(event -> {
                List<Trajectory> trajectoryList = Arrays.asList(gson.fromJson(trajectory.getJson(), Trajectory[].class));
                AnalyzeResult result = Analyzer.getParams(trajectoryList);
                trajectory.fillParams(result);

                EntityManager.update(trajectory);
            });

            button = new Button("Информация");
            buttons.getChildren().add(button);
            button.setOnAction(event -> new InfoDialog(String.format(
                    "Расстояние до оси трубки в начальный момент: %.3f Å\n" +
                            "Начальный зенитный угол: %d°\n" +
                            "Начальный азимутальный угол: %d°\n" +
                            "Длина трубки: %.3f Å\n" +
                            "\n" +
                            "Длина пройденного пути: %.3f Å\n" +
                            "Длина пройденного пути / длина трубки: %.3f\n" +
                            "\n" +
                            "Средняя скорость: %.3f Å/τ₀\n" +
                            "Средняя длина пробега: %.3f Å\n" +
                            "\n" +
                            "Коэффициент диффузии: %.3f Å²/τ₀",
                    trajectory.getRadius(),
                    trajectory.getTeta() == null ? 0 : Math.round(trajectory.getTeta()),
                    trajectory.getFi() == null ? 0 : Math.round(trajectory.getFi()),
                    trajectory.getPathLength() / trajectory.getPathLengthToTubeLength(),
                    trajectory.getPathLength(),
                    trajectory.getPathLengthToTubeLength(),
                    trajectory.getAvgSpeed(),
                    trajectory.getAvgFreePath(),
                    trajectory.getDiffusionCoeff()
            )).start(new Stage()));

            button = new Button("Запустить");
            buttons.getChildren().add(button);
            button.setOnAction(event -> {
                View3D view3D = new View3D(Arrays.asList(gson.fromJson(trajectory.getJson(), Trajectory[].class)));
                view3D.start(new Stage());
            });

            table.add(buttons, 1, i + 1);

            constraints = new RowConstraints();
            constraints.setMinHeight(40);
            table.getRowConstraints().add(constraints);
        }
    }
}
