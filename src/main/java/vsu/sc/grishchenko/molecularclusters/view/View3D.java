package vsu.sc.grishchenko.molecularclusters.view;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import vsu.sc.grishchenko.molecularclusters.animation.RunAnimate;
import vsu.sc.grishchenko.molecularclusters.database.SaveTrajectoriesDialog;
import vsu.sc.grishchenko.molecularclusters.entity.TrajectoryListEntity;
import vsu.sc.grishchenko.molecularclusters.experiment.Analyzer;
import vsu.sc.grishchenko.molecularclusters.math.Trajectory;
import vsu.sc.grishchenko.molecularclusters.math.Trajectory3D;
import vsu.sc.grishchenko.molecularclusters.settings.CurrentSettings;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static javafx.application.Platform.runLater;

/**
 * <p>Класс для отображения трехмерной анимированной визуализации.</p>
 *
 * @author Грищенко Сергей
 * @see Application
 */
public class View3D extends Application {
    /**
     * <p>Корневой объект для создания всех элементов формы визуализации</p>
     *
     * @see Group
     */
    final Group root = new Group();
    /**
     * <p>Объект в котором содержатся трехмерные объекты для отображения осей координат.</p>
     *
     * @see Xform
     */
    final Xform axisGroup = new Xform();
    /**
     * <p>Объект в котором содержатся трехмерные объекты для отображения частиц.</p>
     *
     * @see Xform
     */
    final Xform moleculeGroup = new Xform();
    /**
     * <p>Объект в котором содержатся все трехмерные объекты, отображаемые при визуализации.</p>
     *
     * @see Xform
     */
    final Xform world = new Xform();
    /**
     * <p>Объект-камера.</p>
     *
     * @see PerspectiveCamera
     */
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    /**
     * <p>Первый срез корневого объекта для обеспечения независимости при взаимодействии с объектами этого среза.</p>
     *
     * @see Xform
     */
    final Xform cameraXform = new Xform();
    /**
     * <p>Второй срез корневого объекта для обеспечения независимости при взаимодействии с объектами этого среза.</p>
     *
     * @see Xform
     */
    final Xform cameraXform2 = new Xform();
    /**
     * <p>Третий срез корневого объекта для обеспечения независимости при взаимодействии с объектами этого среза.</p>
     *
     * @see Xform
     */
    final Xform cameraXform3 = new Xform();
    /**
     * <p>Нить, в которой будет выполняться анимация.</p>
     *
     * @see Thread
     */
    private Thread animationThread;
    /**
     * <p>Объект, описывающий анимацию.</p>
     *
     * @see RunAnimate
     */
    private RunAnimate animation;
    /**
     * <p>Объект для сериализации/десериализции Java-объктов
     * в фомате <a href="https://ru.wikipedia.org/wiki/JSON">JSON</a>.</p>
     *
     * @see Gson
     */
    public static Gson gson = new Gson();
    /**
     * <p>Константа, в которой хранится начальное отдаление для {@link View3D#camera}.</p>
     */
    private static final double CAMERA_INITIAL_DISTANCE = -450;
    /**
     * <p>Константа, в которой хранится угол поворота вокруг оси X для {@link View3D#camera}.</p>
     */
    private static final double CAMERA_INITIAL_X_ANGLE = 30.0;
    /**
     * <p>Константа, в которой хранится угол поворота вокруг оси Y для {@link View3D#camera}.</p>
     */
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    /**
     * <p>Константа, в которой хранится значение для определения {@link Camera#nearClip} в {@link View3D#camera}.</p>
     */
    private static final double CAMERA_NEAR_CLIP = 0.1;
    /**
     * <p>Константа, в которой хранится значение для определения {@link Camera#farClip} в {@link View3D#camera}.</p>
     */
    private static final double CAMERA_FAR_CLIP = 10000.0;
    /**
     * <p>Константа, в которой хранится значение длины координатных осей.</p>
     */
    private static final double AXIS_LENGTH = 250.0;
    /**
     * <p>Константа, в которой хранится множитель, изпользуемый при манипцляциях с визуализацией,
     * когда пользователь зажал клавишу ctrl.</p>
     */
    private static final double CONTROL_MULTIPLIER = 0.1;
    /**
     * <p>Константа, в которой хранится множитель, изпользуемый при манипцляциях с визуализацией,
     * когда пользователь зажал клавишу shift.</p>
     */
    private static final double SHIFT_MULTIPLIER = 10.0;
    /**
     * <p>Константа для регулирования скорости отклика на движение мыши при манипуляциях с визуализацией.</p>
     */
    private static final double MOUSE_SPEED = 0.1;
    /**
     * <p>Константа для регулирования скорости вращения вокруг осей при манипуляциях с визуализацией.</p>
     */
    private static final double ROTATION_SPEED = 1.0;
    /**
     * <p>Константа для регулирования скорости перетаскивания объектов при манипуляциях с визуализацией.</p>
     */
    private static final double TRACK_SPEED = 3.0;
    /**
     * <p>Константа для регулирования масштаба отображения объектов.</p>
     */
    private static final double SCALE = 20;

