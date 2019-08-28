package com.michael.util;

import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-28 15:31
 */
public abstract class StringUtils {

    public static boolean hasText(@Nullable String str) {
        return (str != null && !str.isEmpty() && containsText(str));
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
