package com.navercorp.pinpoint.plugin.ons.interceptor;

import com.aliyun.openservices.ons.api.Message;
import com.navercorp.pinpoint.bootstrap.context.*;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.plugin.ons.constant.ServiceTypeConstants;
import com.navercorp.pinpoint.plugin.ons.field.OnsPropertiesGetter;
import com.navercorp.pinpoint.plugin.ons.method.OnsProducerMethodDescriptor;
import com.navercorp.pinpoint.plugin.ons.util.ParameterUtil;

import java.util.Properties;

public class OnsProducerSendInterceptor implements AroundInterceptor {
    private static final OnsProducerMethodDescriptor PRODUCER_ENTRY_METHOD_DESCRIPTOR;

    static {
        PRODUCER_ENTRY_METHOD_DESCRIPTOR = new OnsProducerMethodDescriptor();
    }

    private final PLogger logger;
    private final TraceContext traceContext;
    private final MethodDescriptor methodDescriptor;
    private volatile boolean isFirst;

    public OnsProducerSendInterceptor(final TraceContext traceContext, final MethodDescriptor methodDescriptor) {
        this.logger = PLoggerFactory.getLogger(this.getClass());
        this.isFirst = false;
        this.traceContext = traceContext;
        this.methodDescriptor = methodDescriptor;
        traceContext.cacheApi(OnsProducerSendInterceptor.PRODUCER_ENTRY_METHOD_DESCRIPTOR);
        logger.warn("OnsProducerSendInterceptor constructor running");
    }

    private Trace createTrace(final Message message) throws Throwable {
        logger.warn("OnsProducerSendInterceptor createTrace running");
//        Trace trace = this.traceContext.currentRawTraceObject();
        Trace trace = ParameterUtil.extract(traceContext, message.getUserProperties());
        try {
            if (trace == null) {
                trace = this.traceContext.newTraceObject();
            }
            logger.warn("trace.canSampled(): {}", trace.canSampled());
            if (trace.canSampled()) {
                final SpanRecorder recorder = trace.getSpanRecorder();
                recorder.recordServiceType(ServiceTypeConstants.ONS_SEND);
                recorder.recordApi(OnsProducerSendInterceptor.PRODUCER_ENTRY_METHOD_DESCRIPTOR);
                recorder.recordEndPoint("");
                recorder.recordRemoteAddress("");
                recorder.recordRpcName("Send Topic@" + message.getTopic());
            }
        } catch (Throwable t) {
            this.logger.warn("BEFORE. Cause:{}", t.getMessage(), t);
        }
        return trace;
    }

    @Override
    public void before(final Object target, final Object[] args) {
        logger.warn("OnsProducerSendInterceptor before running");
        try {
            final Message message = (Message) args[0];
            Trace trace = this.traceContext.currentTraceObject();
            logger.warn("OnsProducerSendInterceptor before running, trace {} message {}", trace, message);
            if (trace == null) {
                trace = this.createTrace(message);
                this.isFirst = true;
            } else {
                this.isFirst = false;
            }
            if (!trace.canSampled()) {
                return;
            }
            final SpanEventRecorder recorder = trace.traceBlockBegin();
            recorder.recordServiceType(ServiceTypeConstants.ONS_SEND);
            recorder.recordNextSpanId(trace.getTraceId().getNextTraceId().getSpanId());
        } catch (Throwable th) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn("BEFORE. Caused:{}", th.getMessage(), th);
            }
        }
    }

    @Override
    public void after(final Object target, final Object[] args, final Object result, final Throwable throwable) {
        logger.warn("OnsProducerSendInterceptor after running");
        final Trace trace = this.traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }
        try {
            final Message message = (Message) args[0];
            final Properties properties = ((OnsPropertiesGetter) target)._$PINPOINT$_getProperties();
            final String onsAddr = properties.getProperty("ONSAddr", "");
            final SpanEventRecorder recorder = trace.currentSpanEventRecorder();
            if (!StringUtils.isEmpty(onsAddr)) {
                recorder.recordDestinationId(onsAddr + "@" + message.getTopic());
            } else {
                recorder.recordDestinationId(message.getTopic());
            }
            recorder.recordApi(methodDescriptor);
//            recorder.recordRpcName("Send Topic@" + message.getTopic());
            if (throwable != null) {
                recorder.recordException(throwable);
            }
        } catch (Throwable t) {
            this.logger.warn("AFTER error. Cause:{}", t.getMessage(), t);
        } finally {
            logger.warn("isFirst: {}", this.isFirst);
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
