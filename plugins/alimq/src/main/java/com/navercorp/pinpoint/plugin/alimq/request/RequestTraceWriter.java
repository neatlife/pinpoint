package com.navercorp.pinpoint.plugin.alimq.request;

import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.context.TraceId;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.plugin.alimq.RequestTraceProxy;

public class RequestTraceWriter {
    private final PLogger logger;
    private final boolean isDebug;
    private final TraceContext traceContext;

    public RequestTraceWriter(final TraceContext traceContext) {
        this.logger = PLoggerFactory.getLogger(this.getClass());
        this.isDebug = this.logger.isDebugEnabled();
        this.traceContext = traceContext;
    }

    public void write(final RequestTraceProxy requestTrace, final Trace trace, final TraceId nextId) {
        if (this.isDebug) {
            this.logger.warn("Set request header that is not to be sampled.");
        }
        requestTrace.inject(this.traceContext, trace, nextId);
    }
}
