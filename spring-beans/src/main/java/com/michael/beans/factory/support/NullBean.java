package com.michael.beans.factory.support;

import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-09-20 13:57
 */
final class NullBean {

    NullBean() {}

    @Override
    public boolean equals(@Nullable Object obj) {
        return (this == obj || obj == null);
    }

    @Override
    public int hashCode() {
        return NullBean.class.hashCode();
    }

    @Override
    public String toString() {
        return "null";
    }
}
