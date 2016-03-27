package vsu.sc.grishchenko.molecularclusters.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class PathEntityPK implements Serializable {
    private long trajectoryId;
    private int index;

    @Column(name = "trajectory_id")
    @Id
    public long getTrajectoryId() {
        return trajectoryId;
    }

    public void setTrajectoryId(long trajectoryId) {
        this.trajectoryId = trajectoryId;
    }

    @Column(name = "index")
    @Id
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PathEntityPK that = (PathEntityPK) o;

        if (trajectoryId != that.trajectoryId) return false;
        if (index != that.index) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (trajectoryId ^ (trajectoryId >>> 32));
        result = 31 * result + index;
        return result;
    }
}
