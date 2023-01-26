package com.jesys.notify.daemons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class Daemon {
    abstract void checkNewPosts() throws IOException;

    protected static String readJsonFromUrl(String url) throws IOException {
        log.info("Fetch - " + url);
        try (Scanner scanner = new Scanner(new URL(url).openStream(), "UTF-8")) {
            scanner.useDelimiter("\\A");
            return scanner.next();
        }
    }

    protected static Matcher createMatcher(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(text);
    }
}
