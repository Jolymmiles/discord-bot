package com.jesys.notify.posts;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RulatePost extends AbstractPost {
    private String status;
    private String Title;

    @Override
    protected LocalDateTime convertStringToDate(String s) {
        return null;
    }

    @Override
    public void setDateOfPost(String s) {
        super.dateOfPost = convertStringToDate(s);
    }
}
