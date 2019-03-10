package com.navercorp.pinpoint.bootstrap.plugin.request;


import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.common.util.*;
import com.navercorp.pinpoint.bootstrap.plugin.arms.*;
import com.navercorp.pinpoint.bootstrap.context.*;
import com.navercorp.pinpoint.bootstrap.sampler.*;

public class MyRequestTraceReader
{
    private final PLogger logger;
    private final boolean isDebug;
    private final TraceContext traceContext;
    private final boolean async;

    public MyRequestTraceReader(final TraceContext traceContext) {
        this(traceContext, false);
    }

    public MyRequestTraceReader(final TraceContext traceContext, final boolean async) {
        this.logger = PLoggerFactory.getLogger((Class)this.getClass());
        this.isDebug = this.logger.isDebugEnabled();
        this.traceContext = (TraceContext)Assert.requireNonNull((Object)traceContext, "traceContext must not be null");
        this.async = async;
    }

    public Trace read(final RequestTraceProxy requestTrace) {
        Assert.requireNonNull((Object)requestTrace, "requestTrace must not be n ull");

        final boolean sampling = this.samplingEnable(requestTrace);

        final TraceId traceId = this.populateTraceIdFromRequest(requestTrace);


        if (traceId != null) {
            final Trace trace = this.traceContext.continueTraceObject(traceId);
            if (trace.canSampled()) {
                if (this.isDebug) {
                    this.logger.debug("TraceID exist. continue trace. traceId:{}", (Object)traceId);
                }
            }
            else if (this.isDebug) {
                this.logger.debug("TraceID exist. camSampled is false. skip trace. traceId:{}", (Object)traceId);
            }
            return trace;
        }
        final Trace trace = this.newTrace();
        if (trace.canSampled()) {
            if (this.isDebug) {
                this.logger.debug("TraceID not exist. start new trace.");
            }
        }
        else if (this.isDebug) {
            this.logger.debug("TraceID not exist. camSampled is false. skip trace.");
        }
        return trace;
    }

    private boolean samplingEnable(final RequestTraceProxy requestTrace) {
        final String samplingFlag = requestTrace.getSamplingFlag();
        if (this.isDebug) {
            this.logger.debug("SamplingFlag={}", (Object)samplingFlag);
        }
        return SamplingFlagUtils.isSamplingFlag(samplingFlag);
    }

    private TraceId populateTraceIdFromRequest(final RequestTraceProxy requestTrace) {
        return requestTrace.extract(this.traceContext);
    }

    private Trace newTrace() {
        if (this.async) {
            return this.traceContext.newAsyncTraceObject();
        }
        return this.traceContext.newTraceObject();
    }
}
