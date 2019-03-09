package com.navercorp.pinpoint.bootstrap.plugin.arms;

import com.navercorp.pinpoint.bootstrap.plugin.*;
import com.navercorp.pinpoint.bootstrap.context.*;

public class RequestTraceProxy
{
    private RequestTrace requestTrace;
    private RequestTraceCodec requestTraceCodec;
    
    public RequestTraceProxy(final RequestTrace requestTrace) {
        this.requestTraceCodec = null;
        this.requestTrace = requestTrace;
        this.requestTraceCodec = new PinPonitCodec();
    }
    
    public void inject(final TraceContext traceContext, final Trace trace, final TraceId nextId) {
        this.requestTraceCodec.inject(traceContext, this.requestTrace, trace, nextId);
    }
    
    public TraceId extract(final TraceContext traceContext) {
        return this.requestTraceCodec.extract(traceContext, this.requestTrace);
    }
    
    public String getSamplingFlag()
    {
        return this.requestTraceCodec.getSamplingFlag(this.requestTrace);
    }
}
