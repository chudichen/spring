package com.michael.core;

import com.michael.lang.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author Michael Chu
 * @since 2019-09-25 08:50
 */
public interface ParameterNameDiscoverer {

    /**
     * Return parameter names for a method, or {@code null} if they cannot be determined.
     * <p>Individual entries in the array may be {@code null} if parameter names are only
     * available for some parameters of the given method but not for others. However,
     * it is recommended to use stub parameter names instead wherever feasible.
     * @param method the method to find parameter names for
     * @return an array of parameter names if the names can be resolved,
     * or {@code null} if they cannot
     */
    @Nullable
    String[] getParameterNames(Method method);

    /**
     * Return parameter names for a constructor, or {@code null} if they cannot be determined.
     * <p>Individual entries in the array may be {@code null} if parameter names are only
     * available for some parameters of the given constructor but not for others. However,
     * it is recommended to use stub parameter names instead wherever feasible.
     * @param ctor the constructor to find parameter names for
     * @return an array of parameter names if the names can be resolved,
     * or {@code null} if they cannot
     */
    @Nullable
    String[] getParameterNames(Constructor<?> ctor);
}
