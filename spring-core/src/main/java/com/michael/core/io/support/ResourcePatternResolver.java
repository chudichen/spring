package com.michael.core.io.support;

import com.michael.core.io.Resource;
import com.michael.core.io.ResourceLoader;

import java.io.IOException;

/**
 * @author Michael Chu
 * @since 2019-08-27 18:00
 */
public interface ResourcePatternResolver extends ResourceLoader {

    String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    Resource[] getResources(String locationPattern) throws IOException;
}
