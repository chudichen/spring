package com.michael.context.event;

import com.michael.context.ApplicationEvent;
import com.michael.context.ApplicationListener;
import com.michael.core.ResolvableType;
import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-09-02 20:41
 */
public interface ApplicationEventMulticaster {

    void addApplicationListener(ApplicationListener<?> listener);

    void addApplicationListenerBean(String listenerBeanName);

    void removeApplicationListener(ApplicationListener<?> listener);

    void removeApplicationListenerBean(String listenerBeanName);

    void removeAllListeners();

    void multicastEvent(ApplicationEvent event);

    void multicastEvent(ApplicationEvent event, @Nullable ResolvableType eventType);
}
