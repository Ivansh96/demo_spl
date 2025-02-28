package ru.test.demo.spl.demo_spl.configuration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.integration.amqp.outbound.AmqpOutboundEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageChannel;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.List;

@Configuration()
@ConditionalOnProperty(
    prefix = "application",
    name = "rabbitmq.enable",
    havingValue = "true"
)
@EnableConfigurationProperties(Properties.class)
@AllArgsConstructor
@Slf4j
public class RabbitMQConfiguration {
    private final Properties properties;

    @Bean
    public ConnectionFactory getRabbitMQConnectionFactory() {
        log.warn("RabbitMQ configuration enabled, creating connection factory");
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(properties.getRabbitmq().getHost());
        connectionFactory.setPort(properties.getRabbitmq().getPort());
        connectionFactory.setUsername(properties.getRabbitmq().getUsername());
        connectionFactory.setPassword(properties.getRabbitmq().getPassword());
        connectionFactory.setVirtualHost(properties.getRabbitmq().getVirtualHost());

        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(getRabbitMQConnectionFactory());
        RetryTemplate retryTemplate = new RetryTemplate();

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(properties.getRabbitmq().getInitialInterval());
        backOffPolicy.setMultiplier(properties.getRabbitmq().getMultiplier());
        backOffPolicy.setMaxInterval(properties.getRabbitmq().getMaxInterval());
        retryTemplate.setBackOffPolicy(backOffPolicy);
        template.setRetryTemplate(retryTemplate);

        return template;
    }

    @Bean
    public SimpleMessageListenerContainer listenerContainer() {
        SimpleMessageListenerContainer container =
            new SimpleMessageListenerContainer(getRabbitMQConnectionFactory());
        container.setQueueNames(properties.getIntegration().getEventsQueue());
        container.setConcurrentConsumers(properties.getIntegration().getConcurrentConsumersNumber());
        return container;
    }

    @Bean
    @ServiceActivator(inputChannel = "outboundEventChannel")
    protected AmqpOutboundEndpoint handleOutboundEventChannel(AmqpTemplate amqpTemplate) {
        AmqpOutboundEndpoint outbound = new AmqpOutboundEndpoint(amqpTemplate);
        outbound.setRoutingKey(properties.getIntegration().getEventsQueue());
        return outbound;
    }

    @Bean("inboundChannelAdapter")
    protected AmqpInboundChannelAdapter createInboundChannelAdapter(@Qualifier("inboundEventChannel") MessageChannel channel) {
        AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(listenerContainer());
        adapter.setOutputChannel(channel);

        // TODO Удалить после добавления Protobuf
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("ru.gpn.platform.silverplate.event_collector.*", "java.util.*"));
        adapter.setMessageConverter(converter);

        return adapter;
    }
}
