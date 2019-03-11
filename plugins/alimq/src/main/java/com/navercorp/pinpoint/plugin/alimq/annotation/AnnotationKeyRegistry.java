package com.navercorp.pinpoint.plugin.alimq.annotation;

import com.navercorp.pinpoint.common.trace.AnnotationKey;
import com.navercorp.pinpoint.common.util.apache.IntHashMap;
import com.navercorp.pinpoint.common.util.apache.IntHashMapUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class AnnotationKeyRegistry
{
    private final IntHashMap<com.navercorp.pinpoint.common.trace.AnnotationKey> codeLookupTable;
    private final HashMap<String, com.navercorp.pinpoint.common.trace.AnnotationKey> nameLookupTable;
    private final IntHashMap<com.navercorp.pinpoint.common.trace.AnnotationKey> apiErrorLookupTable;
    
    public AnnotationKeyRegistry(final HashMap<Integer, com.navercorp.pinpoint.common.trace.AnnotationKey> buildMap) {
        if (buildMap == null) {
            throw new NullPointerException("buildMap must not be null");
        }
        this.codeLookupTable = IntHashMapUtils.copy(buildMap);
        this.nameLookupTable = this.buildNameTable(buildMap.values());
        this.apiErrorLookupTable = this.buildApiMetaDataError(buildMap.values());
    }
    
    private HashMap<String, com.navercorp.pinpoint.common.trace.AnnotationKey> buildNameTable(final Collection<com.navercorp.pinpoint.common.trace.AnnotationKey> buildMap) {
        final HashMap<String, com.navercorp.pinpoint.common.trace.AnnotationKey> nameLookupTable = new HashMap<String, com.navercorp.pinpoint.common.trace.AnnotationKey>();
        for (final com.navercorp.pinpoint.common.trace.AnnotationKey annotationKey : buildMap) {
            final com.navercorp.pinpoint.common.trace.AnnotationKey exist = nameLookupTable.put(annotationKey.getName(), annotationKey);
            if (exist != null) {
                throwDuplicatedAnnotationKey(annotationKey, exist);
            }
        }
        return nameLookupTable;
    }
    
    private static void throwDuplicatedAnnotationKey(final com.navercorp.pinpoint.common.trace.AnnotationKey annotationKey, final com.navercorp.pinpoint.common.trace.AnnotationKey exist) {
        throw new IllegalStateException("already exist. annotationKey:" + annotationKey + ", exist:" + exist);
    }
    
    private IntHashMap<com.navercorp.pinpoint.common.trace.AnnotationKey> buildApiMetaDataError(final Collection<com.navercorp.pinpoint.common.trace.AnnotationKey> buildMap) {
        final IntHashMap<com.navercorp.pinpoint.common.trace.AnnotationKey> table = new IntHashMap<com.navercorp.pinpoint.common.trace.AnnotationKey>();
        for (final com.navercorp.pinpoint.common.trace.AnnotationKey annotationKey : buildMap) {
            if (annotationKey.isErrorApiMetadata()) {
                table.put(annotationKey.getCode(), annotationKey);
            }
        }
        return table;
    }
    
    public com.navercorp.pinpoint.common.trace.AnnotationKey findAnnotationKey(final int code) {
        final com.navercorp.pinpoint.common.trace.AnnotationKey annotationKey = this.codeLookupTable.get(code);
        if (annotationKey == null) {
            return com.navercorp.pinpoint.common.trace.AnnotationKey.UNKNOWN;
        }
        return annotationKey;
    }
    
    public com.navercorp.pinpoint.common.trace.AnnotationKey findAnnotationKeyByName(final String keyName) {
        final com.navercorp.pinpoint.common.trace.AnnotationKey annotationKey = this.nameLookupTable.get(keyName);
        if (annotationKey == null) {
            throw new NoSuchElementException(keyName);
        }
        return annotationKey;
    }
    
    public com.navercorp.pinpoint.common.trace.AnnotationKey findApiErrorCode(final int annotationCode) {
        return this.apiErrorLookupTable.get(annotationCode);
    }
    
    public static class Builder
    {
        private final HashMap<Integer, com.navercorp.pinpoint.common.trace.AnnotationKey> buildMap;
        
        public Builder() {
            this.buildMap = new HashMap<Integer, com.navercorp.pinpoint.common.trace.AnnotationKey>();
        }
        
        public void addAnnotationKey(final com.navercorp.pinpoint.common.trace.AnnotationKey annotationKey) {
            if (annotationKey == null) {
                throw new NullPointerException("annotationKey must not be null");
            }
            final int code = annotationKey.getCode();
            final AnnotationKey exist = this.buildMap.put(code, annotationKey);
            if (exist != null) {
                throwDuplicatedAnnotationKey(annotationKey, exist);
            }
        }
        
        public AnnotationKeyRegistry build() {
            return new AnnotationKeyRegistry(this.buildMap);
        }
    }
}
