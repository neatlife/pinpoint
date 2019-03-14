package com.navercorp.pinpoint.plugin.ons.annotation;

public class DefaultAnnotationKeyFactory extends AnnotationKeyFactory {
    public AnnotationKey createAnnotationKey(final int code, final String name, final AnnotationKeyProperty... properties) {
        return new DefaultAnnotationKey(code, name, properties);
    }
}
