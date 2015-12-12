package vsu.sc.grishchenko.molecularclusters.util;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public final class ResourceFactory {
    private static String getFullFileName(Class<?> aClass, String fileName) {
        return "/" + aClass.getPackage().getName().replaceAll("\\.", "/") + "/" + fileName;
    }

    public static URL getResource(Object o, String fileName) {
        return getResource(o.getClass(), fileName);
    }

    public static URL getResource(Class<?> aClass, String fileName) {
        return aClass.getResource(getFullFileName(aClass, fileName));
    }

    public static Properties getProperties(Class<?> aClass, String fileName) {
        Properties properties = new Properties();
        try {
            properties.load(aClass.getResourceAsStream(getFullFileName(aClass, fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
