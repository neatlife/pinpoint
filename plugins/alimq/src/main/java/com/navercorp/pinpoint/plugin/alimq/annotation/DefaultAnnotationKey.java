package com.navercorp.pinpoint.plugin.alimq.annotation;

public class DefaultAnnotationKey implements AnnotationKey
{
    private final int code;
    private final String name;
    private final boolean viewInRecordSet;
    private final boolean errorApiMetadata;
    
    DefaultAnnotationKey(final int code, final String name, final AnnotationKeyProperty... properties) {
        this.code = code;
        this.name = name;
        boolean viewInRecordSet = false;
        boolean errorApiMetadata = false;
        for (final AnnotationKeyProperty property : properties) {
            switch (property) {
                case VIEW_IN_RECORD_SET: {
                    viewInRecordSet = true;
                    break;
                }
                case ERROR_API_METADATA: {
                    errorApiMetadata = true;
                    break;
                }
            }
        }
        this.viewInRecordSet = viewInRecordSet;
        this.errorApiMetadata = errorApiMetadata;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public int getCode() {
        return this.code;
    }
    
    @Override
    public boolean isErrorApiMetadata() {
        return this.errorApiMetadata;
    }
    
    @Override
    public boolean isViewInRecordSet() {
        return this.viewInRecordSet;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AnnotationKey{");
        sb.append("code=").append(this.code);
        sb.append(", name='").append(this.name);
        sb.append('}');
        return sb.toString();
    }
}
