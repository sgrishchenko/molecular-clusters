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

import static vsu.sc.grishchenko.molecularclusters.experiment.Analyzer.toCartesian;
import static vsu.sc.grishchenko.molecularclusters.experiment.Analyzer.toSpherical;

/**
 * <p>Класс-контроллер для обработки событий, происходящих в диалоговом окне для конфигурации задания
 * на выполнение серии экспериментов.</p>
 *
 * @author Грищенко Сергей
 * @see Experiment
 * @see Initializable
 */
public class ExperimentController implements Initializable {
    /**
     * <p>Выподающий список для выбора шаблона заполнения окна конфигурации задания.</p>
     *
     * @see ChoiceBox
     */
    public ChoiceBox<String> template;
    /**
     * <p>Поле ввода метки движущейся точки.</p>
     *
     * @see TextField
     */
    public TextField label;
    /**
     * <p>Флажок установки итерации
     * по координате X начального положения варьируемой частицы.</p>
     *
     * @see CheckBox
     */
    public CheckBox changeX;
    /**
     * <p>Метка для отображения начального значение в итерации
     * по координате X начального положения варьируемой частицы.</p>
     *
     * @see Label
     */
    public Label labelXfrom;
    /**
     * <p>Поле ввода начального значение в итерации
     * по координате X начального положения варьируемой частицы.</p>
     *
     * @see TextField
     */
    public TextField Xfrom;
    /**
     * <p>Поле ввода конечного значение в итерации
     * по координате X начального положения варьируемой частицы.</p>
     *
     * @see TextField
     */
    public TextField Xto;
    /**
     * <p>Поле ввода шага в итерации
     * по координате X начального положения варьируемой частицы.</p>
     *
     * @see TextField
     */
    public TextField Xstep;
    /**
     * <p>Флажок установки итерации
     * по координате Y начального положения варьируемой частицы.</p>
     *
     * @see CheckBox
     */
    public CheckBox changeY;
    /**
     * <p>Метка для отображения начального значение в итерации
     * по координате Y начального положения варьируемой частицы.</p>
     *
     * @see Label
     */
    public Label labelYfrom;
    /**
     * <p>Поле ввода начального значение в итерации
     * по координате Y начального положения варьируемой частицы.</p>
     *
     * @see TextField
     */
    public TextField Yfrom;
    /**
     * <p>Поле ввода конечного значение в итерации
     * по координате Y начального положения варьируемой частицы.</p>
     *
     * @see TextField
     */
    public TextField Yto;
    /**
     * <p>Поле ввода шага в итерации
     * по координате Y начального положения варьируемой частицы.</p>
     *
     * @see TextField
     */
    public TextField Ystep;
    /**
     * <p>Флажок установки итерации
     * по координате Z начального положения варьируемой частицы.</p>
     *
     * @see CheckBox
     */
    public CheckBox changeZ;
    /**
     * <p>Метка для отображения начального значение в итерации
     * по координате Z начального положения варьируемой частицы.</p>
     *
     * @see Label
     */
    public Label labelZfrom;
    /**
     * <p>Поле ввода начального значение в итерации
     * по координате Z начального положения варьируемой частицы.</p>
     *
     * @see TextField
     */
    public TextField Zfrom;
    /**
     * <p>Поле ввода конечного значение в итерации
     * по координате Z начального положения варьируемой частицы.</p>
     *
     * @see TextField
     */
    public TextField Zto;
    /**
     * <p>Поле ввода шага в итерации
     * по координате Z начального положения варьируемой частицы.</p>
     *
     * @see TextField
     */
    public TextField Zstep;
    /**
     * <p>Флажок установки итерации
     * по модулю вектора начальной скорости варьируемой частицы.</p>
     *
     * @see CheckBox
     */
    public CheckBox changeV;
    /**
     * <p>Метка для отображения начального значение в итерации
     * по модулю вектора начальной скорости варьируемой частицы.</p>
     *
     * @see Label
     */
    public Label labelVfrom;
    /**
     * <p>Поле ввода начального значение в итерации
     * по модулю вектора начальной скорости варьируемой частицы.</p>
     *
     * @see TextField
     */
    public TextField Vfrom;
    /**
     * <p>Поле ввода конечного значение в итерации
     * по модулю вектора начальной скорости варьируемой частицы.</p>
     *
     * @see TextField
     */
    public TextField Vto;
    /**
     * <p>Поле ввода шага в итерации
     * по модулю вектора начальной скорости варьируемой частицы.</p>
     *
     * @see TextField
     */
    public TextField Vstep;
    /**
     * <p>Флажок установки итерации
     * по азимутальному углу вектора начальной скорости варьируемой частицы.</p>
     *
     * @see CheckBox
     */
    public CheckBox changeF;
    /**
     * <p>Метка для отображения начального значение в итерации
     * по азимутальному углу вектора начальной скорости варьируемой частицы.</p>
     *
     * @see Label
     */
    public Label labelFfrom;
    /**
     * <p>Поле ввода начального значение в итерации
     * по азимутальному углу вектора начальной скорости варьируемой частицы.</p>
     *
     * @see TextField
     */
    public TextField Ffrom;
    /**
     * <p>Поле ввода конечного значение в итерации
     * по азимутальному углу вектора начальной скорости варьируемой частицы.</p>
     *
     * @see TextField
     */
    public TextField Fto;
    /**
     * <p>Поле ввода шага в итерации
     * по азимутальному углу вектора начальной скорости варьируемой частицы.</p>
     *
     * @see TextField
     */
    public TextField Fstep;
    /**
     * <p>Флажок установки итерации
     * по зенитному углу вектора начальной скорости варьируемой частицы.</p>
     *
     * @see CheckBox
     */
    public CheckBox changeT;
    /**
     * <p>Метка для отображения начального значение в итерации
     * по зенитному углу вектора начальной скорости варьируемой частицы.</p>
     *
     * @see Label
     */
    public Label labelTfrom;
    /**
     * <p>Поле ввода начального значение в итерации
     * по зенитному углу вектора начальной скорости варьируемой частицы.</p>
     *
     * @see TextField
     */
    public TextField Tfrom;
    /**
     * <p>Поле ввода конечного значение в итерации
     * по зенитному углу вектора начальной скорости варьируемой частицы.</p>
     *
     * @see TextField
     */
    public TextField Tto;
    /**
     * <p>Поле ввода шага в итерации
     * по зенитному углу вектора начальной скорости варьируемой частицы.</p>
     *
     * @see TextField
     */
    public TextField Tstep;
    /**
     * <p>Служебный элемент для идентификации пустых итераций
     * при создании шаблонов конфигурации запуска задания.</p>
     *
     * @see Iteration
     */
    private static Iteration<MotionEquationData> emptyIteration = new Iteration<>(null, 0., 0., 0., null);
    /**
     * <p>Карта шаблонов конфигурации запуска задания.
     * Ключи - строковые названия шаблонов, значения - сами объекты конфигурации.</p>
     *
     * @see ExperimentConfig
     */
    private Map<String, ExperimentConfig> templates = new LinkedHashMap<>();
    /**
     * <p>Список измерений начального положения варьируемой частицы.</p>
     *
     * @see Dimension
     */
    private List<Dimension> positionDimensions = new ArrayList<>();
    /**
     * <p>Список измерений вектора начальной скорости варьируемой частицы.</p>
     *
     * @see Dimension
     */
    private List<Dimension> velocityDimensions = new ArrayList<>();
    /**
     * <p>Общий список всех измерений начальных условий для варьируемой частицы,
     * по которым возможны итерации.</p>
     *
     * @see Dimension
     */
    private List<Dimension> allDimensions = new ArrayList<>();
    /**
     * <p>Действие, которое будет выполнено после успешного указания
     * параметров конфигурации запуска.</p>
     *
     * @see Consumer
     */
    private Consumer<ExperimentConfig> onStart;

