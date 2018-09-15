package io.emarketeer.json.rules;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DependenciesInjectionRunner extends BlockJUnit4ClassRunner {

    private static ClassLoader prevClassLoader;
    private final Class<?> clazz;
    private static Map<Class<?>, DynamicURLClassLoader> classLoader = new ConcurrentHashMap<>();

    public DependenciesInjectionRunner(final Class<?> clazz) throws InitializationError {
        super(loadFromCustomClassloader(clazz));
        this.clazz = clazz;

        final LibraryPathSets paths = clazz.getAnnotation(LibraryPathSets.class);
        final URL[] urls = withResourcePath(paths.value());
        Arrays.stream(urls).forEach(classLoader.get(clazz)::addURL);
    }

    private static Class<?> loadFromCustomClassloader(final Class<?> clazz) throws InitializationError {
        try {
            if (classLoader.get(clazz) == null) {
                prevClassLoader = Thread.currentThread().getContextClassLoader();
                classLoader.put(clazz, new DynamicURLClassLoader((URLClassLoader) prevClassLoader, clazz.getPackage().getName()));
            }
            return classLoader.get(clazz).loadClass(clazz.getName());
        } catch (ClassNotFoundException e) {
            throw new InitializationError(e);
        }
    }

    @Override
    public void run(final RunNotifier notifier) {
        final Runnable runnable = () -> super.run(notifier);
        final Thread thread = new Thread(runnable);
        thread.setContextClassLoader(classLoader.get(clazz));
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            classLoader.remove(clazz);
            thread.setContextClassLoader(prevClassLoader);
        }
    }

    private URL[] withResourcePath(final String[] paths) {
        final Collection<URL> deps = Stream.of(paths)
                .map(e -> Thread.currentThread().getContextClassLoader().getResource(e))
                .filter(Objects::nonNull)
                .map(e -> {
                    try {
                        return Paths.get(e.toURI());
                    } catch (final URISyntaxException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .flatMap((Function<Path, Stream<Path>>) path -> {
                    try {
                        return Files.walk(path).filter(Files::isRegularFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(path -> {
                    try {
                        return path.toUri().toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        final URL[] urls = new URL[deps.size()];
        return deps.toArray(urls);
    }
}
