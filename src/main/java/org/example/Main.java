package org.example;

import lombok.var;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

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

public class Main {
    private static final String lastPostId = "";

    private static final String url = "https://boosty.to/jesys";

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
        // 24 янв. в 10:59
        String[] parts = s.split(" ");
        String month = months.get(parts[1]);
        String[] time = parts[3].split(":");
        LocalDate localDate = LocalDate.now();
        String newString = parts[0] + " " + month + " " + localDate.getYear() + " " + time[0] + ":" + time[1];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        return LocalDateTime.parse(newString, formatter);
    }

    public static void main(String[] args) {
        System.out.println("Server time:" + lastPostTime);
        TimeZone tz = TimeZone.getDefault();
        System.out.println(tz.getID());
        String discordToken = "MTA2NzQwMTU5NzE5NTM5NTA4Mg.GOxtlp.6XPK2AYfI5KBIcAdiMsRykaAlu80slKni1Kunk";
        String discordChannelId = "874208586555879444";
        var listOfPosts = new HashSet<Post>();
        try {
            while (true) {
                DiscordApi api = new DiscordApiBuilder().setToken(discordToken).login().join();
                System.out.println("Open discord connection");
                String jsonString = new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A").next();
                String regex = "(?<=<div class=\\\"Feed_feed_vmBqX\\\">)(.*)(?=\\\">)(.*)(?=</div>)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(jsonString);
                String regexForPost = "(?<=<a class=\\\"Link_block_f6iQc CreatedAt_headerLink_8cVbd\\\" )(.{0,100})(?=</a>)";
                Pattern patternForPost = Pattern.compile(regexForPost);
                if (matcher.find()) {
                    String match = matcher.group();
                    Matcher matcherForPost = patternForPost.matcher(match);

                    while (matcherForPost.find()) {
                        var data = matcherForPost.group().split(">");
                        var dataLink = data[0];
                        var dataTime = data[1];
                        Pattern patternForData = Pattern.compile("href=\"(.*?)\"(.*?)");
                        Matcher matcherForData = patternForData.matcher(dataLink);
                        Post post = new Post();
                        if (matcherForData.find()) {
                            post.setLink(matcherForData.group(1));
                        } else {
                            System.out.println("No match found.");
                        }

                        post.setLocalDateTime(convertStringToDate(dataTime));
                        listOfPosts.add(post);
                    }
                } else {
                    System.out.println("Постов нету");
                }

                var s = listOfPosts.stream().sorted(Comparator.comparing(Post::getLocalDateTime)).collect(Collectors.toList());

                for (Post post : s) {
                    var postTime = post.getLocalDateTime();
                    if (postTime.compareTo(lastPostTime) > 0) {
                        System.out.println(post);

                        // Send a message to a channel
                        var linkToPost = "https://boosty.to" + post.getLink();
                        var msg = "Новый пост на бусти!" + "\n" + linkToPost;
                        api.getTextChannelById(discordChannelId).ifPresent(channel -> channel.sendMessage(msg));

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

