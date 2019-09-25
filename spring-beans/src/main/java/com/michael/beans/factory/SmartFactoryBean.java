package com.michael.beans.factory;

import com.michael.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * @author Michael Chu
 * @since 2019-09-24 20:43
 */
public interface SmartFactoryBean<T> extends FactoryBean<T> {


    /**
     * Is the object managed by this factory a prototype? That is,
     * will {@link #getObject()} always return an independent instance?
     * <p>The prototype status of the FactoryBean itself will generally
     * be provided by the owning {@link BeanFactory}; usually, it has to be
     * defined as singleton there.
     * <p>This method is supposed to strictly check for independent instances;
     * it should not return {@code true} for scoped objects or other
     * kinds of non-singleton, non-independent objects. For this reason,
     * this is not simply the inverted form of {@link #isSingleton()}.
     * <p>The default implementation returns {@code false}.
     * @return whether the exposed object is a prototype
     * @see #getObject()
     * @see #isSingleton()
     */
    default boolean isPrototype() {
        return false;
    }

    /**
     * Does this FactoryBean expect eager initialization, that is,
     * eagerly initialize itself as well as expect eager initialization
     * of its singleton object (if any)?
     * <p>A standard FactoryBean is not expected to initialize eagerly:
     * Its {@link #getObject()} will only be called for actual access, even
     * in case of a singleton object. Returning {@code true} from this
     * method suggests that {@link #getObject()} should be called eagerly,
     * also applying post-processors eagerly. This may make sense in case
     * of a {@link #isSingleton() singleton} object, in particular if
     * post-processors expect to be applied on startup.
     * <p>The default implementation returns {@code false}.
     * @return whether eager initialization applies
     * @see ConfigurableListableBeanFactory#preInstantiateSingletons()
     */
    default boolean isEagerInit() {
        return false;
    }
}
