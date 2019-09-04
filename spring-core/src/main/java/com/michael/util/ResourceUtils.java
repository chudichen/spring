package com.michael.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Michael Chu
 * @since 2019-08-23 10:28
 */
public abstract class ResourceUtils {

    public static final String CLASSPATH_URL_PREFIX = "classpath:";

    public static final String FILE_URL_PREFIX = "file:";

    public static final String WAR_URL_PREFIX = "war:";

    public static final String URL_PROTOCOL_FILE = "file";

    public static final String URL_PROTOCOL_JAR = "jar";

    public static final String URL_PROTOCOL_WAR = "war";

    public static final String URL_PROTOCOL_ZIP = "zip";

    public static final String URL_PROTOCOL_WSJAR = "wsjar";

    public static final String URL_PROTOCOL_VFSZIP = "vfszip";

    public static final String URL_PROTOCOL_VFSFILE = "vfsfile";

    public static final String URL_PROTOCOL_VFS = "vfs";

    public static final String JAR_FILE_EXTENSION = ".jar";

    public static final String JAR_URL_SEPARATOR = "!/";

    public static final String WAR_URL_SEPARATOR = "*/";

    public static URI toURI(URL url) throws URISyntaxException {
        return toURI(url.toString());
    }

    public static URI toURI(String location) throws URISyntaxException {
        return new URI(StringUtils.replace(location, " ", "%20"));
    }

    public static boolean isFileURL(URL url) {
        String protocol = url.getProtocol();
        return (URL_PROTOCOL_FILE.equals(protocol) || URL_PROTOCOL_VFSFILE.equals(protocol) ||
                URL_PROTOCOL_VFS.equals(protocol));
    }
}
