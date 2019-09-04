package com.michael.beans.factory;

import com.michael.beans.BeansException;
import com.michael.lang.Nullable;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Michael Chu
 * @since 2019-09-02 20:45
 */
public interface ObjectProvider<T> extends ObjectFactory<T>, Iterable<T> {

    T getObject(Object... args) throws BeansException;

    @Nullable
    T getIfAvailable() throws BeansException;

    default T getIfAvailable(Supplier<T> defaultSupplier) throws BeansException {
        T dependency = getIfAvailable();
        return (dependency != null ? dependency : defaultSupplier.get());
    }

    default void ifAvailable(Consumer<T> dependencyConsumer) throws BeansException {
        T dependency = getIfAvailable();
        if (dependency != null) {
            dependencyConsumer.accept(dependency);
        }
    }

    @Nullable
    T getIfUnique() throws BeansException;

    default T getIfUnique(Supplier<T> defaultSupplier) throws BeansException {
        T dependency = getIfUnique();
        return (dependency != null ? dependency : defaultSupplier.get());
    }

    default void ifUnique(Consumer<T> dependencyConsumer) throws BeansException {
        T dependency = getIfUnique();
        if (dependency != null) {
            dependencyConsumer.accept(dependency);
        }
    }

    @Override
    default Iterator<T> iterator() {
        return stream().iterator();
    }

    /**
     * Return a sequential {@link Stream} over all matching object instances,
     * without specific ordering guarantees (but typically in registration order).
     * @since 5.1
     * @see #iterator()
     * @see #orderedStream()
     */
    default Stream<T> stream() {
        throw new UnsupportedOperationException("Multi element access not supported");
    }

    /**
     * Return a sequential {@link Stream} over all matching object instances,
     * pre-ordered according to the factory's common order comparator.
     * <p>In a standard Spring application context, this will be ordered
     * according to {@link org.springframework.core.Ordered} conventions,
     * and in case of annotation-based configuration also considering the
     * {@link org.springframework.core.annotation.Order} annotation,
     * analogous to multi-element injection points of list/array type.
     * @since 5.1
     * @see #stream()
     * @see org.springframework.core.OrderComparator
     */
    default Stream<T> orderedStream() {
        throw new UnsupportedOperationException("Ordered element access not supported");
    }
}
