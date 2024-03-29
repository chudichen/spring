package com.michael.core;

import com.michael.lang.Nullable;
import com.michael.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author Michael Chu
 * @since 2019-09-25 16:18
 */
public abstract class ParameterizedTypeReference<T> {


    private final Type type;


    protected ParameterizedTypeReference() {
        Class<?> parameterizedTypeReferenceSubclass = findParameterizedTypeReferenceSubclass(getClass());
        Type type = parameterizedTypeReferenceSubclass.getGenericSuperclass();
        Assert.isInstanceOf(ParameterizedType.class, type, "Type must be a parameterized type");
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Assert.isTrue(actualTypeArguments.length == 1, "Number of type arguments must be 1");
        this.type = actualTypeArguments[0];
    }

    private ParameterizedTypeReference(Type type) {
        this.type = type;
    }


    public Type getType() {
        return this.type;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return (this == other || (other instanceof ParameterizedTypeReference &&
                this.type.equals(((ParameterizedTypeReference<?>) other).type)));
    }

    @Override
    public int hashCode() {
        return this.type.hashCode();
    }

    @Override
    public String toString() {
        return "ParameterizedTypeReference<" + this.type + ">";
    }


    /**
     * Build a {@code ParameterizedTypeReference} wrapping the given type.
     * @param type a generic type (possibly obtained via reflection,
     * e.g. from {@link java.lang.reflect.Method#getGenericReturnType()})
     * @return a corresponding reference which may be passed into
     * {@code ParameterizedTypeReference}-accepting methods
     * @since 4.3.12
     */
    public static <T> ParameterizedTypeReference<T> forType(Type type) {
        return new ParameterizedTypeReference<T>(type) {
        };
    }

    private static Class<?> findParameterizedTypeReferenceSubclass(Class<?> child) {
        Class<?> parent = child.getSuperclass();
        if (Object.class == parent) {
            throw new IllegalStateException("Expected ParameterizedTypeReference superclass");
        }
        else if (ParameterizedTypeReference.class == parent) {
            return child;
        }
        else {
            return findParameterizedTypeReferenceSubclass(parent);
        }
    }

}
