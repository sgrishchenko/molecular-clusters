package vsu.sc.grishchenko.molecularclusters.experiment;

import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import vsu.sc.grishchenko.molecularclusters.math.MotionEquationData;

import java.net.URL;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExperimentController implements Initializable {
    public ChoiceBox<String> template;

    public TextField H1;
    public TextField H2;

    public TextField label;

    public CheckBox changeX; public Label labelXfrom; public TextField Xfrom; public TextField Xto; public TextField Xstep;
    public CheckBox changeY; public Label labelYfrom; public TextField Yfrom; public TextField Yto; public TextField Ystep;
    public CheckBox changeZ; public Label labelZfrom; public TextField Zfrom; public TextField Zto; public TextField Zstep;
    public CheckBox changeV; public Label labelVfrom; public TextField Vfrom; public TextField Vto; public TextField Vstep;
    public CheckBox changeT; public Label labelTfrom; public TextField Tfrom; public TextField Tto; public TextField Tstep;
    public CheckBox changeF; public Label labelFfrom; public TextField Ffrom; public TextField Fto; public TextField Fstep;

    private static Iteration<MotionEquationData> emptyIteration = new Iteration<>(null, 0., 0., 0., null);

    private Map<String, ExperimentConfig> templates = new LinkedHashMap<>();

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
        positionDimensions.add(new Dimension("X", changeX, labelXfrom, Xfrom, Xto, Xstep, getAction(MotionEquationData::getInitialPosition, 0)));
        positionDimensions.add(new Dimension("Y", changeY, labelYfrom, Yfrom, Yto, Ystep, getAction(MotionEquationData::getInitialPosition, 1)));
        positionDimensions.add(new Dimension("Y", changeZ, labelZfrom, Zfrom, Zto, Zstep, getAction(MotionEquationData::getInitialPosition, 2)));

        velocityDimensions.add(new Dimension("VX", changeV, labelVfrom, Vfrom, Vto, Vstep, getVAction(MotionEquationData::getInitialVelocity, 0)));
        velocityDimensions.add(new Dimension("VY", changeT, labelTfrom, Tfrom, Tto, Tstep, getVAction(MotionEquationData::getInitialVelocity, 1)));
        velocityDimensions.add(new Dimension("VY", changeF, labelFfrom, Ffrom, Fto, Fstep, getVAction(MotionEquationData::getInitialVelocity, 2)));

        allDimensions.addAll(positionDimensions);
        allDimensions.addAll(velocityDimensions);

        allDimensions.stream().forEach(d -> {
            d.getLabelFrom().textProperty().bind(d.getFrom().textProperty());
            d.getChange().selectedProperty().addListener((observable, oldValue, newValue) -> {
                d.getTo().setDisable(!newValue);
                d.getStep().setDisable(!newValue);
            });
        });

        templates.put("...", new ExperimentConfig() {{
            setInitialPosition(new Double[] {0., 0., 0.});
            setInitialVelocity(new Double[] {0., 0., 0.});

            setIterations(Arrays.asList(
                    emptyIteration,
                    emptyIteration,
                    emptyIteration,
                    emptyIteration,
                    emptyIteration,
                    emptyIteration
            ));
        }});
        templates.put("Сдвиг вдоль оси X + изменение горизонтального угла", new ExperimentConfig() {{
            setInitialPosition(new Double[] {0., -3., 0.});
            setInitialVelocity(new Double[] {75., 90., 90.});

            setIterations(Arrays.asList(
                    new Iteration<>("X", 0, -2, 0.5, positionDimensions.get(0).getAction()),
                    emptyIteration,
                    emptyIteration,
                    emptyIteration,
                    new Iteration<>("VY", 90, 0, 10, velocityDimensions.get(1).getAction()),
                    emptyIteration
            ));
        }});
        templates.put("Изменение вертикального угла для фиксированного горизонтального угла", new ExperimentConfig() {{
            setInitialPosition(new Double[] {0., -3., 0.});
            setInitialVelocity(new Double[] {75., 45., 90.});

            setIterations(Arrays.asList(
                    emptyIteration,
                    emptyIteration,
                    emptyIteration,
                    emptyIteration,
                    emptyIteration,
                    new Iteration<>("VZ", 90, 0, 10, velocityDimensions.get(2).getAction())
            ));
        }});
        template.setItems(FXCollections.observableArrayList(templates.keySet()));
        template.setValue("...");
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
                .map(d -> new Iteration<>(d.getName(), getDouble(d.getFrom()), getDouble(d.getTo()), getDouble(d.getStep()), d.getAction()))
                .collect(Collectors.toList()));

        cancel();
        onStart.accept(config);
    }

    public void setTemplate() {
        ExperimentConfig config = templates.get(template.getValue());

        for (int i = 0; i < 3; i++) {
            positionDimensions.get(i).getFrom().setText(String.valueOf(config.getInitialPosition()[i]));
            velocityDimensions.get(i).getFrom().setText(String.valueOf(config.getInitialVelocity()[i]));
        }

        Dimension dimension;
        Iteration iteration;
        for (int i = 0; i < 6; i++) {
            dimension = allDimensions.get(i);
            iteration = config.getIterations().get(i);

            dimension.getChange().setSelected(iteration != emptyIteration);
            dimension.getTo().setText(String.valueOf(config.getIterations().get(i).getTo()));
            dimension.getStep().setText(String.valueOf(config.getIterations().get(i).getStep()));
        }
    }

    private class Dimension {
        private String name;
        private CheckBox change;
        private Label labelFrom;

        private TextField from;
        private TextField to;
        private TextField step;

        private BiConsumer<MotionEquationData, Double> action;

        public Dimension(String name, CheckBox change, Label labelFrom,
                         TextField from, TextField to, TextField step,
                         BiConsumer<MotionEquationData, Double>  action) {
            this.name = name;
            this.change = change;
            this.labelFrom = labelFrom;
            this.from = from;
            this.to = to;
            this.step = step;
            this.action = action;
        }

        public String getName() {
            return name;
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
