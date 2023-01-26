package com.jesys.notify.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ConfigurationProperties(prefix = "com.jesys")
public class ConnectionProperties {

    /**
     * Service Type
     */
    private List<Service> service = new ArrayList<>();

    /**
     * Boosty
     */
    private Boosty boosty = new Boosty();

    /**
     * Discord
     */
    private Discord discord = new Discord();

    /**
     * Rulate
     */
    private Rulate rulate = new Rulate();


    public enum Service {
        NONE, BOOSTY, RULATE, DISCORD
    }

    @Getter
    @Setter
    public static class Boosty {
        private String authorName;
    }

    @Getter
    @Setter
    public static class Rulate {
        private List<String> book;
    }

    @Getter
    @Setter
    public static class Discord {
        private String token;
        private String channelId;
    }


}
