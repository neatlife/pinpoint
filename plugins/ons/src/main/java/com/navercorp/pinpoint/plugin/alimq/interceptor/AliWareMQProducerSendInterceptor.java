package com.navercorp.pinpoint.plugin.alimq.interceptor;

import com.navercorp.pinpoint.bootstrap.interceptor.*;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.plugin.RequestTrace;
import com.navercorp.pinpoint.bootstrap.plugin.arms.RequestTraceProxy;
import com.navercorp.pinpoint.bootstrap.plugin.request.MyRequestTraceWriter;
import com.navercorp.pinpoint.plugin.alimq.AliWareMQConstants;
import com.navercorp.pinpoint.plugin.alimq.descriptor.AliWareMQProducerEntryMethodDescriptor;
import com.navercorp.pinpoint.plugin.alimq.field.getter.AliWareMQPropertiesGetter;
import com.navercorp.pinpoint.plugin.aliware.mq.descriptor.*;
import com.aliyun.openservices.ons.api.*;


import com.navercorp.pinpoint.plugin.aliware.mq.*;
import com.navercorp.pinpoint.bootstrap.context.*;
import com.navercorp.pinpoint.plugin.aliware.mq.field.getter.*;
import com.navercorp.pinpoint.common.util.*;
import java.util.*;

public class AliWareMQProducerSendInterceptor implements AroundInterceptor
{
    private static final AliWareMQProducerEntryMethodDescriptor PRODUCER_ENTRY_METHOD_DESCRIPTOR;
    private final PLogger logger = PLoggerFactory.getLogger(getClass());
    private final boolean isDebug = logger.isDebugEnabled();
    private final TraceContext traceContext;
    private final MethodDescriptor descriptor;
    private volatile boolean isFirst;
    private MyRequestTraceWriter requestTraceWriter;
    
    public AliWareMQProducerSendInterceptor(final TraceContext traceContext, final MethodDescriptor descriptor) {
        this.logger.info("init traceContext",traceContext.getAgentId());
        this.isFirst = false;
        this.traceContext = traceContext;
        this.requestTraceWriter = null;
        this.descriptor = descriptor;
        traceContext.cacheApi(AliWareMQProducerSendInterceptor.PRODUCER_ENTRY_METHOD_DESCRIPTOR);
        this.requestTraceWriter = new MyRequestTraceWriter(this.traceContext);
    }
    
    private void inject(final Trace trace, final Message message) {
        final TraceId nextId = trace.getTraceId().getNextTraceId();

        this.logger.warn("inject getNextTraceId:{}",nextId);

        final RequestTraceProxy requestTrace = new RequestTraceProxy(new RequestTrace() {
            @Override
            public String getHeader(final String name) {
                return null;
            }

            @Override
            public void setHeader(final String key, final String name) {
                message.putUserProperties(key, name);
            }

            @Override
            public Enumeration getHeaderNames() {
                return null;
            }
        });
     this.requestTraceWriter.write(requestTrace, trace, nextId);
    }
    
    private Trace createTrace(final Message message) throws Throwable {
        Trace trace = this.traceContext.currentRawTraceObject();
        try {
            if (trace == null) {
                trace = this.traceContext.newTraceObject();
            }
            this.logger.warn("BEFORE-createTrace. canSampled:{}",trace.canSampled());
            if (trace.canSampled()) {
                final SpanRecorder recorder = trace.getSpanRecorder();
                recorder.recordServiceType(AliWareMQConstants.ALIWARE_MQ_SEND);
                recorder.recordApi(AliWareMQProducerSendInterceptor.PRODUCER_ENTRY_METHOD_DESCRIPTOR);
                recorder.recordEndPoint("");
                recorder.recordRemoteAddress("");
                recorder.recordRpcName("Send Topic@" + message.getTopic());
            }
        }
        catch (Throwable t) {
           this.logger.warn("BEFORE-createTrace. Cause:{}",t.getMessage(),t);
        }
        return trace;
    }
    @Override
    public void before(final Object target, final Object[] args) {
        if (this.isDebug) {
            this.logger.beforeInterceptor(target, args);
        }
        try {
            final Message message = (Message)args[0];
            Trace trace = this.traceContext.currentTraceObject();
            if (trace == null) {
                trace = this.createTrace(message);
                this.isFirst = true;
            }
            else {
                this.isFirst = false;
            }
            this.inject(trace, message);
            if (trace == null) {
                return;
            }
            if (!trace.canSampled()) {
                return;
            }
            final SpanEventRecorder recorder = trace.traceBlockBegin();
            recorder.recordServiceType(AliWareMQConstants.ALIWARE_MQ_SEND);
        }
        catch (Throwable th) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn("BEFORE. Caused:{}",th.getMessage(),th);
            }
        }
    }

    @Override
    public void after(final Object target, final Object[] args, final Object result, final Throwable throwable) {
        if (this.isDebug) {
            this.logger.afterInterceptor(target, args);
        }
        final Trace trace = this.traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }
        try {
            final Message message = (Message)args[0];
            final Properties properties = ((AliWareMQPropertiesGetter)target)._$PINPOINT$_getProperties();
            final String onsAddr = properties.getProperty("ONSAddr", "");
            final SpanEventRecorder recorder = trace.currentSpanEventRecorder();
            if (!StringUtils.isEmpty(onsAddr)) {
                recorder.recordDestinationId(onsAddr + "@" + message.getTopic());
            }
            else {
                recorder.recordDestinationId(message.getTopic());
            }
            recorder.recordApi(this.descriptor);
            recorder.recordRpcName("Send Topic@" + message.getTopic());
//            recorder.recordEndPoint("Send Topic@" + message.getTopic());
            if (throwable != null) {
                recorder.recordException(throwable);
            }
        }
        catch (Throwable t) {
           this.logger.warn("AFTER error. Cause:{}", (Object)t.getMessage(), (Object)t);
        }
        finally {
            if (this.isFirst) {
                this.traceContext.removeTraceObject();
                trace.traceBlockEnd();
                trace.close();
            }
            else {
                trace.traceBlockEnd();
            }
        }
    }
    
    static {
        PRODUCER_ENTRY_METHOD_DESCRIPTOR = new AliWareMQProducerEntryMethodDescriptor();
    }
}
