package com.michael.context;

import com.michael.beans.FatalBeanException;

/**
 * @author Michael Chu
 * @since 2019-08-27 17:37
 */
@SuppressWarnings("serial")
public class ApplicationContextException extends FatalBeanException {

    public ApplicationContextException(String msg) {
        super(msg);
    }

    public ApplicationContextException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
