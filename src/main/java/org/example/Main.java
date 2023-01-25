package org.example;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class Main {
    private static final String url = "https://boosty.to/" + Config.author;
    private static LocalDateTime lastPostTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
    private static final Map<String, String> months = new HashMap<>();

    static {

        months.put("янв.", "Jan");
        months.put("фев.", "Feb");
        months.put("мар.", "Mar");
        months.put("апр.", "Apr");
        months.put("май.", "May");
        months.put("июн.", "Jun");
        months.put("июл.", "Jul");
        months.put("авг.", "Aug");
        months.put("сен.", "Sep");
        months.put("окт.", "Oct");
        months.put("ноя.", "Nov");
        months.put("дек.", "Dec");
    }


    private static LocalDateTime convertStringToDate(String s) {
        String[] parts = s.split(" ");
        String month = months.get(parts[1]);
        String[] time = parts[3].split(":");
        LocalDate localDate = LocalDate.now();
        String newString = parts[0] + " " + month + " " + localDate.getYear() + " " + time[0] + ":" + time[1];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        return LocalDateTime.parse(newString, formatter);
    }

    private static String readJsonFromUrl() throws IOException {
        log.info("Fetch - " + url);
        try (Scanner scanner = new Scanner(new URL(url).openStream(), "UTF-8")) {
            scanner.useDelimiter("\\A");
            return scanner.next();
        }
    }

    private static Matcher createMatcher(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(text);
    }

    public static void main(String[] args) {
        log.info("Server time: " + lastPostTime + " | " + TimeZone.getDefault());
        log.info(Config.getAuthor());
        var listOfPosts = new HashSet<Post>();
        try {
            DiscordApi api = new DiscordApiBuilder().setToken(Config.getBotToken()).login().join();
            log.info("Open discord connection");
            while (true) {
                var json = readJsonFromUrl();
                var matcherForJson = createMatcher(json, "(?<=<div class=\\\"Feed_feed_vmBqX\\\">)(.*)(?=\\\">)(.*)(?=</div>)");

                if (!matcherForJson.find()) {
                    log.info("Not new post");
                    return;
                }
                var feed = matcherForJson.group();
                var matcherForPost = createMatcher(feed, "(?<=<a class=\\\"Link_block_f6iQc CreatedAt_headerLink_8cVbd\\\" )(.{0,100})(?=</a>)");

                while (matcherForPost.find()) {
                    var linkAndDate = matcherForPost.group().split(">");
                    var matcherForLink = createMatcher(linkAndDate[0], "href=\"(.*?)\"(.*?)");
                    if (!matcherForLink.find()) {
                        log.info("No match found.");
                        return;
                    }
                    var post = new Post();
                    post.setLink("https://boosty.to" + matcherForLink.group(1));
                    post.setLocalDateTime(convertStringToDate(linkAndDate[1]));
                    listOfPosts.add(post);
                }
                var s = listOfPosts.stream().sorted(Comparator.comparing(Post::getLocalDateTime)).collect(Collectors.toList());
                for (Post post : s) {
                    var postTime = post.getLocalDateTime();
                    if (postTime.isAfter(lastPostTime)) {
                        log.info(post.toString());
                        api.getTextChannelById(Config.getChannelId()).ifPresent(channel -> channel.sendMessage(post.getLink()));
                        lastPostTime = post.getLocalDateTime();

                    }
                }
                listOfPosts.clear();
                TimeUnit.SECONDS.sleep(60);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
