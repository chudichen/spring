package com.michael.core.env;

import com.michael.lang.Nullable;
import com.michael.util.PropertyPlaceholderHelper;
import com.michael.util.SystemPropertyUtils;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Michael Chu
 * @since 2019-08-23 10:46
 */
public abstract class AbstractPropertyResolver implements ConfigurablePropertyResolver {

    @Nullable
    private PropertyPlaceholderHelper strictHelper;

    @Nullable
    private PropertyPlaceholderHelper nonStrictHelper;

    private boolean ignoreUnresolvableNestedPlaceholders = false;

    private String placeholderPrefix = SystemPropertyUtils.PLACEHOLDER_PREFIX;

    private String placeholderSuffix = SystemPropertyUtils.PLACEHOLDER_SUFFIX;

    @Nullable
    private String valueSeparator = SystemPropertyUtils.VALUE_SEPARATOR;

    private final Set<String> requiredProperties = new LinkedHashSet<>();

    @Override
    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        if (this.strictHelper == null) {
            this.strictHelper = createPlaceholderHelper(false);
        }
        return null;
    }

    private PropertyPlaceholderHelper createPlaceholderHelper(boolean ignoreUnresolvablePlaceholders) {
        return new PropertyPlaceholderHelper(this.placeholderPrefix, this.placeholderSuffix,
                this.valueSeparator, ignoreUnresolvablePlaceholders);
    }
}
