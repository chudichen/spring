package com.michael.core.annotation;

import java.lang.annotation.Annotation;

/**
 * @author Michael Chu
 * @since 2019-09-25 16:51
 */
@FunctionalInterface
public interface AnnotationFilter {

    /**
     * {@link AnnotationFilter} that matches annotations in the
     * {@code java.lang} and {@code org.springframework.lang} packages
     * and their subpackages.
     */
    AnnotationFilter PLAIN = packages("java.lang", "org.springframework.lang");

    /**
     * {@link AnnotationFilter} that matches annotations in the
     * {@code java} and {@code javax} packages and their subpackages.
     */
    AnnotationFilter JAVA = packages("java", "javax");

    /**
     * {@link AnnotationFilter} that never matches and can be used when no
     * filtering is needed.
     */
    AnnotationFilter NONE = new AnnotationFilter() {
        @Override
        public boolean matches(String typeName) {
            return false;
        }
        @Override
        public String toString() {
            return "No annotation filtering";
        }
    };


    /**
     * Test if the given annotation matches the filter.
     * @param annotation the annotation to test
     * @return {@code true} if the annotation matches
     */
    default boolean matches(Annotation annotation) {
        return matches(annotation.annotationType());
    }

    /**
     * Test if the given type matches the filter.
     * @param type the annotation type to test
     * @return {@code true} if the annotation matches
     */
    default boolean matches(Class<?> type) {
        return matches(type.getName());
    }

    /**
     * Test if the given type name matches the filter.
     * @param typeName the fully qualified class name of the annotation type to test
     * @return {@code true} if the annotation matches
     */
    boolean matches(String typeName);


    /**
     * Create a new {@link AnnotationFilter} that matches annotations in the
     * specified packages.
     * @param packages the annotation packages that should match
     * @return a new {@link AnnotationFilter} instance
     */
    static AnnotationFilter packages(String... packages) {
        return new PackagesAnnotationFilter(packages);
    }

}
