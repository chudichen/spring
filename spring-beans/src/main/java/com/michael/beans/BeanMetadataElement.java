package com.michael.beans;

import com.michael.lang.Nullable;

/**
 * 用于实现带有bean元数据信息的接口
 *
 * @author Michael Chu
 * @since 2019-08-28 15:01
 */
public interface BeanMetadataElement {

    /**
     * 返回{@code Object}元数据的配置信息吗，可能会为{@code null}
     */
    @Nullable
    default Object getSource() {
        return null;
    }
}
