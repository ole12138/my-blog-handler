package com.jingmin.blog.upload.wordpress.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDTO {
    private Integer id;

    private LocalDateTime date;

    private String status = "publish";

    private String title;

    private String content;

    private Acf acf;
}
