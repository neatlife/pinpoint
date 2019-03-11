package com.navercorp.pinpoint.plugin.alimq.arms;


import com.navercorp.pinpoint.plugin.alimq.RequestTrace;
import com.navercorp.pinpoint.plugin.alimq.Trace;
import com.navercorp.pinpoint.plugin.alimq.TraceContext;
import com.navercorp.pinpoint.plugin.alimq.TraceId;

public interface RequestTraceCodec
{
    void inject(final TraceContext p0, final RequestTrace p1, final Trace p2, final TraceId p3);
    
    TraceId extract(final TraceContext p0, final RequestTrace p1);
    
    String getSamplingFlag(final RequestTrace p0);
}
