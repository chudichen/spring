package com.michael.context.event;

import com.michael.context.ApplicationContext;
import com.michael.context.ApplicationEvent;

/**
 * @author Michael Chu
 * @since 2019-09-02 20:34
 */
public abstract class ApplicationContextEvent extends ApplicationEvent {

    public ApplicationContextEvent(ApplicationContext source) {
        super(source);
    }

    public final ApplicationContext getApplicationContext() {
        return (ApplicationContext) getSource();
    }
}
