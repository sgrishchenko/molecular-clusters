package vsu.sc.grishchenko.molecularclusters.settings;

import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    public AnchorPane root;
    public TextField viewCountSteps;
    public TextField viewStepSize;
    public Slider animateStepSize;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CurrentSettings currentSettings = CurrentSettings.getInstance();

        viewCountSteps.setText(currentSettings.getCountSteps().toString());
        viewStepSize.setText(currentSettings.getStepSize().toString());

        Double timeStep = (30 - currentSettings.getAnimateStepSize()) / 30. * 100;
        animateStepSize.setValue(timeStep);
    }

    public void setSettings() {
        CurrentSettings currentSettings = CurrentSettings.getInstance();

        currentSettings.setCountSteps(Integer.parseInt(viewCountSteps.getText()));
        currentSettings.setStepSize(Double.parseDouble(viewStepSize.getText()));

        Integer timeStep = 30 - new Long(Math.round(animateStepSize.getValue() / 100 * 30)).intValue();
        currentSettings.setAnimateStepSize(timeStep);

        cancelSettings();
    }

    public void cancelSettings() {
        ((Stage) root.getScene().getWindow()).close();
    }
}
