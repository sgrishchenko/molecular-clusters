package vsu.sc.grishchenko.molecularclusters.math;

/**
 * <p>Класс для описания цвета частицы. Создан, чтобы не хранить в базе данных
 * и строках в формате <a href="https://ru.wikipedia.org/wiki/JSON">JSON</a> лишней информации.</p>
 *
 * @author Грищенко Сергей
 */
public class Color {
    double red;
    double green;
    double blue;
    double opacity;

    public Color(double red, double green, double blue, double opacity) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.opacity = opacity;
    }

    public double getRed() {
        return red;
    }

    public void setRed(double red) {
        this.red = red;
    }

    public double getGreen() {
        return green;
    }

    public void setGreen(double green) {
        this.green = green;
    }

    public double getBlue() {
        return blue;
    }

    public void setBlue(double blue) {
        this.blue = blue;
    }

    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }
}