    /**
     * <p>Переменная для хранения текущего положения мыши (координата X).</p>
     */
    double mousePosX;
    /**
     * <p>Переменная для хранения текущего положения мыши (координата Y).</p>
     */
    double mousePosY;
    /**
     * <p>Переменная для хранения предыдущего положения мыши (координата X).</p>
     */
    double mouseOldX;
    /**
     * <p>Переменная для хранения предыдущего положения мыши (координата Y).</p>
     */
    double mouseOldY;
    /**
     * <p>Переменная для хранения изменения положения мыши (по координате X).</p>
     */
    double mouseDeltaX;
    /**
     * <p>Переменная для хранения изменения положения мыши (по координате Y).</p>
     */
    double mouseDeltaY;
    /**
     * <p>Переменная для того, чтобы зафиксифовать факт нажатия на кнопку мыши.</p>
     */
    boolean isButtonPressed;
    /**
     * <p>Список рассчитаных траекторий движения частиц.</p>
     *
     * @see Trajectory
     */
    private List<Trajectory> trajectories;

    /**
     * <p>Конструктор для запуска визуализации на основе переданных траекторий движения частиц.</p>
     *
     * @param trajectories {@link View3D#trajectories}
     * @see Trajectory
     */
    public View3D(List<Trajectory> trajectories) {
        this.trajectories = trajectories;
    }

