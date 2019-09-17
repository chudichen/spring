package com.michael.core;

/**
 * @author Michael Chu
 * @since 2019-08-27 09:35
 */
public interface AliasRegistry {

    void registerAlias(String name, String alias);

    void removeAlias(String alias);

    boolean isAlias(String name);

    String[] getAliases(String name);
}
