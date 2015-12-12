package vsu.sc.grishchenko.molecularclusters.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InfoDialog extends Application {
    private String text;

    public InfoDialog(String text) {
        this.text = text;
    }

    @Override
    public void start(Stage stage) {
        stage.initModality(Modality.WINDOW_MODAL);
        VBox vBox = new VBox();
        vBox.getChildren().add(new Text(text));
        Button button = new Button("Ok.");
        button.setOnAction(e -> stage.close());
        vBox.getChildren().add(button);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(5));

        stage.setScene(new Scene(vBox));
        stage.show();
    }
}
