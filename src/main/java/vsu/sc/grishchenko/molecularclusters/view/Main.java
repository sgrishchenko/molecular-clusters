package vsu.sc.grishchenko.molecularclusters.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vsu.sc.grishchenko.molecularclusters.database.EntityManager;
import vsu.sc.grishchenko.molecularclusters.util.ResourceFactory;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(ResourceFactory.getResource(this, "form.fxml"));
        primaryStage.setTitle("Молекулярные кластеры");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        EntityManager.initialise();
        primaryStage.setOnCloseRequest(event -> EntityManager.close());
    }


    public static void main(String[] args) {
        launch(args);
    }
}
