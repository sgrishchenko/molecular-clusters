package vsu.sc.grishchenko.molecularclusters.math;

import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Класс для описания траектории движения частицы в трехмерном пространстве.</p>
 *
 * @author Грищенко Сергей
 */
public class Trajectory3D {
    /**
     * <p>Текстовая метка частицы.</p>
     */
    private String label;
    /**
     * <p>Цвет частицы.</p>
     */
    private Color color = new Color(0.6, 0, 0, 1);
    /**
     * <p>Список объектов {@link Point3D}, описывающих точки трехмерного пространства
     * траектории движения частицы.</p>
     */
    private List<Point3D> path;

    public Trajectory3D(String label, Color color, List<Point3D> path) {
        this.label = label;
        this.color = color;
        this.path = path;
    }

    public static List<Trajectory3D> from(List<Trajectory> trajectories) {
        Set<String> labels = trajectories.stream()
                .map(t -> t.getLabel().replaceAll("[xyz]", ""))
                .collect(Collectors.toSet());

        Map<String, Trajectory> trajectoryMap = trajectories.stream()
                .collect(Collectors.toMap(Trajectory::getLabel, Function.identity()));

        Trajectory trajectoryExample;
        List<Point3D> trajectory;
        List<Trajectory3D> result = new ArrayList<>();

        for (String label : labels) {
            trajectory = new ArrayList<>();
            trajectoryExample = trajectoryMap.get("x" + label);
            for (int i = 0; i < trajectoryExample.getPath().size(); i++) {
                trajectory.add(new Point3D(trajectoryMap.get("x" + label).getPath().get(i),
                        trajectoryMap.get("z" + label).getPath().get(i),
                        -trajectoryMap.get("y" + label).getPath().get(i)));
            }
            result.add(new Trajectory3D(label, trajectoryExample.getColor(), trajectory));
        }

        return result;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<Point3D> getPath() {
        return path;
    }

    public void setPath(List<Point3D> path) {
        this.path = path;
    }
}
