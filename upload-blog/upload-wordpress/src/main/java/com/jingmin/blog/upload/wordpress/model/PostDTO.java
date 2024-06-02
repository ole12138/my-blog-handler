package com.jingmin.blog.upload.wordpress.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDTO {
    private Long id;

    private LocalDateTime date;

    private String status = "publish";

    private String title;

    private String content;

    private Acf acf;

    private List<Long> categories;

    private List<Long> tags;
}
