package com.michael.beans.factory.xml;

import com.michael.commons.logging.Log;
import com.michael.commons.logging.LogFactory;
import com.michael.core.io.ResourceLoader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Michael Chu
 * @since 2019-08-28 15:27
 */
public class ResourceEntityResolver extends DelegatingEntityResolver {

    private static final Log logger = LogFactory.getLog(ResourceEntityResolver.class);

    private final ResourceLoader resourceLoader;

    public ResourceEntityResolver(ResourceLoader resourceLoader) {
        super(resourceLoader.getClassLoader());
        this.resourceLoader = resourceLoader;
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return super.resolveEntity(publicId, systemId);
    }
}
