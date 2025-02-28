package ru.test.demo.spl.demo_spl.configuration;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@EqualsAndHashCode()
@ConfigurationProperties(prefix = "application")
public class Properties {
    @Data
    public static class SyslogProperties {
        private int port;
        private String outputChannelName;
    }

    @Data
    public static class RabbitMQProperties {
        private String host;
        private int port;
        private String username;
        private String password;
        private String virtualHost;
        private String queue;
        private String exchange;
        private String routingKey;
        private String clientId;
        private String clientSecret;
        private Integer initialInterval;
        private Double multiplier;
        private Integer maxInterval;
        private Boolean enable;

    }

    @Data
    public static class IntegrationProperties {
        private String eventsQueue;
        private Integer concurrentConsumersNumber;
        private String eventSchema;
        private String eventTableName;
        private String informationSchema;
        private String columnsTableName;
    }

    @Data
    public static class DatabaseProperties {
        private String url;
        private String username;
        private String password;
    }

    private SyslogProperties syslog;
    private RabbitMQProperties rabbitmq;
    private IntegrationProperties integration;
    private DatabaseProperties database;
}
