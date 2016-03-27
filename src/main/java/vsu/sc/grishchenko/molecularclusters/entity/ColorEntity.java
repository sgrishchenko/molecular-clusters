package vsu.sc.grishchenko.molecularclusters.entity;

import javax.persistence.*;

@Entity
@Table(name = "color")
public class ColorEntity {
    private long id;
    private double r;
    private double g;
    private double b;
    private double opacity;

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
    @Column(name = "R")
    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    @Basic
    @Column(name = "G")
    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    @Basic
    @Column(name = "B")
    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    @Basic
    @Column(name = "opacity")
    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColorEntity that = (ColorEntity) o;

        if (id != that.id) return false;
        if (Double.compare(that.r, r) != 0) return false;
        if (Double.compare(that.g, g) != 0) return false;
        if (Double.compare(that.b, b) != 0) return false;
        if (Double.compare(that.opacity, opacity) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        temp = Double.doubleToLongBits(r);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(g);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(b);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(opacity);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
