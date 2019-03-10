package com.navercorp.pinpoint.bootstrap.plugin;

import com.navercorp.pinpoint.bootstrap.config.*;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.*;

public interface ProfilerPluginSetupContext
{
    ProfilerConfig getConfig();
    
    void addApplicationTypeDetector(final ApplicationTypeDetector... p0);
    
    void addJdbcUrlParser(final JdbcUrlParserV2 p0);
}
