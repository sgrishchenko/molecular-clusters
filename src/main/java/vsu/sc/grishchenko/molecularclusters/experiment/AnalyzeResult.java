package vsu.sc.grishchenko.molecularclusters.experiment;

public class AnalyzeResult {
    double radius;
    double fi;
    double teta;

    double pathLength;
    double pathLengthToTubeLength;
    double avgSpeed;
    double avgFreePath;
    double diffusionCoeff;
    double finalFi;

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

    @ImportantData
    public double getPathLength() {
        return pathLength;
    }

    public void setPathLength(double pathLength) {
        this.pathLength = pathLength;
    }

    @ImportantData
    public double getPathLengthToTubeLength() {
        return pathLengthToTubeLength;
    }

    public void setPathLengthToTubeLength(double pathLengthToTubeLength) {
        this.pathLengthToTubeLength = pathLengthToTubeLength;
    }

    @ImportantData
    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    @ImportantData
    public double getAvgFreePath() {
        return avgFreePath;
    }

    public void setAvgFreePath(double avgFreePath) {
        this.avgFreePath = avgFreePath;
    }

    @ImportantData
    public double getDiffusionCoeff() {
        return diffusionCoeff;
    }

    public void setDiffusionCoeff(double diffusionCoeff) {
        this.diffusionCoeff = diffusionCoeff;
    }

    @ImportantData
    public double getFinalFi() {
        return finalFi;
    }

    public void setFinalFi(double finalFi) {
        this.finalFi = finalFi;
    }
}
