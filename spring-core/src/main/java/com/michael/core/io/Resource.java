package com.michael.core.io;

import java.io.IOException;
import java.net.URL;

/**
 * @author Michael Chu
 * @since 2019-08-23 09:34
 */
public interface Resource extends InputStreamSource {

    boolean exists();

    boolean isReadable();

    boolean isOpen();

    boolean isFile();

    URL getURL() throws IOException;
}
