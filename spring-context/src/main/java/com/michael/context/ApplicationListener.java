package com.michael.context;

import java.util.EventListener;

/**
 * @author Michael Chu
 * @since 2019-08-27 16:58
 */
@FunctionalInterface
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {

    void onApplicationEvent(E event);
}
