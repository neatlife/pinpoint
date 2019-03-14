package com.navercorp.pinpoint.plugin.ons.arms;


import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.context.TraceId;
import com.navercorp.pinpoint.plugin.ons.RequestTrace;

public interface RequestTraceCodec {
    void inject(final TraceContext p0, final RequestTrace p1, final Trace p2, final TraceId p3);

    TraceId extract(final TraceContext p0, final RequestTrace p1);

    String getSamplingFlag(final RequestTrace p0);
}
