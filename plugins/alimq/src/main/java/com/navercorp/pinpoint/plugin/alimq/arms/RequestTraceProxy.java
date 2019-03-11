package com.navercorp.pinpoint.plugin.alimq.arms;

import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.context.TraceId;
import com.navercorp.pinpoint.plugin.alimq.RequestTrace;

public class RequestTraceProxy
{
    private RequestTrace requestTrace;
    private RequestTraceCodec requestTraceCodec;
    
    public RequestTraceProxy(final RequestTrace requestTrace) {
        this.requestTraceCodec = new PinPonitCodec();
        this.requestTrace = requestTrace;
    }
    
    public void inject(final TraceContext traceContext, final Trace trace, final TraceId nextId) {
        this.requestTraceCodec.inject(traceContext, this.requestTrace, trace, nextId);
    }
    
    public TraceId extract(final TraceContext traceContext) {
        return this.requestTraceCodec.extract(traceContext, this.requestTrace);
    }
    
    public String getSamplingFlag() {
        return this.requestTraceCodec.getSamplingFlag(this.requestTrace);
    }
}
