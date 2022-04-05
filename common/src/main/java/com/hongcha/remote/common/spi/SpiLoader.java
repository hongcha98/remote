package com.hongcha.remote.common.spi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SpiLoader {
    private static final String POSITION = "META-INF/service/";

    public static final Map<Load<Object>, List<Loader<Object>>> LOAD_CACHE = new ConcurrentHashMap<>(32);


    public static <T> T load(Class<T> clazz, String name) {
        List<Loader<Object>> loaderList = load(clazz);
        for (Loader<Object> loader : loaderList) {
            if (Objects.equals(loader.getSpiDescribe().name(), name)) {
                return (T) loader.getLoader();
            }
        }
        throw new SpiException("spi load fail");
    }

    public static <T> T load(Class<T> clazz, String name, Class[] parameterTypes, Object[] args) {
        List<Loader<Object>> loaderList = load(clazz, parameterTypes, args);
        for (Loader<Object> loader : loaderList) {
            if (Objects.equals(loader.getSpiDescribe().name(), name)) {
                return (T) loader.getLoader();
            }
        }
        throw new SpiException("spi load fail");
    }

    public static <T> T load(Class<T> clazz, int code) {
        List<Loader<Object>> loaderList = load(clazz);
        for (Loader<Object> loader : loaderList) {
            if (Objects.equals(loader.getSpiDescribe().code(), code)) {
                return (T) loader.getLoader();
            }
        }
        throw new SpiException("spi load fail");
    }

    public static <T> T load(Class<T> clazz, int code, Class[] parameterTypes, Object[] args) {
        List<Loader<Object>> loaderList = load(clazz, parameterTypes, args);
        for (Loader<Object> loader : loaderList) {
            if (Objects.equals(loader.getSpiDescribe().code(), code)) {
                return (T) loader.getLoader();
            }
        }
        throw new SpiException("spi load fail");
    }

    public static <T> List<T> loadAll(Class<T> clazz) {
        return (List<T>) load(clazz).stream().map(Loader::getLoader).collect(Collectors.toList());
    }

    public static <T> List<T> loadAll(Class<T> clazz, Class[] parameterTypes, Object[] args) {
        return (List<T>) load(clazz, parameterTypes, args).stream().map(Loader::getLoader).collect(Collectors.toList());
    }

    private static List<Loader<Object>> load(Class clazz) {
        return load(clazz, new Class[]{}, new Object[]{});
    }

    private static List<Loader<Object>> load(Class clazz, Class[] parameterTypes, Object[] args) {
        return LOAD_CACHE.computeIfAbsent(new Load(clazz, parameterTypes, args), load -> doLoad(clazz, parameterTypes, args));
    }

    private static List<Loader<Object>> doLoad(Class clazz, Class[] parameterTypes, Object[] args) {
        List<Loader<Object>> loaderList = new LinkedList<>();
        try {
            String sourceName = POSITION + clazz.getName();
            Enumeration<URL> urls = clazz.getClassLoader().getResources(sourceName);
            while (urls.hasMoreElements()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(urls.nextElement().openStream()))) {
                    String clazzName;
                    while ((clazzName = reader.readLine()) != null) {
                        if (clazzName.trim().isEmpty()) continue;
                        Class<?> clazzImpl = Class.forName(clazzName);
                        SpiDescribe spiDescribe = clazzImpl.getDeclaredAnnotation(SpiDescribe.class);
                        if (spiDescribe == null) {
                            throw new RuntimeException(clazzName + " no callout SpiDescribe");
                        }
                        Constructor<?> constructor = clazzImpl.getDeclaredConstructor(parameterTypes);
                        loaderList.add(new Loader<>(spiDescribe, constructor.newInstance(args)));
                    }

                }
            }
        } catch (Exception e) {
            throw new SpiException(e);
        }
        loaderList.sort(Loader::compareTo);
        return loaderList;
    }


    static class Load<T> {
        private Class<T> clazz;
        Class[] parameterTypes;
        Object[] args;

        public Load(Class<T> clazz, Class[] parameterTypes, Object[] args) {
            this.clazz = clazz;
            this.parameterTypes = parameterTypes;
            this.args = args;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public void setClazz(Class<T> clazz) {
            this.clazz = clazz;
        }

        public Class[] getParameterTypes() {
            return parameterTypes;
        }

        public void setParameterTypes(Class[] parameterTypes) {
            this.parameterTypes = parameterTypes;
        }

        public Object[] getArgs() {
            return args;
        }

        public void setArgs(Object[] args) {
            this.args = args;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Load load = (Load) o;
            return Objects.equals(clazz, load.clazz) && Arrays.equals(parameterTypes, load.parameterTypes) && Arrays.equals(args, load.args);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(clazz);
            result = 31 * result + Arrays.hashCode(parameterTypes);
            result = 31 * result + Arrays.hashCode(args);
            return result;
        }
    }


}
