package com.michael.beans;

import com.michael.core.NestedRuntimeException;
import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-23 09:48
 */
public abstract class BeansException extends NestedRuntimeException {

    public BeansException(String msg) {
        super(msg);
    }

    public BeansException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
