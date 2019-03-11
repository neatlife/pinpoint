package com.navercorp.pinpoint.plugin.alimq;

import com.navercorp.pinpoint.bootstrap.context.AsyncContext;
import com.navercorp.pinpoint.bootstrap.context.FrameAttachment;
import com.navercorp.pinpoint.bootstrap.context.ParsingResult;
import com.navercorp.pinpoint.common.trace.*;
import com.navercorp.pinpoint.common.annotations.*;

public interface SpanEventRecorder extends FrameAttachment
{
    void recordTime(final boolean p0);
    
    void recordStackTrace(final String p0);
    
    void recordException(final Throwable p0);
    
    void recordException(final boolean p0, final Throwable p1);
    
    void recordApiId(final String p0);
    
    void recordApi(final MethodDescriptor p0);
    
    void recordApi(final MethodDescriptor p0, final Object[] p1);
    
    void recordApi(final MethodDescriptor p0, final Object p1, final int p2);
    
    void recordApi(final MethodDescriptor p0, final Object[] p1, final int p2, final int p3);
    
    void recordApiCachedString(final MethodDescriptor p0, final String p1, final int p2);
    
    ParsingResult recordSqlInfo(final String p0);
    
    void recordSqlParsingResult(final ParsingResult p0);
    
    void recordSqlParsingResult(final ParsingResult p0, final String p1);
    
    void recordAttribute(final AnnotationKey p0, final String p1);
    
    void recordAttribute(final AnnotationKey p0, final int p1);
    
    void recordAttribute(final AnnotationKey p0, final Object p1);
    
    void recordServiceType(final ServiceType p0);
    
    void recordRpcName(final String p0);
    
    void recordDestinationId(final String p0);
    
    void recordEndPoint(final String p0);
    
    void recordNextSpanId(final long p0);
    
    @InterfaceStability.Evolving
    AsyncContext recordNextAsyncContext();
    
    @InterfaceStability.Unstable
    AsyncContext recordNextAsyncContext(final boolean p0);
    
    @Deprecated
    void recordAsyncId(final int p0);
    
    @Deprecated
    void recordNextAsyncId(final int p0);
    
    @Deprecated
    void recordAsyncSequence(final short p0);
}
