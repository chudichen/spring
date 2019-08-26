package com.michael.core.env;

/**
 * @author Michael Chu
 * @since 2019-08-23 10:33
 */
public interface PropertyResolver {

    String resolveRequiredPlaceholders(String text) throws IllegalArgumentException;
}
