package com.navercorp.pinpoint.plugin.alimq.descriptor;

import com.navercorp.pinpoint.bootstrap.context.*;

public class AliWareMQConsumerEntryMethodDescriptor implements MethodDescriptor
{
    private int apiId;
    private int type;
    
    public AliWareMQConsumerEntryMethodDescriptor() {
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
        return AliWareMQConsumerEntryMethodDescriptor.class.getName();
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
        return "AliWareMQ Consumer Invocation";
    }
    @Override
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
}
