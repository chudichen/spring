package com.michael.context.event;

import com.michael.context.ApplicationContext;

/**
 * @author Michael Chu
 * @since 2019-09-02 20:34
 */
public class ContextClosedEvent extends ApplicationContextEvent {

    public ContextClosedEvent(ApplicationContext source) {
        super(source);
    }
}
