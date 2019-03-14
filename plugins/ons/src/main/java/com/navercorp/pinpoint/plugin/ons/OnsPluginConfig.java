package com.navercorp.pinpoint.plugin.ons;

import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;

public class OnsPluginConfig {
    private final boolean alimqEnable;

    public OnsPluginConfig(final ProfilerConfig config) {
        this.alimqEnable = config.readBoolean("profiler.ons.enable", true);
    }

    public boolean isAlimqEnable() {
        return this.alimqEnable;
    }
}
