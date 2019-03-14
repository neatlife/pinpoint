package com.navercorp.pinpoint.plugin.ons;

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

public class OnsPlugin implements ProfilerPlugin, TransformTemplateAware {
    private final PLogger logger;
    private TransformTemplate transformTemplate;

    public OnsPlugin() {
        this.logger = PLoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void setup(final ProfilerPluginSetupContext context) {
        final ProfilerConfig profilerConfig = context.getConfig();
        if (!profilerConfig.isProfileEnable()) {
            this.logger.warn("profiler.enable:false, OnsPlugin disabled");
            return;
        }
        final OnsPluginConfig config = new OnsPluginConfig(context.getConfig());
        if (!config.isOnsEnable()) {
            this.logger.warn("profiler.ons.enable: false, OnsPlugin disabled");
            return;
        }
        this.addProducerEditor();
        this.addConsumerEditor();
    }

    private void addProducerEditor() {
        this.transformTemplate.transform("com.aliyun.openservices.ons.api.impl.rocketmq.ProducerImpl", new TransformCallback() {
            public byte[] doInTransform(final Instrumentor instrumentor, final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws InstrumentException {
                final InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);
                target.addGetter("com.navercorp.pinpoint.plugin.ons.field.OnsPropertiesGetter", "properties");
                final InstrumentMethod sendHandle = target.getDeclaredMethod("send", "com.aliyun.openservices.ons.api.Message");
                if (sendHandle != null) {
                    sendHandle.addInterceptor("com.navercorp.pinpoint.plugin.ons.interceptor.OnsProducerSendInterceptor");
                }
                final InstrumentMethod sendOnewayHandle = target.getDeclaredMethod("sendOneway", "com.aliyun.openservices.ons.api.Message");
                if (sendOnewayHandle != null) {
                    sendOnewayHandle.addInterceptor("com.navercorp.pinpoint.plugin.ons.interceptor.OnsProducerSendInterceptor");
                }
                final InstrumentMethod sendAsyncHandle = target.getDeclaredMethod("sendAsync", "com.aliyun.openservices.ons.api.Message", "com.aliyun.openservices.ons.api.SendCallback");
                if (sendAsyncHandle != null) {
                    sendAsyncHandle.addInterceptor("com.navercorp.pinpoint.plugin.ons.interceptor.OnsProducerSendInterceptor");
                }
                return target.toBytecode();
            }
        });
    }

    private void addConsumerEditor() {
        this.transformTemplate.transform("com.aliyun.openservices.ons.api.impl.rocketmq.ConsumerImpl", new TransformCallback() {
            @Override
            public byte[] doInTransform(Instrumentor instrumentor, ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
                final InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);
                target.addGetter("com.navercorp.pinpoint.plugin.ons.field.OnsPropertiesGetter", "properties");
                return target.toBytecode();
            }
        });

        this.transformTemplate.transform("com.aliyun.openservices.ons.api.impl.rocketmq.ConsumerImpl$MessageListenerImpl", new TransformCallback() {
            public byte[] doInTransform(final Instrumentor instrumentor, final ClassLoader loader, final String className, final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws InstrumentException {
                byte[] result;
                final InstrumentClass target = instrumentor.getInstrumentClass(loader, className, classfileBuffer);
                final InstrumentMethod onsCientReceiveHandle = target.getDeclaredMethod("consumeMessage", "java.util.List", "com.aliyun.openservices.shade.com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext");
                logger.warn("OnsPlugin, onsCientReceiveHandle: {}", onsCientReceiveHandle);
                if (onsCientReceiveHandle != null) {
                    onsCientReceiveHandle.addInterceptor("com.navercorp.pinpoint.plugin.ons.interceptor.OnsConsumerReceiveInterceptor");
                    return target.toBytecode();
                }
                return target.toBytecode();
            }
        });
    }

    @Override
    public void setTransformTemplate(final TransformTemplate transformTemplate) {
        this.transformTemplate = transformTemplate;
    }
}
