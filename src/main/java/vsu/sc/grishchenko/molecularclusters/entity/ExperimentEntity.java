package vsu.sc.grishchenko.molecularclusters.entity;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "experiment")
public class ExperimentEntity {
    private long id;
    private Date date;
    private String movLabel;
    private double x;
    private double y;
    private double z;
    private double vx;
    private double vy;
    private double vz;

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
    @Column(name = "date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Basic
    @Column(name = "mov_label")
    public String getMovLabel() {
        return movLabel;
    }

    public void setMovLabel(String movLabel) {
        this.movLabel = movLabel;
    }

    @Basic
    @Column(name = "X")
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    @Basic
    @Column(name = "Y")
    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Basic
    @Column(name = "Z")
    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    @Basic
    @Column(name = "VX")
    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    @Basic
    @Column(name = "VY")
    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    @Basic
    @Column(name = "VZ")
    public double getVz() {
        return vz;
    }

    public void setVz(double vz) {
        this.vz = vz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExperimentEntity that = (ExperimentEntity) o;

        if (id != that.id) return false;
        if (Double.compare(that.x, x) != 0) return false;
        if (Double.compare(that.y, y) != 0) return false;
        if (Double.compare(that.z, z) != 0) return false;
        if (Double.compare(that.vx, vx) != 0) return false;
        if (Double.compare(that.vy, vy) != 0) return false;
        if (Double.compare(that.vz, vz) != 0) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (movLabel != null ? !movLabel.equals(that.movLabel) : that.movLabel != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (movLabel != null ? movLabel.hashCode() : 0);
        temp = Double.doubleToLongBits(x);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(vx);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(vy);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(vz);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
