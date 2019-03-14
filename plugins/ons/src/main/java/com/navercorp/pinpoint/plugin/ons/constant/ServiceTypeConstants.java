package com.navercorp.pinpoint.plugin.ons.constant;

import com.navercorp.pinpoint.common.trace.ServiceType;
import com.navercorp.pinpoint.common.trace.ServiceTypeFactory;
import com.navercorp.pinpoint.common.trace.ServiceTypeProperty;

public final class ServiceTypeConstants {
    public static final ServiceType ONS_SEND;
    public static final ServiceType ONS_RECV;

    static {
        ONS_SEND = ServiceTypeFactory.of(8410, "ONS_SEND", ServiceTypeProperty.QUEUE, ServiceTypeProperty.RECORD_STATISTICS);
        ONS_RECV = ServiceTypeFactory.of(9898, "ONS_RECV", ServiceTypeProperty.QUEUE, ServiceTypeProperty.RECORD_STATISTICS);
    }
}
