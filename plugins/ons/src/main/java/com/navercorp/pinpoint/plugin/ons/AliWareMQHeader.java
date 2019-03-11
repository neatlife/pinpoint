package com.navercorp.pinpoint.plugin.ons;

import java.util.*;
import com.navercorp.pinpoint.bootstrap.context.*;
import com.navercorp.pinpoint.common.util.*;

public class AliWareMQHeader
{
    public static String getParentApplicationName(final Map<String, String> properties, final String defaultValue) {
        final String parentApplicationName = properties.get(Header.HTTP_PARENT_APPLICATION_NAME.toString());
        if (!StringUtils.isEmpty(parentApplicationName)) {
            return parentApplicationName;
        }
        return defaultValue;
    }
}
