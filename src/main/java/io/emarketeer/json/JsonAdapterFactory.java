package io.emarketeer.json;

import java.net.URLClassLoader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class JsonAdapterFactory implements JsonAdapter {

    private URLClassLoader classLoader;
    private final static String GSON_CLASS = "com.google.gson.Gson";
    private final static String JACKSON_CLASS = "com.fasterxml.jackson.databind.ObjectMapper";

    private final static Map<String, String> AVAILABLE_DRIVERS = new LinkedHashMap<String, String>() {{
        put(JACKSON_CLASS, "io.emarketeer.json.jackson.JacksonAdapter");
        put(GSON_CLASS, "io.emarketeer.json.gson.GsonAdapter");
    }};

    private JsonAdapterFactory(final URLClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    private boolean isClassPresent(final String clazz) {
        try {
            return Class.forName(clazz, false, classLoader) != null;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

    static AtomicReference<JsonAdapter> __resolved = new AtomicReference<>();

    private synchronized JsonAdapter getAdapter() {
        final JsonAdapter resolved = __resolved.get();
        if (resolved != null) {
            return resolved;
        }
        return __resolved.updateAndGet(e -> new AdapterSupplier().get());
    }

    public String toJson(final Object data) {
        return getAdapter().toJson(data);
    }

    public <T> T fromJson(final String input, final Class<T> clazz) {
        return getAdapter().fromJson(input, clazz);
    }

    public <T> List<T> fromJsonToList(final String input) {
        return getAdapter().fromJsonToList(input);
    }

    public static JsonAdapterFactory get() {
        return new JsonAdapterFactory((URLClassLoader) Thread.currentThread().getContextClassLoader());
    }

    private final class AdapterSupplier implements Supplier<JsonAdapter> {
        @Override
        public JsonAdapter get() {
            final ClassLoader currentClassLoader = classLoader;
            for (final Map.Entry<String, String> pair : AVAILABLE_DRIVERS.entrySet()) {
                try {
                    if (isClassPresent(pair.getKey())) {
                        final Class<?> instanceClass = currentClassLoader.loadClass(pair.getValue());
                        return (JsonAdapter) instanceClass.newInstance();
                    }
                } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                    throw new RuntimeException("No json adapter found. Please check your dependencies", ex);
                }
            }
            throw new JsonAdapterException("No json adapter found. Please check your dependencies");
        }
    }
}
