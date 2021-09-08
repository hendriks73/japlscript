/*
 * =================================================
 * Copyright 2021 tagtraum industries incorporated
 * All rights reserved.
 * =================================================
 */
package com.tagtraum.japlscript.types;

import com.tagtraum.japlscript.Codec;

/**
 * Long.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class Long implements Codec<java.lang.Long> {

    private static final Long instance = new Long();

    private Long() {
    }

    public static Long getInstance() {
        return instance;
    }


    @Override
    public java.lang.Long _decode(final String objectReference, final String applicationReference) {
        return java.lang.Long.valueOf(objectReference);
    }

    @Override
    public String _encode(final Object number) {
        return number == null
            ? "null" // or "missing value"?
            : number.toString();
    }

    @Override
    public Class<java.lang.Long> _getJavaType() {
        return java.lang.Long.TYPE;
    }
}