package com.michael.core;

import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-27 17:41
 */
public abstract class NestedExceptionUtils {

    @Nullable
    public static String buildMessage(@Nullable String message, @Nullable Throwable cause) {
        if (cause == null) {
            return message;
        }
        StringBuilder sb = new StringBuilder(64);
        if (message != null) {
            sb.append(message).append("; ");
        }
        sb.append("nested exception is ").append(cause);
        return sb.toString();
    }
}
