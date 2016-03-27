package vsu.sc.grishchenko.molecularclusters.entity;

import javax.persistence.*;

@Entity
@Table(name = "trajectory_list")
public class TrajectoryListEntity {
    private long id;
    private Long experimentId;

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
    @Column(name = "experiment_id")
    public Long getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(Long experimentId) {
        this.experimentId = experimentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrajectoryListEntity that = (TrajectoryListEntity) o;

        if (id != that.id) return false;
        if (experimentId != null ? !experimentId.equals(that.experimentId) : that.experimentId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (experimentId != null ? experimentId.hashCode() : 0);
        return result;
    }
}
