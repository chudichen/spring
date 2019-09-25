package com.michael.cglib.core;

/**
 * @author Michael Chu
 * @since 2019-09-25 19:15
 */
public interface GeneratorStrategy {

    byte[] generate(ClassGenerator var1) throws Exception;

    boolean equals(Object var1);

}
