package com.navercorp.pinpoint.plugin.ons;

import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;

class OnsPluginConfig {
    private final boolean onsEnable;

    OnsPluginConfig(final ProfilerConfig config) {
        this.onsEnable = config.readBoolean("profiler.ons.enable", true);
    }

    boolean isOnsEnable() {
        return this.onsEnable;
    }
}
