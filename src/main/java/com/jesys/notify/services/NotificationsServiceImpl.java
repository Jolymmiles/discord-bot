package com.jesys.notify.services;

import com.jesys.notify.posts.AbstractPost;
import org.springframework.stereotype.Service;

@Service
public interface NotificationsServiceImpl {



    void sendMessage(AbstractPost post);
}
