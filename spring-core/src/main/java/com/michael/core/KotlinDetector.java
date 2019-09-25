package com.michael.core;

import com.michael.commons.logging.Log;
import com.michael.commons.logging.LogFactory;
import com.michael.lang.Nullable;
import com.michael.util.ClassUtils;

import java.lang.annotation.Annotation;

/**
 * @author Michael Chu
 * @since 2019-09-25 08:51
 */
public abstract class KotlinDetector {

    private static final Log logger = LogFactory.getLog(KotlinDetector.class);

    @Nullable
    private static final Class<? extends Annotation> kotlinMetadata;

    private static final boolean kotlinReflectPresent;

    static {
        Class<?> metadata;
        ClassLoader classLoader = KotlinDetector.class.getClassLoader();
        try {
            metadata = ClassUtils.forName("kotlin.Metadata", classLoader);
        }
        catch (ClassNotFoundException ex) {
            // Kotlin API not available - no Kotlin support
            metadata = null;
        }
        kotlinMetadata = (Class<? extends Annotation>) metadata;
        kotlinReflectPresent = ClassUtils.isPresent("kotlin.reflect.full.KClasses", classLoader);
        if (kotlinMetadata != null && !kotlinReflectPresent) {
            logger.info("Kotlin reflection implementation not found at runtime, related features won't be available.");
        }
    }

    /**
     * Determine whether Kotlin is present in general.
     */
    public static boolean isKotlinPresent() {
        return (kotlinMetadata != null);
    }

    /**
     * Determine whether Kotlin reflection is present.
     * @since 5.1
     */
    public static boolean isKotlinReflectPresent() {
        return kotlinReflectPresent;
    }

    /**
     * Determine whether the given {@code Class} is a Kotlin type
     * (with Kotlin metadata present on it).
     */
    public static boolean isKotlinType(Class<?> clazz) {
        return (kotlinMetadata != null && clazz.getDeclaredAnnotation(kotlinMetadata) != null);
    }
}
