package com.michael.beans.factory.xml;

import com.michael.lang.Nullable;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Michael Chu
 * @since 2019-08-28 15:27
 */
public class DelegatingEntityResolver implements EntityResolver {

    public static final String DTD_SUFFIX = ".dtd";

    public static final String XSD_SUFFIX = ".xsd";

    private final EntityResolver dtdResolver;

    private final EntityResolver schemaResolver;

    public DelegatingEntityResolver(@Nullable ClassLoader classLoader) {
        this.dtdResolver = new BeansDtdResolver();
        this.schemaResolver = new PluggableSchemaResolver(classLoader);
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return null;
    }
}
