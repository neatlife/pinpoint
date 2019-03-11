package com.navercorp.pinpoint.plugin.alimq.descriptor;

import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;

public class AliWareMQProducerEntryMethodDescriptor implements MethodDescriptor
{
    private int apiId;
    private int type;
    
    public AliWareMQProducerEntryMethodDescriptor() {
        this.apiId = 0;
        this.type = 100;
    }
    
    public String getMethodName() {
        return "";
    }
    
    public String getClassName() {
        return "";
    }
    
    public String[] getParameterTypes() {
        return null;
    }
    
    public String[] getParameterVariableName() {
        return null;
    }
    
    public String getParameterDescriptor() {
        return "()";
    }
    
    public int getLineNumber() {
        return -1;
    }
    
    public String getFullName() {
        return AliWareMQProducerEntryMethodDescriptor.class.getName();
    }
    
    public int getApiId() {
        return this.apiId;
    }
    
    public void setApiId(final int apiId) {
        this.apiId = apiId;
    }
    
    public String getApiDescriptor() {
        return "AliWareMQ Producer Invocation";
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
}
