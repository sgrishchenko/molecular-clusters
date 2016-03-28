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
import vsu.sc.grishchenko.molecularclusters.math.Trajectory;
import vsu.sc.grishchenko.molecularclusters.view.View3D;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class TrajectoriesController implements Initializable {
    public Gson gson = new Gson();

    public GridPane table;

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

            button = new Button("Информация");
            buttons.getChildren().add(button);

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
