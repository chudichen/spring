package com.michael.core.log;

import com.michael.lang.Nullable;
import com.michael.util.Assert;

import java.util.function.Supplier;

/**
 * @author Michael Chu
 * @since 2019-09-04 17:51
 */
public abstract class LogMessage implements CharSequence {

    @Nullable
    private String result;


    @Override
    public int length() {
        return toString().length();
    }

    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

    /**
     * This will be called by the logging provider, potentially once
     * per log target (therefore locally caching the result here).
     */
    @Override
    public String toString() {
        if (this.result == null) {
            this.result = buildString();
        }
        return this.result;
    }

    abstract String buildString();


    /**
     * Build a lazily resolving message from the given supplier.
     * @param supplier the supplier (typically bound to a Java 8 lambda expression)
     * @see #toString()
     */
    public static LogMessage of(Supplier<? extends CharSequence> supplier) {
        return new SupplierMessage(supplier);
    }

    /**
     * Build a lazily formatted message from the given format string and argument.
     * @param format the format string (following {@link String#format} rules)
     * @param arg1 the argument
     * @see String#format(String, Object...)
     */
    public static LogMessage format(String format, Object arg1) {
        return new FormatMessage1(format, arg1);
    }

    /**
     * Build a lazily formatted message from the given format string and arguments.
     * @param format the format string (following {@link String#format} rules)
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @see String#format(String, Object...)
     */
    public static LogMessage format(String format, Object arg1, Object arg2) {
        return new FormatMessage2(format, arg1, arg2);
    }

    /**
     * Build a lazily formatted message from the given format string and arguments.
     * @param format the format string (following {@link String#format} rules)
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @param arg3 the third argument
     * @see String#format(String, Object...)
     */
    public static LogMessage format(String format, Object arg1, Object arg2, Object arg3) {
        return new FormatMessage3(format, arg1, arg2, arg3);
    }

    /**
     * Build a lazily formatted message from the given format string and arguments.
     * @param format the format string (following {@link String#format} rules)
     * @param arg1 the first argument
     * @param arg2 the second argument
     * @param arg3 the third argument
     * @param arg4 the fourth argument
     * @see String#format(String, Object...)
     */
    public static LogMessage format(String format, Object arg1, Object arg2, Object arg3, Object arg4) {
        return new FormatMessage4(format, arg1, arg2, arg3, arg4);
    }

    /**
     * Build a lazily formatted message from the given format string and varargs.
     * @param format the format string (following {@link String#format} rules)
     * @param args the varargs array (costly, prefer individual arguments)
     * @see String#format(String, Object...)
     */
    public static LogMessage format(String format, Object... args) {
        return new FormatMessageX(format, args);
    }


    private static final class SupplierMessage extends LogMessage {

        private Supplier<? extends CharSequence> supplier;

        SupplierMessage(Supplier<? extends CharSequence> supplier) {
            Assert.notNull(supplier, "Supplier must not be null");
            this.supplier = supplier;
        }

        @Override
        String buildString() {
            return this.supplier.get().toString();
        }
    }


    private static abstract class FormatMessage extends LogMessage {

        protected final String format;

        FormatMessage(String format) {
            Assert.notNull(format, "Format must not be null");
            this.format = format;
        }
    }


    private static final class FormatMessage1 extends FormatMessage {

        private final Object arg1;

        FormatMessage1(String format, Object arg1) {
            super(format);
            this.arg1 = arg1;
        }

        @Override
        protected String buildString() {
            return String.format(this.format, this.arg1);
        }
    }


    private static final class FormatMessage2 extends FormatMessage {

        private final Object arg1;

        private final Object arg2;

        FormatMessage2(String format, Object arg1, Object arg2) {
            super(format);
            this.arg1 = arg1;
            this.arg2 = arg2;
        }

        @Override
        String buildString() {
            return String.format(this.format, this.arg1, this.arg2);
        }
    }


    private static final class FormatMessage3 extends FormatMessage {

        private final Object arg1;

        private final Object arg2;

        private final Object arg3;

        FormatMessage3(String format, Object arg1, Object arg2, Object arg3) {
            super(format);
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.arg3 = arg3;
        }

        @Override
        String buildString() {
            return String.format(this.format, this.arg1, this.arg2, this.arg3);
        }
    }


    private static final class FormatMessage4 extends FormatMessage {

        private final Object arg1;

        private final Object arg2;

        private final Object arg3;

        private final Object arg4;

        FormatMessage4(String format, Object arg1, Object arg2, Object arg3, Object arg4) {
            super(format);
            this.arg1 = arg1;
            this.arg2 = arg2;
            this.arg3 = arg3;
            this.arg4 = arg4;
        }

        @Override
        String buildString() {
            return String.format(this.format, this.arg1, this.arg2, this.arg3, this.arg4);
        }
    }


    private static final class FormatMessageX extends FormatMessage {

        private final Object[] args;

        FormatMessageX(String format, Object... args) {
            super(format);
            this.args = args;
        }

        @Override
        String buildString() {
            return String.format(this.format, this.args);
        }
    }
}
