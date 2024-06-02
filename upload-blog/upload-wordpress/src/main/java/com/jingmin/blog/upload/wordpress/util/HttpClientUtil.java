package com.jingmin.blog.upload.wordpress.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.auth.CredentialsProviderBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


/**
 * apache httpclient util
 * <p>
 * reference: <a href="https://hc.apache.org/httpcomponents-client-5.3.x/examples.html"/>
 */
// @Slf4j
public class HttpClientUtil {

    public static final HttpClient DEFAULT_CLIENT = HttpClients.createDefault();
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.writerWithDefaultPrettyPrinter();
        OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static HttpClient newBasicAuthClient(String host, Integer port, String username, String password) {
        final CredentialsProvider credentialsProvider = CredentialsProviderBuilder.create()
                // .add(new AuthScope("localhost", 8888), "squid", "squid".toCharArray())
                // .add(new AuthScope("httpbin.org", 80), "user", "passwd".toCharArray())
                // issue: （这里的配置失效)
                //   Abbas： I have the same issue, HttpGet works with UsernamePasswordCredentials but HttpPost doesn't. When I use Base64 to manually add Authorization header it works without any issue.
                // https://stackoverflow.com/questions/3283234/http-basic-authentication-in-java-using-httpclient

                .add(new AuthScope(host, port), username, password.toCharArray())// 允许添加多个
                .build();
        return HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();
    }

    public static <T> T postJson(HttpClient client, String url, String json, Map<String, String> headers, Class<T> clazz) {
        String respJson = postJson(client, url, json, headers);
        T obj = null;
        try {
            // System.out.println(respJson);
            obj = OBJECT_MAPPER.readValue(respJson, clazz);
        } catch (JsonProcessingException e) {
            // System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return obj;
    }

    public static <T> T postJson(HttpClient client, String url, String json, TypeReference<T> typeReference) {
        String respJson = postJson(client, url, json, (Map<String, String>) null);
        T obj = null;
        try {
            obj = OBJECT_MAPPER.readValue(respJson, typeReference);
        } catch (JsonProcessingException e) {
            // System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return obj;
    }

    public static String postJson(HttpClient client, String url, String json, Map<String, String> headers) {
        HttpEntity reqEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        ClassicRequestBuilder reqBuilder = ClassicRequestBuilder.post(url)
                .setEntity(reqEntity);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                reqBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        // .setHeader("Authorization", "Basic d2FuZ2ptOlNJWHZZRFF5ZU5Ca1NJQTlDTjJtaG53Sw==")//wangjm:SIXvYDQyeNBkSIA9CN2mhnwK
        ClassicHttpRequest httpPost = reqBuilder.build();

        try {
            HttpClientResponseHandler<String> responseHandler = response -> {
                // log.info(response.getCode() + " " + response.getReasonPhrase());
                if (response.getCode() >= 200 && response.getCode() < 300) {
                    final HttpEntity respEntity = response.getEntity();
                    // do something useful with the response body
                    if (respEntity != null) {
                        return EntityUtils.toString(respEntity);
                    }
                    // and ensure it is fully consumed
                    EntityUtils.consume(respEntity);
                } else {
                    System.err.println(response.getCode() + " " + response.getReasonPhrase() + EntityUtils.toString(response.getEntity()));
                }
                return null;
            };
            return client.execute(httpPost, responseHandler);
        } catch (IOException e) {
            // System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public static <T> T getJson(HttpClient client, String url, String json, Class<T> clazz) {
        String respJson = getJson(client, url, json, (Map<String, String>) null);
        T obj = null;
        try {
            obj = OBJECT_MAPPER.readValue(respJson, clazz);
        } catch (JsonProcessingException e) {
            // System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return obj;
    }

    public static <T> T getJson(HttpClient client, String url, String json, Map<String, String> headers, TypeReference<T> typeReference) {
        String respJson = getJson(client, url, json, headers);
        T obj = null;
        try {
            obj = OBJECT_MAPPER.readValue(respJson, typeReference);
        } catch (JsonProcessingException e) {
            // System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return obj;
    }

    public static String getRespHeader(HttpClient client, Map<String, String> authHeaders, String url, String json, String headerName) {
        HttpEntity reqEntity = null;
        if (json != null) {
            reqEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        }
        ClassicRequestBuilder reqBuilder = ClassicRequestBuilder.get(url)
                .setEntity(reqEntity);
        if (authHeaders != null) {
            for (Map.Entry<String, String> entry : authHeaders.entrySet()) {
                reqBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        ClassicHttpRequest getReq = reqBuilder.build();
        try {
            HttpResponse response = client.execute(getReq);
            return response.getHeader(headerName).getValue();
        } catch (IOException | ProtocolException e) {
            // System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public static String getJson(HttpClient client, String url, String json, Map<String, String> headers) {
        HttpEntity reqEntity = null;
        if (json != null) {
            reqEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        }
        ClassicRequestBuilder reqBuilder = ClassicRequestBuilder.get(url)
                .setEntity(reqEntity);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                reqBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        // .setHeader("Authorization", "Basic d2FuZ2ptOlNJWHZZRFF5ZU5Ca1NJQTlDTjJtaG53Sw==")//wangjm:SIXvYDQyeNBkSIA9CN2mhnwK
        ClassicHttpRequest getReq = reqBuilder.build();
        try {
            HttpClientResponseHandler<String> responseHandler = response -> {
                // log.info(response.getCode() + " " + response.getReasonPhrase());
                if (response.getCode() >= 200 && response.getCode() < 300) {
                    final HttpEntity respEntity = response.getEntity();
                    // do something useful with the response body
                    if (respEntity != null) {
                        return EntityUtils.toString(respEntity);
                    }
                    // and ensure it is fully consumed
                    EntityUtils.consume(respEntity);
                } else {
                    System.err.println(response.getCode() + " " + response.getReasonPhrase() + " " + EntityUtils.toString(response.getEntity()));
                }
                return null;
            };
            return client.execute(getReq, responseHandler);
        } catch (IOException e) {
            // System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public static Map<String, String> buildAuthHeaders(String username, String password) {
        Map<String, String> headers = new HashMap<>();
        String userPassword = username + ":" + password;
        String userPasswordBase64 = Base64.getEncoder().encodeToString(userPassword.getBytes(StandardCharsets.UTF_8));
        headers.put("Authorization", "Basic " + userPasswordBase64);
        return headers;
    }
}
