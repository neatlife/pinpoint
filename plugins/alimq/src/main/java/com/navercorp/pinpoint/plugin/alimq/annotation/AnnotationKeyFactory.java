package com.navercorp.pinpoint.plugin.alimq.annotation;


public abstract class AnnotationKeyFactory
{
    private static final AnnotationKeyFactory DEFAULT_FACTORY;
    
    public static AnnotationKey of(final int code, final String name, final AnnotationKeyProperty... properties) {
        return AnnotationKeyFactory.DEFAULT_FACTORY.createAnnotationKey(code, name, properties);
    }
    
    abstract AnnotationKey createAnnotationKey(final int p0, final String p1, final AnnotationKeyProperty... p2);
    
    static {
        DEFAULT_FACTORY = new DefaultAnnotationKeyFactory();
    }
}
