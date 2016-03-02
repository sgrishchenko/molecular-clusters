package vsu.sc.grishchenko.molecularclusters.view;

import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import vsu.sc.grishchenko.molecularclusters.GlobalSettings;
import vsu.sc.grishchenko.molecularclusters.experiment.AnalyzeResult;
import vsu.sc.grishchenko.molecularclusters.experiment.Analyzer;
import vsu.sc.grishchenko.molecularclusters.experiment.ImportantData;
import vsu.sc.grishchenko.molecularclusters.math.MotionEquationData;
import vsu.sc.grishchenko.molecularclusters.math.Solver;
import vsu.sc.grishchenko.molecularclusters.math.Trajectory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static javafx.application.Platform.runLater;

public class MainController {
    public VBox container;
    public FileChooser fileChooser = new FileChooser();
    public DirectoryChooser directoryChooser = new DirectoryChooser();
    public Gson gson = new Gson();
    public Label status;


    public MainController() {
        fileChooser.setInitialDirectory(new File("."));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Текстовые файлы (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
    }

    private void add(MotionEquationData data) {
        HBox line = new HBox();
        line.setPadding(new Insets(5));

        CheckBox check = new CheckBox();
        check.setPrefHeight(25);

        Label eq = new Label("'' = ");
        eq.setPrefHeight(25);
        eq.setPrefWidth(25);
        Label start = new Label("(");
        start.setPrefHeight(25);
        Label str;

        TextField label = new TextField();
        label.setPrefWidth(36);
        label.setText(data.getLabel());
        TextField expression = new TextField();
        expression.setPrefWidth(221);
        expression.setText(data.getAccelerationEquation());
        TextField input;

        line.getChildren().add(check);
        line.getChildren().add(label);
        line.getChildren().add(eq);
        line.getChildren().add(expression);
        line.getChildren().add(start);
        String[] strs = {", ", ", ", ")(", ", ", ", ", ")"};
        Double[] values = ArrayUtils.addAll(data.getInitialPosition(), data.getInitialVelocity());
        for (int i = 0 ; i < 6 ; i++) {
            input = new TextField();
            input.setPrefWidth(40);
            input.setText(Double.toString(values[i]));
            line.getChildren().add(input);

            str = new Label(strs[i]);
            str.setPrefHeight(25);
            line.getChildren().add(str);
        }
        ColorPicker colorPicker = new ColorPicker(ColorAdapter.from(data.getColor()));
        colorPicker.setPrefWidth(45);
        line.getChildren().add(colorPicker);

        container.getChildren().add(line);
    }

    public void add(ActionEvent actionEvent) {
        int number = container.getChildren().size() + 1;
        add(new MotionEquationData("r" + number));
    }

    public void delete(ActionEvent actionEvent) {
        List<Node> linesToDelete = new ArrayList<Node>();
        CheckBox check;
        for (Node line : container.getChildren()) {
            check = (CheckBox)((HBox) line).getChildren().get(0);
            if (check.isSelected()) linesToDelete.add(line);
        }
        container.getChildren().removeAll(linesToDelete);
    }

    public void start(ActionEvent actionEvent) {
        View3D view3D = new View3D(Solver.solveVerlet(read(), 0.,
                GlobalSettings.getInstance().viewSettings.getCountSteps(),
                GlobalSettings.getInstance().viewSettings.getStepSize()));
        view3D.start(new Stage());
    }

    public void clear(ActionEvent actionEvent) {
        container.getChildren().clear();
        add(actionEvent);
    }

    private Double getDoubleValueFromTextField(Node line, int index) {
        return Double.parseDouble(((TextField) ((HBox) line).getChildren().get(index)).getText());
    }

    private List<MotionEquationData> read() {
        List<MotionEquationData> equations = new ArrayList<>();

        container.getChildren().forEach(line -> equations.add(new MotionEquationData(
                ((TextField) ((HBox) line).getChildren().get(1)).getText(),
                ((TextField) ((HBox) line).getChildren().get(3)).getText(),
                ArrayUtils.<Double>toArray(getDoubleValueFromTextField(line, 5),
                        getDoubleValueFromTextField(line, 7),
                        getDoubleValueFromTextField(line, 9)),
                ArrayUtils.<Double>toArray(getDoubleValueFromTextField(line, 11),
                        getDoubleValueFromTextField(line, 13),
                        getDoubleValueFromTextField(line, 15)),
                ColorAdapter.from(((ColorPicker) ((HBox) line).getChildren().get(17)).getValue())
        )));

        return equations;
    }

    public void open(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(container.getScene().getWindow());
        if (file != null) {
            container.getChildren().clear();
            try (FileReader fileReader = new FileReader(file)) {
                Arrays.asList(gson.fromJson(fileReader, MotionEquationData[].class)).forEach(this::add);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void save(ActionEvent actionEvent) {
        File file = fileChooser.showSaveDialog(container.getScene().getWindow());
        if (file != null) {
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(gson.toJson(read()));
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void showPeriodInfoDialog(DateTime start) {
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendHours()
                .appendSuffix(" ч. ")
                .appendMinutes()
                .appendSuffix(" м. ")
                .appendSeconds()
                .appendSuffix(" с. ")
                .toFormatter();

        new InfoDialog("Эксперименты успешно проведены,\n " +
                "результаты записаны в файлы.\n" +
                "Затраченное время: " + formatter.print(new Period(start, new DateTime()).normalizedStandard()))
                .start(new Stage());
    }

    public void exp1(ActionEvent actionEvent) {
        String filePath = Analyzer.createExperimentDirectory("experiment1/");
        DateTime start = new DateTime();

        saveExperiments(filePath, Analyzer.experiment1(filePath, read()));

        showPeriodInfoDialog(start);
    }

    public void exp2(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(container.getScene().getWindow());
        if (file != null) {
            String filePath = Analyzer.createExperimentDirectory("experiment2/");
            DateTime start = new DateTime();
            try (FileReader fileReader = new FileReader(file)) {
                List<Trajectory> result = Arrays.asList(gson.fromJson(fileReader, Trajectory[].class));;

                saveExperiments(filePath, Analyzer.experiment2(filePath, read(), result));
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

            showPeriodInfoDialog(start);
        }
    }

    private void saveExperiments(String filePath, List<List<AnalyzeResult>> experiments) {
        Stream.of(AnalyzeResult.class.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(ImportantData.class))
                .forEach(m -> {
                    File out = new File(filePath + m.getName().replace("get", "") + ".txt");

                    try (FileWriter writer = new FileWriter(out)) {
                        StringBuilder builder = new StringBuilder("");
                        for (List<AnalyzeResult> row : experiments) {
                            for (AnalyzeResult experiment : row) {
                                builder.append(String.format("%.2f", (Double) m.invoke(experiment)));
                                builder.append("\t");
                            }
                            builder.append("\r\n");
                        }
                        writer.write(builder.toString());
                    } catch (IOException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }

                });
    }

    public void startFromFile(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(container.getScene().getWindow());
        if (file != null) {
            try (FileReader fileReader = new FileReader(file)) {
                List<Trajectory> result = Arrays.asList(gson.fromJson(fileReader, Trajectory[].class));
                View3D view3D = new View3D(result);
                view3D.start(new Stage());
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void analyzeFromFile(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(container.getScene().getWindow());
        if (file != null) {
            try (FileReader fileReader = new FileReader(file)) {
                List<Trajectory> result = Arrays.asList(gson.fromJson(fileReader, Trajectory[].class));
                AnalyzeResult analyzeResult = Analyzer.getParams(result);

                new InfoDialog(String.format("Расстояние до оси трубки в начальный момент: %.3f Å\n" +
                        "Начальный угол с осью трубки: %d°\n" +
                        "Начальный угол с горизонтальной плоскостью XY: %d°\n" +
                        "Длина трубки: %.3f Å\n" +
                        "\n" +
                        "Длина пройденного пути: %.3f Å\n" +
                        "Длина пройденного пути / длина трубки: %.3f\n" +
                        "\n" +
                        "Средняя скорость: %.3f Å/τ₀\n" +
                        "Средняя длина пробега: %.3f Å\n" +
                        "\n" +
                        "Коэффициент диффузии: %.3f Å²/τ₀",
                        analyzeResult.getRadius(),
                        Math.round(analyzeResult.getFi()),
                        Math.round(analyzeResult.getTeta()),
                        analyzeResult.getPathLength() / analyzeResult.getPathLengthToTubeLength(),
                        analyzeResult.getPathLength(),
                        analyzeResult.getPathLengthToTubeLength(),
                        analyzeResult.getAvgSpeed(),
                        analyzeResult.getAvgFreePath(),
                        analyzeResult.getDiffusionCoeff()))
                        .start(new Stage());

            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void analyzeFromFolder(ActionEvent actionEvent) {
        directoryChooser.setInitialDirectory(new File("."));
        File file = directoryChooser.showDialog(container.getScene().getWindow());
        String fileNamePattern = "%s/exp_%d_%d.txt";
        List<Trajectory> result;
        List<List<AnalyzeResult>> experiments = new ArrayList<>();
        if (file != null) {
            for (int i = 0; i < 10; i++) {
                List<AnalyzeResult> row = new ArrayList<>();
                for (int j = 0; j < 10; j++) {
                    try (FileReader fileReader = new FileReader(String.format(fileNamePattern, file.getPath(), i, j))) {
                        result = Arrays.asList(gson.fromJson(fileReader, Trajectory[].class));
                        row.add(Analyzer.getParams(result));
                    } catch(IOException ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                experiments.add(row);
            }
            saveExperiments(file.getPath() + "/", experiments);
            new InfoDialog("Параметры экспериментов\nуспешно пересчитаны").start(new Stage());
        }
    }

    public void settings(ActionEvent actionEvent) throws Exception {
        GlobalSettings.ExperimentSettings experimentSettings = GlobalSettings.getInstance().experimentSettings;
        if (experimentSettings.getMovingPointLabel() == null) {
            List<MotionEquationData> dataList = read();
            if (dataList != null && !dataList.isEmpty()) {
                experimentSettings.setMovingPointLabel(dataList.get(dataList.size() - 1).getLabel());
            }
        }
        new Settings().start(new Stage());
    }

    private void setWorkStatus(Runnable work) {
        status.setText("Выполняются вычисления...");
        new Thread(() -> {
            runLater(() -> {
                try {
                    work.run();
                } catch (Exception e) {
                    e.printStackTrace();
                    status.setText("В процессе вычислений произошла ошибка.");
                }
                status.setText("");
            });
        }).start();
    }
}
