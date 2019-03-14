package com.navercorp.pinpoint.plugin.ons.method;

import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;

public class OnsProduceMethod implements MethodDescriptor {
    private int apiId;
    private int type;

    public OnsProduceMethod() {
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
        return OnsProduceMethod.class.getName();
    }

    public int getApiId() {
        return this.apiId;
    }

    public void setApiId(final int apiId) {
        this.apiId = apiId;
    }

    public String getApiDescriptor() {
        return "Ons Producer Invocation";
    }

    public int getType() {
        return this.type;
    }

    public void setType(final int type) {
        this.type = type;
    }
}
