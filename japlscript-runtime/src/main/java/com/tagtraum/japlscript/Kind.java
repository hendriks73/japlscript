/*
 * =================================================
 * Copyright 2006 tagtraum industries incorporated
 * All rights reserved.
 * =================================================
 */
package com.tagtraum.japlscript;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Kind annotation (e.g. "command", "element", "property") for generated
 * methods.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Kind {

    /**
     * Value.
     *
     * @return value
     */
    String value();
}