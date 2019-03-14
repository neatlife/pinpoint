package com.navercorp.pinpoint.plugin.alimq.annotation;

public class DefaultAnnotationKeyFactory extends AnnotationKeyFactory {
    public AnnotationKey createAnnotationKey(final int code, final String name, final AnnotationKeyProperty... properties) {
        return new DefaultAnnotationKey(code, name, properties);
    }
}
