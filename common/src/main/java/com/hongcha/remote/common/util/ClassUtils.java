package com.hongcha.remote.common.util;

import java.lang.reflect.Constructor;

public class ClassUtils {

    public static Object instantiate(String className) throws ReflectiveOperationException {
        return instantiate(className, Object.class);
    }

    /**
     * 无参构造
     *
     * @param className
     * @param superClass
     * @param <T>
     * @return
     * @throws ClassNotFoundException
     */
    public static <T> T instantiate(String className, Class<T> superClass) throws ReflectiveOperationException {
        Class<?> clazz = Class.forName(className);
        return instantiate(clazz, superClass);
    }


    public static <T> T instantiate(Class<T> clazz) throws ReflectiveOperationException {
        return (T) instantiate(clazz, Object.class);
    }


    public static <T> T instantiate(Class<?> clazz, Class<T> superClass) throws ReflectiveOperationException {

        if (clazz.isInterface()) {
            throw new ReflectiveOperationException(clazz.getName() + " is interface");
        }
        if (clazz.isAssignableFrom(superClass)) {
            throw new ReflectiveOperationException(clazz.getName() + " no is " + superClass.getName() + " impl");
        }

        Constructor<?> clazzDeclaredConstructor = clazz.getDeclaredConstructor();

        return (T) clazzDeclaredConstructor.newInstance();
    }


}
