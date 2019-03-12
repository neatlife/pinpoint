package com.navercorp.pinpoint.plugin.alimq.interceptor;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.message.MessageExt;
import com.navercorp.pinpoint.bootstrap.context.*;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.plugin.alimq.AliWareMQConstants;
import com.navercorp.pinpoint.plugin.alimq.AliWareMQHeader;
import com.navercorp.pinpoint.plugin.alimq.RequestTrace;
import com.navercorp.pinpoint.plugin.alimq.RequestTraceProxy;
import com.navercorp.pinpoint.plugin.alimq.annotation.AnnotationKey;
import com.navercorp.pinpoint.plugin.alimq.descriptor.AliWareMQConsumerEntryMethodDescriptor;
import com.navercorp.pinpoint.plugin.alimq.field.getter.AliWareMQPropertiesGetter;
import com.navercorp.pinpoint.plugin.alimq.request.RequestTraceReader;

import java.lang.reflect.Field;
import java.util.*;

public class AliWareMQConsumerReceiveInterceptor implements AroundInterceptor {
    private static final AliWareMQConsumerEntryMethodDescriptor CONSUMER_ENTRY_METHOD_DESCRIPTOR;

    static {
        CONSUMER_ENTRY_METHOD_DESCRIPTOR = new AliWareMQConsumerEntryMethodDescriptor();
    }

    private final PLogger logger;
    private final boolean isDebug;
    private final TraceContext traceContext;
    private final MethodDescriptor methodDescriptor;
    private final RequestTraceReader requestTraceReader;

    public AliWareMQConsumerReceiveInterceptor(final TraceContext traceContext, final MethodDescriptor methodDescriptor) {
        this.logger = PLoggerFactory.getLogger(this.getClass());
        this.isDebug = this.logger.isDebugEnabled();
        this.traceContext = traceContext;
        this.methodDescriptor = methodDescriptor;
        traceContext.cacheApi(AliWareMQConsumerReceiveInterceptor.CONSUMER_ENTRY_METHOD_DESCRIPTOR);
        this.requestTraceReader = new RequestTraceReader(this.traceContext);
        logger.warn("AliWareMQConsumerReceiveInterceptor constructor running");
    }

    @Override
    public void before(final Object target, final Object[] args) {
        if (this.isDebug) {
            this.logger.beforeInterceptor(target, args);
        }
        logger.warn("AliWareMQConsumerReceiveInterceptor before running");
        try {
            final Field outerField = target.getClass().getDeclaredField("this$0");
            outerField.setAccessible(true);
            final Object consumerTarget = outerField.get(target);
            final Properties consumerProperties = ((AliWareMQPropertiesGetter) consumerTarget)._$PINPOINT$_getProperties();
            final String onsAddr = consumerProperties.getProperty("NAMESRV_ADDR", "");
            final List<MessageExt> msgsRMQList = (List<MessageExt>) args[0];
            final MessageExt msgRMQ = msgsRMQList.get(0);
            final Map<String, String> properties = msgRMQ.getProperties();
            final Trace trace = this.createTrace(properties, msgRMQ, onsAddr);
            logger.warn("AliWareMQConsumerReceiveInterceptor before running, trace: {}, canSampled: {}, onsAddr: {}", trace, trace.canSampled(), onsAddr);
            if (trace == null) {
                return;
            }
            if (!trace.canSampled()) {
                return;
            }
            final SpanEventRecorder recorder = trace.traceBlockBegin();
            recorder.recordServiceType(AliWareMQConstants.ALIWARE_MQ_RECV);
            if (!StringUtils.isEmpty(onsAddr)) {
                recorder.recordEndPoint(onsAddr + "@" + msgRMQ.getTopic());
            } else {
                recorder.recordEndPoint(msgRMQ.getTopic());
            }
            final long delay = System.currentTimeMillis() - msgRMQ.getBornTimestamp();
            recorder.recordAttribute(AnnotationKey.ALIWARE_MQ_CONSUMER_DELAY, delay);
        } catch (Throwable th) {
            this.logger.warn("AliWareMQConsumerReceiveInterceptor BEFORE. Caused:{}", th.getMessage(), th);
        }
    }

