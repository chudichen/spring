package com.michael.core.io.support;

import com.michael.core.io.InputStreamSource;
import com.michael.core.io.Resource;
import com.michael.lang.Nullable;
import com.michael.util.Assert;
import com.michael.util.ObjectUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * @author Michael Chu
 * @since 2019-09-25 16:27
 */
public class EncodedResource implements InputStreamSource {


    private final Resource resource;

    @Nullable
    private final String encoding;

    @Nullable
    private final Charset charset;


    /**
     * Create a new {@code EncodedResource} for the given {@code Resource},
     * not specifying an explicit encoding or {@code Charset}.
     * @param resource the {@code Resource} to hold (never {@code null})
     */
    public EncodedResource(Resource resource) {
        this(resource, null, null);
    }

    /**
     * Create a new {@code EncodedResource} for the given {@code Resource},
     * using the specified {@code encoding}.
     * @param resource the {@code Resource} to hold (never {@code null})
     * @param encoding the encoding to use for reading from the resource
     */
    public EncodedResource(Resource resource, @Nullable String encoding) {
        this(resource, encoding, null);
    }

    /**
     * Create a new {@code EncodedResource} for the given {@code Resource},
     * using the specified {@code Charset}.
     * @param resource the {@code Resource} to hold (never {@code null})
     * @param charset the {@code Charset} to use for reading from the resource
     */
    public EncodedResource(Resource resource, @Nullable Charset charset) {
        this(resource, null, charset);
    }

    private EncodedResource(Resource resource, @Nullable String encoding, @Nullable Charset charset) {
        super();
        Assert.notNull(resource, "Resource must not be null");
        this.resource = resource;
        this.encoding = encoding;
        this.charset = charset;
    }


    /**
     * Return the {@code Resource} held by this {@code EncodedResource}.
     */
    public final Resource getResource() {
        return this.resource;
    }

    /**
     * Return the encoding to use for reading from the {@linkplain #getResource() resource},
     * or {@code null} if none specified.
     */
    @Nullable
    public final String getEncoding() {
        return this.encoding;
    }

    /**
     * Return the {@code Charset} to use for reading from the {@linkplain #getResource() resource},
     * or {@code null} if none specified.
     */
    @Nullable
    public final Charset getCharset() {
        return this.charset;
    }

    /**
     * Determine whether a {@link Reader} is required as opposed to an {@link InputStream},
     * i.e. whether an {@linkplain #getEncoding() encoding} or a {@link #getCharset() Charset}
     * has been specified.
     * @see #getReader()
     * @see #getInputStream()
     */
    public boolean requiresReader() {
        return (this.encoding != null || this.charset != null);
    }

    /**
     * 当设置了编码属性的时候，Spring会使用相应的编码作为输入流。
     *
     * Open a {@code java.io.Reader} for the specified resource, using the specified
     * {@link #getCharset() Charset} or {@linkplain #getEncoding() encoding}
     * (if any).
     * @throws IOException if opening the Reader failed
     * @see #requiresReader()
     * @see #getInputStream()
     */
    public Reader getReader() throws IOException {
        if (this.charset != null) {
            return new InputStreamReader(this.resource.getInputStream(), this.charset);
        }
        else if (this.encoding != null) {
            return new InputStreamReader(this.resource.getInputStream(), this.encoding);
        }
        else {
            return new InputStreamReader(this.resource.getInputStream());
        }
    }

    /**
     * Open an {@code InputStream} for the specified resource, ignoring any specified
     * {@link #getCharset() Charset} or {@linkplain #getEncoding() encoding}.
     * @throws IOException if opening the InputStream failed
     * @see #requiresReader()
     * @see #getReader()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return this.resource.getInputStream();
    }


    @Override
    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof EncodedResource)) {
            return false;
        }
        EncodedResource otherResource = (EncodedResource) other;
        return (this.resource.equals(otherResource.resource) &&
                ObjectUtils.nullSafeEquals(this.charset, otherResource.charset) &&
                ObjectUtils.nullSafeEquals(this.encoding, otherResource.encoding));
    }

    @Override
    public int hashCode() {
        return this.resource.hashCode();
    }

    @Override
    public String toString() {
        return this.resource.toString();
    }

}
