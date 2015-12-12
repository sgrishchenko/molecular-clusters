package vsu.sc.grishchenko.molecularclusters.animation;

import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import vsu.sc.grishchenko.molecularclusters.view.Xform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javafx.application.Platform.runLater;

public class RunAnimate implements Runnable {
    private Map<Sphere, List<Point3D>> atoms;
    private ObservableList<Node> nodes;
    private int timeStep;
    private Long numberSteps;
    private double scale;

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
        Double[] oldCoordinates = new Double[3];
        for (int i = 0; i < numberSteps; i++) {
            try {
                Thread.sleep(timeStep);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            final int stepNumber = i;
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

                        nodes.add(createAtom(oldCoordinates[0], oldCoordinates[1], oldCoordinates[2],
                                0.3 * scale, Color.GREY, Color.DARKGRAY));
                    }
                }
            });
        }
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
}
