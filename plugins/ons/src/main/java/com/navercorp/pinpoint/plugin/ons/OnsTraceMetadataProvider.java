package com.navercorp.pinpoint.plugin.ons;

import com.navercorp.pinpoint.common.trace.TraceMetadataProvider;
import com.navercorp.pinpoint.common.trace.TraceMetadataSetupContext;
import com.navercorp.pinpoint.plugin.ons.constant.ServiceTypeConstants;

public class OnsTraceMetadataProvider implements TraceMetadataProvider {
    public void setup(final TraceMetadataSetupContext context) {
        context.addServiceType(ServiceTypeConstants.ONS_SEND);
        context.addServiceType(ServiceTypeConstants.ONS_RECV);
    }
}
