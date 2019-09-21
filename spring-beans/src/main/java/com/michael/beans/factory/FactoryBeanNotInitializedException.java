package com.michael.beans.factory;

import com.michael.beans.FatalBeanException;

/**
 * @author Michael Chu
 * @since 2019-09-20 13:56
 */
@SuppressWarnings("serial")
public class FactoryBeanNotInitializedException extends FatalBeanException {

    public FactoryBeanNotInitializedException() {
        super("FactoryBean is not fully initialized yet");
    }

    public FactoryBeanNotInitializedException(String msg) {
        super(msg);
    }
}
