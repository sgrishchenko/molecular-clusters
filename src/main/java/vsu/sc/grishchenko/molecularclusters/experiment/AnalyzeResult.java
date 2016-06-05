package vsu.sc.grishchenko.molecularclusters.experiment;

/**
 * <p>Класс для хранения макроскопических параметров,
 * полученных на основе рассчитанных траекторий движения частиц модели.</p>
 *
 * @author Грищенко Сергей
 */
public class AnalyzeResult {
    /**
     * <p>Радиус нанотрубки.</p>
     */
    double radius;
    /**
     * <p>Начальный азимутальный угол.</p>
     */
    double fi;
    /**
     * <p>Начальный зенитный угол.</p>
     */
    double teta;
    /**
     * <p>Длина пройденного пути в нанотрубке.</p>
     */
    double pathLength;
    /**
     * <p>Отношение {@link AnalyzeResult#pathLength} к длине нанотрубки.</p>
     */
    double pathLengthToTubeLength;
    /**
     * <p>Средняя скорость движения.</p>
     */
    double avgSpeed;
    /**
     * <p>Средняя длина свободного пробега.</p>
     */
    double avgFreePath;
    /**
     * <p>Коэффициент диффузии.</p>
     */
    double diffusionCoeff;
    /**
     * <p>Азимутальный угол при вылете из трубки.</p>
     */
    double finalFi;
    /**
     * <p>Зенитный угол при вылете из трубки.</p>
     */
    double finalTeta;

    public AnalyzeResult(double radius, double fi, double teta) {
        this.radius = radius;
        this.fi = fi;
        this.teta = teta;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getFi() {
        return fi;
    }

    public void setFi(double fi) {
        this.fi = fi;
    }

    public double getTeta() {
        return teta;
    }

    public void setTeta(double teta) {
        this.teta = teta;
    }

    public double getPathLength() {
        return pathLength;
    }

    public void setPathLength(double pathLength) {
        this.pathLength = pathLength;
    }

    public double getPathLengthToTubeLength() {
        return pathLengthToTubeLength;
    }

    public void setPathLengthToTubeLength(double pathLengthToTubeLength) {
        this.pathLengthToTubeLength = pathLengthToTubeLength;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public double getAvgFreePath() {
        return avgFreePath;
    }

    public void setAvgFreePath(double avgFreePath) {
        this.avgFreePath = avgFreePath;
    }

    public double getDiffusionCoeff() {
        return diffusionCoeff;
    }

    public void setDiffusionCoeff(double diffusionCoeff) {
        this.diffusionCoeff = diffusionCoeff;
    }

    public double getFinalFi() {
        return finalFi;
    }

    public void setFinalFi(double finalFi) {
        this.finalFi = finalFi;
    }

    public double getFinalTeta() {
        return finalTeta;
    }

    public void setFinalTeta(double finalTeta) {
        this.finalTeta = finalTeta;
    }
}
