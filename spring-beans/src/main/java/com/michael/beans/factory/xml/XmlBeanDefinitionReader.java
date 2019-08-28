package com.michael.beans.factory.xml;

import com.michael.beans.factory.support.AbstractBeanDefinitionReader;
import com.michael.beans.factory.support.BeanDefinitionRegistry;
import com.michael.lang.Nullable;
import com.michael.util.xml.XmlValidationModeDetector;
import org.xml.sax.EntityResolver;


/**
 * @author Michael Chu
 * @since 2019-08-27 19:24
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

    public static final int VALIDATION_NONE = XmlValidationModeDetector.VALIDATION_NONE;

    public static final int VALIDATION_AUTO = XmlValidationModeDetector.VALIDATION_AUTO;

    private final XmlValidationModeDetector validationModeDetector = new XmlValidationModeDetector();

    private int validationMode = VALIDATION_AUTO;

    private boolean namespaceAware = false;

    @Nullable
    private EntityResolver entityResolver;

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }


    public void setEntityResolver(@Nullable EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    public void setValidating(boolean validating) {
        this.validationMode = (validating ? VALIDATION_AUTO : VALIDATION_NONE);
        this.namespaceAware = !validating;
    }
}
