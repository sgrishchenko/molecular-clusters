package vsu.sc.grishchenko.molecularclusters.entity;

import vsu.sc.grishchenko.molecularclusters.experiment.AnalyzeResult;

import javax.persistence.*;

@Entity
@Table(name = "trajectory_list")
public class TrajectoryListEntity {
    private long id;
    private String name;
    private String json;
    private ExperimentEntity experiment;

    private Double radius;
    private Double fi;
    private Double teta;
    private Double pathLength;
    private Double pathLengthToTubeLength;
    private Double avgSpeed;
    private Double avgFreePath;
    private Double diffusionCoeff;
    private Double finalFi;

    public TrajectoryListEntity() {
    }

    public TrajectoryListEntity(String name, String json, AnalyzeResult result) {
        this.name = name;
        this.json = json;
        fillParams(result);
    }

    public TrajectoryListEntity(ExperimentEntity experiment, String name, String json, AnalyzeResult result) {
        this(name, json, result);
        this.experiment = experiment;
    }

    public void fillParams(AnalyzeResult result) {
        if (!Double.isNaN(result.getRadius())) this.radius = result.getRadius();
        if (!Double.isNaN(result.getFi())) this.fi = result.getFi();
        if (!Double.isNaN(result.getTeta())) this.teta = result.getTeta();
        if (!Double.isNaN(result.getPathLength())) this.pathLength = result.getPathLength();
        if (!Double.isNaN(result.getPathLengthToTubeLength())) this.pathLengthToTubeLength = result.getPathLengthToTubeLength();
        if (!Double.isNaN(result.getAvgSpeed())) this.avgSpeed = result.getAvgSpeed();
        if (!Double.isNaN(result.getAvgFreePath())) this.avgFreePath = result.getAvgFreePath();
        if (!Double.isNaN(result.getDiffusionCoeff())) this.diffusionCoeff = result.getDiffusionCoeff();
        if (!Double.isNaN(result.getFi())) this.finalFi = result.getFi();
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "json")
    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrajectoryListEntity that = (TrajectoryListEntity) o;

        if (id != that.id) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (json != null ? !json.equals(that.json) : that.json != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (json != null ? json.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "experiment_id", referencedColumnName = "id")
    public ExperimentEntity getExperiment() {
        return experiment;
    }

    public void setExperiment(ExperimentEntity experiment) {
        this.experiment = experiment;
    }

    @Basic
    @Column(name = "radius")
    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    @Basic
    @Column(name = "fi")
    public Double getFi() {
        return fi;
    }

    public void setFi(Double fi) {
        this.fi = fi;
    }

    @Basic
    @Column(name = "teta")
    public Double getTeta() {
        return teta;
    }

    public void setTeta(Double teta) {
        this.teta = teta;
    }

    @Basic
    @Column(name = "path_length")
    public Double getPathLength() {
        return pathLength;
    }

    public void setPathLength(Double pathLength) {
        this.pathLength = pathLength;
    }

    @Basic
    @Column(name = "path_length_to_tube_length")
    public Double getPathLengthToTubeLength() {
        return pathLengthToTubeLength;
    }

    public void setPathLengthToTubeLength(Double pathLengthToTubeLength) {
        this.pathLengthToTubeLength = pathLengthToTubeLength;
    }

    @Basic
    @Column(name = "avg_speed")
    public Double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(Double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    @Basic
    @Column(name = "avg_free_path")
    public Double getAvgFreePath() {
        return avgFreePath;
    }

    public void setAvgFreePath(Double avgFreePath) {
        this.avgFreePath = avgFreePath;
    }

    @Basic
    @Column(name = "diffusion_coeff")
    public Double getDiffusionCoeff() {
        return diffusionCoeff;
    }

    public void setDiffusionCoeff(Double diffusionCoeff) {
        this.diffusionCoeff = diffusionCoeff;
    }

    @Basic
    @Column(name = "final_fi")
    public Double getFinalFi() {
        return finalFi;
    }

    public void setFinalFi(Double finalFi) {
        this.finalFi = finalFi;
    }
}
