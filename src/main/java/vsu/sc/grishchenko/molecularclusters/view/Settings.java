package vsu.sc.grishchenko.molecularclusters.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vsu.sc.grishchenko.molecularclusters.util.ResourceFactory;

public class Settings extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(ResourceFactory.getResource(Main.class, "settings.fxml"));
        stage.setTitle("Настройки");
        stage.setScene(new Scene(root));
        stage.show();
    }
}