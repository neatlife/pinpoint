package com.navercorp.pinpoint.bootstrap.plugin;

import java.util.*;

public interface RequestTrace
{
    String getHeader(final String p0);
    
    void setHeader(final String p0, final String p1);
    
    Enumeration getHeaderNames();
}
