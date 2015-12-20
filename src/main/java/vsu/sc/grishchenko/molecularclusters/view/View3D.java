package vsu.sc.grishchenko.molecularclusters.view;

import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.stage.Stage;
import vsu.sc.grishchenko.molecularclusters.GlobalSettings;
import vsu.sc.grishchenko.molecularclusters.animation.RunAnimate;
import vsu.sc.grishchenko.molecularclusters.math.MotionEquationData;
import vsu.sc.grishchenko.molecularclusters.math.Solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class View3D extends Application {

    final Group root = new Group();
    final Xform axisGroup = new Xform();
    final Xform moleculeGroup = new Xform();
    final Xform world = new Xform();
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
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
    private static final double TRACK_SPEED = 0.3;

    private static final double modifierFactor = 0.5;

    private static final double scale = 12;

    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;

    List<MotionEquationData> equations;
    Map<String, List<Double>> trajectories;

    public View3D(List<MotionEquationData> equations) {
        this.equations = equations;
    }

    public View3D(Map<String, List<Double>> trajectories) {
        this.trajectories = trajectories;
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
        GlobalSettings.ViewSettings settings = GlobalSettings.getInstance().viewSettings;

        Map<String, List<Double>> result = trajectories != null
                ? trajectories
                : Solver.solveVerlet(equations, 0., settings.getCountSteps(), settings.getStepSize());

        List<List<Point3D>> trajectories = new ArrayList<>();
        List<Point3D> points;
        Set<String> labels;
        if (equations != null) {
            labels = equations.stream()
                    .map(MotionEquationData::getLabel)
                    .collect(Collectors.toSet());
        } else {
            labels = this.trajectories.keySet().stream()
                    .map(key -> key.replaceAll("[xyz]", ""))
                    .collect(Collectors.toSet());
        }
        for (String label : labels) {
            points = new ArrayList<>();
            for (int i = 0; i < result.get("x" + label).size(); i++) {
                points.add(new Point3D(result.get("x" + label).get(i),
                        result.get("z" + label).get(i),
                        -result.get("y" + label).get(i)));
            }
            trajectories.add(points);
        }

        new Thread(new RunAnimate(trajectories, moleculeGroup, settings.getAnimateStepSize(), scale)).start();
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
                double z = camera.getTranslateZ();
                double newZ = z + Math.max(mouseDeltaX, mouseDeltaY) * MOUSE_SPEED * modifier;
                camera.setTranslateZ(newZ);
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
                    camera.setTranslateZ(camera.getTranslateZ() + 10);
                    break;
                case MINUS:
                case UNDERSCORE:
                    camera.setTranslateZ(camera.getTranslateZ() - 10);
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
    }

    public static void main(String[] args) {
        launch(args);
    }
}
