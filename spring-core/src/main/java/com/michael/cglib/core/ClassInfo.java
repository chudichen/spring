package com.michael.cglib.core;

import com.michael.asm.Type;

/**
 * @author Michael Chu
 * @since 2019-09-25 19:11
 */
public abstract class ClassInfo {

    protected ClassInfo() {
    }

    public abstract Type getType();

    public abstract Type getSuperType();

    public abstract Type[] getInterfaces();

    public abstract int getModifiers();

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else {
            return o instanceof ClassInfo && this.getType().equals(((ClassInfo) o).getType());
        }
    }

    @Override
    public int hashCode() {
        return this.getType().hashCode();
    }

    @Override
    public String toString() {
        return this.getType().getClassName();
    }

}
