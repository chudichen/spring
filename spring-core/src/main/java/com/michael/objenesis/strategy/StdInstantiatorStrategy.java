package com.michael.objenesis.strategy;

import com.michael.objenesis.instantiator.ObjectInstantiator;
import com.michael.objenesis.instantiator.sun.UnsafeFactoryInstantiator;

import java.io.Serializable;

/**
 * @author Michael Chu
 * @since 2019-09-25 19:39
 */
public class StdInstantiatorStrategy extends BaseInstantiatorStrategy {

    public StdInstantiatorStrategy() {
    }

    public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {
        if (!PlatformDescription.isThisJVM("Java HotSpot") && !PlatformDescription.isThisJVM("OpenJDK")) {
            if (PlatformDescription.isThisJVM("Dalvik")) {
                if (PlatformDescription.isAndroidOpenJDK()) {
                    return new UnsafeFactoryInstantiator(type);
                } else if (PlatformDescription.ANDROID_VERSION <= 10) {
                    return new Android10Instantiator(type);
                } else {
                    return (ObjectInstantiator)(PlatformDescription.ANDROID_VERSION <= 17 ? new Android17Instantiator(type) : new Android18Instantiator(type));
                }
            } else if (PlatformDescription.isThisJVM("GNU libgcj")) {
                return new GCJInstantiator(type);
            } else {
                return (ObjectInstantiator)(PlatformDescription.isThisJVM("PERC") ? new PercInstantiator(type) : new UnsafeFactoryInstantiator(type));
            }
        } else if (PlatformDescription.isGoogleAppEngine() && PlatformDescription.SPECIFICATION_VERSION.equals("1.7")) {
            return (ObjectInstantiator)(Serializable.class.isAssignableFrom(type) ? new ObjectInputStreamInstantiator(type) : new AccessibleInstantiator(type));
        } else {
            return new SunReflectionFactoryInstantiator(type);
        }
    }
}
