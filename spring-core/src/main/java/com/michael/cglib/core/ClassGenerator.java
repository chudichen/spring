package com.michael.cglib.core;

import com.michael.asm.ClassVisitor;

/**
 * @author Michael Chu
 * @since 2019-09-25 17:57
 */
public interface ClassGenerator {

    void generateClass(ClassVisitor classVisitor) throws Exception;

}
