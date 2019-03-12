package com.navercorp.pinpoint.plugin.alimq.interceptor;

import com.aliyun.openservices.ons.api.Message;
import com.navercorp.pinpoint.bootstrap.context.*;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.plugin.alimq.AliWareMQConstants;
import com.navercorp.pinpoint.plugin.alimq.RequestTrace;
import com.navercorp.pinpoint.plugin.alimq.RequestTraceProxy;
import com.navercorp.pinpoint.plugin.alimq.descriptor.AliWareMQProducerEntryMethodDescriptor;
import com.navercorp.pinpoint.plugin.alimq.field.getter.AliWareMQPropertiesGetter;
import com.navercorp.pinpoint.plugin.alimq.request.RequestTraceWriter;

import java.util.Enumeration;
import java.util.Properties;

public class AliWareMQProducerSendInterceptor implements AroundInterceptor {
    private static final AliWareMQProducerEntryMethodDescriptor PRODUCER_ENTRY_METHOD_DESCRIPTOR;

    static {
        PRODUCER_ENTRY_METHOD_DESCRIPTOR = new AliWareMQProducerEntryMethodDescriptor();
    }

    private final PLogger logger;
    private final boolean isDebug;
    private final TraceContext traceContext;
    private final MethodDescriptor descriptor;
    private volatile boolean isFirst;
    private RequestTraceWriter requestTraceWriter;

    public AliWareMQProducerSendInterceptor(final TraceContext traceContext, final MethodDescriptor descriptor) {
        this.logger = PLoggerFactory.getLogger(this.getClass());
        this.isDebug = this.logger.isDebugEnabled();
        this.isFirst = false;
        this.requestTraceWriter = null;
        this.traceContext = traceContext;
        this.descriptor = descriptor;
        traceContext.cacheApi(AliWareMQProducerSendInterceptor.PRODUCER_ENTRY_METHOD_DESCRIPTOR);
        this.requestTraceWriter = new RequestTraceWriter(this.traceContext);
        logger.warn("AliWareMQProducerSendInterceptor constructor running");
    }

    private void inject(final Trace trace, final Message message) {
        logger.warn("AliWareMQProducerSendInterceptor inject running");
        final TraceId nextId = trace.getTraceId().getNextTraceId();
        final RequestTraceProxy requestTrace = new RequestTraceProxy(new RequestTrace() {
            public String getHeader(final String name) {
                return null;
            }

            public void setHeader(final String key, final String name) {
                message.putUserProperties(key, name);
            }

            public Enumeration getHeaderNames() {
                return null;
            }
        });
        this.requestTraceWriter.write(requestTrace, trace, nextId);
    }

    private Trace createTrace(final Message message) throws Throwable {
        logger.warn("AliWareMQProducerSendInterceptor createTrace running");
        Trace trace = this.traceContext.currentRawTraceObject();
        try {
            if (trace == null) {
                trace = this.traceContext.newTraceObject();
            }
            logger.warn("trace.canSampled(): {}", trace.canSampled());
            if (trace.canSampled()) {
                final SpanRecorder recorder = trace.getSpanRecorder();
                recorder.recordServiceType(AliWareMQConstants.ALIWARE_MQ_SEND);
                recorder.recordApi(AliWareMQProducerSendInterceptor.PRODUCER_ENTRY_METHOD_DESCRIPTOR);
                recorder.recordEndPoint("");
                recorder.recordRemoteAddress("");
                recorder.recordRpcName("Send Topic@" + message.getTopic());
            }
        } catch (Throwable t) {
            this.logger.warn("BEFORE. Cause:{}", t.getMessage(), t);
        }
        return trace;
    }

    public void before(final Object target, final Object[] args) {
        logger.warn("AliWareMQProducerSendInterceptor before running");
        if (this.isDebug) {
            this.logger.beforeInterceptor(target, args);
        }
        try {
            final Message message = (Message) args[0];
            Trace trace = this.traceContext.currentTraceObject();
            logger.warn("AliWareMQProducerSendInterceptor before running, trace {} message {}", trace, message);
            if (trace == null) {
                trace = this.createTrace(message);
                this.isFirst = true;
            } else {
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
        } catch (Throwable th) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn("BEFORE. Caused:{}", th.getMessage(), th);
            }
        }
    }

    public void after(final Object target, final Object[] args, final Object result, final Throwable throwable) {
        logger.warn("AliWareMQProducerSendInterceptor after running");
        if (this.isDebug) {
            this.logger.afterInterceptor(target, args);
        }
        final Trace trace = this.traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }
        try {
            final Message message = (Message) args[0];
            final Properties properties = ((AliWareMQPropertiesGetter) target)._$PINPOINT$_getProperties();
            final String onsAddr = properties.getProperty("ONSAddr", "");
            final SpanEventRecorder recorder = trace.currentSpanEventRecorder();
            if (!StringUtils.isEmpty(onsAddr)) {
                recorder.recordDestinationId(onsAddr + "@" + message.getTopic());
            } else {
                recorder.recordDestinationId(message.getTopic());
            }
            recorder.recordApi(this.descriptor);
            recorder.recordRpcName("Send Topic@" + message.getTopic());
            if (throwable != null) {
                recorder.recordException(throwable);
            }
        } catch (Throwable t) {
            this.logger.warn("AFTER error. Cause:{}", t.getMessage(), t);
        } finally {
            if (this.isFirst) {
                this.traceContext.removeTraceObject();
                trace.traceBlockEnd();
                trace.close();
            } else {
                trace.traceBlockEnd();
            }
        }
    }
}