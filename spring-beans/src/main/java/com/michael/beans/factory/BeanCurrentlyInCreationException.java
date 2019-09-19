package com.michael.beans.factory;

/**
 * 此异常抛出以防当前bean正在被建造
 *
 * @author Michael Chu
 * @since 2019-09-17 20:32
 */
public class BeanCurrentlyInCreationException extends BeanCreationException {

    /**
     * 使用默认message，创造一个异常来标识有不可解析的循环引用
     *
     * @param beanName
     */
    public BeanCurrentlyInCreationException(String beanName) {
        super(beanName,
                "Requested bean is currently in creation: Is there an unresolvable circular reference?");
    }

    /**
     * 手动传递message
     *
     * @param beanName
     * @param msg
     */
    public BeanCurrentlyInCreationException(String beanName, String msg) {
        super(beanName, msg);
    }
}
