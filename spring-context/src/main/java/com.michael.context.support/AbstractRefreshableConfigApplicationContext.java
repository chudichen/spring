package com.michael.context.support;

import com.michael.util.Assert;
import com.michael.lang.Nullable;

/**
 * @author Michael Chu
 * @since 2019-08-22 20:47
 */
public abstract class AbstractRefreshableConfigApplicationContext extends AbstractRefreshableApplicationContext {

    @Nullable
    private String[] configLocations;

    /**
     * 为应用设置配置文件的地址，如果没有设置则使用默认模式的
     *
     * @param locations 配置
     */
    public void setConfigLocations(@Nullable String... locations) {
        if (locations != null) {
            Assert.noNullElements(locations, "Config locations must not be null");
            this.configLocations = new String[locations.length];
            for (int i = 0; i < locations.length; i++) {
                this.configLocations[i] = resolvePath(locations[i]).trim();
            }
        } else {
            this.configLocations = null;
        }
    }

    protected String resolvePath(String path) {
        return getEnvironment().resolveRequiredPlaceholders(path);
    }
}
