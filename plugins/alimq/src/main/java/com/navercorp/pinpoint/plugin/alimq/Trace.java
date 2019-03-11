package com.navercorp.pinpoint.plugin.alimq;

import com.navercorp.pinpoint.bootstrap.context.AsyncTraceId;
import com.navercorp.pinpoint.bootstrap.context.scope.TraceScope;

public interface Trace extends StackOperation
{
    long getId();
    
    long getStartTime();
    
    @Deprecated
    Thread getBindThread();
    
    long getThreadId();
    
    String getRpcName();
    
    TraceId getTraceId();
    
    void updateEagleEyeTraceId(final String p0);
    
    @Deprecated
    AsyncTraceId getAsyncTraceId();
    
    boolean canSampled();
    
    boolean canRealSampled();
    
    boolean isRoot();
    
    boolean isAsync();
    
    SpanRecorder getSpanRecorder();
    
    SpanEventRecorder currentSpanEventRecorder();
    
    void close();
    
    TraceScope getScope(final String p0);
    
    TraceScope addScope(final String p0);
    
    void notifyBefore();
    
    void notifyAfter();
}
