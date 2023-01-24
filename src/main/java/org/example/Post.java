package org.example;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Post {
    private String link;
    private LocalDateTime localDateTime;
}
