package com.navercorp.pinpoint.plugin.ons.descriptor;

import com.navercorp.pinpoint.bootstrap.context.*;

public class AliWareMQProducerEntryMethodDescriptor implements MethodDescriptor
{
    private int apiId;
    private int type;
    
    public AliWareMQProducerEntryMethodDescriptor() {
        this.apiId = 0;
        this.type = 100;
    }
    @Override
    public String getMethodName() {
        return "";
    }
    @Override
    public String getClassName() {
        return "";
    }
    @Override
    public String[] getParameterTypes() {
        return null;
    }

    @Override
    public String[] getParameterVariableName() {
        return null;
    }

    @Override
    public String getParameterDescriptor() {
        return "()";
    }

    @Override
    public int getLineNumber() {
        return -1;
    }

    @Override
    public String getFullName() {
        return AliWareMQProducerEntryMethodDescriptor.class.getName();
    }

    @Override
    public int getApiId() {
        return this.apiId;
    }

    @Override
    public void setApiId(final int apiId) {
        this.apiId = apiId;
    }

    @Override
    public String getApiDescriptor() {
        return "AliWareMQ Producer Invocation";
    }
    @Override
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
}
