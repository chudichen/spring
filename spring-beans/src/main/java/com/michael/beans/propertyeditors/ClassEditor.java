package com.michael.beans.propertyeditors;

import com.michael.lang.Nullable;
import com.michael.util.ClassUtils;
import com.michael.util.StringUtils;

import java.beans.PropertyEditorSupport;

/**
 * @author Michael Chu
 * @since 2019-09-25 09:18
 */
public class ClassEditor extends PropertyEditorSupport {

    @Nullable
    private final ClassLoader classLoader;


    /**
     * Create a default ClassEditor, using the thread context ClassLoader.
     */
    public ClassEditor() {
        this(null);
    }

    /**
     * Create a default ClassEditor, using the given ClassLoader.
     * @param classLoader the ClassLoader to use
     * (or {@code null} for the thread context ClassLoader)
     */
    public ClassEditor(@Nullable ClassLoader classLoader) {
        this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
    }


    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasText(text)) {
            setValue(ClassUtils.resolveClassName(text.trim(), this.classLoader));
        }
        else {
            setValue(null);
        }
    }

    @Override
    public String getAsText() {
        Class<?> clazz = (Class<?>) getValue();
        if (clazz != null) {
            return ClassUtils.getQualifiedName(clazz);
        }
        else {
            return "";
        }
    }

}
