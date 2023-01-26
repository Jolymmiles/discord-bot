package com.jesys.notify.config.connections;

import com.jesys.notify.config.properties.ConnectionProperties;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnExpression("#{T(java.util.Arrays).asList(environment['com.jesys.service']).contains('discord')}")
public class DiscordConnectionConfig {
    private final ConnectionProperties.Discord discordConnectionProperties;

    @Autowired
    public DiscordConnectionConfig(ConnectionProperties connectionProperties) {
        log.info("Configuring Reports via Discord");
        this.discordConnectionProperties = connectionProperties.getDiscord();
    }

    @Bean
    public DiscordApi discordApiClient() {
        try {
            return new DiscordApiBuilder().setToken(discordConnectionProperties.getToken()).login().join();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
