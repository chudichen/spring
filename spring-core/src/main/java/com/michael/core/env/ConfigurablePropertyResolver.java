package com.michael.core.env;

/**
 * @author Michael Chu
 * @since 2019-08-23 10:33
 */
public interface ConfigurablePropertyResolver extends PropertyResolver {

    /**
     * 对属性进行验证，可以结合{#initPropertySources}
     *
     * @throws MissingRequiredPropertiesException
     */
    void validateRequiredProperties() throws MissingRequiredPropertiesException;
}
