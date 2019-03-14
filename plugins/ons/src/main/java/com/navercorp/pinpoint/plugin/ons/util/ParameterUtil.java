package com.navercorp.pinpoint.plugin.ons.util;

import com.navercorp.pinpoint.bootstrap.context.Header;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.context.TraceId;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.util.NumberUtils;

import java.util.Map;
import java.util.Properties;

public class ParameterUtil {
    private static final PLogger logger = PLoggerFactory.getLogger("ParameterUtil");

    public static void inject(TraceContext traceContext, final Trace trace, final Map<String, String> properties) {
        final TraceId nextId = trace.getTraceId().getNextTraceId();

        properties.put(Header.HTTP_TRACE_ID.toString(), nextId.getTransactionId());
        properties.put(Header.HTTP_SPAN_ID.toString(), String.valueOf(nextId.getSpanId()));
        properties.put(Header.HTTP_PARENT_SPAN_ID.toString(), String.valueOf(nextId.getParentSpanId()));
        properties.put(Header.HTTP_PARENT_APPLICATION_NAME.toString(), traceContext.getApplicationName());
        properties.put(Header.HTTP_PARENT_APPLICATION_TYPE.toString(), Short.toString(traceContext.getServerTypeCode()));
        properties.put(Header.HTTP_FLAGS.toString(), String.valueOf(nextId.getFlags()));

        final boolean sampling = trace.canSampled();
        if (!sampling) {
            properties.put(Header.HTTP_SAMPLED.toString(), "0");
        }
    }

    public static Trace extract(TraceContext traceContext, final Properties properties) throws Throwable {
        final String traceId = (String) properties.get(Header.HTTP_TRACE_ID.toString());
        final Trace trace;
        if (traceId == null) {
            trace = traceContext.newTraceObject();
        } else {
            final TraceId id = traceContext.createTraceId(
                    traceId,
                    NumberUtils.parseLong((String) properties.get(Header.HTTP_PARENT_SPAN_ID.toString()), -1L),
                    NumberUtils.parseLong((String) properties.get(Header.HTTP_SPAN_ID.toString()), -1L),
                    NumberUtils.parseShort((String) properties.get(Header.HTTP_FLAGS.toString()), (short) -1)
            );
            trace = traceContext.continueTraceObject(id);
        }
        return trace;
    }
}
