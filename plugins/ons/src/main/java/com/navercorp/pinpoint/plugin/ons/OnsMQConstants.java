package com.navercorp.pinpoint.plugin.ons;

import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.trace.ServiceTypeFactory;
import com.navercorp.pinpoint.common.trace.ServiceTypeProperty;

public final class OnsMQConstants {
    public static final ServiceType ALIWARE_MQ_SEND;
    public static final ServiceType ALIWARE_MQ_RECV;
    public static final String BLANK_ADDRESS = "";

    static {
        ALIWARE_MQ_SEND = ServiceTypeFactory.of(8410, "ALIWARE_MQ_SEND", ServiceTypeProperty.QUEUE, ServiceTypeProperty.RECORD_STATISTICS);
        ALIWARE_MQ_RECV = ServiceTypeFactory.of(9898, "ALIWARE_MQ_RECV", ServiceTypeProperty.QUEUE, ServiceTypeProperty.RECORD_STATISTICS);
    }
}
