package com.michael.util;

import java.io.*;
import java.util.Properties;

/**
 * @author Michael Chu
 * @since 2019-09-25 16:28
 */
public interface PropertiesPersister {

    /**
     * Load properties from the given InputStream into the given
     * Properties object.
     * @param props the Properties object to load into
     * @param is the InputStream to load from
     * @throws IOException in case of I/O errors
     * @see java.util.Properties#load
     */
    void load(Properties props, InputStream is) throws IOException;

    /**
     * Load properties from the given Reader into the given
     * Properties object.
     * @param props the Properties object to load into
     * @param reader the Reader to load from
     * @throws IOException in case of I/O errors
     */
    void load(Properties props, Reader reader) throws IOException;

    /**
     * Write the contents of the given Properties object to the
     * given OutputStream.
     * @param props the Properties object to store
     * @param os the OutputStream to write to
     * @param header the description of the property list
     * @throws IOException in case of I/O errors
     * @see java.util.Properties#store
     */
    void store(Properties props, OutputStream os, String header) throws IOException;

    /**
     * Write the contents of the given Properties object to the
     * given Writer.
     * @param props the Properties object to store
     * @param writer the Writer to write to
     * @param header the description of the property list
     * @throws IOException in case of I/O errors
     */
    void store(Properties props, Writer writer, String header) throws IOException;

    /**
     * Load properties from the given XML InputStream into the
     * given Properties object.
     * @param props the Properties object to load into
     * @param is the InputStream to load from
     * @throws IOException in case of I/O errors
     * @see java.util.Properties#loadFromXML(java.io.InputStream)
     */
    void loadFromXml(Properties props, InputStream is) throws IOException;

    /**
     * Write the contents of the given Properties object to the
     * given XML OutputStream.
     * @param props the Properties object to store
     * @param os the OutputStream to write to
     * @param header the description of the property list
     * @throws IOException in case of I/O errors
     * @see java.util.Properties#storeToXML(java.io.OutputStream, String)
     */
    void storeToXml(Properties props, OutputStream os, String header) throws IOException;

    /**
     * Write the contents of the given Properties object to the
     * given XML OutputStream.
     * @param props the Properties object to store
     * @param os the OutputStream to write to
     * @param encoding the encoding to use
     * @param header the description of the property list
     * @throws IOException in case of I/O errors
     * @see java.util.Properties#storeToXML(java.io.OutputStream, String, String)
     */
    void storeToXml(Properties props, OutputStream os, String header, String encoding) throws IOException;

}
