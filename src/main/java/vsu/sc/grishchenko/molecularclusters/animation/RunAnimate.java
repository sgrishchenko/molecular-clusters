package vsu.sc.grishchenko.molecularclusters.animation;

import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
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
    private int oldTimePointer = 0;
    private boolean isPaused = false;
    private boolean isStopped = false;
    private boolean isUpdated = false;
    private Stack<List<Sphere>> frames = new Stack<>();

    public RunAnimate(List<List<Point3D>> trajectories, Xform root, int timeStep, double scale) {
        this.timeStep = timeStep;
        this.scale = scale;
        nodes = root.getChildren();
        atoms = new HashMap<>(trajectories.size());

        Point3D point;
        for (List<Point3D> t : trajectories) {
            point = t.get(0);
            Sphere atom = createAtom(point.getX() * scale, point.getY() * scale, point.getZ() * scale,
                    0.5 * scale, Color.RED, Color.DARKRED);
            nodes.add(atom);
            atoms.put(atom, t);
        }
        numberSteps = trajectories.stream().mapToLong(t -> (long) t.size()).max().getAsLong();
    }

    @Override
    public void run() {
        while (!isStopped) {
            if (!isUpdated) {
                try {
                    Thread.sleep(timeStep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                isUpdated = false;
            }
            if (!isPaused && timePointer < numberSteps) {
                timePointer++;
            }
            if (timePointer != oldTimePointer) actualize();
        }
    }

    private void actualize() {
        if (timePointer >= numberSteps) {
            isPaused = true;
            timePointer = numberSteps.intValue();
            return;
        }

        List<Sphere> frame = new ArrayList<>();
        Double[] oldCoordinates = new Double[3];

        final int stepNumber = timePointer;
        final int oldStepNumber = oldTimePointer;

        runLater(() -> {
            for (Map.Entry<Sphere, List<Point3D>> shape : atoms.entrySet()) {
                if (stepNumber >= shape.getValue().size()) continue;

                Sphere atom = shape.getKey();
                oldCoordinates[0] = atom.getTranslateX();
                oldCoordinates[1] = atom.getTranslateY();
                oldCoordinates[2] = atom.getTranslateZ();

                atom.setTranslateX(shape.getValue().get(stepNumber).getX() * scale);
                atom.setTranslateY(shape.getValue().get(stepNumber).getY() * scale);
                atom.setTranslateZ(shape.getValue().get(stepNumber).getZ() * scale);

                if (oldCoordinates[0] != atom.getTranslateX()
                        || oldCoordinates[1] != atom.getTranslateY()
                        || oldCoordinates[2] != atom.getTranslateZ()) {

                    if (stepNumber > oldStepNumber) {
                        Sphere tail = createAtom(oldCoordinates[0], oldCoordinates[1], oldCoordinates[2],
                                0.3 * scale, Color.GREY, Color.DARKGRAY);
                        nodes.add(tail);
                        frame.add(tail);
                    }
                }
            }
            if (!frame.isEmpty()) frames.push(frame);
            if (stepNumber < oldStepNumber && !frames.isEmpty()) {
                frames.pop().forEach(nodes::remove);
            }
        });

        oldTimePointer = timePointer;
    }

    private Sphere createAtom(double X, double Y, double Z, double diameter, Color specular, Color diffuse) {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(diffuse);
        redMaterial.setSpecularColor(specular);

        Sphere atom = new Sphere(diameter);
        atom.setMaterial(redMaterial);
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

    public void next() {
        timePointer++;
        isUpdated = true;
    }

    public void prev() {
        timePointer--;
        isUpdated = true;
    }

    public void reset() {
        timePointer = 0;
        frames.stream().flatMap(Collection::stream).forEach(nodes::remove);
        frames.clear();
    }

    public void stop() {
        isStopped = true;
    }
}
