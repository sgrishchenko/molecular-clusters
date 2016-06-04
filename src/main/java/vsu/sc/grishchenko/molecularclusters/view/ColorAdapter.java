package vsu.sc.grishchenko.molecularclusters.view;

import vsu.sc.grishchenko.molecularclusters.math.Color;

/**
 * <p>Класс, который позволяет конвертировать
 * класс {@link Color}
 * в класс {@link javafx.scene.paint.Color} и наоборот.</p>
 *
 * @author Грищенко Сергей
 * @see Color
 * @see javafx.scene.paint.Color
 */
public final class ColorAdapter {
    public static Color from(javafx.scene.paint.Color color) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getOpacity());
    }

    public static javafx.scene.paint.Color from(Color color) {
        return new javafx.scene.paint.Color(color.getRed(), color.getGreen(), color.getBlue(), color.getOpacity());
    }
}
