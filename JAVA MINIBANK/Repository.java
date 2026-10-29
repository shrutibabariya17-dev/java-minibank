import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class Repository<T> {

    private final HashMap<String, T> store;

    public Repository() {
        this.store = new HashMap<>();
    }

    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null.");
        }

        Field idField = null;
        Class<?> cls = entity.getClass();

        while (cls != null && idField == null) {
            for (Field f : cls.getDeclaredFields()) {
                if (f.isAnnotationPresent(Id.class)) {
                    idField = f;
                    break;
                }
            }
            cls = cls.getSuperclass();
        }

        if (idField == null) {
            throw new IllegalArgumentException("No @Id field found on " + entity.getClass().getSimpleName());
        }

        idField.setAccessible(true);

        Object idValue;
        try {
            idValue = idField.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not read @Id field: " + e.getMessage());
        }

        if (idValue == null) {
            throw new IllegalArgumentException("@Id field value must not be null.");
        }

        String key = idValue.toString();
        store.put(key, entity);
        return entity;
    }

    public T findById(String id) {
        if (id == null) {
            return null;
        }
        return store.get(id);
    }

    public ArrayList<T> findAll() {
        return new ArrayList<>(store.values());
    }

    public boolean delete(String id) {
        if (id == null) {
            return false;
        }
        return store.remove(id) != null;
    }

    @SafeVarargs
    public final void saveAll(T... items) {
        if (items == null) {
            return;
        }
        for (T item : items) {
            save(item);
        }
    }

    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) {
            return 0.0;
        }
        double sum = 0.0;
        for (N value : values) {
            if (value != null) {
                sum += value.doubleValue();
            }
        }
        return sum / values.length;
    }
}
