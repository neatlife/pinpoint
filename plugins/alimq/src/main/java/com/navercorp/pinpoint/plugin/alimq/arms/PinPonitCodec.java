package com.navercorp.pinpoint.plugin.alimq.arms;


import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.context.TraceId;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.util.NumberUtils;
import com.navercorp.pinpoint.plugin.alimq.RequestTrace;

public class PinPonitCodec implements RequestTraceCodec {


    public static final String META_TRACE_ID = "Pinpoint-TraceID";
    public static final String META_SPAN_ID = "Pinpoint-SpanID";
    public static final String META_PARENT_SPAN_ID = "Pinpoint-pSpanID";
    public static final String META_SAMPLED = "Pinpoint-Sampled";
    public static final String META_FLAGS = "Pinpoint-Flags";
    public static final String META_PARENT_APPLICATION_NAME = "Pinpoint-pAppName";
    public static final String META_PARENT_APPLICATION_TYPE = "Pinpoint-pAppType";
    private final boolean isDebug;
    private PLogger logger;

    public PinPonitCodec() {
        this.logger = PLoggerFactory.getLogger(this.getClass());
        this.isDebug = this.logger.isDebugEnabled();
    }

    @Override
    public void inject(final TraceContext traceContext, final RequestTrace requestTrace, final Trace trace, final TraceId nextId) {

        requestTrace.setHeader(META_TRACE_ID, nextId.getTransactionId());
        requestTrace.setHeader(META_SPAN_ID, String.valueOf(nextId.getSpanId()));
        requestTrace.setHeader(META_PARENT_SPAN_ID, String.valueOf(nextId.getParentSpanId()));


        requestTrace.setHeader(META_PARENT_APPLICATION_NAME, traceContext.getApplicationName());
        requestTrace.setHeader(META_PARENT_APPLICATION_TYPE, Short.toString(traceContext.getServerTypeCode()));
        requestTrace.setHeader(META_FLAGS, String.valueOf(nextId.getFlags()));

        final boolean sampling = trace.canSampled();
        logger.warn("sampling: {}", sampling);
        if (!sampling) {
            this.logger.warn("set Sampling flag=false");
            requestTrace.setHeader(META_SAMPLED, "0");
        }
    }


    @Override
    public TraceId extract(final TraceContext traceContext, final RequestTrace requestTrace) {

        this.logger.warn("TraceContext {}", traceContext.getAgentId());
        this.logger.warn("META_TRACE_ID {}", requestTrace.getHeader(META_TRACE_ID));
        this.logger.warn("META_PARENT_SPAN_ID {}", requestTrace.getHeader(META_PARENT_SPAN_ID));
        this.logger.warn("META_SPAN_ID {}", requestTrace.getHeader(META_SPAN_ID));


        final String traceId = requestTrace.getHeader(META_TRACE_ID);
        if (traceId != null) {
            final long parentSpanID = NumberUtils.parseLong(requestTrace.getHeader(META_PARENT_SPAN_ID), -1L);

            final long spanID = NumberUtils.parseLong(requestTrace.getHeader(META_SPAN_ID), -1L);

            final TraceId id = traceContext.createTraceId(traceId, parentSpanID, spanID, (short) 0);  //createTraceId(traceId,parentSpanID,spanID,(short)0);
            this.logger.warn("TraceID exist. continue trace. {}", id);
            return id;
        }
        return null;
    }


    @Override
    public String getSamplingFlag(final RequestTrace requestTrace) {
        return requestTrace.getHeader(META_SAMPLED);
    }
}
