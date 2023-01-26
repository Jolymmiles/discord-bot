package com.jesys.notify.daemons;

import com.jesys.notify.config.properties.ConnectionProperties;
import com.jesys.notify.posts.RulatePost;
import com.jesys.notify.services.NotificationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
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
@ConditionalOnExpression("#{T(java.util.Arrays).asList(environment['com.jesys.service']).contains('rulate')}")
public class RulateDaemon extends Daemon {
    private LocalDateTime lastPostTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
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
        for (String url : rulateBooks) {
            log.info("Check: " + url);
            var listOfPosts = new HashSet<RulatePost>();
            var json = readJsonFromUrl(url);
            /*var matcherForJson = createMatcher(json, "<table class=\"table table-condensed table-striped\" id=\"Chapters\">.*</table>");
            Pattern.compile("<table class=\"table table-condensed table-striped\" id=\"Chapters\">\\n(.*?)\\n</table>");

            if (matcherForJson.find()) {
                log.info("Not new post");
                return;
            }

            var feed = matcherForJson.group();

            Pattern pattern = Pattern.compile("<tr id=\\\"c_\\d{6}\\\" data-id=\\\"\\d{6}\\\" class=\\\"chapter_row  \\\">(.</tr>");
            var matcherForPost = createMatcher(json, "<tr id=\\\"c_\\d{6}\\\" data-id=\\\"\\d{6}\\\" class=\\\"chapter_row  \\\">(.{0,20})</tr>");
            */

            while (matcherForPost.find()) {
                var rawPost = matcherForPost.group();
                var statusOfTranslate = createMatcher(rawPost, "КП=(\\d\\.\\d+)").group(1);
                var matcherForDataInPost = createMatcher(rawPost, "<td class=\\\"t\\\"><a (.*?)</a></td>");
                var linkAndTitle = matcherForDataInPost.group().split(">");
                var matcherForLink = createMatcher(linkAndTitle[0], "href=\"(.*?)\"(.*?)");
                if (!matcherForLink.find()) {
                    log.info("No match found.");
                    return;
                }
                var post = new RulatePost();
                post.setLink("https://tl.rulate.ru/" + matcherForLink.group(1));
                post.setTitle(linkAndTitle[1]);
                post.setStatus(statusOfTranslate);

                var date = createMatcher(rawPost, "title=\".*\"").group().replace("title=","").replace("\"","");
                post.setDateOfPost(date);
                listOfPosts.add(post);
            }
            var s = listOfPosts.stream().sorted(Comparator.comparing(RulatePost::getDateOfPost)).toList();
            for (RulatePost post : s) {
                if (post.getDateOfPost().isAfter(lastPostTime) & post.getStatus().equals("КП=1.0")) {
                    log.info(post.toString());
                    notificationsService.sendMessage(post);
                    lastPostTime = post.getDateOfPost();

                }
            }
            listOfPosts.clear();
        }
    }
}
