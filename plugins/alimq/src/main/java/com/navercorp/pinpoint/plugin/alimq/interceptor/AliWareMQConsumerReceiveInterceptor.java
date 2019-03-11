package com.navercorp.pinpoint.plugin.alimq.interceptor;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.message.MessageExt;
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
import com.navercorp.pinpoint.plugin.alimq.TraceContext;
import com.navercorp.pinpoint.plugin.alimq.MethodDescriptor;
import com.navercorp.pinpoint.plugin.alimq.Trace;
import com.navercorp.pinpoint.plugin.alimq.SpanEventRecorder;
import com.navercorp.pinpoint.plugin.alimq.SpanRecorder;

import java.lang.reflect.Field;
import java.util.*;

public class AliWareMQConsumerReceiveInterceptor implements AroundInterceptor
{
    private static final AliWareMQConsumerEntryMethodDescriptor CONSUMER_ENTRY_METHOD_DESCRIPTOR;
    private final PLogger logger;
    private final boolean isDebug;
    private final TraceContext traceContext;
    private final MethodDescriptor methodDescriptor;
    private final RequestTraceReader requestTraceReader;

    public AliWareMQConsumerReceiveInterceptor(final TraceContext traceContext, final MethodDescriptor methodDescriptor) {
        this.logger = PLoggerFactory.getLogger((Class)this.getClass());
        this.isDebug = this.logger.isDebugEnabled();
        this.traceContext = traceContext;
        this.methodDescriptor = methodDescriptor;
        traceContext.cacheApi((MethodDescriptor)AliWareMQConsumerReceiveInterceptor.CONSUMER_ENTRY_METHOD_DESCRIPTOR);
        this.requestTraceReader = new RequestTraceReader(this.traceContext);
    }
    
    public void before(final Object target, final Object[] args) {
        if (this.isDebug) {
            this.logger.beforeInterceptor(target, args);
        }
        try {
            final Field outerField = target.getClass().getDeclaredField("this$0");
            outerField.setAccessible(true);
            final Object consumerTarget = outerField.get(target);
            final Properties consumerProperties = ((AliWareMQPropertiesGetter)consumerTarget)._$PINPOINT$_getProperties();
            final String onsAddr = consumerProperties.getProperty("ONSAddr", "");
            final List<MessageExt> msgsRMQList = (List<MessageExt>)args[0];
            final MessageExt msgRMQ = msgsRMQList.get(0);
            final Map<String, String> properties = (Map<String, String>)msgRMQ.getProperties();
            final Trace trace = this.createTrace(properties, msgRMQ, onsAddr);
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
            }
            else {
                recorder.recordEndPoint(msgRMQ.getTopic());
            }
            final long delay = System.currentTimeMillis() - msgRMQ.getBornTimestamp();
            recorder.recordAttribute(AnnotationKey.ALIWARE_MQ_CONSUMER_DELAY, (Object)delay);
        }
        catch (Throwable th) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("BEFORE. Caused:{}", (Object)th.getMessage(), (Object)th);
            }
        }
    }
    
    public void after(final Object target, final Object[] args, final Object result, final Throwable throwable) {
        if (this.isDebug) {
            this.logger.afterInterceptor(target, args, result, throwable);
        }
        final Trace trace = this.traceContext.currentRawTraceObject();
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
        }
        catch (Throwable th) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("after. Caused:{}", (Object)th.getMessage(), (Object)th);
            }
        }
        finally {
            this.traceContext.removeTraceObject();
            trace.traceBlockEnd();
            trace.close();
        }
    }
    
    private Trace createTrace(final Map<String, String> properties, final MessageExt msgRMQ, final String onsAddr) throws Throwable {
        final RequestTraceProxy requestTrace = new RequestTraceProxy((RequestTrace)new RequestTrace() {
            public String getHeader(final String name) {
                return properties.get(name);
            }
            
            public void setHeader(final String key, final String name) {
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
        if (trace.canSampled()) {
            final SpanRecorder recorder = trace.getSpanRecorder();
            this.recordRootSpan(properties, recorder, msgRMQ, onsAddr);
        }
        return trace;
    }
    
    private void recordRootSpan(final Map<String, String> properties, final SpanRecorder recorder, final MessageExt messageExt, final String onsAddr) {
        recorder.recordServiceType(AliWareMQConstants.ALIWARE_MQ_RECV);
        recorder.recordApi((MethodDescriptor)AliWareMQConsumerReceiveInterceptor.CONSUMER_ENTRY_METHOD_DESCRIPTOR);
        if (!StringUtils.isEmpty(onsAddr)) {
            recorder.recordEndPoint(onsAddr + "@" + messageExt.getTopic());
        }
        else {
            recorder.recordEndPoint(messageExt.getTopic());
        }
        recorder.recordRemoteAddress(messageExt.getBornHostString());
        recorder.recordRpcName("Recv Topic@" + messageExt.getTopic());
        recorder.recordAcceptorHost(messageExt.getBornHostString());
        final String parentApplicationName = AliWareMQHeader.getParentApplicationName(properties, null);
        if (parentApplicationName != null) {
            recorder.recordParentApplication(parentApplicationName);
        }
    }
    
    static {
        CONSUMER_ENTRY_METHOD_DESCRIPTOR = new AliWareMQConsumerEntryMethodDescriptor();
    }
}
