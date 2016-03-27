package vsu.sc.grishchenko.molecularclusters.entity;

import javax.persistence.*;

@Entity
@Table(name = "iteration")
public class IterationEntity {
    private long id;
    private double from;
    private double to;
    private double step;
    private String dimension;

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
    @Column(name = "from")
    public double getFrom() {
        return from;
    }

    public void setFrom(double from) {
        this.from = from;
    }

    @Basic
    @Column(name = "to")
    public double getTo() {
        return to;
    }

    public void setTo(double to) {
        this.to = to;
    }

    @Basic
    @Column(name = "step")
    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    @Basic
    @Column(name = "dimension")
    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IterationEntity that = (IterationEntity) o;

        if (id != that.id) return false;
        if (Double.compare(that.from, from) != 0) return false;
        if (Double.compare(that.to, to) != 0) return false;
        if (Double.compare(that.step, step) != 0) return false;
        if (dimension != null ? !dimension.equals(that.dimension) : that.dimension != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        temp = Double.doubleToLongBits(from);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(to);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(step);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (dimension != null ? dimension.hashCode() : 0);
        return result;
    }
}
