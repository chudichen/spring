package com.michael.core.env;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Michael Chu
 * @since 2019-08-23 10:42
 */
public class MutablePropertySources implements PropertySources {

    private final List<PropertySource<?>> propertySourceList = new CopyOnWriteArrayList<>();

    @Override
    public Iterator<PropertySource<?>> iterator() {
        return this.propertySourceList.iterator();
    }
}
