package com.michael.core.io;

/**
 * @author Michael Chu
 * @since 2019-09-04 17:21
 */
public interface ContextResource extends Resource {

    String getPathWithinContext();
}
