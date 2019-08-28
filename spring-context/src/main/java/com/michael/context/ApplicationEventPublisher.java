package com.michael.context;

/**
 * @author Michael Chu
 * @since 2019-08-27 17:59
 */
@FunctionalInterface
public interface ApplicationEventPublisher {

    default void publishEvent(ApplicationEvent event) {
        publishEvent((Object) event);
    }

    void publishEvent(Object event);
}
