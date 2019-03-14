package com.navercorp.pinpoint.plugin.alimq;

import com.navercorp.pinpoint.common.trace.TraceMetadataProvider;
import com.navercorp.pinpoint.common.trace.TraceMetadataSetupContext;

public class OnsMQTraceMetadataProvider implements TraceMetadataProvider {
    public void setup(final TraceMetadataSetupContext context) {
        context.addServiceType(OnsMQConstants.ALIWARE_MQ_SEND);
        context.addServiceType(OnsMQConstants.ALIWARE_MQ_RECV);
    }
}
