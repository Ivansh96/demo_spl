package ru.test.demo.spl.demo_spl.configuration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.syslog.RFC5424MessageConverter;
import org.springframework.integration.syslog.inbound.SyslogReceivingChannelAdapterSupport;
import org.springframework.integration.syslog.inbound.UdpSyslogReceivingChannelAdapter;
import org.springframework.messaging.MessageChannel;
import ru.gpn.platform.silverplate.event_collector.cef.lib.model.Event;
import ru.test.demo.spl.demo_spl.service.StorageService;


@Configuration()
@EnableConfigurationProperties(Properties.class)
@AllArgsConstructor
@Slf4j
public class IntegrationConfiguration {
    private final Properties properties;
    private final StorageService storageService;

    @Bean
    protected SyslogReceivingChannelAdapterSupport createSyslogReceivingChannelAdapter() {
        UdpSyslogReceivingChannelAdapter channelAdapter = new UdpSyslogReceivingChannelAdapter();
        channelAdapter.setPort(properties.getSyslog().getPort());
        channelAdapter.setOutputChannelName(properties.getSyslog().getOutputChannelName());
        channelAdapter.setConverter(createRFC5424MessageConverter());

        return channelAdapter;
    }

    @Bean
    protected RFC5424MessageConverter createRFC5424MessageConverter() {
        return new RFC5424MessageConverter();
    }

    @Bean("syslogChannel")
    protected MessageChannel createSyslogChannel() {
        return new DirectChannel();
    }

    @Bean("inboundEventChannel")
    protected MessageChannel createInboundEventChannel() {
        return new DirectChannel();
    }

    @Bean("outboundEventChannel")
    protected MessageChannel createOutboundEventChannel() {
        return new DirectChannel();
    }

    @Bean
    @ConditionalOnProperty(
        prefix = "application",
        name = "rabbitmq.enable",
        havingValue = "false"
    )
    public IntegrationFlow syslogFlow() {
        log.warn("RabbitMQ configuration disabled");
        return IntegrationFlow
            .from("outboundEventChannel")
            .channel("inboundEventChannel")
            .get();
    }

    @ServiceActivator(inputChannel = "inboundEventChannel")
    private void handleInboundEventChannel(Event event) throws Exception {
        storageService.storeEvent(event.setProcessedAt((new java.util.Date()).getTime()));
    }
}
