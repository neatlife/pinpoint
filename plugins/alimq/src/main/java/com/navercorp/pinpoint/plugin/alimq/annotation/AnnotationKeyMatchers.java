package com.navercorp.pinpoint.plugin.alimq.annotation;

import com.navercorp.pinpoint.common.trace.AnnotationKey;
import com.navercorp.pinpoint.common.trace.AnnotationKeyMatcher;
import com.navercorp.pinpoint.common.util.AnnotationKeyUtils;

public final class AnnotationKeyMatchers
{
    public static final com.navercorp.pinpoint.common.trace.AnnotationKeyMatcher NOTHING_MATCHER;
    public static final com.navercorp.pinpoint.common.trace.AnnotationKeyMatcher ARGS_MATCHER;
    
    private AnnotationKeyMatchers() {
    }
    
    public static com.navercorp.pinpoint.common.trace.AnnotationKeyMatcher exact(final com.navercorp.pinpoint.common.trace.AnnotationKey key) {
        return new ExactMatcher(key);
    }
    
    static {
        NOTHING_MATCHER = new com.navercorp.pinpoint.common.trace.AnnotationKeyMatcher() {
            @Override
            public boolean matches(final int code) {
                return false;
            }
            
            @Override
            public String toString() {
                return "NOTHING_MATCHER";
            }
        };
        ARGS_MATCHER = new com.navercorp.pinpoint.common.trace.AnnotationKeyMatcher() {
            @Override
            public boolean matches(final int code) {
                return AnnotationKeyUtils.isArgsKey(code);
            }
            
            @Override
            public String toString() {
                return "ARGS_MATCHER";
            }
        };
    }
    
    private static class ExactMatcher implements AnnotationKeyMatcher
    {
        private final int code;
        
        public ExactMatcher(final AnnotationKey key) {
            this.code = key.getCode();
        }
        
        @Override
        public boolean matches(final int code) {
            return this.code == code;
        }
        
        @Override
        public String toString() {
            return "ExactMatcher(" + this.code + ")";
        }
    }
}
