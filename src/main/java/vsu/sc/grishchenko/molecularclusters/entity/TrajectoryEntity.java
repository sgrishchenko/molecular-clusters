package vsu.sc.grishchenko.molecularclusters.entity;

import javax.persistence.*;

@Entity
@Table(name = "trajectory")
public class TrajectoryEntity {
    private long id;
    private long listId;
    private String label;
    private Long colorId;

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
    @Column(name = "list_id")
    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }

    @Basic
    @Column(name = "label")
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Basic
    @Column(name = "color_id")
    public Long getColorId() {
        return colorId;
    }

    public void setColorId(Long colorId) {
        this.colorId = colorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrajectoryEntity that = (TrajectoryEntity) o;

        if (id != that.id) return false;
        if (listId != that.listId) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (colorId != null ? !colorId.equals(that.colorId) : that.colorId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (listId ^ (listId >>> 32));
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (colorId != null ? colorId.hashCode() : 0);
        return result;
    }
}
