package com.navercorp.pinpoint.plugin.ons;

import com.navercorp.pinpoint.bootstrap.config.*;

public class AliWareMQPluginConfig
{
    private final boolean aliWareMQEnable;
    
    public AliWareMQPluginConfig(final ProfilerConfig config) {
        this.aliWareMQEnable = config.readBoolean("profiler.ons.enable", true);
    }
    
    public boolean isAliWareMQEnable() {
        return this.aliWareMQEnable;
    }
}