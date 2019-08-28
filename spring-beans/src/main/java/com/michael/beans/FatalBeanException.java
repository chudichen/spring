package com.michael.beans;

import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-27 17:37
 */
public class FatalBeanException extends BeansException {

    public FatalBeanException(String msg) {
        super(msg);
    }

    public FatalBeanException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
