package com.michael.beans;

import com.michael.lang.Nullable;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author Michael Chu
 * @since 2019-09-19 10:17
 */
public class MutablePropertyValues implements PropertyValues, Serializable {

    private final List<PropertyValue> propertyValueList;

    @Nullable
    private Set<String> processedProperties;

    private volatile boolean converted = false;

    public MutablePropertyValues() {
        this.propertyValueList = new ArrayList<>(0);
    }

    public boolean isEmpty() {
        return this.propertyValueList.isEmpty();
    }

    @Override
    public Iterator<PropertyValue> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super PropertyValue> action) {

    }

    @Override
    public Spliterator<PropertyValue> spliterator() {
        return null;
    }
}