    public void setOnStart(Consumer<ExperimentConfig> onStart) {
        this.onStart = onStart;
    }

    /**
     * <p>Служебный метод для создания функции, записывающей
     * вещественное число в определенную ячейку массива,
     * который является полем объекта {@link MotionEquationData}.</p>
     *
     * @param getter метод получения поля объекта {@link MotionEquationData}.
     *               Это поле должно быть массивом вещественных чисел
     * @param index  индекс ячейки массива, в которую созданная функция будет записывать переданное значение
     * @return функцию, принимающую в качестве аргументов объект {@link MotionEquationData},
     * в который требуется записать значение, и само значение,
     * которое требуется записать в поле объекта {@link MotionEquationData}.
     * @see MotionEquationData
     * @see Function
     * @see BiConsumer
     */
    private BiConsumer<MotionEquationData, Double> getAction(Function<MotionEquationData, Double[]> getter, int index) {
        return (data, value) -> getter.apply(data)[index] = value;
    }

    /**
     * <p>Служебный метод для создания функции, записывающей
     * вещественное число в определенную ячейку массива,
     * который является полем объекта {@link MotionEquationData}
     * (отличается от {@link ExperimentController#getAction(Function, int)} тем,
     * что в созданную функцию будут передаваться данные в сферических координатах).</p>
     *
     * @param getter метод получения поля объекта {@link MotionEquationData}.
     *               Это поле должно быть массивом вещественных чисел
     * @param index  индекс ячейки массива, в которую созданная функция будет записывать переданное значение
     * @return функцию, принимающая в качестве аргументов объект {@link MotionEquationData},
     * в который требуется записать значение, и само значение,
     * которое требуется записать в поле объекта {@link MotionEquationData}.
     * @see MotionEquationData
     * @see Function
     * @see BiConsumer
     */
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

