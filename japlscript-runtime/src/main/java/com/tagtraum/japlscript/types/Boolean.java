/*
 * =================================================
 * Copyright 2021 tagtraum industries incorporated
 * All rights reserved.
 * =================================================
 */
package com.tagtraum.japlscript.types;

import com.tagtraum.japlscript.Codec;

/**
 * Boolean.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class Boolean implements Codec<java.lang.Boolean> {

    private static final Boolean instance = new Boolean();

    private Boolean() {
    }

    public static Boolean getInstance() {
        return instance;
    }


    @Override
    public java.lang.Boolean _decode(final String objectReference, final String applicationReference) {
        return java.lang.Boolean.valueOf(objectReference);
    }

    @Override
    public String _encode(final Object aBoolean) {
        return aBoolean == null
            ? "null" // or "missing value"?
            : aBoolean.toString();
    }

    @Override
    public Class<java.lang.Boolean> _getJavaType() {
        return java.lang.Boolean.TYPE;
    }

}
