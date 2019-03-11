package com.navercorp.pinpoint.plugin.alimq;

import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;

public class AliWareMQPluginConfig
{
    private final boolean aliWareMQEnable;
    
    public AliWareMQPluginConfig(final ProfilerConfig config) {
        this.aliWareMQEnable = config.readBoolean("profiler.aliWareMQ.enable", true);
    }
    
    public boolean isAliWareMQEnable() {
        return this.aliWareMQEnable;
    }
}
