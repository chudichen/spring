package com.michael.core;

import com.michael.lang.Nullable;

import java.io.IOException;

/**
 * @author Michael Chu
 * @since 2019-09-04 17:59
 */
public class NestedIOException extends IOException {

    static {
        NestedExceptionUtils.class.getName();
    }

    public NestedIOException(String msg) {
        super(msg);
    }

    public NestedIOException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }

    @Override
    @Nullable
    public String getMessage() {
        return NestedExceptionUtils.buildMessage(super.getMessage(), getCause());
    }
}
