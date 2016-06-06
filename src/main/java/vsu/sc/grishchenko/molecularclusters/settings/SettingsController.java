package vsu.sc.grishchenko.molecularclusters.settings;

import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * <p>Класс-контроллер для обработки событий, происходящих в диалоговом окне настроек приложения.</p>
 *
 * @author Грищенко Сергей
 * @see Settings
 * @see Initializable
 */
public class SettingsController implements Initializable {
    /**
     * <p>Основной контейнер диалогового окна.</p>
     *
     * @see AnchorPane
     */
    public AnchorPane root;
    /**
     * <p>Поле ввода количества шагов при выполнении алгоритма численного интегрирования.</p>
     *
     * @see CurrentSettings#countSteps
     * @see TextField
     */
    public TextField viewCountSteps;
    /**
     * <p>Поле ввода величина сдвига по времени при выполнении алгоритма численного интегрирования.</p>
     *
     * @see CurrentSettings#stepSize
     * @see TextField
     */
    public TextField viewStepSize;
    /**
     * <p>Ползунок для задания время между шагами анимации при демонстрации трехмерной анимированной модели.</p>
     *
     * @see CurrentSettings#animateStepSize
     * @see TextField
     */
    public Slider animateStepSize;

    /**
     * <p>Инициализация формы диалогового окна на основе текущих значений в объекте,
     * полученном с помощью метода {@link CurrentSettings#getInstance()}</p>
     *
     * @see CurrentSettings
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CurrentSettings currentSettings = CurrentSettings.getInstance();

        viewCountSteps.setText(currentSettings.getCountSteps().toString());
        viewStepSize.setText(currentSettings.getStepSize().toString());

        Double timeStep = (30 - currentSettings.getAnimateStepSize()) / 30. * 100;
        animateStepSize.setValue(timeStep);
    }

    /**
     * <p>Обработчик события при нажатии на кнопку "OK" в диалоговом окне.
     * Сохраняет данные, введенные на форме диалогового окна, как текущие настройки.</p>
     */
    public void setSettings() {
        CurrentSettings currentSettings = CurrentSettings.getInstance();

        currentSettings.setCountSteps(Integer.parseInt(viewCountSteps.getText()));
        currentSettings.setStepSize(Double.parseDouble(viewStepSize.getText()));

        Integer timeStep = 30 - new Long(Math.round(animateStepSize.getValue() / 100 * 30)).intValue();
        currentSettings.setAnimateStepSize(timeStep);

        cancelSettings();
    }

    /**
     * <p>Обработчик события при нажатии на кнопку "Отмена" в диалоговом окне.
     * Закрывает диалоговое окно.</p>
     */
    public void cancelSettings() {
        ((Stage) root.getScene().getWindow()).close();
    }
}
