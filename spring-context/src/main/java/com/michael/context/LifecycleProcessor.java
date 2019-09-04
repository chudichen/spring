package com.michael.context;

/**
 * @author Michael Chu
 * @since 2019-09-02 20:39
 */
public interface LifecycleProcessor extends Lifecycle {

    void onRefresh();

    void onClose();
}
