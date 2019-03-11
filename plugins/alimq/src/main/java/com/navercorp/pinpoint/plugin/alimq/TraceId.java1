package com.navercorp.pinpoint.plugin.alimq;

import java.util.Map;

public interface TraceId
{
    TraceId getNextTraceId();
    
    long getSpanId();
    
    String getAgentId();
    
    String getClientIp();
    
    String getServerIp();
    
    long getParentSpanId();
    
    boolean isRoot();
    
    String getEagleEyeTraceId();
    
    String getEagleEyeRpcId();
    
    String getRootApp();
    
    short getFlags();
    
    void withBaggage(final Map<String, String> p0);
    
    Map<String, String> baggageItems();
    
    void updateEagleEyeTraceId(final String p0);
}
