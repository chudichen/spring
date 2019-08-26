package com.michael.core.env;

/**
 * @author Michael Chu
 * @since 2019-08-23 10:39
 */
public abstract class AbstractEnvironment implements ConfigurableEnvironment {

    private final MutablePropertySources propertySources = new MutablePropertySources();

    private final ConfigurablePropertyResolver propertyResolver =
            new PropertySourcesPropertyResolver(this.propertySources);

    @Override
    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        return null;
    }
}
