package com.michael.context.support;

import com.michael.beans.factory.config.BeanFactoryPostProcessor;
import com.michael.beans.factory.config.ConfigurableListableBeanFactory;
import com.michael.beans.factory.support.BeanDefinitionRegistry;
import com.michael.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import com.michael.core.PriorityOrdered;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Michael Chu
 * @since 2019-08-27 09:25
 */
final class PostProcessorRegistrationDelegate {

    private PostProcessorRegistrationDelegate() {}


    public static void invokeBeanFactoryPostProcessors(
            ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

        Set<String> processedBeans = new HashSet<>();

        if (beanFactory instanceof BeanDefinitionRegistry) {
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
            List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
            List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

            for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
                if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                    BeanDefinitionRegistryPostProcessor registryProcessor =
                            (BeanDefinitionRegistryPostProcessor) postProcessor;
                    registryProcessor.postProcessBeanDefinitionRegistry(registry);
                    registryProcessors.add(registryProcessor);
                } else {
                    regularPostProcessors.add(postProcessor);
                }
            }

            List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

            String[] postProcessorNames =
                    beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
            for (String ppName : postProcessorNames) {
                if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                    currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                    processedBeans.add(ppName);
                }
            }
        }
    }
}
