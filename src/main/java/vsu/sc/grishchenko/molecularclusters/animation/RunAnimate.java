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

import java.util.*;

import static javafx.application.Platform.runLater;

/**
 * <p>Класс для управления воспроизвелением анимации
 * при демонстрации трехмерной анимированной модели.</p>
 *
 * @author Грищенко Сергей
 * @see Runnable
 */
public class RunAnimate implements Runnable {
    /**
     * <p>Радиус сфер, которые символизируют частицы модели.</p>
     */
    private double radius;
    /**
     * <p>Радиус сфер, которые символизируют следы траекторий, оставленные частицами модели.</p>
     */
    private double tailRadius;
    /**
     * <p>Карта, сопоставляющая элементы для моделирования частиц и траектории их движения.
     * Ключи - объекты {@link Group}, каждый из которых содержит внутри
     * {@link Sphere}(сама частица) и {@link Text}(её строковое обозначение).
     * Значения - списоки точек трехмерного простанства, в которых положения частицы
     * хранятся в соответствии с хронологическим порядком.</p>
     *
     * @see Group
     * @see Sphere
     * @see Text
     * @see Point3D
     */
    private Map<Group, List<Point3D>> atoms;
    /**
     * <p>Список, в котором хранятся все элементы трехмерной модели.</p>
     *
     * @see ObservableList
     * @see Node
     */
    private ObservableList<Node> nodes;
    /**
     * <p>Время в миллисекундах между шагами анимации.</p>
     */
    private int timeStep;
    /**
     * <p>Количество шагов анимации.</p>
     */
    private long numberSteps;
    /**
     * <p>Масштаб отображения объектов анимации.</p>
     */
    private double scale;
    /**
     * <p>Номер текущей временной метки воспроизведения анимации.</p>
     */
    private int timePointer = 0;
    /**
     * <p>Флаг, при устаовке которого в состоянии <code>true</code> воспроизведение приостанавливается.</p>
     */
    private boolean isPaused = false;
    /**
     * <p>Флаг, при устаовке которого в состоянии <code>true</code> воспроизведение полность останавливается.</p>
     */
    private boolean isStopped = false;
    /**
     * <p>Стэк, в котором хранятся элементы, созданные на каждом шаге воспроизведения анимации.
     * Эти элементы - сферы, которые формируют траектрии движения частиц.
     * В общем случае на определнном шаге воспроизведения будет создано столько таких сфер,
     * сколько частиц в этот момент двигалось.</p>
     *
     * @see Stack
     * @see Sphere
     */
    private final Stack<List<Sphere>> frames = new Stack<>();

    /**
     * @param trajectories список траекторий частиц
     * @param root         контейнер {@link Group}, в который будут добавляться элементы визуализации модели
     * @param timeStep     параметр для инициализауии {@link RunAnimate#timeStep}
     * @param scale        параметр для инициализауии {@link RunAnimate#scale}
     * @see Trajectory3D
     * @see Group
     */
    public RunAnimate(List<Trajectory3D> trajectories, Group root, int timeStep, double scale) {
        this.timeStep = timeStep;
        this.scale = scale;
        nodes = root.getChildren();
        atoms = new HashMap<>(trajectories.size());

        radius = 0.5 * scale;
        tailRadius = 0.3 * scale;

        Point3D point;
        for (Trajectory3D t : trajectories) {
            point = t.getPath().get(0);
            Sphere atom = createAtom(point.getX() * scale, point.getY() * scale, point.getZ() * scale,
                    radius, ColorAdapter.from(t.getColor()));

            Text text = new Text(t.getLabel());
            text.setFont(new Font(12));
            text.setFill(Color.BLACK);
            text.setTranslateX(point.getX() * scale + radius);
            text.setTranslateY(point.getY() * scale + radius);
            text.setTranslateZ(point.getZ() * scale + radius);

            Group group = new Group(atom, text);
            nodes.add(group);
            atoms.put(group, t.getPath());
        }
        numberSteps = trajectories.stream().mapToLong(t -> (long) t.getPath().size()).max().getAsLong();
    }

    /**
     * <p>Метод, позволяющий запустить процесс воспроизведение.</p>
     */
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

    /**
     * <p>Метод создания частицы модели.</p>
     *
     * @param X      пространственная координата X
     * @param Y      пространственная координата Y
     * @param Z      пространственная координата Z
     * @param radius радиус частицы
     * @param color  цвет частицы
     * @return созданную частицу для визуализации.
     * @see Color
     * @see Sphere
     */
    private Sphere createAtom(double X, double Y, double Z, double radius, Color color) {
        final PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        material.setSpecularColor(color.darker());

        Sphere atom = new Sphere(radius);
        atom.setMaterial(material);
        atom.setTranslateX(X);
        atom.setTranslateY(Y);
        atom.setTranslateZ(Z);

        return atom;
    }

    /**
     * <p>Метод, который возобновляет воспроизведение.</p>
     */
    public void play() {
        isPaused = false;
    }

    /**
     * <p>Метод, который приостанавливает воспроизведение.</p>
     */
    public void pause() {
        isPaused = true;
    }

    /**
     * <p>Метод, который обновляет положения отображаемых частиц при воспроизведении.</p>
     *
     * @param addTail следует ли в рамках метода создавать частицы,
     *                которые символизируют следы траекторий, оставленные частицами модели.
     * @return список созданных в рамках обновления частиц.
     * Частицы будут созданы только, если параметр <code>addTail</code> будет иметь значение <code>true</code>.
     * В противном случае метод вернет пустой список.
     */
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
                    node.setTranslateX(node.getTranslateX() + radius);
                    node.setTranslateY(node.getTranslateY() + radius);
                    node.setTranslateZ(node.getTranslateZ() + radius);
                    continue;
                }

                if (!addTail) continue;
                if (oldCoordinates[0] != node.getTranslateX()
                        || oldCoordinates[1] != node.getTranslateY()
                        || oldCoordinates[2] != node.getTranslateZ()) {

                    Sphere tail = createAtom(oldCoordinates[0], oldCoordinates[1], oldCoordinates[2],
                            tailRadius, Color.GREY);
                    nodes.add(tail);
                    frame.add(tail);
                }
            }
        }

        return frame;
    }

    /**
     * <p>Метод, который переводит модель в следующее состояний.
     * Используется для пошагового воспроизведения.</p>
     */
    public void next() {
        if (timePointer >= numberSteps - 1) {
            isPaused = true;
            return;
        }
        timePointer++;

        frames.push(updateAtomsPosition(true));
    }

    /**
     * <p>Метод, который переводит модель в предыдущее состояний.
     * Используется для пошагового воспроизведения.</p>
     */
    public void prev() {
        if (timePointer < 1) return;
        timePointer--;

        updateAtomsPosition(false);
        frames.pop().forEach(nodes::remove);
    }

    /**
     * <p>Метод, который приостанавливает воспроизведение
     * и переводит модель в начальное положение.</p>
     */
    public void reset() {
        timePointer = 0;
        frames.stream().flatMap(Collection::stream).forEach(nodes::remove);
        frames.clear();
        updateAtomsPosition(false);
    }

    /**
     * <p>Метод, который полностью оставливает воспроизведение.
     * После его вызова дальнейшее воспроизвеение невозможно.</p>
     */
    public void stop() {
        isStopped = true;
    }
}
