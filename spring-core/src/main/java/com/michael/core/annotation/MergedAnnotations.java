package com.michael.core.annotation;

import com.michael.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author Michael Chu
 * @since 2019-09-25 16:35
 */
public interface MergedAnnotations extends Iterable<MergedAnnotation<Annotation>> {


    /**
     * Determine if the specified annotation is either directly present or
     * meta-present.
     * <p>Equivalent to calling {@code get(annotationType).isPresent()}.
     * @param annotationType the annotation type to check
     * @return {@code true} if the annotation is present
     */
    <A extends Annotation> boolean isPresent(Class<A> annotationType);

    /**
     * Determine if the specified annotation is either directly present or
     * meta-present.
     * <p>Equivalent to calling {@code get(annotationType).isPresent()}.
     * @param annotationType the fully qualified class name of the annotation type
     * to check
     * @return {@code true} if the annotation is present
     */
    boolean isPresent(String annotationType);

    /**
     * Determine if the specified annotation is directly present.
     * <p>Equivalent to calling {@code get(annotationType).isDirectlyPresent()}.
     * @param annotationType the annotation type to check
     * @return {@code true} if the annotation is present
     */
    <A extends Annotation> boolean isDirectlyPresent(Class<A> annotationType);

    /**
     * Determine if the specified annotation is directly present.
     * <p>Equivalent to calling {@code get(annotationType).isDirectlyPresent()}.
     * @param annotationType the fully qualified class name of the annotation type
     * to check
     * @return {@code true} if the annotation is present
     */
    boolean isDirectlyPresent(String annotationType);

