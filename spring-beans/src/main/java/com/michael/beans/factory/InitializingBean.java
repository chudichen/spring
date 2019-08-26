package com.michael.beans.factory;

/**
 * @author Michael Chu
 * @since 2019-08-26 19:52
 */
public interface InitializingBean {

    void afterPropertiesSet() throws Exception;
}
