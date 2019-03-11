package com.navercorp.pinpoint.plugin.alimq.arms;

import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.plugin.alimq.*;

import java.math.BigInteger;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class JaegerCodec implements RequestTraceCodec
{
    public static final String BAGGAGE_HEADER_KEY = "jaeger-baggage";
    public static final String SPAN_CONTEXT_KEY = "uber-trace-id";
    protected static final byte flagSampled = 1;
    private static final String BAGGAGE_KEY_PREFIX = "uberctx-";
    private PLogger logger;
    private final boolean isDebug;
    
    public JaegerCodec() {
        this.logger = PLoggerFactory.getLogger((Class)this.getClass());
        this.isDebug = this.logger.isDebugEnabled();
    }
    
    private String prefixedKey(final String key, final String prefix) {
        return prefix + key;
    }
    
    private String contextAsString(final TraceId nextId) {
        final int intFlag = nextId.getFlags() & 0xFF;
        return nextId.getEagleEyeTraceId() + ":" + Long.toHexString(nextId.getSpanId()) + ":" + Long.toHexString(nextId.getParentSpanId()) + ":" + Integer.toHexString(intFlag);
    }
    
    @Override
    public void inject(final TraceContext traceContext, final RequestTrace requestTrace, final Trace trace, final TraceId nextId) {
        requestTrace.setHeader("uber-trace-id", this.contextAsString(nextId));
        if (nextId.baggageItems() != null && nextId.baggageItems().size() > 0) {
            for (final Map.Entry<String, String> entry : nextId.baggageItems().entrySet()) {
                requestTrace.setHeader(this.prefixedKey(entry.getKey(), "uberctx-"), entry.getValue());
            }
        }
    }
    
    private String unprefixedKey(final String key, final String prefix) {
        return key.substring(prefix.length());
    }
    
    @Override
    public TraceId extract(final TraceContext traceContext, final RequestTrace requestTrace) {
        final String context = requestTrace.getHeader("uber-trace-id");
        if (StringUtils.isEmpty(context)) {
            return null;
        }
        final String[] parts = context.split(":");
        if (parts.length != 4) {
            return null;
        }
        final String traceId = String.valueOf(new BigInteger(parts[0], 16).longValue());
        final long parentSpanID = new BigInteger(parts[2], 16).longValue();
        final long spanID = new BigInteger(parts[1], 16).longValue();
        final short flags = new BigInteger(parts[3], 16).byteValue();
        final TraceId id = traceContext.createTraceId(traceId, "", "", "", parentSpanID, spanID, flags);
        final Enumeration headerNames = requestTrace.getHeaderNames();
        Map<String, String> baggage = null;
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                final String key = (String) headerNames.nextElement();
                if (!StringUtils.isEmpty(key) && key.startsWith("uberctx-")) {
                    if (baggage == null) {
                        baggage = new HashMap<String, String>();
                    }
                    final String value = requestTrace.getHeader(key);
                    baggage.put(this.unprefixedKey(key, "uberctx-"), value);
                }
                else {
                    if (StringUtils.isEmpty(key) || !key.equals("jaeger-baggage")) {
                        continue;
                    }
                    final String header = requestTrace.getHeader(key);
                    if (StringUtils.isEmpty(header)) {
                        continue;
                    }
                    for (final String part : header.split("\\s*,\\s*")) {
                        final String[] kv = part.split("\\s*=\\s*");
                        if (kv.length == 2) {
                            if (baggage == null) {
                                baggage = new HashMap<String, String>();
                            }
                            baggage.put(kv[0], kv[1]);
                        }
                        else {
                            this.logger.debug("malformed token in {} header: {}", (Object)"jaeger-baggage", (Object)part);
                        }
                    }
                }
            }
        }
        id.withBaggage(baggage);
        if (this.isDebug) {
            this.logger.debug("TraceID exist. continue trace. {}", (Object)id);
        }
        return id;
    }
    
    @Override
    public String getSamplingFlag(final RequestTrace requestTrace) {
        final String context = requestTrace.getHeader("uber-trace-id");
        if (StringUtils.isEmpty(context)) {
            return "0";
        }
        final String[] parts = context.split(":");
        if (parts.length != 4) {
            return null;
        }
        final short flags = new BigInteger(parts[3], 16).byteValue();
        return String.valueOf((flags & 0x1) == 0x1);
    }
}
