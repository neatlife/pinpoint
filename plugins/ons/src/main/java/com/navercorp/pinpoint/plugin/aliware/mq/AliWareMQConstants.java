package com.navercorp.pinpoint.plugin.aliware.mq;

import com.navercorp.pinpoint.common.trace.*;

public final class AliWareMQConstants
{
    public static final ServiceType ALIWARE_MQ_SEND;
    public static final ServiceType ALIWARE_MQ_RECV;
    public static final String BLANK_ADDRESS = "";
    
    static {
        ALIWARE_MQ_SEND = ServiceTypeFactory.of(8410, "ALIWARE_MQ_SEND", new ServiceTypeProperty[] { ServiceTypeProperty.QUEUE, ServiceTypeProperty.RECORD_STATISTICS });
        ALIWARE_MQ_RECV = ServiceTypeFactory.of(8420, "ALIWARE_MQ_RECV", new ServiceTypeProperty[] { ServiceTypeProperty.QUEUE, ServiceTypeProperty.RECORD_STATISTICS });
    }

    public static final AnnotationKey ALIWARE_MQ_CONSUMER_DELAY = AnnotationKeyFactory.of(201, "aliWareMQConsumerDelay", new AnnotationKeyProperty[0]);
}
