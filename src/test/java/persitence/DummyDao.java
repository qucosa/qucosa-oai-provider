package persitence;

import de.qucosa.oai.provider.persitence.Dao;

import java.util.Collections;

public class DummyDao<T> implements Dao {
    @Override
    public Object save(Object object) {
        return null;
    }

    @Override
    public Object save(Collections objects) {
        return null;
    }

    @Override
    public Object update(Object object) {
        return null;
    }

    @Override
    public Object update(Collections objects) {
        return null;
    }

    @Override
    public Object findAll() {
        return null;
    }

    @Override
    public Object findById(Object value) {
        return null;
    }

    @Override
    public Object findByColumnAndValue(String column, Object value) {
        return null;
    }

    @Override
    public Object delete(String column, Object value) {
        return null;
    }
}
