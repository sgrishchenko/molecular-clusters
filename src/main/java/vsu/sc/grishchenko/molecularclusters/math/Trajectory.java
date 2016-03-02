package vsu.sc.grishchenko.molecularclusters.math;

import java.util.List;

public class Trajectory {
    private String label;
    private Color color = new Color(0.6, 0, 0, 1);
    private List<Double> path;

    public Trajectory(String label, Color color, List<Double> path) {
        this.label = label;
        this.color = color;
        this.path = path;
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

    public List<Double> getPath() {
        return path;
    }

    public void setPath(List<Double> path) {
        this.path = path;
    }
}
