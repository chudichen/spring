package com.michael.beans.factory;

/**
 * 一个需要当销毁时释放资源的接口
 *
 * @author Michael Chu
 * @since 2019-09-17 20:39
 */
public interface DisposableBean {

    void destroy() throws Exception;
}
