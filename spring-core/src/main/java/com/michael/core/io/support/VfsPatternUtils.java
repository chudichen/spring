package com.michael.core.io.support;

import com.michael.core.io.VfsUtils;
import com.michael.lang.Nullable;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;

/**
 * @author Michael Chu
 * @since 2019-09-25 17:42
 */
abstract class VfsPatternUtils extends VfsUtils {

    @Nullable
    static Object getVisitorAttributes() {
        return doGetVisitorAttributes();
    }

    static String getPath(Object resource) {
        String path = doGetPath(resource);
        return (path != null ? path : "");
    }

    static Object findRoot(URL url) throws IOException {
        return getRoot(url);
    }

    static void visit(Object resource, InvocationHandler visitor) throws IOException {
        Object visitorProxy = Proxy.newProxyInstance(
                VIRTUAL_FILE_VISITOR_INTERFACE.getClassLoader(),
                new Class<?>[] {VIRTUAL_FILE_VISITOR_INTERFACE}, visitor);
        invokeVfsMethod(VIRTUAL_FILE_METHOD_VISIT, resource, visitorProxy);
    }

}
