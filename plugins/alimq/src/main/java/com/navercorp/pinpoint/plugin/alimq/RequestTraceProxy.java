package com.navercorp.pinpoint.plugin.alimq;

import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.plugin.alimq.arms.EagleEyeCodec;
import com.navercorp.pinpoint.plugin.alimq.arms.JaegerCodec;
import com.navercorp.pinpoint.plugin.alimq.arms.RequestTraceCodec;
import com.navercorp.pinpoint.plugin.alimq.arms.ZipKinCodec;

public class RequestTraceProxy
{
    private RequestTrace requestTrace;
    private RequestTraceCodec requestTraceCodec;
    
    public RequestTraceProxy(final RequestTrace requestTrace) {
        this.requestTraceCodec = null;
        this.requestTrace = requestTrace;
        if (!StringUtils.isEmpty(requestTrace.getHeader(EagleEyeCodec.TRACE_ID_NAME))) {
            this.requestTraceCodec = new EagleEyeCodec();
        }
        else if (!StringUtils.isEmpty(requestTrace.getHeader("uber-trace-id"))) {
            this.requestTraceCodec = new JaegerCodec();
        }
        else if (!StringUtils.isEmpty(requestTrace.getHeader("X-B3-TraceId"))) {
            this.requestTraceCodec = new ZipKinCodec();
        }
        else {
            this.requestTraceCodec = new EagleEyeCodec();
        }
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
