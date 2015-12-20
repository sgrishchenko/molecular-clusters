package vsu.sc.grishchenko.molecularclusters.view;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import vsu.sc.grishchenko.molecularclusters.GlobalSettings;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    public TextField viewCountSteps;
    public TextField viewStepSize;
    public Slider animateStepSize;

    public TextField experimentCountSteps;
    public TextField experimentStepSize;
    public TextField movingPointLabel;

    public TextField chiralityFirstIndex;
    public TextField chiralitySecondIndex;

    public TextField initialPositionX;
    public TextField initialPositionY;
    public TextField initialPositionZ;

    public TextField initialVelocity;
    public AnchorPane root;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GlobalSettings.ViewSettings viewSettings = GlobalSettings.getInstance().viewSettings;

        viewCountSteps.setText(viewSettings.getCountSteps().toString());
        viewStepSize.setText(viewSettings.getStepSize().toString());

        Double timeStep = (30 - viewSettings.getAnimateStepSize()) / 30. * 100;
        animateStepSize.setValue(timeStep);

        GlobalSettings.ExperimentSettings experimentSettings = GlobalSettings.getInstance().experimentSettings;

        experimentCountSteps.setText(experimentSettings.getCountSteps().toString());
        experimentStepSize.setText(experimentSettings.getStepSize().toString());
        movingPointLabel.setText(experimentSettings.getMovingPointLabel());

        chiralityFirstIndex.setText(experimentSettings.getChirality()[0].toString());
        chiralitySecondIndex.setText(experimentSettings.getChirality()[1].toString());

        initialPositionX.setText(experimentSettings.getInitialPosition()[0].toString());
        initialPositionY.setText(experimentSettings.getInitialPosition()[1].toString());
        initialPositionZ.setText(experimentSettings.getInitialPosition()[2].toString());

        initialVelocity.setText(experimentSettings.getInitialVelocity().toString());
    }

    public void setSettings(ActionEvent actionEvent) {
        GlobalSettings.ViewSettings viewSettings = GlobalSettings.getInstance().viewSettings;

        viewSettings.setCountSteps(Integer.parseInt(viewCountSteps.getText()));
        viewSettings.setStepSize(Double.parseDouble(viewStepSize.getText()));

        Integer timeStep = 30 - new Long(Math.round(animateStepSize.getValue() / 100 * 30)).intValue();
        viewSettings.setAnimateStepSize(timeStep);

        GlobalSettings.ExperimentSettings experimentSettings = GlobalSettings.getInstance().experimentSettings;

        experimentSettings.setCountSteps(Integer.parseInt(experimentCountSteps.getText()));
        experimentSettings.setStepSize(Double.parseDouble(experimentStepSize.getText()));
        experimentSettings.setMovingPointLabel(movingPointLabel.getText());

        experimentSettings.setChirality(new Integer[]{
                Integer.parseInt(chiralityFirstIndex.getText()),
                Integer.parseInt(chiralitySecondIndex.getText())
        });

        experimentSettings.setInitialPosition(new Double[]{
                Double.parseDouble(initialPositionX.getText()),
                Double.parseDouble(initialPositionY.getText()),
                Double.parseDouble(initialPositionZ.getText())
        });

        experimentSettings.setInitialVelocity(Double.parseDouble(initialVelocity.getText()));

        ((Stage) root.getScene().getWindow()).close();
    }

    public void cancelSettings(ActionEvent actionEvent) {
        ((Stage) root.getScene().getWindow()).close();
    }
}
