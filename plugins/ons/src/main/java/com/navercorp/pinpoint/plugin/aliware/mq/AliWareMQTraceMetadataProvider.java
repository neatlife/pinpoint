package com.navercorp.pinpoint.plugin.aliware.mq;

import com.navercorp.pinpoint.common.trace.*;

public class AliWareMQTraceMetadataProvider implements TraceMetadataProvider
{
    @Override
    public void setup(final TraceMetadataSetupContext context) {
        context.addServiceType(AliWareMQConstants.ALIWARE_MQ_SEND);
        context.addServiceType(AliWareMQConstants.ALIWARE_MQ_RECV);
    }
}
