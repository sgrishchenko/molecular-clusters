package vsu.sc.grishchenko.molecularclusters.experiment;

import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import vsu.sc.grishchenko.molecularclusters.math.MotionEquationData;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExperimentController implements Initializable {
    public ChoiceBox template;

    public TextField H1;
    public TextField H2;

    public TextField label;

    public CheckBox changeX; public Label labelXfrom; public TextField Xfrom; public TextField Xto; public TextField Xstep;
    public CheckBox changeY; public Label labelYfrom; public TextField Yfrom; public TextField Yto; public TextField Ystep;
    public CheckBox changeZ; public Label labelZfrom; public TextField Zfrom; public TextField Zto; public TextField Zstep;
    public CheckBox changeV; public Label labelVfrom; public TextField Vfrom; public TextField Vto; public TextField Vstep;
    public CheckBox changeT; public Label labelTfrom; public TextField Tfrom; public TextField Tto; public TextField Tstep;
    public CheckBox changeF; public Label labelFfrom; public TextField Ffrom; public TextField Fto; public TextField Fstep;

    private List<Dimension> positionDimensions = new ArrayList<>();
    private List<Dimension> velocityDimensions = new ArrayList<>();

    private List<Dimension> allDimensions = new ArrayList<>();

    private Consumer<ExperimentConfig> onStart;

    public void setOnStart(Consumer<ExperimentConfig> onStart) {
        this.onStart = onStart;
    }

    private BiConsumer<MotionEquationData, Double> getAction(Function<MotionEquationData, Double[]> getter, int index) {
        return (data, value) -> getter.apply(data)[index] = value;
    }

    private BiConsumer<MotionEquationData, Double> getVAction(Function<MotionEquationData, Double[]> getter, int index) {
        return (data, value) -> {
            Double[] spherical = toSpherical(getter.apply(data));
            spherical[index] = value;
            Double[] cartesian = toCartesian(spherical);
            for (int i = 0; i < 3; i++) {
                getter.apply(data)[i] = cartesian[i];
            }
        };
    }

    private Double[] toCartesian(Double[] spherical) {
        return new Double[] {
                spherical[0] * Math.sin(Math.toRadians(spherical[2])) * Math.cos(Math.toRadians(spherical[1])),
                spherical[0] * Math.sin(Math.toRadians(spherical[2])) * Math.sin(Math.toRadians(spherical[1])),
                spherical[0] * Math.cos(Math.toRadians(spherical[2]))
        };
    }

    private Double[] toSpherical(Double[] cartesian) {
        Double r = Math.sqrt(Math.pow(cartesian[0], 2) + Math.pow(cartesian[1], 2) + Math.pow(cartesian[2], 2));
        return new Double[]{
                r,
                Math.toDegrees(Math.atan(cartesian[1] / cartesian[0])),
                Math.toDegrees(Math.acos(cartesian[2] / r))
        };
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        positionDimensions.add(new Dimension(changeX, labelXfrom, Xfrom, Xto, Xstep, getAction(MotionEquationData::getInitialPosition, 0)));
        positionDimensions.add(new Dimension(changeY, labelYfrom, Yfrom, Yto, Ystep, getAction(MotionEquationData::getInitialPosition, 1)));
        positionDimensions.add(new Dimension(changeZ, labelZfrom, Zfrom, Zto, Zstep, getAction(MotionEquationData::getInitialPosition, 2)));

        velocityDimensions.add(new Dimension(changeV, labelVfrom, Vfrom, Vto, Vstep, getVAction(MotionEquationData::getInitialVelocity, 0)));
        velocityDimensions.add(new Dimension(changeT, labelTfrom, Tfrom, Tto, Tstep, getVAction(MotionEquationData::getInitialVelocity, 1)));
        velocityDimensions.add(new Dimension(changeF, labelFfrom, Ffrom, Fto, Fstep, getVAction(MotionEquationData::getInitialVelocity, 2)));

        allDimensions.addAll(positionDimensions);
        allDimensions.addAll(velocityDimensions);

        allDimensions.stream().forEach(d -> d.getLabelFrom().textProperty().bind(d.getFrom().textProperty()));
    }

    public void cancel() {
        ((Stage) label.getScene().getWindow()).close();
    }

    private static double getDouble(TextField field) {
        if (field.getText().trim().isEmpty()) return 0;
        return Double.parseDouble(field.getText().trim());
    }
    public void start() {
        ExperimentConfig config = new ExperimentConfig();

        config.setMovingPointLabel(label.getText());

        config.setInitialPosition(positionDimensions.stream().map(d -> getDouble(d.getFrom())).toArray(Double[]::new));
        config.setInitialVelocity(toCartesian(velocityDimensions.stream().map(d -> getDouble(d.getFrom())).toArray(Double[]::new)));

        config.setIterations(allDimensions
                .stream()
                .filter(d -> d.getChange().isSelected())
                .map(d -> new Iteration<>(getDouble(d.getFrom()), getDouble(d.getTo()), getDouble(d.getStep()), d.getAction()))
                .collect(Collectors.toList()));

        cancel();
        onStart.accept(config);
    }

    private class Dimension {
        private CheckBox change;
        private Label labelFrom;

        private TextField from;
        private TextField to;
        private TextField step;

        private BiConsumer<MotionEquationData, Double> action;

        public Dimension(CheckBox change, Label labelFrom,
                         TextField from, TextField to, TextField step,
                         BiConsumer<MotionEquationData, Double>  action) {
            this.change = change;
            this.labelFrom = labelFrom;
            this.from = from;
            this.to = to;
            this.step = step;
            this.action = action;
        }

        public CheckBox getChange() {
            return change;
        }

        public Label getLabelFrom() {
            return labelFrom;
        }

        public TextField getFrom() {
            return from;
        }

        public TextField getTo() {
            return to;
        }

        public TextField getStep() {
            return step;
        }

        public BiConsumer<MotionEquationData, Double> getAction() {
            return action;
        }
    }
}
