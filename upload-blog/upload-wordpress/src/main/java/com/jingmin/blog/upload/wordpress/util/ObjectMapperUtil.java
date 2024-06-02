package com.jingmin.blog.upload.wordpress.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

public class ObjectMapperUtil {
    public static final ObjectMapper DEFAULT = new ObjectMapper();

    public static final ObjectMapper SNAKE = new ObjectMapper();

    static {
        SNAKE.writerWithDefaultPrettyPrinter();
        SNAKE.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        SNAKE.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
