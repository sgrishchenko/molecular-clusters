package vsu.sc.grishchenko.molecularclusters.math;

import java.util.List;

/**
 * <p>Класс для описания траетории движения частицы в одномерном пространстве.</p>
 *
 * @author Грищенко Сергей
 */
public class Trajectory {
    /**
     * <p>Текстовая метка частицы.</p>
     */
    private String label;
    /**
     * <p>Цвет частицы.</p>
     */
    private Color color = new Color(0.6, 0, 0, 1);
    /**
     * <p>Список вещественных значений, описывающий траекторию движения.</p>
     */
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
