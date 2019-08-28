package com.michael.util;

import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-23 10:00
 */
public abstract class Assert {

    /**
     * 断言：数组没有{@code null}元素
     *
     * @param array 待检测数组
     * @param message 提示信息
     */
    public static void noNullElements(@Nullable Object[] array, String message) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    throw new IllegalArgumentException(message);
                }
            }
        }
    }

    public static void notNull(@Nullable Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void hasText(@Nullable String text, String message) {
        if (!StringUtils.hasText(text)) {
            throw new IllegalArgumentException(message);
        }
    }
}
