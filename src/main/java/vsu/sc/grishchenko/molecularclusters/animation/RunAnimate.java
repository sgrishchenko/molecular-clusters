package vsu.sc.grishchenko.molecularclusters.animation;

import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import vsu.sc.grishchenko.molecularclusters.math.Trajectory3D;
import vsu.sc.grishchenko.molecularclusters.view.ColorAdapter;
import vsu.sc.grishchenko.molecularclusters.view.Xform;

import java.util.*;

import static javafx.application.Platform.runLater;

public class RunAnimate implements Runnable {
    private static double RADIUS;
    private static double TAIL_RADIUS;

    private Map<Group, List<Point3D>> atoms;
    private ObservableList<Node> nodes;
    private int timeStep;
    private Long numberSteps;
    private double scale;
    private int timePointer = 0;
    private boolean isPaused = false;
    private boolean isStopped = false;
    private final Stack<List<Sphere>> frames = new Stack<>();

    public RunAnimate(List<Trajectory3D> trajectories, Xform root, int timeStep, double scale) {
        this.timeStep = timeStep;
        this.scale = scale;
        nodes = root.getChildren();
        atoms = new HashMap<>(trajectories.size());

        RADIUS = 0.5 * scale;
        TAIL_RADIUS = 0.3 * scale;

        Point3D point;
        for (Trajectory3D t : trajectories) {
            point = t.getPath().get(0);
            Sphere atom = createAtom(point.getX() * scale, point.getY() * scale, point.getZ() * scale,
                    RADIUS, ColorAdapter.from(t.getColor()), ColorAdapter.from(t.getColor()).darker());

            Text text = new Text(t.getLabel());
            text.setFont(new Font(12));
            text.setFill(Color.LIME);
            text.setTranslateX(point.getX() * scale + RADIUS);
            text.setTranslateY(point.getY() * scale + RADIUS);
            text.setTranslateZ(point.getZ() * scale + RADIUS);

            Group group = new Group(atom, text);
            nodes.add(group);
            atoms.put(group, t.getPath());
        }
        numberSteps = trajectories.stream().mapToLong(t -> (long) t.getPath().size()).max().getAsLong();
    }

    @Override
    public void run() {
        while (!isStopped) {
            try {
                Thread.sleep(timeStep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!isPaused) runLater(this::next);
        }
    }

    private Sphere createAtom(double X, double Y, double Z, double radius, Color specular, Color diffuse) {
        final PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(diffuse);
        material.setSpecularColor(specular);

        Sphere atom = new Sphere(radius);
        atom.setMaterial(material);
        atom.setTranslateX(X);
        atom.setTranslateY(Y);
        atom.setTranslateZ(Z);

        return atom;
    }

    public void play() {
        isPaused = false;
    }

    public void pause() {
        isPaused = true;
    }

    private List<Sphere> updateAtomsPosition(boolean addTail) {
        Double[] oldCoordinates = new Double[3];
        List<Sphere> frame = new ArrayList<>();

        for (Map.Entry<Group, List<Point3D>> group : atoms.entrySet()) {
            List<Point3D> trajectory = group.getValue();
            for (Node node : group.getKey().getChildren()) {
                oldCoordinates[0] = node.getTranslateX();
                oldCoordinates[1] = node.getTranslateY();
                oldCoordinates[2] = node.getTranslateZ();

                node.setTranslateX(trajectory.get(timePointer).getX() * scale);
                node.setTranslateY(trajectory.get(timePointer).getY() * scale);
                node.setTranslateZ(trajectory.get(timePointer).getZ() * scale);

                if (node instanceof Text) {
                    node.setTranslateX(node.getTranslateX() + RADIUS);
                    node.setTranslateY(node.getTranslateY() + RADIUS);
                    node.setTranslateZ(node.getTranslateZ() + RADIUS);
                    continue;
                }

                if (!addTail) continue;
                if (oldCoordinates[0] != node.getTranslateX()
                        || oldCoordinates[1] != node.getTranslateY()
                        || oldCoordinates[2] != node.getTranslateZ()) {

                    Sphere tail = createAtom(oldCoordinates[0], oldCoordinates[1], oldCoordinates[2],
                            TAIL_RADIUS, Color.GREY, Color.DARKGRAY);
                    nodes.add(tail);
                    frame.add(tail);
                }
            }
        }

        return frame;
    }

    public void next() {
        if (timePointer >= numberSteps - 1) {
            isPaused = true;
            return;
        }
        timePointer++;

        frames.push(updateAtomsPosition(true));
    }

    public void prev() {
        if (timePointer < 1) return;
        timePointer--;

        updateAtomsPosition(false);
        frames.pop().forEach(nodes::remove);
    }

    public void reset() {
        timePointer = 0;
        frames.stream().flatMap(Collection::stream).forEach(nodes::remove);
        frames.clear();
        updateAtomsPosition(false);
    }

    public void stop() {
        isStopped = true;
    }
}
