/*
 * =================================================
 * Copyright 2021 tagtraum industries incorporated
 * All rights reserved.
 * =================================================
 */
package com.tagtraum.japlscript.generation;

import java.util.Arrays;
import java.util.Comparator;

/**
 * ParameterSignature.
 *
 * @author <a href="mailto:hs@tagtraum.com">Hendrik Schreiber</a>
 */
public class ParameterSignature {

    private final AnnotationSignature[] annotations;
    private final String name;
    private final String type;
    private final String description;

    public ParameterSignature(final String name, final String description, final String type, final AnnotationSignature... annotations) {
        this.annotations = annotations;
        if (this.annotations != null) {
            Arrays.sort(this.annotations, Comparator.comparing(AnnotationSignature::toString));
        }
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public String toJavadoc() {
        if (description == null) return " * @param " + name;
        else return " * @param " + name + " " + description;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ParameterSignature that = (ParameterSignature) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(annotations, that.annotations)) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return type != null ? type.equals(that.type) : that.type == null;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(annotations);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (annotations != null) {
            for (final AnnotationSignature annotationSignature : annotations) {
                sb.append(annotationSignature).append(' ');
            }
        }
        sb.append(type).append(' ').append(name);
        return sb.toString();
    }
}
