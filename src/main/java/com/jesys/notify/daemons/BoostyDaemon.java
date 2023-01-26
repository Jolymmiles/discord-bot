package com.jesys.notify.daemons;

import com.jesys.notify.posts.BoostyPost;
import com.jesys.notify.services.NotificationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashSet;

@Slf4j
@Component
public class BoostyDaemon extends Daemon {

    @Autowired
    private NotificationsService notificationsService;
    @Value("${com.jesys.boosty.author-name}")
    private String url;
    private static LocalDateTime lastPostTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));

    @Override
    @Scheduled(fixedRate = 60000)
    public void checkNewPosts() throws IOException {
        log.info("Check: " + url);
        var listOfPosts = new HashSet<BoostyPost>();
        var json = readJsonFromUrl(url);
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
            var post = new BoostyPost();
            post.setLink("https://boosty.to" + matcherForLink.group(1));
            post.setDateOfPost(linkAndDate[1]);
            listOfPosts.add(post);
        }
        var s = listOfPosts.stream().sorted(Comparator.comparing(BoostyPost::getDateOfPost)).toList();
        for (BoostyPost boostyPost : s) {
            var postTime = boostyPost.getDateOfPost();
            if (postTime.isAfter(lastPostTime)) {
                log.info(boostyPost.toString());
                notificationsService.sendMessage(boostyPost);
                lastPostTime = boostyPost.getDateOfPost();

            }
        }
        listOfPosts.clear();


    }

}
