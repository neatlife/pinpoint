package com.navercorp.pinpoint.plugin.alimq;

import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.bootstrap.context.AsyncTraceId;
import com.navercorp.pinpoint.bootstrap.context.ParsingResult;
import com.navercorp.pinpoint.bootstrap.context.ServerMetaDataHolder;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.JdbcContext;
import com.navercorp.pinpoint.common.annotations.InterfaceAudience;

public interface TraceContext
{
    Trace currentTraceObject();
    
    Trace currentRawTraceObject();
    
    Trace continueTraceObject(final TraceId p0, final boolean p1);
    
    Trace continueTraceObject(final Trace p0);
    
    Trace newTraceObject();
    
    @InterfaceAudience.LimitedPrivate({ "vert.x" })
    Trace newAsyncTraceObject();
    
    @InterfaceAudience.LimitedPrivate({ "vert.x" })
    Trace continueAsyncTraceObject(final TraceId p0);
    
    Trace continueAsyncTraceObject(final AsyncTraceId p0, final int p1, final long p2);
    
    Trace removeTraceObject();
    
    String getAgentId();
    
    String getApplicationName();
    
    long getAgentStartTime();
    
    short getServerTypeCode();
    
    String getServerType();
    
    String cacheApi(final MethodDescriptor p0);
    
    String cacheString(final String p0);
    
    ParsingResult parseSql(final String p0);
    
    boolean cacheSql(final ParsingResult p0);
    
    TraceId createTraceId(final String p0, final String p1, final String p2, final String p3, final long p4, final long p5, final short p6);
    
    Trace disableSampling();
    
    ProfilerConfig getProfilerConfig();
    
    ServerMetaDataHolder getServerMetaDataHolder();
    
    @Deprecated
    int getAsyncId();
    
    JdbcContext getJdbcContext();
}
