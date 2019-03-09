package com.navercorp.pinpoint.bootstrap.plugin.request;

import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.context.TraceId;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.plugin.arms.RequestTraceProxy;

public class MyRequestTraceWriter
{
    private final PLogger logger;
    private final boolean isDebug;
    private final TraceContext traceContext;

    public MyRequestTraceWriter(final TraceContext traceContext) {
        this.logger = PLoggerFactory.getLogger((Class)this.getClass());
        this.isDebug = this.logger.isDebugEnabled();
        this.traceContext = traceContext;
    }

    public void write(final RequestTraceProxy requestTrace, final Trace trace, final TraceId nextId) {
        if (this.isDebug) {
            this.logger.debug("Set request header that is not to be sampled.");
        }
        requestTrace.inject(this.traceContext, trace, nextId);
    }

}
