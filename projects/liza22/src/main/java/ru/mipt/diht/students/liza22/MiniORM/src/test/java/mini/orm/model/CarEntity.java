package mini.orm.model;

import mini.orm.api.Column;
import mini.orm.api.PrimaryKey;
import mini.orm.api.Table;

import java.util.Date;

@Table(name = "cars")
public final class CarEntity {

    private static final int CONST31 = 31;

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

    public void setId(int carid) {
        this.id = carid;
    }

    public String getName() {
        return name;
    }

    public void setName(String carname) {
        this.name = carname;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String carcolor) {
        this.color = carcolor;
    }

    public int getCountOfDoors() {
        return countOfDoors;
    }

    public void setCountOfDoors(int carcountOfDoors) {
        this.countOfDoors = carcountOfDoors;
    }

    public boolean isTruck() {
        return truck;
    }

    public void setTruck(boolean cartruck) {
        this.truck = cartruck;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date carreleaseDate) {
        this.releaseDate = carreleaseDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CarEntity carEntity = (CarEntity) o;

        if (id != carEntity.id) {
            return false;
        }
        if (countOfDoors != carEntity.countOfDoors) {
            return false;
        }
        if (truck != carEntity.truck) {
            return false;
        }
        if (name != null && !name.equals(carEntity.name) || name == null && carEntity.name != null) {
            return false;
        }
        if (color != null && !color.equals(carEntity.color)
                || color == null && carEntity.color != null) {
            return false;
        }
        return !(releaseDate != null && !releaseDate.equals(carEntity.releaseDate)
                || releaseDate == null && carEntity.releaseDate != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = CONST31 * result;
        if (name != null) {
            result = result + name.hashCode();
        }
        result = CONST31 * result;
        if (color != null) {
            result = result + color.hashCode();
        }
        result = CONST31 * result + countOfDoors;
        result = CONST31 * result;
        if (truck != 0) {
            result = result + 1;
        }
        result = CONST31 * result;
        if (releaseDate != null) {
            result = result + releaseDate.hashCode();
        }
        return result;
    }

    @Override
    public String toString() {
        return "CarEntity{"
                + "id=" + id
                + ", name='" + name
                + '\''
                + ", color='" + color
                + '\''
                + ", countOfDoors=" + countOfDoors
                + ", truck=" + truck
                + ", releaseDate=" + releaseDate
                + '}';
    }
}
