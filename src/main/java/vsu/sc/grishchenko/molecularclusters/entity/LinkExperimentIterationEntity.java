package vsu.sc.grishchenko.molecularclusters.entity;

import javax.persistence.*;

@Entity
@Table(name = "link_experiment_iteration")
@IdClass(LinkExperimentIterationEntityPK.class)
public class LinkExperimentIterationEntity {
    private long experimentId;
    private long iterationId;

    @Id
    @Column(name = "experiment_id")
    public long getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(long experimentId) {
        this.experimentId = experimentId;
    }

    @Id
    @Column(name = "iteration_id")
    public long getIterationId() {
        return iterationId;
    }

    public void setIterationId(long iterationId) {
        this.iterationId = iterationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LinkExperimentIterationEntity that = (LinkExperimentIterationEntity) o;

        if (experimentId != that.experimentId) return false;
        if (iterationId != that.iterationId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (experimentId ^ (experimentId >>> 32));
        result = 31 * result + (int) (iterationId ^ (iterationId >>> 32));
        return result;
    }
}
