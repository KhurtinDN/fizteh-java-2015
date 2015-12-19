package mini.orm.model;

import mini.orm.api.Column;
import mini.orm.api.Table;

@Table
public class IncorrectEntityWithoutPrimaryKeyAnn {

    @Column
    private Integer id;

    @Column
    private String name;
}
