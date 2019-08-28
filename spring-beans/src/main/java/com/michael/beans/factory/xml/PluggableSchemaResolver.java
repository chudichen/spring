package com.michael.beans.factory.xml;

import com.michael.commons.logging.Log;
import com.michael.commons.logging.LogFactory;
import com.michael.lang.Nullable;
import com.michael.util.Assert;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Map;

/**
 * @author Michael Chu
 * @since 2019-08-28 15:30
 */
public class PluggableSchemaResolver implements EntityResolver {

    public static final String DEFAULT_SCHEMA_MAPPINGS_LOCATION = "META-INF/spring.schemas";


    private static final Log logger = LogFactory.getLog(PluggableSchemaResolver.class);

    @Nullable
    private final ClassLoader classLoader;

    private final String schemaMappingsLocation;

    /** Stores the mapping of schema URL -> local schema path. */
    @Nullable
    private volatile Map<String, String> schemaMappings;

    public PluggableSchemaResolver(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.schemaMappingsLocation = DEFAULT_SCHEMA_MAPPINGS_LOCATION;
    }

    public PluggableSchemaResolver(@Nullable ClassLoader classLoader, String schemaMappingsLocation) {
        Assert.hasText(schemaMappingsLocation, "'schemaMappingsLocation' must not be empty");
        this.classLoader = classLoader;
        this.schemaMappingsLocation = schemaMappingsLocation;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return null;
    }
}
