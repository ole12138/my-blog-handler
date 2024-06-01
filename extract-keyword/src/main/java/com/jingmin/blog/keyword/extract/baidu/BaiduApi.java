package com.jingmin.blog.keyword.extract.baidu;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import okhttp3.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public class BaiduApi {
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();
    static final ObjectMapper MAPPER = new ObjectMapper();

    static Token TOKEN = null;


    /**
     * 百度智能云获取token
     * <a ref="https://ai.baidu.com/ai-doc/REFERENCE/Ck3dwjhhu"/>
     * @param accessKey api key
     * @param secretKey secret key
     * @return accessToken
     * @throws IOException 获取token失败
     */
    public static String getToken(String accessKey, String secretKey) throws IOException {
        //需要获取新token
        if(TOKEN == null || LocalDateTime.now().isAfter(TOKEN.getCreateTime().plusSeconds(TOKEN.getExpires_in()))){
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://aip.baidubce.com/oauth/2.0/token?client_id="+accessKey+"&client_secret="+secretKey+"&grant_type=client_credentials")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();
            Response response = HTTP_CLIENT.newCall(request).execute();
            if(response.body() == null) {
                return null;
            }
            Token token = MAPPER.readValue(response.body().string(), Token.class);
            //缓存token
            if(token != null) {
                TOKEN = token;
            }
        }
        return TOKEN.getAccess_token();
    }

    /**
     * 生成关键字/文章标签（调用百度智能云-自然语言处理技术NLP-文章标签接口）
     * <a ref="https://cloud.baidu.com/doc/NLP/s/7k6z52ggx"/>
     * @param content 文章内容
     * @param title 文章标题
     * @param accessToken 需要提前获取accessToken
     * @return Keywords
     */
    public static Keywords getKeywords(String content, String title, String accessToken) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        KeywordRequestBody requestBody = KeywordRequestBody.builder().content(content).title(title).build();
        RequestBody body = RequestBody.create(mediaType, MAPPER.writeValueAsString(requestBody));
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/nlp/v1/keyword?charset=UTF-8&access_token=" + accessToken)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        if(response.body() == null) {
            return null;
        }
        return MAPPER.readValue(response.body().string(), Keywords.class);
    }

    @Data
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeywordRequestBody {
        private String content;
        private String title;
    }

    @Data
    @ToString
    public static class Keywords {
        private List<Item> items;
        private String log_id;
        private String error_msg;
        private String error_code;

        @Data
        @ToString
        public static class Item {
            private String tag;
            private Float score;
        }
    }

    @Data
    @ToString
    public static class Token {
        private String refresh_token;
        private Integer expires_in;
        private String session_key;
        private String access_token;
        private String scope;
        private String session_secret;
        private LocalDateTime createTime = LocalDateTime.now();


        private String log_id;
        private String error_msg;
        private String error_code;
    }
}
