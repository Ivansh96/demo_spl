package ru.test.demo.spl.demo_spl.configuration;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.gpn.platform.silverplate.event_collector.cef.lib.model.EventFactory;
import ru.gpn.platform.silverplate.event_collector.cef.lib.model.EventFactoryImpl;

@Configuration()
@EnableConfigurationProperties(Properties.class)
@AllArgsConstructor
public class ApplicationConfiguration {
    @Bean
    protected EventFactory eventParser() {
        return new EventFactoryImpl();
    }
}
