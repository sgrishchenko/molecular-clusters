package vsu.sc.grishchenko.molecularclusters.database;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vsu.sc.grishchenko.molecularclusters.util.ResourceFactory;

public class Trajectories extends Application{
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(ResourceFactory.getResource(this, "trajectories.fxml"));
        stage.setTitle("Список сохраненных расчетов");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
