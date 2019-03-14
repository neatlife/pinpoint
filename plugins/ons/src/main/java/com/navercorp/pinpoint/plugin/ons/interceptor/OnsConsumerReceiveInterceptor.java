package com.navercorp.pinpoint.plugin.ons.interceptor;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.message.MessageExt;
import com.navercorp.pinpoint.bootstrap.context.*;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.util.NumberUtils;
import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.plugin.ons.constant.AnnotationKeyConstant;
import com.navercorp.pinpoint.plugin.ons.constant.ServiceTypeConstants;
import com.navercorp.pinpoint.plugin.ons.method.OnsConsumerMethodDescriptor;
import com.navercorp.pinpoint.plugin.ons.field.OnsPropertiesGetter;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class OnsConsumerReceiveInterceptor implements AroundInterceptor {
    private static final OnsConsumerMethodDescriptor CONSUMER_ENTRY_METHOD_DESCRIPTOR;

    static {
        CONSUMER_ENTRY_METHOD_DESCRIPTOR = new OnsConsumerMethodDescriptor();
    }

    private final PLogger logger;
    private final TraceContext traceContext;
    private final MethodDescriptor methodDescriptor;

    public OnsConsumerReceiveInterceptor(final TraceContext traceContext, final MethodDescriptor methodDescriptor) {
        this.logger = PLoggerFactory.getLogger(this.getClass());
        this.traceContext = traceContext;
        this.methodDescriptor = methodDescriptor;
        traceContext.cacheApi(OnsConsumerReceiveInterceptor.CONSUMER_ENTRY_METHOD_DESCRIPTOR);
        logger.warn("OnsConsumerReceiveInterceptor constructor running");
    }

    @Override
    public void before(final Object target, final Object[] args) {
        logger.warn("OnsConsumerReceiveInterceptor before running");
        try {
            final Field outerField = target.getClass().getDeclaredField("this$0");
            outerField.setAccessible(true);
            final Object consumerTarget = outerField.get(target);
            final Properties consumerProperties = ((OnsPropertiesGetter) consumerTarget)._$PINPOINT$_getProperties();
            final String onsAddr = consumerProperties.getProperty("NAMESRV_ADDR", "");
            final List<MessageExt> msgsRMQList = (List<MessageExt>) args[0];
            final MessageExt msgRMQ = msgsRMQList.get(0);
            final Map<String, String> properties = msgRMQ.getProperties();
            final Trace trace = this.createTrace(properties, msgRMQ, onsAddr);
            logger.warn("OnsConsumerReceiveInterceptor before running, trace: {}, canSampled: {}, onsAddr: {}, properties: {}", trace, trace.canSampled(), onsAddr, properties);
            if (trace == null) {
                return;
            }
            if (!trace.canSampled()) {
                return;
            }
            final SpanEventRecorder recorder = trace.traceBlockBegin();
            recorder.recordServiceType(ServiceTypeConstants.ONS_RECV);
            if (!StringUtils.isEmpty(onsAddr)) {
                recorder.recordEndPoint(onsAddr + "@" + msgRMQ.getTopic());
            } else {
                recorder.recordEndPoint(msgRMQ.getTopic());
            }
            final long delay = System.currentTimeMillis() - msgRMQ.getBornTimestamp();
            recorder.recordAttribute(AnnotationKeyConstant.ONS_CONSUMER_DELAY, delay);
            logger.warn("OnsConsumerReceiveInterceptor before running, ending");
        } catch (Throwable th) {
            this.logger.warn("OnsConsumerReceiveInterceptor BEFORE. Caused:{}", th.getMessage(), th);
        }
    }

    @Override
    public void after(final Object target, final Object[] args, final Object result, final Throwable throwable) {
        logger.warn("OnsConsumerReceiveInterceptor after running");
        final Trace trace = this.traceContext.currentRawTraceObject();
        logger.warn("OnsConsumerReceiveInterceptor after running, trace: {}", trace);
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
            this.logger.warn("OnsConsumerReceiveInterceptor after. Caused:{}", th.getMessage(), th);
        } finally {
            this.traceContext.removeTraceObject();
            trace.traceBlockEnd();
            trace.close();
        }
    }

    private Trace createTrace(final Map<String, String> properties, final MessageExt msgRMQ, final String onsAddr) throws Throwable {
        logger.warn("OnsConsumerReceiveInterceptor createTrace running");
        this.logger.warn("TraceContext {}", traceContext.getAgentId());
        this.logger.warn("META_TRACE_ID {}", properties.get(Header.HTTP_TRACE_ID.toString()));
        this.logger.warn("META_PARENT_SPAN_ID {}", properties.get(Header.HTTP_PARENT_SPAN_ID.toString()));
        this.logger.warn("META_SPAN_ID {}", properties.get(Header.HTTP_SPAN_ID.toString()));

        final String traceId = properties.get(Header.HTTP_TRACE_ID.toString());
        final Trace trace;
        if (traceId == null) {
            trace = traceContext.newTraceObject();
        } else {
            final TraceId id = traceContext.createTraceId(
                    traceId,
                    NumberUtils.parseLong(properties.get(Header.HTTP_PARENT_SPAN_ID.toString()), -1L),
                    NumberUtils.parseLong(properties.get(Header.HTTP_SPAN_ID.toString()), -1L),
                    NumberUtils.parseShort(properties.get(Header.HTTP_FLAGS.toString()), (short) -1)
            );
            this.logger.warn("TraceID exist. continue trace. {}", id);
            trace = this.traceContext.continueTraceObject(id);
        }
        logger.warn("OnsConsumerReceiveInterceptor createTrace, trace: {}, canSampled {}", trace, trace.canSampled());
        if (trace.canSampled()) {
            final SpanRecorder recorder = trace.getSpanRecorder();
            this.recordRootSpan(properties, recorder, msgRMQ, onsAddr);
        }
        return trace;
    }

    private void recordRootSpan(final Map<String, String> properties, final SpanRecorder recorder, final MessageExt messageExt, final String onsAddr) {
        logger.warn("OnsConsumerReceiveInterceptor recordRootSpan running");
        recorder.recordApi(OnsConsumerReceiveInterceptor.CONSUMER_ENTRY_METHOD_DESCRIPTOR);
        if (!StringUtils.isEmpty(onsAddr)) {
            recorder.recordEndPoint(onsAddr + "@" + messageExt.getTopic());
        } else {
            recorder.recordEndPoint(messageExt.getTopic());
        }
        recorder.recordRemoteAddress(messageExt.getBornHostString());
        recorder.recordRpcName("Recv Topic@" + messageExt.getTopic());
        recorder.recordAcceptorHost(messageExt.getBornHostString());
        final String parentApplicationName = properties.get(Header.HTTP_PARENT_APPLICATION_NAME.toString());
        logger.warn("parentApplicationame {}", parentApplicationName);
        if (parentApplicationName != null) {
            logger.warn("OnsConsumerReceiveInterceptor applicationType {}", Short.valueOf(properties.get(Header.HTTP_PARENT_APPLICATION_TYPE.toString())));
            recorder.recordParentApplication(parentApplicationName, Short.valueOf(properties.get(Header.HTTP_PARENT_APPLICATION_TYPE.toString())));
        }
    }
}
