package vsu.sc.grishchenko.molecularclusters.view;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import vsu.sc.grishchenko.molecularclusters.GlobalSettings;
import vsu.sc.grishchenko.molecularclusters.animation.RunAnimate;
import vsu.sc.grishchenko.molecularclusters.math.Trajectory;
import vsu.sc.grishchenko.molecularclusters.math.Trajectory3D;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static javafx.application.Platform.runLater;

public class View3D extends Application {

    final Group root = new Group();
    final Xform axisGroup = new Xform();
    final Xform moleculeGroup = new Xform();
    final Xform world = new Xform();
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    public FileChooser fileChooser = new FileChooser();
    private Thread animationThread;
    private RunAnimate animation;
    public static Gson gson = new Gson();
    private static final double CAMERA_INITIAL_DISTANCE = -450;
    private static final double CAMERA_INITIAL_X_ANGLE = 60.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    private static final double AXIS_LENGTH = 250.0;

    private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 2.0;
    private static final double TRACK_SPEED = 3.0;

    private static final double modifierFactor = 0.5;

    private static final double scale = 12;

    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;

    boolean isButtonPressed;

    List<Trajectory> trajectories;

    public View3D(List<Trajectory> trajectories) {
        this.trajectories = trajectories;
        fileChooser.setInitialDirectory(new File("."));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Текстовые файлы (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
    }

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

    private ImageView getImageView(String name) {
        Image image = new Image(getClass().getResourceAsStream(String.format("/icons/%s.png", name)));
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(16);
        imageView.setFitWidth(16);
        return imageView;
    }

    private void buildButtons() {
        final int[] i = {-440};
        BiConsumer<Button, Consumer<RunAnimate>> rewindHandler = (button, action) -> {
            button.setOnMousePressed(e -> {
                isButtonPressed = true;
                new Thread(() -> {
                    while (isButtonPressed) {
                        runLater(() -> action.accept(animation));
                        try {
                            Thread.sleep(GlobalSettings.getInstance().viewSettings.getAnimateStepSize());
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
                File file = fileChooser.showSaveDialog(root.getScene().getWindow());
                if (file == null) return;
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(gson.toJson(trajectories));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                MainController.updateTitle(root.getScene(), file);
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

    private void buildMolecule() {
        animation = new RunAnimate(Trajectory3D.from(trajectories),
                moleculeGroup,
                GlobalSettings.getInstance().viewSettings.getAnimateStepSize(),
                scale);
        animationThread = new Thread(animation);
        animationThread.start();
        world.getChildren().addAll(moleculeGroup);
    }

    private void handleMouse(Scene scene, final Node root) {

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
                cameraXform.ry.setAngle(cameraXform.ry.getAngle() -
                        mouseDeltaX * modifierFactor * modifier * ROTATION_SPEED);  //
                cameraXform.rx.setAngle(cameraXform.rx.getAngle() +
                        mouseDeltaY * modifierFactor * modifier * ROTATION_SPEED);  // -
            } else if (me.isSecondaryButtonDown()) {
                double z = cameraXform2.getTranslateZ();
                double delta = Math.max(Math.abs(mouseDeltaX), Math.abs(mouseDeltaY));
                if (delta == Math.abs(mouseDeltaX) && mouseDeltaX < 0) modifier *= -1;
                if (delta == Math.abs(mouseDeltaY) && mouseDeltaY < 0) modifier *= -1;
                double newZ = z + delta * MOUSE_SPEED * modifier * 10;
                cameraXform2.setTranslateZ(newZ);
            } else if (me.isMiddleButtonDown()) {
                cameraXform2.t.setX(cameraXform2.t.getX() +
                        mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);  // -
                cameraXform2.t.setY(cameraXform2.t.getY() +
                        mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);  // -
            }
        }); // setOnMouseDragged
    } //handleMouse

    private void handleKeyboard(Scene scene, final Node root) {

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
                    cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
                    cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
                    break;
                case X:
                    axisGroup.setVisible(!axisGroup.isVisible());
                    break;
                case V:
                    moleculeGroup.setVisible(!moleculeGroup.isVisible());
                    break;
            } // switch
        });  // setOnKeyPressed
    }  //  handleKeyboard()

    @Override
    public void start(Stage primaryStage) {
        buildCamera();
        buildButtons();
        buildAxes();
        buildMolecule();

        Scene scene = new Scene(root, 1024, 600, true);
        scene.setFill(Color.WHITE);
        handleKeyboard(scene, world);
        handleMouse(scene, world);

        primaryStage.setTitle("Molecular Clusters");
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
    }

    public static void main(String[] args) {
        launch(args);
    }
}