    /**
     * <p>Метод, в котором объеты трехмерной визуализации распределяются по срезам,
     * и инициализируются параметры {@link View3D#camera}.</p>
     */
    private void buildCamera() {
        root.getChildren().add(world);
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);

        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
    }

    /**
     * <p>Метод для получения объекта, хранящего иконку кнопки из ресурсных файлов проекта.</p>
     *
     * @param name название ресурсного файла, в котором хранится изображение иконки
     * @return ссылку на объект, содержащий информацию об иконке.
     * @see ImageView
     */
    private ImageView getImageView(String name) {
        Image image = new Image(getClass().getResourceAsStream(String.format("/icons/%s.png", name)));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        return imageView;
    }

    /**
     * <p>Метод, в котором на форму визуализации добавляются кнопки,
     * и назначаются действия, которые будут выполняться при нажатии на них.</p>
     */
    private void buildButtons() {
        final int[] i = {-440};
        BiConsumer<Button, Consumer<RunAnimate>> rewindHandler = (button, action) -> {
            button.setOnMousePressed(e -> {
                isButtonPressed = true;
                new Thread(() -> {
                    while (isButtonPressed) {
                        runLater(() -> action.accept(animation));
                        try {
                            Thread.sleep(CurrentSettings.getInstance().getAnimateStepSize());
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();
            });

            button.setOnMouseReleased(e -> isButtonPressed = false);
        };

        new LinkedHashMap<String, Consumer<Button>>() {{
            put("save", button -> button.setOnAction(e -> {
                try {
                    new SaveTrajectoriesDialog(new TrajectoryListEntity("",
                            gson.toJson(trajectories),
                            Analyzer.getParams(trajectories))).start(new Stage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }));
            put("play", button -> button.setOnAction(e -> animation.play()));
            put("pause", button -> button.setOnAction(e -> animation.pause()));
            put("reset", button -> button.setOnAction(e -> animation.reset()));
            put("prev", button -> button.setOnAction(e -> animation.prev()));
            put("next", button -> button.setOnAction(e -> animation.next()));
            put("prevRewind", button -> rewindHandler.accept(button, RunAnimate::prev));
            put("nextRewind", button -> rewindHandler.accept(button, RunAnimate::next));
        }}.forEach((name, handler) -> {
            Button button = new Button();
            button.setGraphic(getImageView(name));
            button.setTranslateZ(-CAMERA_INITIAL_DISTANCE);
            handler.accept(button);
            button.setLayoutX(i[0] += 40);
            button.setLayoutY(-230);
            cameraXform3.getChildren().add(button);
        });
    }

    /**
     * <p>Метод, в котором строятся оси координат.</p>
     */
    private void buildAxes() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);

        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);

        final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
        final Box yAxis = new Box(1, AXIS_LENGTH, 1);
        final Box zAxis = new Box(1, 1, AXIS_LENGTH);

        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);

        axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        axisGroup.setVisible(true);
        world.getChildren().addAll(axisGroup);
    }

    /**
     * <p>Метод, в котором на основе {@link View3D#trajectories} запускается анимация.</p>
     *
     * @see RunAnimate
     * @see Trajectory
     * @see Trajectory3D
     */
    private void buildMolecule() {
        animation = new RunAnimate(Trajectory3D.from(trajectories),
                moleculeGroup,
                CurrentSettings.getInstance().getAnimateStepSize(),
                SCALE);
        animationThread = new Thread(animation);
        animationThread.start();
        world.getChildren().addAll(moleculeGroup);

        world.lookupAll("Text").forEach(node -> {
            node.getTransforms().add(new Rotate(180, Rotate.Z_AXIS));
            node.getTransforms().add(new Rotate(-CAMERA_INITIAL_Y_ANGLE, Rotate.Y_AXIS));
            node.getTransforms().add(new Rotate(-CAMERA_INITIAL_X_ANGLE, Rotate.X_AXIS));
        });
    }

    /**
     * <p>Метод, в котором назначаются обработчики для событий мыши.</p>
     *
     * @param scene сцена, на которую назначаются обработчики событий
     * @see Scene
     * @see EventHandler
     */
    private void handleMouse(Scene scene) {

        scene.setOnMousePressed(me -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
        });
        scene.setOnMouseDragged(me -> {
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);

            double modifier = 1.0;

            if (me.isControlDown()) {
                modifier = CONTROL_MULTIPLIER;
            }
            if (me.isShiftDown()) {
                modifier = SHIFT_MULTIPLIER;
            }
            if (me.isPrimaryButtonDown()) {
                double deltaX = mouseDeltaX * modifier * ROTATION_SPEED;
                double deltaY = mouseDeltaY * modifier * ROTATION_SPEED;

                world.lookupAll("Text").forEach(node -> {
                    Rotate ry = (Rotate) node.getTransforms().get(1);
                    Rotate rx = (Rotate) node.getTransforms().get(2);

                    ry.setAngle(ry.getAngle() + deltaX);
                    rx.setAngle(rx.getAngle() - deltaY);
                });

                cameraXform.ry.setAngle(cameraXform.ry.getAngle() - deltaX);
                cameraXform.rx.setAngle(cameraXform.rx.getAngle() + deltaY);

            } else if (me.isSecondaryButtonDown()) {
                double z = cameraXform2.getTranslateZ();
                double delta = Math.max(Math.abs(mouseDeltaX), Math.abs(mouseDeltaY));
                if (delta == Math.abs(mouseDeltaX) && mouseDeltaX < 0) modifier *= -1;
                if (delta == Math.abs(mouseDeltaY) && mouseDeltaY < 0) modifier *= -1;
                double newZ = z + delta * MOUSE_SPEED * modifier * 10;
                cameraXform2.setTranslateZ(newZ);
            } else if (me.isMiddleButtonDown()) {
                cameraXform2.t.setX(cameraXform2.t.getX() +
                        mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
                cameraXform2.t.setY(cameraXform2.t.getY() +
                        mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
            }
        });
    }

    /**
     * <p>Метод, в котором назначаются обработчики для событий клавиатуры.</p>
     *
     * @param scene сцена, на которую назначаются обработчики событий
     * @see Scene
     * @see EventHandler
     */
    private void handleKeyboard(Scene scene) {

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case PLUS:
                case EQUALS:
                    camera.setTranslateZ(cameraXform2.getTranslateZ() + 10);
                    break;
                case MINUS:
                case UNDERSCORE:
                    camera.setTranslateZ(cameraXform2.getTranslateZ() - 10);
                    break;
                case Z:
                    cameraXform2.t.setX(0.0);
                    cameraXform2.t.setY(0.0);

                    world.lookupAll("Text").forEach(node -> {
                        Rotate ry = (Rotate) node.getTransforms().get(1);
                        Rotate rx = (Rotate) node.getTransforms().get(2);

                        ry.setAngle(-CAMERA_INITIAL_Y_ANGLE);
                        rx.setAngle(-CAMERA_INITIAL_X_ANGLE);
                    });

                    cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
                    cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
                    break;
                case X:
                    axisGroup.setVisible(!axisGroup.isVisible());
                    break;
                case V:
                    moleculeGroup.setVisible(!moleculeGroup.isVisible());
                    break;
                case C:
                    world.lookupAll("Text").forEach(node -> node.setVisible(!node.isVisible()));
                    break;
            }
        });
    }

    @Override
    public void start(Stage primaryStage) {
        buildCamera();
        buildButtons();
        buildAxes();
        buildMolecule();

        Scene scene = new Scene(root, 1024, 600, true);
        scene.setFill(Color.WHITE);
        handleKeyboard(scene);
        handleMouse(scene);

        primaryStage.setTitle("Трехмерная анимированная модель");
        primaryStage.setScene(scene);

        scene.setCamera(camera);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            animation.stop();
            try {
                animationThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        primaryStage.setResizable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
