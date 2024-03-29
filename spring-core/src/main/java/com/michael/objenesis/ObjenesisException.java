package com.michael.objenesis;

/**
 * @author Michael Chu
 * @since 2019-09-25 19:41
 */
public class ObjenesisException extends RuntimeException {

    private static final long serialVersionUID = -2677230016262426968L;

    public ObjenesisException(String msg) {
        super(msg);
    }

    public ObjenesisException(Throwable cause) {
        super(cause);
    }

    public ObjenesisException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
