package com.navercorp.pinpoint.bootstrap.plugin.arms;

import com.navercorp.pinpoint.bootstrap.plugin.*;
import com.navercorp.pinpoint.bootstrap.context.*;

public interface RequestTraceCodec
{
    void inject(final TraceContext p0, final RequestTrace p1, final Trace p2, final TraceId p3);
    
    TraceId extract(final TraceContext p0, final RequestTrace p1);
    
    String getSamplingFlag(final RequestTrace p0);
}
