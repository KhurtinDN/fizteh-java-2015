package mini.orm.model;

import mini.orm.api.Column;
import mini.orm.api.PrimaryKey;

public class IncorrectEntityWithoutTableAnn {

    @PrimaryKey
    @Column
    private Integer id;

    @Column
    private String name;
}
