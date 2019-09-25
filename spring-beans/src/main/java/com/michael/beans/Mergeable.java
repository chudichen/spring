package com.michael.beans;

import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-09-25 08:56
 */
public interface Mergeable {

    /**
     * Is merging enabled for this particular instance?
     */
    boolean isMergeEnabled();

    /**
     * Merge the current value set with that of the supplied object.
     * <p>The supplied object is considered the parent, and values in
     * the callee's value set must override those of the supplied object.
     * @param parent the object to merge with
     * @return the result of the merge operation
     * @throws IllegalArgumentException if the supplied parent is {@code null}
     * @throws IllegalStateException if merging is not enabled for this instance
     * (i.e. {@code mergeEnabled} equals {@code false}).
     */
    Object merge(@Nullable Object parent);
}
