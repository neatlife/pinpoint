package com.navercorp.pinpoint.plugin.alimq;

import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;

public class OnsPluginConfig {
    private final boolean alimqEnable;

    public OnsPluginConfig(final ProfilerConfig config) {
        this.alimqEnable = config.readBoolean("profiler.alimq.enable", true);
    }

    public boolean isAlimqEnable() {
        return this.alimqEnable;
    }
}
