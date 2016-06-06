package vsu.sc.grishchenko.molecularclusters.experiment;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vsu.sc.grishchenko.molecularclusters.util.ResourceFactory;

import java.util.function.Consumer;

/**
 * <p>Класс, который позволяет запускать диалоговое окно для конфигурации задания
 * на выполнение серии экспериментов.</p>
 *
 * @author Грищенко Сергей
 * @see Application
 */
public class Experiment extends Application {
    /**
     * <p>Действие, которое необходимо выполнить после успешного указания
     * параметров конфигурации запуска.</p>
     *
     * @see ExperimentConfig
     * @see Consumer
     */
    private Consumer<ExperimentConfig> onStart;

    public Experiment(Consumer<ExperimentConfig> onStart) {
        this.onStart = onStart;
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(ResourceFactory.getResource(this, "experiment.fxml"));
        Parent root = loader.load();
        ExperimentController controller = loader.getController();
        controller.setOnStart(onStart);
        stage.setTitle("Конфигурация эксперимента");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
