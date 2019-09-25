package com.michael.core.annotation;

import com.michael.core.DecoratingProxy;
import com.michael.core.OrderComparator;
import com.michael.lang.Nullable;
import com.michael.core.annotation.MergedAnnotations.SearchStrategy;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.List;

/**
 * @author Michael Chu
 * @since 2019-09-25 16:34
 */
public class AnnotationAwareOrderComparator extends OrderComparator {

    /**
     * Shared default instance of {@code AnnotationAwareOrderComparator}.
     */
    public static final AnnotationAwareOrderComparator INSTANCE = new AnnotationAwareOrderComparator();


    /**
     * This implementation checks for {@link Order @Order} or
     * {@link javax.annotation.Priority @Priority} on various kinds of
     * elements, in addition to the {@link org.springframework.core.Ordered}
     * check in the superclass.
     */
    @Override
    @Nullable
    protected Integer findOrder(Object obj) {
        Integer order = super.findOrder(obj);
        if (order != null) {
            return order;
        }
        return findOrderFromAnnotation(obj);
    }

    @Nullable
    private Integer findOrderFromAnnotation(Object obj) {
        AnnotatedElement element = (obj instanceof AnnotatedElement ? (AnnotatedElement) obj : obj.getClass());
        MergedAnnotations annotations = MergedAnnotations.from(element, SearchStrategy.EXHAUSTIVE);
        Integer order = OrderUtils.getOrderFromAnnotations(element, annotations);
        if (order == null && obj instanceof DecoratingProxy) {
            return findOrderFromAnnotation(((DecoratingProxy) obj).getDecoratedClass());
        }
        return order;
    }

    /**
     * This implementation retrieves an @{@link javax.annotation.Priority}
     * value, allowing for additional semantics over the regular @{@link Order}
     * annotation: typically, selecting one object over another in case of
     * multiple matches but only one object to be returned.
     */
    @Override
    @Nullable
    public Integer getPriority(Object obj) {
        if (obj instanceof Class) {
            return OrderUtils.getPriority((Class<?>) obj);
        }
        Integer priority = OrderUtils.getPriority(obj.getClass());
        if (priority == null  && obj instanceof DecoratingProxy) {
            return getPriority(((DecoratingProxy) obj).getDecoratedClass());
        }
        return priority;
    }


    /**
     * Sort the given List with a default AnnotationAwareOrderComparator.
     * <p>Optimized to skip sorting for lists with size 0 or 1,
     * in order to avoid unnecessary array extraction.
     * @param list the List to sort
     * @see java.util.List#sort(java.util.Comparator)
     */
    public static void sort(List<?> list) {
        if (list.size() > 1) {
            list.sort(INSTANCE);
        }
    }

    /**
     * Sort the given array with a default AnnotationAwareOrderComparator.
     * <p>Optimized to skip sorting for lists with size 0 or 1,
     * in order to avoid unnecessary array extraction.
     * @param array the array to sort
     * @see java.util.Arrays#sort(Object[], java.util.Comparator)
     */
    public static void sort(Object[] array) {
        if (array.length > 1) {
            Arrays.sort(array, INSTANCE);
        }
    }

    /**
     * Sort the given array or List with a default AnnotationAwareOrderComparator,
     * if necessary. Simply skips sorting when given any other value.
     * <p>Optimized to skip sorting for lists with size 0 or 1,
     * in order to avoid unnecessary array extraction.
     * @param value the array or List to sort
     * @see java.util.Arrays#sort(Object[], java.util.Comparator)
     */
    public static void sortIfNecessary(Object value) {
        if (value instanceof Object[]) {
            sort((Object[]) value);
        }
        else if (value instanceof List) {
            sort((List<?>) value);
        }
    }

}
