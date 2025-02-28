package ru.test.demo.spl.demo_spl.configuration;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Name;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration()
@EnableConfigurationProperties(Properties.class)
@AllArgsConstructor
public class DatabaseConfiguration {
    private Properties properties;

    @Bean
    protected DSLContext createDSLContext() throws SQLException {
        Connection conn = DriverManager.getConnection(
            properties.getDatabase().getUrl(),
            properties.getDatabase().getUsername(),
            properties.getDatabase().getPassword()
        );

        Settings settings = new Settings();
        return DSL.using(conn, SQLDialect.POSTGRES, settings);
    }

    @Bean("eventTableName")
    protected Name getEventTableName() {
        return DSL.name(properties.getIntegration().getEventSchema(), properties.getIntegration().getEventTableName());
    }

    @Bean("columnsTableName")
    protected Name getColumnsTableName() {
        return DSL.name(properties.getIntegration().getInformationSchema(), properties.getIntegration().getColumnsTableName());
    }
}