    /**
     * <p>Инициализация полного списка измерений начальных условий для варьируемой частицы,
     * по которым возможны итерации и списка шаблонов для заполнения формы конфигурации.</p>
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        positionDimensions.add(new Dimension("X", changeX, labelXfrom, Xfrom, Xto, Xstep, getAction(MotionEquationData::getInitialPosition, 0)));
        positionDimensions.add(new Dimension("Y", changeY, labelYfrom, Yfrom, Yto, Ystep, getAction(MotionEquationData::getInitialPosition, 1)));
        positionDimensions.add(new Dimension("Z", changeZ, labelZfrom, Zfrom, Zto, Zstep, getAction(MotionEquationData::getInitialPosition, 2)));

        velocityDimensions.add(new Dimension("VR", changeV, labelVfrom, Vfrom, Vto, Vstep, getVAction(MotionEquationData::getInitialVelocity, 0)));
        velocityDimensions.add(new Dimension("VF", changeF, labelFfrom, Ffrom, Fto, Fstep, getVAction(MotionEquationData::getInitialVelocity, 1)));
        velocityDimensions.add(new Dimension("VT", changeT, labelTfrom, Tfrom, Tto, Tstep, getVAction(MotionEquationData::getInitialVelocity, 2)));


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
            setInitialPosition(new Double[]{0., 0., 0.});
            setInitialVelocity(new Double[]{0., 0., 0.});

            setIterations(Arrays.asList(
                    emptyIteration,
                    emptyIteration,
                    emptyIteration,
                    emptyIteration,
                    emptyIteration,
                    emptyIteration
            ));
        }});
        templates.put("Сдвиг вдоль оси X + изменение зенитного угла", new ExperimentConfig() {{
            setInitialPosition(new Double[]{0., 0., -3.});
            setInitialVelocity(new Double[]{75., 0., 0.});

            setIterations(Arrays.asList(
                    new Iteration<>("X", 0, -2, 0.25, positionDimensions.get(0).getAction()),
                    emptyIteration,
                    emptyIteration,
                    emptyIteration,
                    emptyIteration,
                    new Iteration<>("VZ", 0, 90, 10, velocityDimensions.get(2).getAction())
            ));
        }});
        templates.put("Изменение азимутального угла", new ExperimentConfig() {{
            setInitialPosition(new Double[]{0., 0., -3.});
            setInitialVelocity(new Double[]{75., 0., 30.});

            setIterations(Arrays.asList(
                    emptyIteration,
                    emptyIteration,
                    emptyIteration,
                    emptyIteration,
                    new Iteration<>("VY", 0, 90, 10, velocityDimensions.get(1).getAction()),
                    emptyIteration
            ));
        }});
        template.setItems(FXCollections.observableArrayList(templates.keySet()));
        template.setValue("...");
    }

    /**
     * <p>Обработчик события при нажатии на кнопку "Отмена" в диалоговом окне.
     * Закрывает диалоговое окно.</p>
     */
    public void cancel() {
        ((Stage) label.getScene().getWindow()).close();
    }

