package com.michael.cglib.proxy;

import com.michael.cglib.core.Signature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Michael Chu
 * @since 2019-09-25 17:47
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class MethodProxy {

    private Signature sig1;

    private Signature sig2;

    private CreateInfo createInfo;

    private final Object initLock = new Object();

    private volatile FastClassInfo fastClassInfo;

    /**
     * For internal use by {@link Enhancer} only; see the {@link org.springframework.cglib.reflect.FastMethod} class
     * for similar functionality.
     */
    public static MethodProxy create(Class c1, Class c2, String desc, String name1, String name2) {
        MethodProxy proxy = new MethodProxy();
        proxy.sig1 = new Signature(name1, desc);
        proxy.sig2 = new Signature(name2, desc);
        proxy.createInfo = new CreateInfo(c1, c2);
        return proxy;
    }

    private void init() {
        /*
         * Using a volatile invariant allows us to initialize the FastClass and
         * method index pairs atomically.
         *
         * Double-checked locking is safe with volatile in Java 5.  Before 1.5 this
         * code could allow fastClassInfo to be instantiated more than once, which
         * appears to be benign.
         */
        if (fastClassInfo == null) {
            synchronized (initLock) {
                if (fastClassInfo == null) {
                    CreateInfo ci = createInfo;

                    FastClassInfo fci = new FastClassInfo();
                    fci.f1 = helper(ci, ci.c1);
                    fci.f2 = helper(ci, ci.c2);
                    fci.i1 = fci.f1.getIndex(sig1);
                    fci.i2 = fci.f2.getIndex(sig2);
                    fastClassInfo = fci;
                    createInfo = null;
                }
            }
        }
    }


    private static class FastClassInfo {

        FastClass f1;

        FastClass f2;

        int i1;

        int i2;
    }


    private static class CreateInfo {

        Class c1;

        Class c2;

        NamingPolicy namingPolicy;

        GeneratorStrategy strategy;

        boolean attemptLoad;

        public CreateInfo(Class c1, Class c2) {
            this.c1 = c1;
            this.c2 = c2;
            AbstractClassGenerator fromEnhancer = AbstractClassGenerator.getCurrent();
            if (fromEnhancer != null) {
                namingPolicy = fromEnhancer.getNamingPolicy();
                strategy = fromEnhancer.getStrategy();
                attemptLoad = fromEnhancer.getAttemptLoad();
            }
        }
    }


    private static FastClass helper(CreateInfo ci, Class type) {
        FastClass.Generator g = new FastClass.Generator();
        g.setType(type);
        // SPRING PATCH BEGIN
        g.setContextClass(type);
        // SPRING PATCH END
        g.setClassLoader(ci.c2.getClassLoader());
        g.setNamingPolicy(ci.namingPolicy);
        g.setStrategy(ci.strategy);
        g.setAttemptLoad(ci.attemptLoad);
        return g.create();
    }

    private MethodProxy() {
    }

    /**
     * Return the signature of the proxied method.
     */
    public Signature getSignature() {
        return sig1;
    }

    /**
     * Return the name of the synthetic method created by CGLIB which is
     * used by {@link #invokeSuper} to invoke the superclass
     * (non-intercepted) method implementation. The parameter types are
     * the same as the proxied method.
     */
    public String getSuperName() {
        return sig2.getName();
    }

    /**
     * Return the {@link org.springframework.cglib.reflect.FastClass} method index
     * for the method used by {@link #invokeSuper}. This index uniquely
     * identifies the method within the generated proxy, and therefore
     * can be useful to reference external metadata.
     * @see #getSuperName
     */
    public int getSuperIndex() {
        init();
        return fastClassInfo.i2;
    }

    // For testing
    FastClass getFastClass() {
        init();
        return fastClassInfo.f1;
    }

    // For testing
    FastClass getSuperFastClass() {
        init();
        return fastClassInfo.f2;
    }

    /**
     * Return the <code>MethodProxy</code> used when intercepting the method
     * matching the given signature.
     * @param type the class generated by Enhancer
     * @param sig the signature to match
     * @return the MethodProxy instance, or null if no applicable matching method is found
     * @throws IllegalArgumentException if the Class was not created by Enhancer or does not use a MethodInterceptor
     */
    public static MethodProxy find(Class type, Signature sig) {
        try {
            Method m = type.getDeclaredMethod(MethodInterceptorGenerator.FIND_PROXY_NAME,
                    MethodInterceptorGenerator.FIND_PROXY_TYPES);
            return (MethodProxy) m.invoke(null, new Object[]{sig});
        }
        catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException("Class " + type + " does not use a MethodInterceptor");
        }
        catch (IllegalAccessException | InvocationTargetException ex) {
            throw new CodeGenerationException(ex);
        }
    }

    /**
     * Invoke the original method, on a different object of the same type.
     * @param obj the compatible object; recursion will result if you use the object passed as the first
     * argument to the MethodInterceptor (usually not what you want)
     * @param args the arguments passed to the intercepted method; you may substitute a different
     * argument array as long as the types are compatible
     * @throws Throwable the bare exceptions thrown by the called method are passed through
     * without wrapping in an <code>InvocationTargetException</code>
     * @see MethodInterceptor#intercept
     */
    public Object invoke(Object obj, Object[] args) throws Throwable {
        try {
            init();
            FastClassInfo fci = fastClassInfo;
            return fci.f1.invoke(fci.i1, obj, args);
        }
        catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
        catch (IllegalArgumentException ex) {
            if (fastClassInfo.i1 < 0)
                throw new IllegalArgumentException("Protected method: " + sig1);
            throw ex;
        }
    }

    /**
     * Invoke the original (super) method on the specified object.
     * @param obj the enhanced object, must be the object passed as the first
     * argument to the MethodInterceptor
     * @param args the arguments passed to the intercepted method; you may substitute a different
     * argument array as long as the types are compatible
     * @throws Throwable the bare exceptions thrown by the called method are passed through
     * without wrapping in an <code>InvocationTargetException</code>
     * @see MethodInterceptor#intercept
     */
    public Object invokeSuper(Object obj, Object[] args) throws Throwable {
        try {
            init();
            FastClassInfo fci = fastClassInfo;
            return fci.f2.invoke(fci.i2, obj, args);
        }
        catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

}
