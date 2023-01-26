package com.jesys.notify.posts;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class AbstractPost {
    protected String link;
    protected LocalDateTime dateOfPost;

    protected abstract LocalDateTime convertStringToDate(String s);

    public abstract void setDateOfPost(String s);
}
