package com.michael.util;

import java.io.*;
import java.util.Properties;

/**
 * @author Michael Chu
 * @since 2019-09-25 16:29
 */
public class DefaultPropertiesPersister implements PropertiesPersister {

    @Override
    public void load(Properties props, InputStream is) throws IOException {
        props.load(is);
    }

    @Override
    public void load(Properties props, Reader reader) throws IOException {
        props.load(reader);
    }

    @Override
    public void store(Properties props, OutputStream os, String header) throws IOException {
        props.store(os, header);
    }

    @Override
    public void store(Properties props, Writer writer, String header) throws IOException {
        props.store(writer, header);
    }

    @Override
    public void loadFromXml(Properties props, InputStream is) throws IOException {
        props.loadFromXML(is);
    }

    @Override
    public void storeToXml(Properties props, OutputStream os, String header) throws IOException {
        props.storeToXML(os, header);
    }

    @Override
    public void storeToXml(Properties props, OutputStream os, String header, String encoding) throws IOException {
        props.storeToXML(os, header, encoding);
    }
}
