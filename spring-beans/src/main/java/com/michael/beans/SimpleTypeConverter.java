package com.michael.beans;

/**
 * @author Michael Chu
 * @since 2019-09-25 09:09
 */
public class SimpleTypeConverter extends TypeConverterSupport {

    public SimpleTypeConverter() {
        this.typeConverterDelegate = new TypeConverterDelegate(this);
        registerDefaultEditors();
    }
}
