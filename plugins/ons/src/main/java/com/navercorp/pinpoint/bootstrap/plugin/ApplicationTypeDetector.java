package com.navercorp.pinpoint.bootstrap.plugin;

import com.navercorp.pinpoint.common.trace.*;
import com.navercorp.pinpoint.bootstrap.resolver.*;

public interface ApplicationTypeDetector
{
    ServiceType getApplicationType();
    
    boolean detect(final ConditionProvider p0);
}
