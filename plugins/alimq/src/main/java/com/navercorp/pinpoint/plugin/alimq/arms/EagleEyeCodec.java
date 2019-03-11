package com.navercorp.pinpoint.plugin.alimq.arms;

import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.util.NumberUtils;
import com.navercorp.pinpoint.bootstrap.util.StringUtils;
import com.navercorp.pinpoint.plugin.alimq.*;

import java.util.HashMap;
import java.util.Map;

public class EagleEyeCodec implements RequestTraceCodec
{
    public static final String TRACE_ID_NAME;
    private PLogger logger;
    private final boolean isDebug;
    
    public EagleEyeCodec() {
        this.logger = PLoggerFactory.getLogger((Class)this.getClass());
        this.isDebug = this.logger.isDebugEnabled();
    }
    
    @Override
    public void inject(final TraceContext traceContext, final RequestTrace requestTrace, final Trace trace, final TraceId nextId) {
        requestTrace.setHeader(Header.EAGLEEYE_TRACE_ID.toString(), nextId.getEagleEyeTraceId());
        requestTrace.setHeader(Header.EAGLEEYE_RPC_ID.toString(), nextId.getEagleEyeRpcId());
        requestTrace.setHeader(Header.SPAN_ID.toString(), String.valueOf(nextId.getSpanId()));
        requestTrace.setHeader(Header.PARENT_SPAN_ID.toString(), String.valueOf(nextId.getParentSpanId()));
        requestTrace.setHeader(Header.PARENT_APPLICATION_NAME.toString(), traceContext.getApplicationName());
        requestTrace.setHeader(Header.PARENT_RPC_NAME.toString(), trace.getRpcName());
        requestTrace.setHeader(Header.EAGLEEYE_IP.toString(), nextId.getServerIp());
        requestTrace.setHeader(Header.EAGLEEYE_ROOT_APP.toString(), nextId.getRootApp());
        final boolean sampling = trace.canRealSampled();
        if (!sampling) {
            if (this.isDebug) {
                this.logger.debug("set Sampling flag=false");
            }
            requestTrace.setHeader(Header.SAMPLED.toString(), "s0");
        }
        final Map<String, String> baggage = nextId.baggageItems();
        if (baggage != null && baggage.size() > 0) {
            final StringBuffer sb = new StringBuffer();
            for (final Map.Entry<String, String> entry : baggage.entrySet()) {
                sb.append(entry.getKey() + "=" + entry.getValue()).append("&");
            }
            requestTrace.setHeader(Header.EAGLEEYE_USERDATA.toString(), sb.toString());
        }
    }
    
    @Override
    public TraceId extract(final TraceContext traceContext, final RequestTrace requestTrace) {
        final String traceId = requestTrace.getHeader(Header.EAGLEEYE_TRACE_ID.toString());
        if (traceId != null) {
            final String eagleEyeRpcId = requestTrace.getHeader(Header.EAGLEEYE_RPC_ID.toString());
            final long parentSpanID = NumberUtils.parseLong(requestTrace.getHeader(Header.PARENT_SPAN_ID.toString()), -1L);
            final long spanID = NumberUtils.parseLong(requestTrace.getHeader(Header.SPAN_ID.toString()), -1L);
            final String clientIp = requestTrace.getHeader(Header.EAGLEEYE_IP.toString());
            final String rootApp = requestTrace.getHeader(Header.EAGLEEYE_ROOT_APP.toString());
            final TraceId id = traceContext.createTraceId(traceId, eagleEyeRpcId, clientIp, rootApp, parentSpanID, spanID, (short)0);
            if (this.isDebug) {
                this.logger.debug("TraceID exist. continue trace. {}", (Object)id);
            }
            final String userData = requestTrace.getHeader(Header.EAGLEEYE_USERDATA.toString());
            if (!StringUtils.isEmpty(userData)) {
                final Map<String, String> baggage = new HashMap<String, String>();
                final String[] arr$;
                final String[] entries = arr$ = userData.split("&");
                for (final String entry : arr$) {
                    final int index = entry.indexOf("=");
                    if (index > -1) {
                        baggage.put(entry.substring(0, index), entry.substring(index + 1));
                    }
                }
                id.withBaggage(baggage);
            }
            return id;
        }
        return null;
    }
    
    @Override
    public String getSamplingFlag(final RequestTrace requestTrace) {
        return requestTrace.getHeader(Header.SAMPLED.toString());
    }
    
    static {
        TRACE_ID_NAME = Header.EAGLEEYE_TRACE_ID.toString();
    }
}
