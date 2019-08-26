package com.michael.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Michael Chu
 * @since 2019-08-23 09:36
 */
public interface InputStreamSource {

    InputStream getInputStream() throws IOException;
}
