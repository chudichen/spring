package com.michael.beans.factory.config;

import com.michael.beans.factory.ObjectFactory;
import jdk.internal.jline.internal.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-26 14:04
 */
public interface Scope {

    Object get(String name, ObjectFactory<?> objectFactory);

    @Nullable
    Object remove(String name);

    void registerDestructionCallback(String name, Runnable callback);

    @Nullable
    Object resolveContextualObject(String key);

    @Nullable
    String getConversationId();
}
