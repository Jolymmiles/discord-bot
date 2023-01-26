package com.jesys.notify.daemons;

import com.jesys.notify.config.properties.ConnectionProperties;
import com.jesys.notify.posts.RulatePost;
import com.jesys.notify.services.NotificationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
public class RulateDaemon extends Daemon{

    private static LocalDateTime lastPostTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
    private final List<String> rulateBooks;
    @Autowired
    private NotificationsService notificationsService;

    @Autowired
    public RulateDaemon(ConnectionProperties connectionProperties) {
        this.rulateBooks = connectionProperties.getRulate().getBook();
    }

    @Scheduled(fixedRate = 60000)
    @Override
    public void checkNewPosts() throws IOException {
        for (String url : rulateBooks){
            log.info("Check: " + url);
            var listOfPosts = new HashSet<RulatePost>();
            var json = readJsonFromUrl(url);
            var matcherForJson = createMatcher(json, "<table class=\"table table-condensed table-striped\" id=\"Chapters\">(.*?)</table>");
            if (!matcherForJson.find()) {
                log.info("Not new post");
                return;
            }

            var feed = matcherForJson.group();
            var matcherForPost = createMatcher(feed, "<tr id=\\\"c_\\d{6}\\\" data-id=\\\"\\d{6}\\\" class=\\\"chapter_row  \\\">.*</tr>");
            while (matcherForPost.find()) {

                var linkAndDate = matcherForPost.group().split(">");
                var matcherForLink = createMatcher(linkAndDate[0], "href=\"(.*?)\"(.*?)");
                if (!matcherForLink.find()) {
                    log.info("No match found.");
                    return;
                }
                var post = new RulatePost();
                post.setLink("https://tl.rulate.ru/" + matcherForLink.group(1));
                post.setDateOfPost(linkAndDate[1]);
                listOfPosts.add(post);
            }
            var s = listOfPosts.stream().sorted(Comparator.comparing(RulatePost::getDateOfPost)).toList();
            for (RulatePost post : s) {
                if (post.getDateOfPost().isAfter(lastPostTime) & post.getStatus().equals("100%")) {
                    log.info(post.toString());
                    notificationsService.sendMessage(post);
                    lastPostTime = post.getDateOfPost();

                }
            }
            listOfPosts.clear();
        }
    }
}
