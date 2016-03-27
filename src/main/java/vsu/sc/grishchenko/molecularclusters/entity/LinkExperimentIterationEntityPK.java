package vsu.sc.grishchenko.molecularclusters.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class LinkExperimentIterationEntityPK implements Serializable {
    private long experimentId;
    private long iterationId;

    @Column(name = "experiment_id")
    @Id
    public long getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(long experimentId) {
        this.experimentId = experimentId;
    }

    @Column(name = "iteration_id")
    @Id
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

        LinkExperimentIterationEntityPK that = (LinkExperimentIterationEntityPK) o;

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
