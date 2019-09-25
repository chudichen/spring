package com.michael.core;

/**
 * @author Michael Chu
 * @since 2019-09-25 17:14
 */
public class DefaultParameterNameDiscoverer extends PrioritizedParameterNameDiscoverer {

    public DefaultParameterNameDiscoverer() {
        if (!GraalDetector.inImageCode()) {
            if (KotlinDetector.isKotlinReflectPresent()) {
                addDiscoverer(new KotlinReflectionParameterNameDiscoverer());
            }
            addDiscoverer(new StandardReflectionParameterNameDiscoverer());
            addDiscoverer(new LocalVariableTableParameterNameDiscoverer());
        }
    }
}
