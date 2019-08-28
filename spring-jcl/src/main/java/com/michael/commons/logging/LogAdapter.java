package com.michael.commons.logging;

/**
 * @author Michael Chu
 * @since 2019-08-27 16:19
 */
final class LogAdapter {

    private static final String LOG4J_SPI = "org.apache.logging.log4j.spi.ExtendedLogger";

    private static final String LOG4J_SLF4J_PROVIDER = "org.apache.logging.slf4j.SLF4JProvider";

    private static final String SLF4J_SPI = "org.slf4j.spi.LocationAwareLogger";

    private static final String SLF4J_API = "org.slf4j.Logger";

}
