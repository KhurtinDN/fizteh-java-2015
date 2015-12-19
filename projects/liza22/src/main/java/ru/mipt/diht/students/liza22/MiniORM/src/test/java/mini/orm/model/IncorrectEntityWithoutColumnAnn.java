package mini.orm.model;

import mini.orm.api.Column;
import mini.orm.api.PrimaryKey;
import mini.orm.api.Table;

@Table
public class IncorrectEntityWithoutColumnAnn {

    @PrimaryKey
    @Column
    private Integer id;

    private String name;
}
