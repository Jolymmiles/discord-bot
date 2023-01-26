package com.jesys.notify.services;

import com.jesys.notify.posts.AbstractPost;
import org.javacord.api.DiscordApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class NotificationsService implements NotificationsServiceImpl {

    private final DiscordApi discordApi;

    @Value("${com.jesys.discord.channelId}")
    private String channelId;

    @Autowired
    public NotificationsService(@Qualifier("discordApiClient") DiscordApi discordApi) {
        this.discordApi = discordApi;
    }

    @Override
    public void sendMessage(AbstractPost post) {
        discordApi.getTextChannelById(channelId).ifPresent(channel -> channel.sendMessage(post.getLink()));
    }
}
