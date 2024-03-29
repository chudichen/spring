package com.michael.beans.factory.support;

import com.michael.beans.factory.BeanDefinitionStoreException;
import com.michael.beans.factory.BeanFactoryUtils;
import com.michael.beans.factory.config.BeanDefinition;
import com.michael.util.ObjectUtils;
import com.michael.util.StringUtils;

/**
 * @author Michael Chu
 * @since 2019-08-28 15:03
 */
public class BeanDefinitionReaderUtils {

    public static final String GENERATED_BEAN_NAME_SEPARATOR = BeanFactoryUtils.GENERATED_BEAN_NAME_SEPARATOR;

    public static String generateBeanName(BeanDefinition beanDefinition, BeanDefinitionRegistry registry)
            throws BeanDefinitionStoreException {

        return generateBeanName(beanDefinition, registry, false);
    }

    public static String generateBeanName(
            BeanDefinition definition, BeanDefinitionRegistry registry, boolean isInnerBean)
            throws BeanDefinitionStoreException {

        String generatedBeanName = definition.getBeanClassName();
        if (generatedBeanName == null) {
            if (definition.getParentName() != null) {
                generatedBeanName = definition.getParentName() + "$child";
            }
            else if (definition.getFactoryBeanName() != null) {
                generatedBeanName = definition.getFactoryBeanName() + "$created";
            }
        }
        if (!StringUtils.hasText(generatedBeanName)) {
            throw new BeanDefinitionStoreException("Unnamed bean definition specifies neither " +
                    "'class' nor 'parent' nor 'factory-bean' - can't generate bean name");
        }

        String id = generatedBeanName;
        if (isInnerBean) {
            // Inner bean: generate identity hashcode suffix.
            id = generatedBeanName + GENERATED_BEAN_NAME_SEPARATOR + ObjectUtils.getIdentityHexString(definition);
        }
        else {
            // Top-level bean: use plain class name with unique suffix if necessary.
            return uniqueBeanName(generatedBeanName, registry);
        }
        return id;
    }

    /**
     * Turn the given bean name into a unique bean name for the given bean factory,
     * appending a unique counter as suffix if necessary.
     * @param beanName the original bean name
     * @param registry the bean factory that the definition is going to be
     * registered with (to check for existing bean names)
     * @return the unique bean name to use
     * @since 5.1
     */
    public static String uniqueBeanName(String beanName, BeanDefinitionRegistry registry) {
        String id = beanName;
        int counter = -1;

        // Increase counter until the id is unique.
        while (counter == -1 || registry.containsBeanDefinition(id)) {
            counter++;
            id = beanName + GENERATED_BEAN_NAME_SEPARATOR + counter;
        }
        return id;
    }
}
