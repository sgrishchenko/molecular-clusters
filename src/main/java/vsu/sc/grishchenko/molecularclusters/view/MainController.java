package vsu.sc.grishchenko.molecularclusters.view;

import com.google.gson.Gson;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import vsu.sc.grishchenko.molecularclusters.database.Trajectories;
import vsu.sc.grishchenko.molecularclusters.experiment.Experiment;
import vsu.sc.grishchenko.molecularclusters.experiment.ExperimentTask;
import vsu.sc.grishchenko.molecularclusters.math.MotionEquationData;
import vsu.sc.grishchenko.molecularclusters.math.Solver;
import vsu.sc.grishchenko.molecularclusters.math.Trajectory;
import vsu.sc.grishchenko.molecularclusters.settings.CurrentSettings;
import vsu.sc.grishchenko.molecularclusters.settings.Settings;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * <p>Класс-контроллер для обработки событий, происходящих на основной форме приложения.</p>
 *
 * @author Грищенко Сергей
 * @see Main
 */
public class MainController {
    /**
     * <p>Вертикальный котейнер, содержащий элементы управления(преимущественно поля ввода) для описания
     * параметров частицы, которая будет уствовать в моделировании физической системы.</p>
     *
     * @see VBox
     */
    public VBox container;
    /**
     * <p>Объект для выбора файлов.</p>
     *
     * @see FileChooser
     */
    public FileChooser fileChooser = new FileChooser();
    /**
     * <p>Объект для сериализации/десериализции Java-объктов
     * в фомате <a href="https://ru.wikipedia.org/wiki/JSON">JSON</a>.</p>
     *
     * @see <a href="http://static.javadoc.io/com.google.code.gson/gson/2.6.2/com/google/gson/Gson.html">Gson</a>
     */
    public Gson gson = new Gson();
    /**
     * <p>Строка состояния(строка, в которой выводится информация о текущих процессах в приложении).</p>
     *
     * @see Label
     */
    public Label status;
    /**
     * <p>Объект, хранящий ссылку на файл, с которым ассоцирована рабочая область приложения в текущий момент.</p>
     *
     * @see File
     */
    private File currentFile;

