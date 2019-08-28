package com.michael.commons.logging;

/**
 * @author Michael Chu
 * @since 2019-08-27 16:17
 */
public abstract class LogFactory {

    public static Log getLog(Class<?> clazz) {
        return getLog(clazz.getName());
    }

    public static Log getLog(String name) {
        return null;
    }
}
