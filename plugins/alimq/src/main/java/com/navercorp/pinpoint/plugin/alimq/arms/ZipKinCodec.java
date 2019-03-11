package com.navercorp.pinpoint.plugin.alimq.arms;

import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.util.NumberUtils;
import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.plugin.alimq.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static com.navercorp.pinpoint.plugin.alimq.arms.HexCodec.toLowerHex;

public class ZipKinCodec implements RequestTraceCodec
{
    protected static final String TRACE_ID_NAME = "X-B3-TraceId";
    protected static final String SPAN_ID_NAME = "X-B3-SpanId";
    protected static final String PARENT_SPAN_ID_NAME = "X-B3-ParentSpanId";
    protected static final String SAMPLED_NAME = "X-B3-Sampled";
    protected static final String FLAGS_NAME = "X-B3-Flags";
    protected static final String BAGGAGE_PREFIX = "baggage-";
    private PLogger logger;
    private final boolean isDebug;
    
    public ZipKinCodec() {
        this.logger = PLoggerFactory.getLogger((Class)this.getClass());
        this.isDebug = this.logger.isDebugEnabled();
    }
    
    @Override
    public void inject(final TraceContext traceContext, final RequestTrace requestTrace, final Trace trace, final TraceId nextId) {
        requestTrace.setHeader("X-B3-TraceId", toLowerHex(Long.parseLong(nextId.getEagleEyeTraceId())));
        requestTrace.setHeader("X-B3-SpanId", toLowerHex(nextId.getSpanId()));
        requestTrace.setHeader("X-B3-ParentSpanId", toLowerHex(nextId.getParentSpanId()));
        requestTrace.setHeader("X-B3-Flags", String.valueOf(nextId.getFlags()));
        final boolean sampling = trace.canRealSampled();
        if (!sampling) {
            if (this.isDebug) {
                this.logger.debug("set Sampling flag=false");
            }
            requestTrace.setHeader("X-B3-Sampled", "0");
        }
        if (nextId.baggageItems() != null && nextId.baggageItems().size() > 0) {
            for (final Map.Entry<String, String> entry : nextId.baggageItems().entrySet()) {
                requestTrace.setHeader("baggage-" + entry.getKey(), entry.getValue());
            }
        }
    }
    
    @Override
    public TraceId extract(final TraceContext traceContext, final RequestTrace requestTrace) {
        String traceId = null;
        long spanId = -1L;
        long parentId = -1L;
        short flags = 0;
        Map<String, String> baggage = null;
        final Enumeration headerNames = requestTrace.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                final String key = (String) headerNames.nextElement();
                if (key.equalsIgnoreCase("X-B3-TraceId")) {
                    traceId = String.valueOf(HexCodec.lowerHexToUnsignedLong(requestTrace.getHeader(key)));
                }
                else if (key.equalsIgnoreCase("X-B3-ParentSpanId")) {
                    parentId = HexCodec.lowerHexToUnsignedLong(requestTrace.getHeader(key));
                }
                else if (key.equalsIgnoreCase("X-B3-SpanId")) {
                    spanId = HexCodec.lowerHexToUnsignedLong(requestTrace.getHeader(key));
                }
                else if (key.equalsIgnoreCase("X-B3-Flags")) {
                    flags = NumberUtils.parseShort(requestTrace.getHeader("X-B3-Flags"), (short)0);
                }
                if (!StringUtils.isEmpty(key) && key.startsWith("baggage-")) {
                    if (baggage == null) {
                        baggage = new HashMap<String, String>();
                    }
                    baggage.put(key, requestTrace.getHeader(key));
                }
            }
        }
        if (!StringUtils.isEmpty(traceId)) {
            final TraceId id = traceContext.createTraceId(traceId, "0", "", "", parentId, spanId, flags);
            if (baggage != null) {
                id.withBaggage(baggage);
            }
            if (this.isDebug) {
                this.logger.debug("TraceID exist. continue trace. {}", (Object)id);
            }
            return id;
        }
        return null;
    }
    
    @Override
    public String getSamplingFlag(final RequestTrace requestTrace) {
        return requestTrace.getHeader("X-B3-Sampled");
    }
}
