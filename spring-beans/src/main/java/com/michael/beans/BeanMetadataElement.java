package com.michael.beans;

import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-28 15:01
 */
public interface BeanMetadataElement {

    @Nullable
    default Object getSource() {
        return null;
    }
}
