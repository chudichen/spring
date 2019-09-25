package com.michael.core.annotation;

import com.michael.commons.logging.Log;
import com.michael.commons.logging.LogFactory;
import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-09-25 17:09
 */
enum IntrospectionFailureLogger {

    DEBUG {
        @Override
        public boolean isEnabled() {
            return getLogger().isDebugEnabled();
        }
        @Override
        public void log(String message) {
            getLogger().debug(message);
        }
    },

    INFO {
        @Override
        public boolean isEnabled() {
            return getLogger().isInfoEnabled();
        }
        @Override
        public void log(String message) {
            getLogger().info(message);
        }
    };


    @Nullable
    private static Log logger;


    void log(String message, @Nullable Object source, Exception ex) {
        String on = (source != null ? " on " + source : "");
        log(message + on + ": " + ex);
    }

    abstract boolean isEnabled();

    abstract void log(String message);


    private static Log getLogger() {
        Log logger = IntrospectionFailureLogger.logger;
        if (logger == null) {
            logger = LogFactory.getLog(MergedAnnotation.class);
            IntrospectionFailureLogger.logger = logger;
        }
        return logger;
    }
}