    @Override
    public void after(final Object target, final Object[] args, final Object result, final Throwable throwable) {
        logger.warn("AliWareMQConsumerReceiveInterceptor after running");
        if (this.isDebug) {
            this.logger.afterInterceptor(target, args, result, throwable);
        }
        final Trace trace = this.traceContext.currentRawTraceObject();
        logger.warn("AliWareMQConsumerReceiveInterceptor after running, trace: {}", trace);
        if (trace == null) {
            return;
        }
        if (!trace.canSampled()) {
            this.traceContext.removeTraceObject();
            return;
        }
        try {
            final SpanEventRecorder recorder = trace.currentSpanEventRecorder();
            recorder.recordApi(this.methodDescriptor);
            if (throwable != null) {
                recorder.recordException(throwable);
            }
        } catch (Throwable th) {
            this.logger.warn("AliWareMQConsumerReceiveInterceptor after. Caused:{}", th.getMessage(), th);
        } finally {
            this.traceContext.removeTraceObject();
            trace.traceBlockEnd();
            trace.close();
        }
    }

    private Trace createTrace(final Map<String, String> properties, final MessageExt msgRMQ, final String onsAddr) throws Throwable {
        logger.warn("AliWareMQConsumerReceiveInterceptor createTrace running");
//        final RequestTraceProxy requestTrace = new RequestTraceProxy(new RequestTrace() {
//            public String getHeader(final String name) {
//                return properties.get(name);
//            }
//
//            public void setHeader(final String key, final String name) {
//            }
//
//            public Enumeration getHeaderNames() {
//                final Vector v = new Vector();
//                if (properties != null && properties.size() > 0) {
//                    for (final Map.Entry<String, String> entry : properties.entrySet()) {
//                        v.add(entry.getKey());
//                    }
//                }
//                return v.elements();
//            }
//        });
//        final Trace trace = this.requestTraceReader.read(requestTrace);
        //final Trace trace = this.traceContext.currentTraceObject();
        //final Trace trace = this.traceContext.currentRawTraceObject();
        Trace trace = this.traceContext.currentRawTraceObject();
        if (trace == null) {
            trace = this.traceContext.newTraceObject();
        }
        if (trace.canSampled()) {
            final SpanRecorder recorder = trace.getSpanRecorder();
            this.recordRootSpan(properties, recorder, msgRMQ, onsAddr);
        }
        return trace;
    }

    private void recordRootSpan(final Map<String, String> properties, final SpanRecorder recorder, final MessageExt messageExt, final String onsAddr) {
        logger.warn("AliWareMQConsumerReceiveInterceptor recordRootSpan running");
        recorder.recordServiceType(AliWareMQConstants.ALIWARE_MQ_RECV);
        recorder.recordApi(AliWareMQConsumerReceiveInterceptor.CONSUMER_ENTRY_METHOD_DESCRIPTOR);
        if (!StringUtils.isEmpty(onsAddr)) {
            recorder.recordEndPoint(onsAddr + "@" + messageExt.getTopic());
        } else {
            recorder.recordEndPoint(messageExt.getTopic());
        }
        recorder.recordRemoteAddress(messageExt.getBornHostString());
        recorder.recordRpcName("Recv Topic@" + messageExt.getTopic());
        recorder.recordAcceptorHost(messageExt.getBornHostString());
        final String parentApplicationName = AliWareMQHeader.getParentApplicationName(properties, null);
        logger.warn("parentApplicationame {}", parentApplicationName);
        if (parentApplicationName != null) {
            recorder.recordParentApplication(parentApplicationName, (short) 0);
        }
    }
}
