package vsu.sc.grishchenko.molecularclusters.database;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import vsu.sc.grishchenko.molecularclusters.entity.TrajectoryListEntity;

public class SaveTrajectoriesDialog extends Application {
    private TrajectoryListEntity trajectory;

    public SaveTrajectoriesDialog(TrajectoryListEntity trajectory) {
        this.trajectory = trajectory;
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.initModality(Modality.WINDOW_MODAL);

        VBox vBox = new VBox();
        vBox.setSpacing(5);

        vBox.getChildren().add(new Label("Укажите назание для расчета:"));

        TextField nameField = new TextField();
        vBox.getChildren().add(nameField);

        Label errorMessage = new Label();
        errorMessage.setTextFill(Color.RED);
        vBox.getChildren().add(errorMessage);

        Button button = new Button("Сохранить");
        button.setDefaultButton(true);
        button.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                errorMessage.setText("Название не указано");
                stage.sizeToScene();
                return;
            }
            if (name.length() > 100) {
                errorMessage.setText("Слишком длинное название");
                stage.sizeToScene();
                return;
            }
            if (EntityManager.isExists("name", name, TrajectoryListEntity.class)) {
                errorMessage.setText("Расчет с таким названием\nуже существует");
                stage.sizeToScene();
                return;
            }

            errorMessage.setText("");
            trajectory.setName(name);
            EntityManager.saveOrUpdate(trajectory);
            stage.close();
        });
        vBox.getChildren().add(button);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(5));

        stage.setResizable(false);
        stage.setScene(new Scene(vBox));
        stage.show();
        stage.sizeToScene();
    }
}
