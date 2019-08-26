package com.michael.util;

/**
 * @author Michael Chu
 * @since 2019-08-23 11:00
 */
public abstract class SystemPropertyUtils {

    /** Prefix for system property placeholders: "${". */
    public static final String PLACEHOLDER_PREFIX = "${";

    /** Suffix for system property placeholders: "}". */
    public static final String PLACEHOLDER_SUFFIX = "}";

    /** Value separator for system property placeholders: ":". */
    public static final String VALUE_SEPARATOR = ":";
}
