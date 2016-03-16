package vsu.sc.grishchenko.molecularclusters.animation;

import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import vsu.sc.grishchenko.molecularclusters.math.Trajectory3D;
import vsu.sc.grishchenko.molecularclusters.view.ColorAdapter;
import vsu.sc.grishchenko.molecularclusters.view.Xform;

import java.util.*;

import static javafx.application.Platform.runLater;

public class RunAnimate implements Runnable {
    private Map<Sphere, List<Point3D>> atoms;
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

        Point3D point;
        for (Trajectory3D t : trajectories) {
            point = t.getPath().get(0);
            Sphere atom = createAtom(point.getX() * scale, point.getY() * scale, point.getZ() * scale,
                    0.5 * scale, ColorAdapter.from(t.getColor()), ColorAdapter.from(t.getColor()).darker());
            nodes.add(atom);
            atoms.put(atom, t.getPath());
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

    private Sphere createAtom(double X, double Y, double Z, double diameter, Color specular, Color diffuse) {
        final PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(diffuse);
        material.setSpecularColor(specular);

        Sphere atom = new Sphere(diameter);
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

        for (Map.Entry<Sphere, List<Point3D>> shape : atoms.entrySet()) {
            Sphere atom = shape.getKey();
            oldCoordinates[0] = atom.getTranslateX();
            oldCoordinates[1] = atom.getTranslateY();
            oldCoordinates[2] = atom.getTranslateZ();

            atom.setTranslateX(shape.getValue().get(timePointer).getX() * scale);
            atom.setTranslateY(shape.getValue().get(timePointer).getY() * scale);
            atom.setTranslateZ(shape.getValue().get(timePointer).getZ() * scale);

            if (!addTail) continue;
            if (oldCoordinates[0] != atom.getTranslateX()
                    || oldCoordinates[1] != atom.getTranslateY()
                    || oldCoordinates[2] != atom.getTranslateZ()) {

                Sphere tail = createAtom(oldCoordinates[0], oldCoordinates[1], oldCoordinates[2],
                        0.3 * scale, Color.GREY, Color.DARKGRAY);
                nodes.add(tail);
                frame.add(tail);
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
