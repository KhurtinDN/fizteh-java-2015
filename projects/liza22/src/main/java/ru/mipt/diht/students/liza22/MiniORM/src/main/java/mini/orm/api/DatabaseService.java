package mini.orm.api;

import java.util.List;

/**
 * Main ORM interface.
 * @param <T> type of entity
 * @param <K> type of primary key for entity
 */
public interface DatabaseService<T, K> {

    T queryById(K entityKey);

    List<T> queryForAll();

    void insert(T newEntity);

    void update(T updatedEntity);

    void delete(T deleteEntity);

    void createTable();

    void dropTable();
}
