package com.navercorp.pinpoint.plugin.ons.interceptor;

import com.navercorp.pinpoint.bootstrap.interceptor.*;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.plugin.ons.AliWareMQConstants;
import com.navercorp.pinpoint.plugin.ons.AliWareMQHeader;
import com.navercorp.pinpoint.plugin.ons.descriptor.AliWareMQConsumerEntryMethodDescriptor;
import com.navercorp.pinpoint.plugin.ons.field.getter.AliWareMQPropertiesGetter;
import com.navercorp.pinpoint.bootstrap.plugin.request.*;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.message.*;
import com.navercorp.pinpoint.common.util.*;
import java.lang.reflect.*;
import java.util.*;
import com.navercorp.pinpoint.bootstrap.context.*;

public class AliWareMQConsumerReceiveInterceptor implements AroundInterceptor
{
    private static final AliWareMQConsumerEntryMethodDescriptor CONSUMER_ENTRY_METHOD_DESCRIPTOR;
    private final PLogger logger = PLoggerFactory.getLogger(getClass());
    private final boolean isDebug = logger.isDebugEnabled();

    private final TraceContext traceContext;
    private final MethodDescriptor methodDescriptor;
    private final MyRequestTraceReader requestTraceReader;
    
    public AliWareMQConsumerReceiveInterceptor(final TraceContext traceContext, final MethodDescriptor methodDescriptor) {

        this.traceContext = traceContext;
        this.logger.info("init traceContext",traceContext.getAgentId());
        this.methodDescriptor = methodDescriptor;
        traceContext.cacheApi(AliWareMQConsumerReceiveInterceptor.CONSUMER_ENTRY_METHOD_DESCRIPTOR);
        this.requestTraceReader = new MyRequestTraceReader(this.traceContext);
    }

    @Override
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
            final Map<String, String> properties = (msgRMQ.getProperties());

            // 获取线索。当前事务没有被配置时，它是空的
            final Trace trace = this.createTrace(properties, msgRMQ, onsAddr);
            if (trace == null) {
                return;
            }
            this.logger.warn("consumer cansampled:{}",trace.canSampled());
            if (!trace.canSampled()) {
                return;
            }

            // 开始跟踪
            final SpanEventRecorder recorder = trace.traceBlockBegin();
            recorder.recordServiceType(AliWareMQConstants.ALIWARE_MQ_RECV);

            this.logger.warn("consumer onsAddr:{}",onsAddr);
            if (!StringUtils.isEmpty(onsAddr)) {
                recorder.recordEndPoint(onsAddr + "@" + msgRMQ.getTopic());
            }
            else {
                recorder.recordEndPoint(msgRMQ.getTopic());
            }

            final long delay = System.currentTimeMillis() - msgRMQ.getBornTimestamp();
            // 跟踪不提供记录返回值的方法。你必须把它记录为一个属性
            recorder.recordAttribute(AliWareMQConstants.ALIWARE_MQ_CONSUMER_DELAY, delay);
        }
        catch (Throwable th) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("BEFORE. Caused:{}",th.getMessage(), th);
            }
        }
    }
    @Override
    public void after(final Object target, final Object[] args, final Object result, final Throwable throwable) {
        if (this.isDebug) {
           this.logger.afterInterceptor(target, args, result, throwable);
        }


        final Trace trace = this.traceContext.currentRawTraceObject();
        if (trace == null) {
            return;
        }
        this.logger.warn("consumer after. trace:{}", trace);
        this.logger.warn("consumer after. args:{}", args);
        this.logger.warn("consumer after. Object:{}", result);
        this.logger.warn("consumer after. canSampled:{}", trace.canSampled());
        if (!trace.canSampled()) {
            this.traceContext.removeTraceObject();
            return;
        }

        try {
            // 获取当前事件记录器
            final SpanEventRecorder recorder = trace.currentSpanEventRecorder();
            // 记录方法签名和参数
            recorder.recordApi(this.methodDescriptor);
            this.logger.warn("consumer after. methodDescriptor:{}", this.methodDescriptor);

            if (throwable != null) {
                //如果有异常，记录异常
                recorder.recordException(throwable);
            }

        }
        catch (Throwable th) {
            this.logger.warn("after. Caused:{}", th.getMessage(), th);
            if (this.logger.isInfoEnabled()) {
                this.logger.info("after. Caused:{}",th.getMessage(),th);
            }
        }
        finally {
            this.traceContext.removeTraceObject();
            // 结束跟踪
            trace.traceBlockEnd();
            trace.close();
        }
    }
    
    private Trace createTrace(final Map<String, String> properties, final MessageExt msgRMQ, final String onsAddr) throws Throwable {

//         // 生成traceid
//        final RequestTraceProxy requestTrace = new RequestTraceProxy(new RequestTrace() {
//            @Override
//            public String getHeader(final String name) {
//                return properties.get(name);
//            }
//
//            @Override
//            public void setHeader(final String key, final String name) {
//            }
//            @Override
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
        Trace trace = this.traceContext.currentRawTraceObject();
        if (trace == null) {
            trace = this.traceContext.newTraceObject();
        }

        //final Trace trace = this.requestTraceReader.read(requestTrace);
        if (trace.canSampled()) {
            final SpanRecorder recorder = trace.getSpanRecorder();
            this.recordRootSpan(properties, recorder, msgRMQ, onsAddr);
        }
        return trace;
    }




    private void recordRootSpan(final Map<String, String> properties, final SpanRecorder recorder, final MessageExt messageExt, final String onsAddr) {
        recorder.recordServiceType(AliWareMQConstants.ALIWARE_MQ_RECV);
        recorder.recordApi(AliWareMQConsumerReceiveInterceptor.CONSUMER_ENTRY_METHOD_DESCRIPTOR);
        if (!StringUtils.isEmpty(onsAddr)) {
            recorder.recordEndPoint(onsAddr + "@" + messageExt.getTopic());
        }
        else {
            recorder.recordEndPoint(messageExt.getTopic());
        }
        recorder.recordRemoteAddress(messageExt.getBornHostString());

        String rpcName = String.format("Recv Topic@%s TAG:%s Key:%s ",messageExt.getTopic(),messageExt.getTags(),messageExt.getKeys());
        recorder.recordRpcName(rpcName);


        recorder.recordAcceptorHost(messageExt.getBornHostString());
        final String parentApplicationName = AliWareMQHeader.getParentApplicationName(properties, null);
        if (parentApplicationName != null) {
            recorder.recordParentApplication(parentApplicationName,(short) 0);
        }
    }
    
    static {
        CONSUMER_ENTRY_METHOD_DESCRIPTOR = new AliWareMQConsumerEntryMethodDescriptor();
    }
}
