package vsu.sc.grishchenko.molecularclusters.util;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * <p>Класс с набором утилитных методов для работы с ресурсами проета.</p>
 *
 * @author Грищенко Сергей
 */
public final class ResourceFactory {
    /**
     * <p>Метод получения полного пути к файлу,
     * который должен храниться во вленных друг в друга папках,
     * соответствующих пакету переданного класса.</p>
     *
     * @param aClass   класс, пакет которого нужно использовать для доступа к файлу
     * @param fileName имя файла
     * @return полный путь к файлу
     */
    private static String getFullFileName(Class<?> aClass, String fileName) {
        return "/" + aClass.getPackage().getName().replaceAll("\\.", "/") + "/" + fileName;
    }

    /**
     * <p>Метод получения ссылки на ресурс проекта,
     * который должен храниться во вленных друг в друга папках,
     * соответствующих переданному объекту.</p>
     *
     * @param o        объект, пакет которого нужно использовать для доступа к файлу
     * @param fileName имя файла
     * @return ссылку на ресурс проекта
     * @see URL
     */
    public static URL getResource(Object o, String fileName) {
        return getResource(o.getClass(), fileName);
    }

    /**
     * <p>Метод получения ссылки на ресурс проекта,
     * который должен храниться во вленных друг в друга папках,
     * соответствующих переданному классу.</p>
     *
     * @param aClass   класс, пакет которого нужно использовать для доступа к файлу
     * @param fileName имя файла
     * @return ссылку на ресурс проекта
     * @see URL
     */
    public static URL getResource(Class<?> aClass, String fileName) {
        return aClass.getResource(getFullFileName(aClass, fileName));
    }

    /**
     * <p>Метод получения настроек в файле <i>properties</i>,
     * который должен храниться во вленных друг в друга папках,
     * соответствующих переданному классу.</p>
     *
     * @param aClass   класс, пакет которого нужно использовать для доступа к файлу
     * @param fileName имя файла <i>properties</i>
     * @return карту настроек
     * @see Properties
     */
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
