package com.navercorp.pinpoint.plugin.ons;

import java.util.Enumeration;

public interface RequestTrace {
    String getHeader(final String p0);

    void setHeader(final String p0, final String p1);

    Enumeration getHeaderNames();
}
