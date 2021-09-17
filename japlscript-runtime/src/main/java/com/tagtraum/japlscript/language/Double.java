/*
 * =================================================
 * Copyright 2021 tagtraum industries incorporated
 * All rights reserved.
 * =================================================
 */
package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;
import com.tagtraum.japlscript.Codec;

/**
 * Double.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class Double implements Codec<java.lang.Double> {

    private static final Double instance = new Double();
    private static final TypeClass[] CLASSES = {
        new TypeClass("number", new Chevron("class", "nmbr").toString(), null, null),
        new TypeClass("real", new Chevron("class", "doub").toString(), null, null)
    };

    private Double() {
    }

    public static Double getInstance() {
        return instance;
    }


    @Override
    public java.lang.Double _decode(final String objectReference, final String applicationReference) {
        return java.lang.Double.valueOf(objectReference);
    }

    @Override
    public String _encode(final Object number) {
        return number == null
            ? "null" // or "missing value"?
            : number.toString();
    }

    @Override
    public Class<java.lang.Double> _getJavaType() {
        return java.lang.Double.TYPE;
    }

    @Override
    public TypeClass[] _getAppleScriptTypes() {
        return CLASSES;
    }
}