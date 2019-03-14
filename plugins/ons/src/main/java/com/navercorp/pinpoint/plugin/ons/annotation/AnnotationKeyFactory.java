package com.navercorp.pinpoint.plugin.ons.annotation;


public abstract class AnnotationKeyFactory {
    private static final AnnotationKeyFactory DEFAULT_FACTORY;

    static {
        DEFAULT_FACTORY = new DefaultAnnotationKeyFactory();
    }

    public static AnnotationKey of(final int code, final String name, final AnnotationKeyProperty... properties) {
        return AnnotationKeyFactory.DEFAULT_FACTORY.createAnnotationKey(code, name, properties);
    }

    abstract AnnotationKey createAnnotationKey(final int p0, final String p1, final AnnotationKeyProperty... p2);
}
