package com.michael.util.xml;

/**
 * @author Michael Chu
 * @since 2019-08-28 15:36
 */
public class XmlValidationModeDetector {

    /**
     * Indicates that the validation should be disabled.
     */
    public static final int VALIDATION_NONE = 0;

    /**
     * Indicates that the validation mode should be auto-guessed, since we cannot find
     * a clear indication (probably choked on some special characters, or the like).
     */
    public static final int VALIDATION_AUTO = 1;

    /**
     * Indicates that DTD validation should be used (we found a "DOCTYPE" declaration).
     */
    public static final int VALIDATION_DTD = 2;

    /**
     * Indicates that XSD validation should be used (found no "DOCTYPE" declaration).
     */
    public static final int VALIDATION_XSD = 3;


    /**
     * The token in a XML document that declares the DTD to use for validation
     * and thus that DTD validation is being used.
     */
    private static final String DOCTYPE = "DOCTYPE";

    /**
     * The token that indicates the start of an XML comment.
     */
    private static final String START_COMMENT = "<!--";

    /**
     * The token that indicates the end of an XML comment.
     */
    private static final String END_COMMENT = "-->";


    /**
     * Indicates whether or not the current parse position is inside an XML comment.
     */
    private boolean inComment;
}
