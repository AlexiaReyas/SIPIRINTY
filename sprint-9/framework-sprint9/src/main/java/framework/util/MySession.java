package framework.util;

import java.util.LinkedHashMap;
import java.util.Map;

public final class MySession {
    public static final String STORAGE_KEY = MySession.class.getName() + ".attributes";

    private final Map<String, Object> attributes;

    public MySession() {
        this.attributes = new LinkedHashMap<>();
    }

    private MySession(Map<String, Object> attributes) {
        this.attributes = new LinkedHashMap<>(attributes);
    }

    public static MySession from(Map<?, ?> source) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            if (entry.getKey() instanceof String key) {
                values.put(key, entry.getValue());
            }
        }
        return new MySession(values);
    }

    public Object get(String name) {
        return attributes.get(name);
    }

    public <T> T get(String name, Class<T> type) {
        Object value = attributes.get(name);
        return value == null ? null : type.cast(value);
    }

    public void set(String name, Object value) {
        if (value == null) {
            attributes.remove(name);
            return;
        }
        attributes.put(name, value);
    }

    public Object remove(String name) {
        return attributes.remove(name);
    }

    public boolean contains(String name) {
        return attributes.containsKey(name);
    }

    public void clear() {
        attributes.clear();
    }

    public Map<String, Object> snapshot() {
        return new LinkedHashMap<>(attributes);
    }
}