    /**
     * <p>Служебный метод для получение вещественного числа, записанного в текстовом поле.</p>
     *
     * @param field текстовое поле
     * @return вещественное число.
     * @see TextField
     */
    private static double getDouble(TextField field) {
        if (field.getText().trim().isEmpty()) return 0;
        return Double.parseDouble(field.getText().trim());
    }

    /**
     * <p>Обработчик события при нажатии на кнопку "OK" в диалоговом окне.
     * На основе введенных пользователем данных создается
     * и заполняется объект {@link ExperimentConfig}.
     * Затем вызывется {@link ExperimentController#onStart},
     * в аргумент которого передается созданный объект.</p>
     *
     * @see ExperimentConfig
     * @see ExperimentController#onStart
     */
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

    /**
     * <p>Обработчик события при выборе значение в элементе {@link ExperimentController#template}.
     * Поле выбора значения форма диалогового окна заполнятся значениями,
     * описанными при задании шаблона в методе {@link ExperimentController#initialize(URL, ResourceBundle)}.</p>
     *
     * @see ExperimentController#template
     * @see ExperimentController#initialize(URL, ResourceBundle)
     */
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

    /**
     * <p>Класс, описывающий объекты для компактной группировки
     * элементов формы, относящихся к опреденному измерению начальных условий для варьируемой частицы.
     * Также используется для создания объектов {@link Iteration}.</p>
     *
     * @see Iteration
     */
    private class Dimension {
        /**
         * <p>Название измерения.</p>
         */
        private String name;
        /**
         * <p>Флажек для включения итерации по измерению.</p>
         *
         * @see CheckBox
         */
        private CheckBox change;
        /**
         * <p>Метка для отображения начального значение в итерации по измерению.</p>
         *
         * @see Label
         */
        private Label labelFrom;
        /**
         * <p>Поле ввода начального значение в итерации по измерению.</p>
         *
         * @see TextField
         */
        private TextField from;
        /**
         * <p>Поле ввода конечного значение в итерации по измерению.</p>
         *
         * @see TextField
         */
        private TextField to;
        /**
         * <p>Поле ввода шага в итерации по измерению.</p>
         *
         * @see TextField
         */
        private TextField step;
        /**
         * <p>Действие, которое необходимо выполнить с объектом {@link MotionEquationData}
         * и текущим вещественным значением в процессе итерации.</p>
         *
         * @see MotionEquationData
         * @see BiConsumer
         */
        private BiConsumer<MotionEquationData, Double> action;

        public Dimension(String name, CheckBox change, Label labelFrom,
                         TextField from, TextField to, TextField step,
                         BiConsumer<MotionEquationData, Double> action) {
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
