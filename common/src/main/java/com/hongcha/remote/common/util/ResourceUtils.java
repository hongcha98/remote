package com.hongcha.remote.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

public class ResourceUtils {

    public static List<Properties> getRosources(ClassLoader classLoader, String resourcesName) throws IOException {
        return getRosources(classLoader, resourcesName, inputStream -> {
            try {
                Properties properties = new Properties();
                properties.load(inputStream);
                return properties;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
    }

    public static <T> List<T> getRosources(ClassLoader classLoader, String resourcesName, Function<InputStream, T> inputStreamMapping) throws IOException {
        Enumeration<URL> urls = (classLoader != null ?
                classLoader.getResources(resourcesName) :
                ClassLoader.getSystemResources(resourcesName));
        List<T> result = new LinkedList();
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            try (InputStream inputStream = url.openStream()) {
                T t = inputStreamMapping.apply(inputStream);
                result.add(t);
            }
        }
        return result;
    }

}
