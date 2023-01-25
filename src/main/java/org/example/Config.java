package org.example;

import lombok.Getter;
import lombok.var;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    @Getter
    public static final String botToken;
    @Getter
    public static final String channelId;
    @Getter
    public static final String author;

    static {
        String path = new File("src/main/resources/config.properties").getAbsolutePath();
        Properties config = new Properties();
        try (var input = new FileInputStream(path)) {
            config.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        botToken = System.getenv("TOKEN") != null ? System.getenv("TOKEN") : config.getProperty("token");
        channelId = System.getenv("CHANNELID")  != null ? System.getenv("CHANNELID") : config.getProperty("channelId");
        author = System.getenv("AUTHORNAME")  != null ? System.getenv("AUTHORNAME") : config.getProperty("authorName");
    }
}
