package com.navercorp.pinpoint.plugin.ons.interceptor;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.message.MessageExt;
import com.navercorp.pinpoint.bootstrap.context.*;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.plugin.ons.OnsMQConstants;
import com.navercorp.pinpoint.plugin.ons.OnsMQHeader;
import com.navercorp.pinpoint.plugin.ons.RequestTrace;
import com.navercorp.pinpoint.plugin.ons.RequestTraceProxy;
import com.navercorp.pinpoint.plugin.ons.annotation.AnnotationKey;
import com.navercorp.pinpoint.plugin.ons.descriptor.OnsMQConsumerEntryMethodDescriptor;
import com.navercorp.pinpoint.plugin.ons.field.getter.OnsMQPropertiesGetter;
import com.navercorp.pinpoint.plugin.ons.request.RequestTraceReader;

import java.lang.reflect.Field;
import java.util.*;

public class OnsMQConsumerReceiveInterceptor implements AroundInterceptor {
    private static final OnsMQConsumerEntryMethodDescriptor CONSUMER_ENTRY_METHOD_DESCRIPTOR;

    static {
        CONSUMER_ENTRY_METHOD_DESCRIPTOR = new OnsMQConsumerEntryMethodDescriptor();
    }

    private final PLogger logger;
    private final boolean isDebug;
    private final TraceContext traceContext;
    private final MethodDescriptor methodDescriptor;
    private final RequestTraceReader requestTraceReader;

    public OnsMQConsumerReceiveInterceptor(final TraceContext traceContext, final MethodDescriptor methodDescriptor) {
        this.logger = PLoggerFactory.getLogger(this.getClass());
        this.isDebug = this.logger.isDebugEnabled();
        this.traceContext = traceContext;
        this.methodDescriptor = methodDescriptor;
        traceContext.cacheApi(OnsMQConsumerReceiveInterceptor.CONSUMER_ENTRY_METHOD_DESCRIPTOR);
        this.requestTraceReader = new RequestTraceReader(this.traceContext);
        logger.warn("OnsMQConsumerReceiveInterceptor constructor running");
    }

    @Override
    public void before(final Object target, final Object[] args) {
        if (this.isDebug) {
            this.logger.beforeInterceptor(target, args);
        }
        logger.warn("OnsMQConsumerReceiveInterceptor before running");
        try {
            final Field outerField = target.getClass().getDeclaredField("this$0");
            outerField.setAccessible(true);
            final Object consumerTarget = outerField.get(target);
            final Properties consumerProperties = ((OnsMQPropertiesGetter) consumerTarget)._$PINPOINT$_getProperties();
            final String onsAddr = consumerProperties.getProperty("NAMESRV_ADDR", "");
            final List<MessageExt> msgsRMQList = (List<MessageExt>) args[0];
            final MessageExt msgRMQ = msgsRMQList.get(0);
            final Map<String, String> properties = msgRMQ.getProperties();
            final Trace trace = this.createTrace(properties, msgRMQ, onsAddr);
            logger.warn("OnsMQConsumerReceiveInterceptor before running, trace: {}, canSampled: {}, onsAddr: {}, properties: {}", trace, trace.canSampled(), onsAddr, properties);
            if (trace == null) {
                return;
            }
            if (!trace.canSampled()) {
                return;
            }
            final SpanEventRecorder recorder = trace.traceBlockBegin();
            recorder.recordServiceType(OnsMQConstants.ALIWARE_MQ_RECV);
            if (!StringUtils.isEmpty(onsAddr)) {
                recorder.recordEndPoint(onsAddr + "@" + msgRMQ.getTopic());
            } else {
                recorder.recordEndPoint(msgRMQ.getTopic());
            }
            final long delay = System.currentTimeMillis() - msgRMQ.getBornTimestamp();
            recorder.recordAttribute(AnnotationKey.ALIWARE_MQ_CONSUMER_DELAY, delay);
            logger.warn("OnsMQConsumerReceiveInterceptor before running, ending");
        } catch (Throwable th) {
            this.logger.warn("OnsMQConsumerReceiveInterceptor BEFORE. Caused:{}", th.getMessage(), th);
        }
    }

    @Override
    public void after(final Object target, final Object[] args, final Object result, final Throwable throwable) {
        logger.warn("OnsMQConsumerReceiveInterceptor after running");
        if (this.isDebug) {
            this.logger.afterInterceptor(target, args, result, throwable);
        }
        final Trace trace = this.traceContext.currentRawTraceObject();
        logger.warn("OnsMQConsumerReceiveInterceptor after running, trace: {}", trace);
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
            this.logger.warn("OnsMQConsumerReceiveInterceptor after. Caused:{}", th.getMessage(), th);
        } finally {
            this.traceContext.removeTraceObject();
            trace.traceBlockEnd();
            trace.close();
        }
    }

    private Trace createTrace(final Map<String, String> properties, final MessageExt msgRMQ, final String onsAddr) throws Throwable {
        logger.warn("OnsMQConsumerReceiveInterceptor createTrace running");
        final RequestTraceProxy requestTrace = new RequestTraceProxy(new RequestTrace() {
            public String getHeader(final String name) {
                return properties.get(name);
            }

            public void setHeader(final String key, final String name) {
                logger.warn("OnsMQConsumerReceiveInterceptor setheader key {} name {}", key, name);
                properties.put(key, name);
            }

            public Enumeration getHeaderNames() {
                final Vector v = new Vector();
                if (properties != null && properties.size() > 0) {
                    for (final Map.Entry<String, String> entry : properties.entrySet()) {
                        v.add(entry.getKey());
                    }
                }
                return v.elements();
            }
        });
        final Trace trace = this.requestTraceReader.read(requestTrace);
//        final Trace trace = traceContext.newTraceObject();
        logger.warn("OnsMQConsumerReceiveInterceptor createTrace, trace: {}, canSampled {}", trace, trace.canSampled());
        if (trace.canSampled()) {
            final SpanRecorder recorder = trace.getSpanRecorder();
            this.recordRootSpan(properties, recorder, msgRMQ, onsAddr);
        }
        return trace;
    }

    private void recordRootSpan(final Map<String, String> properties, final SpanRecorder recorder, final MessageExt messageExt, final String onsAddr) {
        logger.warn("OnsMQConsumerReceiveInterceptor recordRootSpan running");
        recorder.recordApi(OnsMQConsumerReceiveInterceptor.CONSUMER_ENTRY_METHOD_DESCRIPTOR);
        if (!StringUtils.isEmpty(onsAddr)) {
            recorder.recordEndPoint(onsAddr + "@" + messageExt.getTopic());
        } else {
            recorder.recordEndPoint(messageExt.getTopic());
        }
        recorder.recordRemoteAddress(messageExt.getBornHostString());
        recorder.recordRpcName("Recv Topic@" + messageExt.getTopic());
        recorder.recordAcceptorHost(messageExt.getBornHostString());
        final String parentApplicationName = OnsMQHeader.getParentApplicationName(properties, null);
        logger.warn("parentApplicationame {}", parentApplicationName);
        if (parentApplicationName != null) {
            logger.warn("OnsMQConsumerReceiveInterceptor applicationType {}", OnsMQHeader.getParentApplicationType(properties));
            recorder.recordParentApplication(parentApplicationName, OnsMQHeader.getParentApplicationType(properties));
        }
    }
}
