package com.jesys.notify.config;

import com.jesys.notify.config.connections.DiscordConnectionConfig;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConfigurationPropertiesScan("com.jesys.notify.config.properties")
@Import({DiscordConnectionConfig.class})
public class NotifyConfig {
}
