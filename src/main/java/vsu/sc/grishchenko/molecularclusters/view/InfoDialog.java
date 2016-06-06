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

/**
 * <p>Класс, который позволяет запускать из любого места в приложении
 * простые диалоговые окна с информацией.</p>
 *
 * @author Грищенко Сергей
 * @see Application
 */
public class InfoDialog extends Application {
    private String text;


    /**
     * @param text текст сообщения, который будет отображен в диалоговом окне.
     */
    public InfoDialog(String text) {
        this.text = text;
    }

    @Override
    public void start(Stage stage) {
        stage.initModality(Modality.WINDOW_MODAL);
        VBox vBox = new VBox();
        vBox.getChildren().add(new Text(text));
        Button button = new Button("OK");
        button.setDefaultButton(true);
        button.setOnAction(e -> stage.close());
        vBox.getChildren().add(button);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(5));
        vBox.setSpacing(5);

        stage.setResizable(false);
        stage.setScene(new Scene(vBox));
        stage.show();
    }
}
