package com.michael.util;

import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-26 09:35
 */
public abstract class ClassUtils {

    @Nullable
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // 无法获取到classLoader
        }
        if (cl == null) {
            // 没有找classLoader使用当前类的classLoader
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    // 使用bootstrap classloader
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // 依然无法获取到classLoader，告诉调用者为null
                }
            }
        }
        return cl;
    }
}
