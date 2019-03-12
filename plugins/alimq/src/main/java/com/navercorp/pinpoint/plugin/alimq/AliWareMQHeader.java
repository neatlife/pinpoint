package com.navercorp.pinpoint.plugin.alimq;

import com.navercorp.pinpoint.bootstrap.context.Header;
import com.navercorp.pinpoint.common.util.StringUtils;

import java.util.Map;

public class AliWareMQHeader {
    public static String getParentApplicationName(final Map<String, String> properties, final String defaultValue) {
        final String parentApplicationName = properties.get(Header.HTTP_PARENT_APPLICATION_NAME.toString());
        if (!StringUtils.isEmpty(parentApplicationName)) {
            return parentApplicationName;
        }
        return defaultValue;
    }
}
