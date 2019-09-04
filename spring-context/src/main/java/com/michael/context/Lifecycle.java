package com.michael.context;

/**
 * @author Michael Chu
 * @since 2019-09-02 20:40
 */
public interface Lifecycle {

    void start();

    void stop();

    boolean isRunning();
}
