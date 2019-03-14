package com.navercorp.pinpoint.plugin.ons.constant;

import com.navercorp.pinpoint.common.trace.AnnotationKey;
import com.navercorp.pinpoint.common.trace.AnnotationKeyFactory;

public interface AnnotationKeyConstant extends com.navercorp.pinpoint.common.trace.AnnotationKey {
    AnnotationKey ONS_CONSUMER_DELAY = AnnotationKeyFactory.of(201, "onsConsumerDelay");

    String getName();

    int getCode();

    boolean isErrorApiMetadata();

    boolean isViewInRecordSet();
}
