package io.emarketeer.json.rules;

import java.net.URL;
import java.net.URLClassLoader;

class DynamicURLClassLoader extends URLClassLoader {

    private final String prefix;

    DynamicURLClassLoader(final URLClassLoader parent, final String prefix) {
        super(parent.getURLs());
        this.prefix = prefix;
    }

    @Override
    public void addURL(final URL url) {
        super.addURL(url);
    }

    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        if (name.startsWith(prefix)) {
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                c = findClass(name);
            }
            return c;
        }
        return super.loadClass(name);
    }
}
