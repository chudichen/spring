package com.michael.context;

import java.util.EventObject;

/**
 * @author Michael Chu
 * @since 2019-08-27 16:59
 */
public abstract class ApplicationEvent extends EventObject {

    private static final long serialVersionUID = -2141064771209386732L;

    private final long timestamp;

    public ApplicationEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }

    public final long getTimestamp() {
        return this.timestamp;
    }
}
