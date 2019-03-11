package com.navercorp.pinpoint.plugin.alimq;

import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentClass;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentException;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentMethod;
import com.navercorp.pinpoint.bootstrap.instrument.Instrumentor;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformCallback;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplate;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplateAware;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPlugin;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPluginSetupContext;

import java.security.ProtectionDomain;

public class AliWareMQPlugin implements ProfilerPlugin, TransformTemplateAware
{
    private final PLogger logger;
    private TransformTemplate transformTemplate;
    
    public AliWareMQPlugin() {
        this.logger = PLoggerFactory.getLogger((Class)this.getClass());
    }
    
    public void setup(final ProfilerPluginSetupContext context) {
        final ProfilerConfig profilerConfig = context.getConfig();
        if (!profilerConfig.isProfileEnable()) {
            this.logger.info("profiler.enable:false, AliWareMQPlugin disabled");
            return;
        }
        final AliWareMQPluginConfig config = new AliWareMQPluginConfig(context.getConfig());
        if (!config.isAliWareMQEnable()) {
            this.logger.info("profiler.aliWareMQ.enable: false, AliWareMQPlugin disabled");
            return;
        }
        this.addProducerEditor();
        this.addConsumerEditor();
    }
    
    private void addProducerEditor() {
        this.transformTemplate.transform("com.aliyun.openservices.ons.api.impl.rocketmq.ProducerImpl", (TransformCallback)new TransformCallback() {
            public byte[] doInTransform(final Instrumentor instrumentor, final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws InstrumentException {
                final InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);
                target.addGetter("AliWareMQPropertiesGetter", "properties");
                final InstrumentMethod sendHandle = target.getDeclaredMethod("send", new String[] { "com.aliyun.openservices.ons.api.Message" });
                if (sendHandle != null) {
                    sendHandle.addInterceptor("AliWareMQProducerSendInterceptor");
                }
                final InstrumentMethod sendOnewayHandle = target.getDeclaredMethod("sendOneway", new String[] { "com.aliyun.openservices.ons.api.Message" });
                if (sendOnewayHandle != null) {
                    sendOnewayHandle.addInterceptor("AliWareMQProducerSendInterceptor");
                }
                final InstrumentMethod sendAsyncHandle = target.getDeclaredMethod("sendAsync", new String[] { "com.aliyun.openservices.ons.api.Message", "com.aliyun.openservices.ons.api.SendCallback" });
                if (sendAsyncHandle != null) {
                    sendAsyncHandle.addInterceptor("AliWareMQProducerSendInterceptor");
                }
                return target.toBytecode();
            }
        });
    }
    
    private void addConsumerEditor() {
        this.transformTemplate.transform("com.aliyun.openservices.ons.api.impl.rocketmq.ConsumerImpl", (TransformCallback)new TransformCallback() {
            public byte[] doInTransform(final Instrumentor instrumentor, final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws InstrumentException {
                final InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);
                target.addGetter("AliWareMQPropertiesGetter", "properties");
                return target.toBytecode();
            }
        });
        this.transformTemplate.transform("com.aliyun.openservices.ons.api.impl.rocketmq.ConsumerImpl$MessageListenerImpl", (TransformCallback)new TransformCallback() {
            public byte[] doInTransform(final Instrumentor instrumentor, final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws InstrumentException {
                final InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);
                final InstrumentMethod onsCientReceiveHandle = target.getDeclaredMethod("consumeMessage", new String[] { "java.util.List", "com.aliyun.openservices.shade.com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext" });
                if (onsCientReceiveHandle != null) {
                    onsCientReceiveHandle.addInterceptor("AliWareMQConsumerReceiveInterceptor");
                    return target.toBytecode();
                }
                final InstrumentMethod pandoraReceiveHandle = target.getDeclaredMethod("consumeMessage", new String[] { "java.util.List", "com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext" });
                if (pandoraReceiveHandle != null) {
                    pandoraReceiveHandle.addInterceptor("PandoraMQConsumerReceiveInterceptor");
                    return target.toBytecode();
                }
                return target.toBytecode();
            }
        });
    }
    
    public void setTransformTemplate(final TransformTemplate transformTemplate) {
        this.transformTemplate = transformTemplate;
    }
}
