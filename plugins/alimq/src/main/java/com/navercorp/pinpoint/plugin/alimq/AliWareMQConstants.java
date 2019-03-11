package com.navercorp.pinpoint.plugin.alimq;

import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.trace.ServiceTypeFactory;
import com.navercorp.pinpoint.common.trace.ServiceTypeProperty;

public final class AliWareMQConstants
{
    public static final ServiceType ALIWARE_MQ_SEND;
    public static final ServiceType ALIWARE_MQ_RECV;
    public static final String BLANK_ADDRESS = "";
    
    static {
        ALIWARE_MQ_SEND = ServiceTypeFactory.of(8410, "ALIWARE_MQ_SEND", new ServiceTypeProperty[] { ServiceTypeProperty.QUEUE, ServiceTypeProperty.RECORD_STATISTICS });
        ALIWARE_MQ_RECV = ServiceTypeFactory.of(8420, "ALIWARE_MQ_RECV", new ServiceTypeProperty[] { ServiceTypeProperty.QUEUE, ServiceTypeProperty.RECORD_STATISTICS });
    }
}
