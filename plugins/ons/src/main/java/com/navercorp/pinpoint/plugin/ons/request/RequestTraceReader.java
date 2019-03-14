package com.navercorp.pinpoint.plugin.ons.request;

import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.context.TraceId;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.sampler.SamplingFlagUtils;
import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.plugin.ons.RequestTraceProxy;

public class RequestTraceReader {
    private final PLogger logger;
    private final boolean isDebug;
    private final TraceContext traceContext;
    private final boolean async;

    public RequestTraceReader(final TraceContext traceContext) {
        this(traceContext, false);
    }

    public RequestTraceReader(final TraceContext traceContext, final boolean async) {
        this.logger = PLoggerFactory.getLogger(this.getClass());
        this.isDebug = this.logger.isDebugEnabled();
        this.traceContext = (TraceContext) Assert.requireNonNull((Object) traceContext, "traceContext must not be null");
        this.async = async;
    }

    public Trace read(final RequestTraceProxy requestTrace) {
        Assert.requireNonNull((Object) requestTrace, "requestTrace must not be n ull");
        final boolean sampling = this.samplingEnable(requestTrace);
        final TraceId traceId = requestTrace.extract(this.traceContext);
        logger.warn("RequestTraceReader read, traceid {}", traceId);
        if (traceId != null) {
            final Trace trace = this.traceContext.continueTraceObject(traceId);
            logger.warn("RequestTraceReader read, traceid {} trace {}", traceId, trace);
            if (trace.canSampled()) {
                this.logger.warn("TraceID exist. continue trace. traceId:{}", traceId);
            } else {
                this.logger.warn("TraceID exist. camSampled is false. skip trace. traceId:{}", traceId);
            }
            return trace;
        }
        final Trace trace = this.newTrace();
        if (trace.canSampled()) {
            this.logger.warn("TraceID not exist. start new trace.");
        }
        this.logger.warn("TraceID not exist. camSampled is false. skip trace.");
        return trace;
    }

    private boolean samplingEnable(final RequestTraceProxy requestTrace) {
        final String samplingFlag = requestTrace.getSamplingFlag();
        if (this.isDebug) {
            this.logger.warn("SamplingFlag={}", samplingFlag);
        }
        return SamplingFlagUtils.isSamplingFlag(samplingFlag);
    }

    private Trace newTrace() {
        if (this.async) {
            return this.traceContext.newAsyncTraceObject();
        }
        return this.traceContext.newTraceObject();
    }
}
