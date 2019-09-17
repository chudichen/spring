package com.michael.beans.factory;

/**
 * @author Michael Chu
 * @since 2019-09-16 20:55
 */
public class BeanCreationNotAllowedException extends BeanCreationException {

    public BeanCreationNotAllowedException(String beanName, String msg) {
        super(beanName, msg);
    }
}
