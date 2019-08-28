package com.michael.beans.factory.xml;

import com.michael.commons.logging.Log;
import com.michael.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Michael Chu
 * @since 2019-08-28 15:29
 */
public class BeansDtdResolver implements EntityResolver {

    private static final String DTD_EXTENSION = ".dtd";

    private static final String DTD_NAME = "spring-beans";

    private static final Log logger = LogFactory.getLog(BeansDtdResolver.class);

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return null;
    }
}