    /**
     * <p>В конструкторе по умолчанию инициализируются:</p>
     * <ul>
     * <li>Начальная директория {@link MainController#fileChooser} - дирректория, откуда была запущена программа;</li>
     * <li>Фильтр расширений для {@link MainController#fileChooser} - только файлы в формате <a href="https://ru.wikipedia.org/wiki/JSON">JSON</a>;</li>
     * </ul>
     */
    public MainController() {
        fileChooser.setInitialDirectory(new File("."));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Файлы в формате JSON (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
    }

    /**
     * <p>Метод, который реализует добаление строки в рабочей области программы (в {@link MainController#container}).</p>
     *
     * @param data проинициализированный объект {@link MotionEquationData}
     * @see MotionEquationData
     */
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
        for (int i = 0; i < 6; i++) {
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

    /**
     * <p>Обработчик события при нажатии на кнопку "Добавить".
     * Добавляет в рабочую область новую строку для ввода информации.</p>
     */
    public void add() {
        int number = container.getChildren().size() + 1;
        add(new MotionEquationData("r" + number));
    }

    /**
     * <p>Обработчик события при нажатии на кнопку "Удалить".
     * Удаляет из рабочей области строки, в которых проставлен выбор с помощью элемента {@link CheckBox}.</p>
     */
    public void delete() {
        List<Node> linesToDelete = new ArrayList<>();
        CheckBox check;
        for (Node line : container.getChildren()) {
            check = (CheckBox) ((HBox) line).getChildren().get(0);
            if (check.isSelected()) linesToDelete.add(line);
        }
        container.getChildren().removeAll(linesToDelete);
    }

    /**
     * <p>Обработчик события при нажатии на кнопку "Запустить".
     * С помощью {@link MainController#startTask(Task, EventHandler)} pапускает фоновый процесс
     * на расчет траекторий движения частиц.
     * После поле завершения расчета открывается окно, демонстрирующее трехмерную анимированную визуализацию.</p>
     *
     * @see Task
     * @see View3D
     * @see Solver
     */
    public void start() {
        startTask(new Task<List<Trajectory>>() {
                      @Override
                      protected List<Trajectory> call() throws Exception {
                          List<Trajectory> result;
                          updateMessage("Выполняются вычисления...");
                          result = Solver.solveVerlet(read(), 0.,
                                  CurrentSettings.getInstance().getCountSteps(),
                                  CurrentSettings.getInstance().getStepSize());
                          updateMessage("Вычисления выполнены");

                          return result;
                      }
                  },
                event -> {
                    @SuppressWarnings("unchecked")
                    View3D view3D = new View3D((List<Trajectory>) event.getSource().getValue());
                    view3D.start(new Stage());
                });

    }

    /**
     * <p>Обработчик события при нажатии на кнопку "Очистить".
     * Удаляет из рабочей области все строки и добавляет одну незаполненную строку.</p>
     */
    public void clear() {
        container.getChildren().clear();
        add();
    }

    /**
     * <p>Служебный метод для считывания информации из текстового поля в строке рабочей области.</p>
     *
     * @param line  контейнер, содержащий элементы управления строки
     * @param index номер текстового поля в строке
     * @return вещественное число, полученное из значения, которое было введенно в текстовое поле.
     */
    private double getDoubleValueFromTextField(Node line, int index) {
        return Double.parseDouble(((TextField) ((HBox) line).getChildren().get(index)).getText());
    }

    /**
     * <p>Служебный метод, который считывает информацию о физической модели из рабочей области
     * и каждую строку преобразует в объект {@link MotionEquationData}</p>
     *
     * @return список объектов {@link MotionEquationData}.
     * @see MotionEquationData
     */
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

    /**
     * <p>Обработчик события при выборе в главном меню приложения пункта <i>Файл/Открыть</i>.
     * Запускает диалоговое окно для выбора файла. После того как пользователь совершил выбор,
     * из выбранного файла считывается информация и выводится в рабочую область приложения.</p>
     */
    public void open() {
        File file = fileChooser.showOpenDialog(container.getScene().getWindow());
        if (file != null) {
            container.getChildren().clear();
            try (FileReader fileReader = new FileReader(file)) {
                Arrays.asList(gson.fromJson(fileReader, MotionEquationData[].class)).forEach(this::add);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            currentFile = file;
            updateTitle();
        }
    }

    /**
     * <p>Обработчик события при выборе в главном меню приложения пункта <i>Файл/Сохранить</i>.
     * Если в объекте {@link MainController#currentFile} нет информации о текущем файле,
     * открывается диалоговое окно для выбора файла (см. {@link MainController#saveAs()}).
     * Иначе считывается информация из рабочей области и записывается в файл,
     * информация о котором хранится в {@link MainController#currentFile}.</p>
     */
    public void save() {
        if (currentFile == null) {
            saveAs();
        } else {
            save(currentFile);
        }
    }

    /**
     * <p>Обработчик события при выборе в главном меню приложения пункта <i>Файл/Сохранить как...</i>.
     * Запускает диалоговое окно для выбора файла. После того, как пользователь выбрал файл,
     * считывается информациия из рабочей области приложения и записывается в выбранный файл.</p>
     */
    public void saveAs() {
        File file = fileChooser.showSaveDialog(container.getScene().getWindow());
        if (file == null) return;

        save(file);
        currentFile = file;
        updateTitle();
    }

    /**
     * <p>Метод, который считывает из рабочей области информацию с помощью метода {@link MainController#read()}
     * и записывает ей в учазанный файл с помощью объекта {@link MainController#gson}.</p>
     *
     * @param file ссылка на файл, в который требуется сохранить информацию
     */
    private void save(File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(gson.toJson(read()));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * <p>Метод, который обновляет заголовок основного окна программы, выводя информции о файле,
     * с которым в текущий момент ассоциирована рабочая область.</p>
     */
    private void updateTitle() {
        Stage stage = (Stage) container.getScene().getWindow();
        if (!stage.getTitle().endsWith("]")) {
            stage.setTitle(stage.getTitle() + " - [] - []");
        }
        stage.setTitle(stage.getTitle().replaceAll(
                "( - \\[).*(\\] - \\[).*(\\])$",
                "$1" + Matcher.quoteReplacement(currentFile.getName()) + "$2"
                        + Matcher.quoteReplacement(currentFile.getPath()) + "$3"
        ));
    }

    /**
     * <p>Метод, который выводит диалоговое окно с информацией о том,
     * какое количество времении потребовалось для выполнения серии экспериментов.</p>
     *
     * @param start время, когда задание на расчет серии экспериментов было запущено
     * @see InfoDialog
     */
    private void showPeriodInfoDialog(DateTime start) {
        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendHours()
                .appendSuffix(" ч. ")
                .appendMinutes()
                .appendSuffix(" м. ")
                .appendSeconds()
                .appendSuffix(" с. ")
                .toFormatter();

        new InfoDialog("Эксперименты успешно проведены,\n" +
                "результаты сохранены в базу данных.\n" +
                "Затраченное время: " + formatter.print(new Period(start, new DateTime()).normalizedStandard()))
                .start(new Stage());
    }

    /**
     * <p>Обработчик события при выборе в главном меню приложения пункта <i>Файл/Настройки...</i>.
     * Запускает диалоговое окно для установки параметров рассчетов, выполняемых в приложении.</p>
     *
     * @throws Exception исключения, возникающие при работе с настройками.
     * @see Settings
     */
    public void settings() throws Exception {
        new Settings().start(new Stage());
    }

    /**
     * <p>Метод, который запускает фоновый процесс в приложении.</p>
     *
     * @param task        объект, описывающий процесс
     * @param onSucceeded обработчик собития успешного завершения процесса
     * @see Thread
     * @see Task
     */
    private void startTask(final Task task, EventHandler<WorkerStateEvent> onSucceeded) {
        status.textProperty().bind(task.messageProperty());
        task.setOnSucceeded(onSucceeded);

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * <p>Обработчик события при нажатии на кнопку "Задание".
     * Открывает диалоговое окно для конфигурации серии экспериментов.
     * После того, как пользователь завершил конфигурацию,
     * запускается фоновый процесс с помошью {@link MainController#startTask(Task, EventHandler)}.</p>
     *
     * @throws Exception исключения, возникающие при конфигурации задания.
     * @see Task
     * @see Experiment
     * @see Solver
     */
    public void task() throws Exception {
        new Experiment((config -> {
            DateTime start = new DateTime();
            CurrentSettings settings = CurrentSettings.getInstance();
            Function<List<MotionEquationData>, List<Trajectory>> source
                    = dataList -> Solver.solveVerlet(dataList, 0, settings.getCountSteps(), settings.getStepSize());
            startTask(new ExperimentTask(read(), source, config), event -> showPeriodInfoDialog(start));
        })).start(new Stage());
    }

    /**
     * <p>Обработчик события при нажатии на кнопку "База".
     * Открывает диалоговое окно, с котором можно просмотреть тректории движениия чистиц,
     * сохраненные в базе данных.</p>
     *
     * @throws Exception исключения, возникающие при просмотре тректорий движениия чистиц.
     * @see Trajectories
     */
    public void viewTrajectories() throws Exception {
        new Trajectories().start(new Stage());
    }
}
