package com.michael.beans.factory;

import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-09-16 09:12
 */
public interface FactoryBean<T> {

    /**
     * 返回由{@link FactoryBean}创建的bean实例，如果是isSingleton()返回true，
     * 则该实例会放到Spring容器中单例缓存池中。
     *
     * @return
     * @throws Exception
     */
    @Nullable
    T getObject() throws Exception;

    /**
     * 返回{@link FactoryBean}创建的bean类型
     *
     * @return
     */
    @Nullable
    Class<?> getObjectType();

    /**
     * 返回由FactoryBean创建的bean实例是singleton还是prototype
     *
     * @return
     */
    default boolean isSingleton() {
        return true;
    }

}
