package com.michael.core;

import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-23 09:50
 */
public abstract class NestedRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -6494839057242810630L;

    static {
        NestedExceptionUtils.class.getName();
    }

    public NestedRuntimeException(String msg) {
        super(msg);
    }

    public NestedRuntimeException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
