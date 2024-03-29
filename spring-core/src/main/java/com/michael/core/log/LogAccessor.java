package com.michael.core.log;

import com.michael.commons.logging.LogFactory;

import java.util.function.Supplier;

import com.michael.commons.logging.Log;
import com.michael.commons.logging.LogFactory;

/**
 * @author Michael Chu
 * @since 2019-09-04 17:46
 */
public class LogAccessor {

    private final Log log;


    /**
     * Create a new accessor for the given Commons Log.
     * @see LogFactory#getLog(Class)
     * @see LogFactory#getLog(String)
     */
    public LogAccessor(Log log) {
        this.log = log;
    }

    /**
     * Create a new accessor for the specified Commons Log category.
     * @see LogFactory#getLog(Class)
     */
    public LogAccessor(Class<?> logCategory) {
        this.log = LogFactory.getLog(logCategory);
    }

    /**
     * Create a new accessor for the specified Commons Log category.
     * @see LogFactory#getLog(String)
     */
    public LogAccessor(String logCategory) {
        this.log = LogFactory.getLog(logCategory);
    }


    /**
     * Return the target Commons Log.
     */
    public final Log getLog() {
        return this.log;
    }


    // Log level checks

    /**
     * Is fatal logging currently enabled?
     */
    public boolean isFatalEnabled() {
        return this.log.isFatalEnabled();
    }

    /**
     * Is error logging currently enabled?
     */
    public boolean isErrorEnabled() {
        return this.log.isErrorEnabled();
    }

    /**
     * Is warn logging currently enabled?
     */
    public boolean isWarnEnabled() {
        return this.log.isWarnEnabled();
    }

    /**
     * Is info logging currently enabled?
     */
    public boolean isInfoEnabled() {
        return this.log.isInfoEnabled();
    }

    /**
     * Is debug logging currently enabled?
     */
    public boolean isDebugEnabled() {
        return this.log.isDebugEnabled();
    }

    /**
     * Is trace logging currently enabled?
     */
    public boolean isTraceEnabled() {
        return this.log.isTraceEnabled();
    }


    // Plain log methods

    /**
     * Log a message with fatal log level.
     * @param message the message to log
     */
    public void fatal(CharSequence message) {
        this.log.fatal(message);
    }

    /**
     * Log an error with fatal log level.
     * @param cause the exception to log
     * @param message the message to log
     */
    public void fatal(Throwable cause, CharSequence message) {
        this.log.fatal(message, cause);
    }

    /**
     * Log a message with error log level.
     * @param message the message to log
     */
    public void error(CharSequence message) {
        this.log.error(message);
    }

    /**
     * Log an error with error log level.
     * @param cause the exception to log
     * @param message the message to log
     */
    public void error(Throwable cause, CharSequence message) {
        this.log.error(message, cause);
    }

    /**
     * Log a message with warn log level.
     * @param message the message to log
     */
    public void warn(CharSequence message) {
        this.log.warn(message);
    }

    /**
     * Log an error with warn log level.
     * @param cause the exception to log
     * @param message the message to log
     */
    public void warn(Throwable cause, CharSequence message) {
        this.log.warn(message, cause);
    }

    /**
     * Log a message with info log level.
     * @param message the message to log
     */
    public void info(CharSequence message) {
        this.log.info(message);
    }

    /**
     * Log an error with info log level.
     * @param cause the exception to log
     * @param message the message to log
     */
    public void info(Throwable cause, CharSequence message) {
        this.log.info(message, cause);
    }

    /**
     * Log a message with debug log level.
     * @param message the message to log
     */
    public void debug(CharSequence message) {
        this.log.debug(message);
    }

    /**
     * Log an error with debug log level.
     * @param cause the exception to log
     * @param message the message to log
     */
    public void debug(Throwable cause, CharSequence message) {
        this.log.debug(message, cause);
    }

    /**
     * Log a message with trace log level.
     * @param message the message to log
     */
    public void trace(CharSequence message) {
        this.log.trace(message);
    }

    /**
     * Log an error with trace log level.
     * @param cause the exception to log
     * @param message the message to log
     */
    public void trace(Throwable cause, CharSequence message) {
        this.log.trace(message, cause);
    }


    // Supplier-based log methods

    /**
     * Log a message with fatal log level.
     * @param messageSupplier a lazy supplier for the message to log
     */
    public void fatal(Supplier<? extends CharSequence> messageSupplier) {
        this.log.fatal(LogMessage.of(messageSupplier));
    }

    /**
     * Log an error with fatal log level.
     * @param cause the exception to log
     * @param messageSupplier a lazy supplier for the message to log
     */
    public void fatal(Throwable cause, Supplier<? extends CharSequence> messageSupplier) {
        this.log.fatal(LogMessage.of(messageSupplier), cause);
    }

    /**
     * Log a message with error log level.
     * @param messageSupplier a lazy supplier for the message to log
     */
    public void error(Supplier<? extends CharSequence> messageSupplier) {
        this.log.error(LogMessage.of(messageSupplier));
    }

    /**
     * Log an error with error log level.
     * @param cause the exception to log
     * @param messageSupplier a lazy supplier for the message to log
     */
    public void error(Throwable cause, Supplier<? extends CharSequence> messageSupplier) {
        this.log.error(LogMessage.of(messageSupplier), cause);
    }

    /**
     * Log a message with warn log level.
     * @param messageSupplier a lazy supplier for the message to log
     */
    public void warn(Supplier<? extends CharSequence> messageSupplier) {
        this.log.warn(LogMessage.of(messageSupplier));
    }

    /**
     * Log an error with warn log level.
     * @param cause the exception to log
     * @param messageSupplier a lazy supplier for the message to log
     */
    public void warn(Throwable cause, Supplier<? extends CharSequence> messageSupplier) {
        this.log.warn(LogMessage.of(messageSupplier), cause);
    }

    /**
     * Log a message with info log level.
     * @param messageSupplier a lazy supplier for the message to log
     */
    public void info(Supplier<? extends CharSequence> messageSupplier) {
        this.log.info(LogMessage.of(messageSupplier));
    }

    /**
     * Log an error with info log level.
     * @param cause the exception to log
     * @param messageSupplier a lazy supplier for the message to log
     */
    public void info(Throwable cause, Supplier<? extends CharSequence> messageSupplier) {
        this.log.info(LogMessage.of(messageSupplier), cause);
    }

    /**
     * Log a message with debug log level.
     * @param messageSupplier a lazy supplier for the message to log
     */
    public void debug(Supplier<? extends CharSequence> messageSupplier) {
        this.log.debug(LogMessage.of(messageSupplier));
    }

    /**
     * Log an error with debug log level.
     * @param cause the exception to log
     * @param messageSupplier a lazy supplier for the message to log
     */
    public void debug(Throwable cause, Supplier<? extends CharSequence> messageSupplier) {
        this.log.debug(LogMessage.of(messageSupplier), cause);
    }

    /**
     * Log a message with trace log level.
     * @param messageSupplier a lazy supplier for the message to log
     */
    public void trace(Supplier<? extends CharSequence> messageSupplier) {
        this.log.trace(LogMessage.of(messageSupplier));
    }

    /**
     * Log an error with trace log level.
     * @param cause the exception to log
     * @param messageSupplier a lazy supplier for the message to log
     */
    public void trace(Throwable cause, Supplier<? extends CharSequence> messageSupplier) {
        this.log.trace(LogMessage.of(messageSupplier), cause);
    }

}
