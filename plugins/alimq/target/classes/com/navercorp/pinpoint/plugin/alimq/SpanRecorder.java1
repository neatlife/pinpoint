package com.navercorp.pinpoint.plugin.alimq;

import com.navercorp.pinpoint.bootstrap.context.FrameAttachment;
import com.navercorp.pinpoint.common.trace.*;

public interface SpanRecorder extends FrameAttachment
{
    boolean canSampled();
    
    boolean isRoot();
    
    void recordStartTime(final long p0);
    
    void recordTime(final boolean p0);
    
    void recordException(final Throwable p0);
    
    void recordException(final boolean p0, final Throwable p1);
    
    void recordApiId(final String p0);
    
    void recordApi(final MethodDescriptor p0);
    
    void recordApi(final MethodDescriptor p0, final Object[] p1);
    
    void recordApi(final MethodDescriptor p0, final Object p1, final int p2);
    
    void recordApi(final MethodDescriptor p0, final Object[] p1, final int p2, final int p3);
    
    void recordApiCachedString(final MethodDescriptor p0, final String p1, final int p2);
    
    void recordAttribute(final AnnotationKey p0, final String p1);
    
    void recordAttribute(final AnnotationKey p0, final int p1);
    
    void recordAttribute(final AnnotationKey p0, final Object p1);
    
    void recordServiceType(final ServiceType p0);
    
    void recordRpcName(final String p0);
    
    void recordRemoteAddress(final String p0);
    
    void recordEndPoint(final String p0);
    
    void recordParentApplication(final String p0);
    
    void recordParentRpcName(final String p0);
    
    void recordAcceptorHost(final String p0);
    
    void recordLogging(final LoggingInfo p0);
    
    void recordRequestSize(final int p0);
    
    void recordResponseSize(final int p0);
    
    void recoredStatusCode(final int p0);
}
