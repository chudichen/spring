package com.michael.beans.factory.support;

import com.michael.beans.factory.config.BeanDefinitionHolder;
import com.michael.beans.factory.config.DependencyDescriptor;

/**
 * 判断指定bean是否有资格成为自动装配的依赖的策略接口
 *
 * @author Michael Chu
 * @since 2019-09-18 10:02
 */
public interface AutowireCandidateResolver {

    default boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        return bdHolder.getBeanDefinition().isAutowireCandidate();
    }

}
