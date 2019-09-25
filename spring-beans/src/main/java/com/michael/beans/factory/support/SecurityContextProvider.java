package com.michael.beans.factory.support;

import java.security.AccessControlContext;

/**
 * 提供者中运行代码安全的上下文工厂
 *
 * @author Michael Chu
 * @since 2019-09-24 19:51
 */
public interface SecurityContextProvider {

    AccessControlContext getAccessControlContext();
}
