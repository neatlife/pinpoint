package com.navercorp.pinpoint.plugin.alimq;

public enum Header {
    EAGLEEYE_TRACE_ID("EagleEye-TraceID"),
    EAGLEEYE_RPC_ID("EagleEye-RpcID"),
    EAGLEEYE_IP("EagleEye-IP"),
    EAGLEEYE_ROOT_APP("EagleEye-ROOT-APP"),
    SPAN_ID("EagleEye-SpanID"),
    PARENT_SPAN_ID("EagleEye-pSpanID"),
    SAMPLED("EagleEye-Sampled"),
    PARENT_APPLICATION_NAME("EagleEye-pAppName"),
    PARENT_RPC_NAME("EagleEye-pRpc"),
    EAGLEEYE_USERDATA("EagleEye-UserData");

    private String name;

    Header(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
