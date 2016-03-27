package vsu.sc.grishchenko.molecularclusters.entity;

import javax.persistence.*;

@Entity
@Table(name = "path")
@IdClass(PathEntityPK.class)
public class PathEntity {
    private long trajectoryId;
    private int index;
    private double value;

    @Id
    @Column(name = "trajectory_id")
    public long getTrajectoryId() {
        return trajectoryId;
    }

    public void setTrajectoryId(long trajectoryId) {
        this.trajectoryId = trajectoryId;
    }

    @Id
    @Column(name = "index")
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Basic
    @Column(name = "value")
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PathEntity that = (PathEntity) o;

        if (trajectoryId != that.trajectoryId) return false;
        if (index != that.index) return false;
        if (Double.compare(that.value, value) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (trajectoryId ^ (trajectoryId >>> 32));
        result = 31 * result + index;
        temp = Double.doubleToLongBits(value);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
