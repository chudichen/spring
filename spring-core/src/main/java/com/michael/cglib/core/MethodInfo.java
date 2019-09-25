package com.michael.cglib.core;

import com.michael.asm.Type;

/**
 * @author Michael Chu
 * @since 2019-09-25 19:04
 */
public abstract class MethodInfo {

    protected MethodInfo() {
    }

    public abstract ClassInfo getClassInfo();

    public abstract int getModifiers();

    public abstract Signature getSignature();

    public abstract Type[] getExceptionTypes();

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else {
            return o instanceof MethodInfo && this.getSignature().equals(((MethodInfo) o).getSignature());
        }
    }

    @Override
    public int hashCode() {
        return this.getSignature().hashCode();
    }

    @Override
    public String toString() {
        return this.getSignature().toString();
    }

}
