package com.michael.beans;

import com.michael.lang.Nullable;

import java.beans.PropertyEditor;

/**
 * @author Michael Chu
 * @since 2019-09-16 11:13
 */
public interface PropertyEditorRegistry {

    void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor);

    void registerCustomEditor(@Nullable Class<?> requiredType, @Nullable String propertyPath, PropertyEditor propertyEditor);

    @Nullable
    PropertyEditor findCustomEditor(@Nullable Class<?> requiredType, @Nullable String propertyPath);
}
