package mini.orm.model;

import mini.orm.api.Column;
import mini.orm.api.PrimaryKey;
import mini.orm.api.Table;

import java.util.Date;

@Table(name = "cars")
public class CarEntity {
    @PrimaryKey
    @Column(name = "car_id")
    private int id;

    @Column
    private String name;

    @Column
    private String color;

    @Column
    private int countOfDoors;

    @Column(name = "is_truck")
    private boolean truck;

    @Column
    private Date releaseDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getCountOfDoors() {
        return countOfDoors;
    }

    public void setCountOfDoors(int countOfDoors) {
        this.countOfDoors = countOfDoors;
    }

    public boolean isTruck() {
        return truck;
    }

    public void setTruck(boolean truck) {
        this.truck = truck;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CarEntity carEntity = (CarEntity) o;

        if (id != carEntity.id) return false;
        if (countOfDoors != carEntity.countOfDoors) return false;
        if (truck != carEntity.truck) return false;
        if (name != null ? !name.equals(carEntity.name) : carEntity.name != null) return false;
        if (color != null ? !color.equals(carEntity.color) : carEntity.color != null) return false;
        return !(releaseDate != null ? !releaseDate.equals(carEntity.releaseDate) : carEntity.releaseDate != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + countOfDoors;
        result = 31 * result + (truck ? 1 : 0);
        result = 31 * result + (releaseDate != null ? releaseDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CarEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", countOfDoors=" + countOfDoors +
                ", truck=" + truck +
                ", releaseDate=" + releaseDate +
                '}';
    }
}