    /**
     * Return the {@linkplain MergedAnnotationSelectors#nearest() nearest} matching
     * annotation or meta-annotation of the specified type, or
     * {@link MergedAnnotation#missing()} if none is present.
     * @param annotationType the annotation type to get
     * @return a {@link MergedAnnotation} instance
     */
    <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType);

    /**
     * Return the {@linkplain MergedAnnotationSelectors#nearest() nearest} matching
     * annotation or meta-annotation of the specified type, or
     * {@link MergedAnnotation#missing()} if none is present.
     * @param annotationType the annotation type to get
     * @param predicate a predicate that must match, or {@code null} if only
     * type matching is required
     * @return a {@link MergedAnnotation} instance
     * @see MergedAnnotationPredicates
     */
    <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType,
                                                   @Nullable Predicate<? super MergedAnnotation<A>> predicate);

    /**
     * Return a matching annotation or meta-annotation of the specified type, or
     * {@link MergedAnnotation#missing()} if none is present.
     * @param annotationType the annotation type to get
     * @param predicate a predicate that must match, or {@code null} if only
     * type matching is required
     * @param selector a selector used to choose the most appropriate annotation
     * within an aggregate, or {@code null} to select the
     * {@linkplain MergedAnnotationSelectors#nearest() nearest}
     * @return a {@link MergedAnnotation} instance
     * @see MergedAnnotationPredicates
     * @see MergedAnnotationSelectors
     */
    <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType,
                                                   @Nullable Predicate<? super MergedAnnotation<A>> predicate,
                                                   @Nullable MergedAnnotationSelector<A> selector);

    /**
     * Return the {@linkplain MergedAnnotationSelectors#nearest() nearest} matching
     * annotation or meta-annotation of the specified type, or
     * {@link MergedAnnotation#missing()} if none is present.
     * @param annotationType the fully qualified class name of the annotation type
     * to get
     * @return a {@link MergedAnnotation} instance
     */
    <A extends Annotation> MergedAnnotation<A> get(String annotationType);

    /**
     * Return the {@linkplain MergedAnnotationSelectors#nearest() nearest} matching
     * annotation or meta-annotation of the specified type, or
     * {@link MergedAnnotation#missing()} if none is present.
     * @param annotationType the fully qualified class name of the annotation type
     * to get
     * @param predicate a predicate that must match, or {@code null} if only
     * type matching is required
     * @return a {@link MergedAnnotation} instance
     * @see MergedAnnotationPredicates
     */
    <A extends Annotation> MergedAnnotation<A> get(String annotationType,
                                                   @Nullable Predicate<? super MergedAnnotation<A>> predicate);

    /**
     * Return a matching annotation or meta-annotation of the specified type, or
     * {@link MergedAnnotation#missing()} if none is present.
     * @param annotationType the fully qualified class name of the annotation type
     * to get
     * @param predicate a predicate that must match, or {@code null} if only
     * type matching is required
     * @param selector a selector used to choose the most appropriate annotation
     * within an aggregate, or {@code null} to select the
     * {@linkplain MergedAnnotationSelectors#nearest() nearest}
     * @return a {@link MergedAnnotation} instance
     * @see MergedAnnotationPredicates
     * @see MergedAnnotationSelectors
     */
    <A extends Annotation> MergedAnnotation<A> get(String annotationType,
                                                   @Nullable Predicate<? super MergedAnnotation<A>> predicate,
                                                   @Nullable MergedAnnotationSelector<A> selector);

    /**
     * Stream all annotations and meta-annotations that match the specified
     * type. The resulting stream follows the same ordering rules as
     * {@link #stream()}.
     * @param annotationType the annotation type to match
     * @return a stream of matching annotations
     */
    <A extends Annotation> Stream<MergedAnnotation<A>> stream(Class<A> annotationType);

    /**
     * Stream all annotations and meta-annotations that match the specified
     * type. The resulting stream follows the same ordering rules as
     * {@link #stream()}.
     * @param annotationType the fully qualified class name of the annotation type
     * to match
     * @return a stream of matching annotations
     */
    <A extends Annotation> Stream<MergedAnnotation<A>> stream(String annotationType);

    /**
     * Stream all annotations and meta-annotations contained in this collection.
     * The resulting stream is ordered first by the
     * {@linkplain MergedAnnotation#getAggregateIndex() aggregate index} and then
     * by the annotation distance (with the closest annotations first). This ordering
     * means that, for most use-cases, the most suitable annotations appear
     * earliest in the stream.
     * @return a stream of annotations
     */
    Stream<MergedAnnotation<Annotation>> stream();


    /**
     * Create a new {@link MergedAnnotations} instance containing all
     * annotations and meta-annotations from the specified element. The
     * resulting instance will not include any inherited annotations. If you
     * want to include those as well you should use
     * {@link #from(AnnotatedElement, SearchStrategy)} with an appropriate
     * {@link SearchStrategy}.
     * @param element the source element
     * @return a {@link MergedAnnotations} instance containing the element's
     * annotations
     */
    static MergedAnnotations from(AnnotatedElement element) {
        return from(element, SearchStrategy.DIRECT);
    }

    /**
     * Create a new {@link MergedAnnotations} instance containing all
     * annotations and meta-annotations from the specified element and,
     * depending on the {@link SearchStrategy}, related inherited elements.
     * @param element the source element
     * @param searchStrategy the search strategy to use
     * @return a {@link MergedAnnotations} instance containing the merged
     * element annotations
     */
    static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy) {
        return from(element, searchStrategy, RepeatableContainers.standardRepeatables(), AnnotationFilter.PLAIN);
    }

    /**
     * Create a new {@link MergedAnnotations} instance containing all
     * annotations and meta-annotations from the specified element and,
     * depending on the {@link SearchStrategy}, related inherited elements.
     * @param element the source element
     * @param searchStrategy the search strategy to use
     * @param repeatableContainers the repeatable containers that may be used by
     * the element annotations or the meta-annotations
     * @param annotationFilter an annotation filter used to restrict the
     * annotations considered
     * @return a {@link MergedAnnotations} instance containing the merged
     * element annotations
     */
    static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy,
                                  RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {

        return TypeMappedAnnotations.from(element, searchStrategy, repeatableContainers, annotationFilter);
    }

    /**
     * Create a new {@link MergedAnnotations} instance from the specified
     * annotations.
     * @param annotations the annotations to include
     * @return a {@link MergedAnnotations} instance containing the annotations
     * @see #from(Object, Annotation...)
     */
    static MergedAnnotations from(Annotation... annotations) {
        return from(null, annotations);
    }

    /**
     * Create a new {@link MergedAnnotations} instance from the specified
     * annotations.
     * @param source the source for the annotations. This source is used only
     * for information and logging. It does not need to <em>actually</em>
     * contain the specified annotations, and it will not be searched.
     * @param annotations the annotations to include
     * @return a {@link MergedAnnotations} instance containing the annotations
     * @see #from(Annotation...)
     * @see #from(AnnotatedElement)
     */
    static MergedAnnotations from(@Nullable Object source, Annotation... annotations) {
        return from(source, annotations, RepeatableContainers.standardRepeatables(), AnnotationFilter.PLAIN);
    }

    /**
     * Create a new {@link MergedAnnotations} instance from the specified
     * annotations.
     * @param source the source for the annotations. This source is used only
     * for information and logging. It does not need to <em>actually</em>
     * contain the specified annotations, and it will not be searched.
     * @param annotations the annotations to include
     * @param repeatableContainers the repeatable containers that may be used by
     * meta-annotations
     * @param annotationFilter an annotation filter used to restrict the
     * annotations considered
     * @return a {@link MergedAnnotations} instance containing the annotations
     */
    static MergedAnnotations from(@Nullable Object source, Annotation[] annotations,
                                  RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {

        return TypeMappedAnnotations.from(source, annotations, repeatableContainers, annotationFilter);
    }

    /**
     * Create a new {@link MergedAnnotations} instance from the specified
     * collection of directly present annotations. This method allows a
     * {@link MergedAnnotations} instance to be created from annotations that
     * are not necessarily loaded using reflection. The provided annotations
     * must all be {@link MergedAnnotation#isDirectlyPresent() directly present}
     * and must have a {@link MergedAnnotation#getAggregateIndex() aggregate
     * index} of {@code 0}.
     * <p>
     * The resulting {@link MergedAnnotations} instance will contain both the
     * specified annotations, and any meta-annotations that can be read using
     * reflection.
     * @param annotations the annotations to include
     * @return a {@link MergedAnnotations} instance containing the annotations
     * @see MergedAnnotation#of(ClassLoader, Object, Class, java.util.Map)
     */
    static MergedAnnotations of(Collection<MergedAnnotation<?>> annotations) {
        return MergedAnnotationsCollection.of(annotations);
    }


    /**
     * Search strategies supported by
     * {@link MergedAnnotations#from(AnnotatedElement, SearchStrategy)}.
     *
     * <p>Each strategy creates a different set of aggregates that will be
     * combined to create the final {@link MergedAnnotations}.
     */
    enum SearchStrategy {

        /**
         * Find only directly declared annotations, without considering
         * {@link Inherited @Inherited} annotations and without searching
         * superclasses or implemented interfaces.
         */
        DIRECT,

        /**
         * Find all directly declared annotations as well as any
         * {@link Inherited @Inherited} superclass annotations. This strategy
         * is only really useful when used with {@link Class} types since the
         * {@link Inherited @Inherited} annotation is ignored for all other
         * {@linkplain AnnotatedElement annotated elements}. This strategy does
         * not search implemented interfaces.
         */
        INHERITED_ANNOTATIONS,

        /**
         * Find all directly declared and superclass annotations. This strategy
         * is similar to {@link #INHERITED_ANNOTATIONS} except the annotations
         * do not need to be meta-annotated with {@link Inherited @Inherited}.
         * This strategy does not search implemented interfaces.
         */
        SUPERCLASS,

        /**
         * Perform a full search of all related elements, including those on any
         * superclasses or implemented interfaces. Superclass annotations do
         * not need to be meta-annotated with {@link Inherited @Inherited}.
         */
        EXHAUSTIVE
    }

}
