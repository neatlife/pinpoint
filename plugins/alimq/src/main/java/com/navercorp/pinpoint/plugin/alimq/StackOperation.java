package com.navercorp.pinpoint.plugin.alimq;

public interface StackOperation
{
    public static final int DEFAULT_STACKID = -1;
    public static final int ROOT_STACKID = 0;
    
    SpanEventRecorder traceBlockBegin();
    
    SpanEventRecorder traceBlockBegin(final int p0);
    
    void traceBlockEnd();
    
    void traceBlockEnd(final int p0);
    
    boolean isRootStack();
    
    int getCallStackFrameId();
}
