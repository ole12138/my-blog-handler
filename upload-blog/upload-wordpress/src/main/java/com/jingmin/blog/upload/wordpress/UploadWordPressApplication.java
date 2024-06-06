package com.jingmin.blog.upload.wordpress;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.jingmin.blog.upload.wordpress.model.*;
import com.jingmin.blog.upload.wordpress.model.dto.CategoryDTO;
import com.jingmin.blog.upload.wordpress.model.dto.PostDTO;
import com.jingmin.blog.upload.wordpress.model.dto.TagDTO;
import com.jingmin.blog.upload.wordpress.util.CommandLineParser;
import com.jingmin.blog.upload.wordpress.util.HttpClientUtil;
import com.jingmin.blog.upload.wordpress.util.ObjectMapperUtil;
import com.jingmin.blog.upload.wordpress.util.WordPressUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.io.FileUtils;
import org.apache.hc.client5.http.classic.HttpClient;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class UploadWordPressApplication {

    private Properties properties;

    private String wpHost;

    private String wpPort;

    private String wpSchema;

    private String wpUser;

    private String wpPassword;

    private HttpClient httpClient;

    public static void main(String[] args) throws IOException {
        UploadWordPressApplication application = new UploadWordPressApplication();
        application.run(args);
    }

    public UploadWordPressApplication() throws IOException {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("application.properties");
             Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            properties = new Properties();
            properties.load(reader);
            wpHost = System.getenv("WORDPRESS_HOST") != null ? System.getenv("WORDPRESS_HOST") : System.getProperty("wordpress.host", properties.getProperty("wordpress.host"));
            wpPort = System.getenv("WORDPRESS_PORT") != null ? System.getenv("WORDPRESS_PORT") : System.getProperty("wordpress.port", properties.getProperty("wordpress.port"));
            wpSchema = System.getenv("WORDPRESS_SCHEMA") != null ? System.getenv("WORDPRESS_SCHEMA") : System.getProperty("wordpress.schema", properties.getProperty("wordpress.schema"));
            wpUser = System.getenv("WORDPRESS_USER") != null ? System.getenv("WORDPRESS_USER") : System.getProperty("wordpress.user", properties.getProperty("wordpress.user"));
            wpPassword = System.getenv("WORDPRESS_PASSWORD") != null ? System.getenv("WORDPRESS_PASSWORD") : System.getProperty("wordpress.password", properties.getProperty("wordpress.password"));
            httpClient = HttpClientUtil.newBasicAuthClient(wpHost, Integer.parseInt(wpPort), wpUser, wpPassword);
        }
    }

    public void run(String[] args) throws IOException {
        CommandLine commandLine = CommandLineParser.parseArgs(args);
        String uuid = UUID.randomUUID().toString();
        List<String> keywords = new ArrayList<>();
        for (Option option : commandLine.getOptions()) {
            String k = option.getKey();
            String v = option.getValue(k);
            if ("k".equals(k)) {
                keywords.add(v);
            }
            if ("u".equals(k)) {
                uuid = v;
            }
        }
        String filePath = commandLine.getArgs()[0];
        File file = Path.of(filePath).toFile();
        handleFile(file, uuid, keywords);

    }

    /**
     * 处理单个markdown文件
     */
    private void handleFile(File file, String uuid, List<String> keywords) throws IOException {
        if (!file.exists()) {
            System.err.println("File does not exist");
            return;
        }
        if (!file.isFile()) {
            System.err.println("File is not a file");
        }

        // 读取文章内容
        String content = FileUtils.readFileToString(file, "UTF-8");

        // 标题
        String title = file.getName()
                .replaceAll("\\.md$", "")
                .replaceAll("\\.markdown$", "")
                .replaceAll("\\.html", "");

        String postUrl = wpSchema + "://" + wpHost + ":" + wpPort + "/wp-json/wp/v2/posts";
        String categoryUrl = wpSchema + "://" + wpHost + ":" + wpPort + "/wp-json/wp/v2/categories";
        String tagUrl = wpSchema + "://" + wpHost + ":" + wpPort + "/wp-json/wp/v2/tags";

        Map<String, String> authHeaders = HttpClientUtil.buildAuthHeaders(wpUser, wpPassword);

        // 将文章所有的keyword 都建为 Category分类
        Map<String, Category> nameCategoryMap = WordPressUtil.getNameCategoryMap(httpClient, authHeaders, categoryUrl);
        List<Long> categoryIds = new ArrayList<>();
        for (String keyword : keywords) {
            Category category = nameCategoryMap.get(keyword);
            if (category == null) {
                category = WordPressUtil.createCategory(httpClient, authHeaders, categoryUrl, keyword);
                nameCategoryMap.put(keyword, category);
            }
            categoryIds.add(category.getId());
        }

        // 将文章所有的keyword 都建为 Tag标签
        Map<String, Tag> nameTagMap = WordPressUtil.getNameTagMap(httpClient, authHeaders, tagUrl);
        List<Long> tagIds = new ArrayList<>();
        for (String keyword : keywords) {
            Tag tag = nameTagMap.get(keyword);
            if (tag == null) {
                tag = WordPressUtil.createTag(httpClient, authHeaders, tagUrl, keyword);
                nameTagMap.put(keyword, tag);
            }
            tagIds.add(tag.getId());
        }

        // 检查是否已经存在对应的文章
        Map<String, Post> uuidPostMap = WordPressUtil.getUuidPostMap(httpClient, authHeaders, postUrl);
        Post historyPost = uuidPostMap.get(uuid);
        Post post = null;
        if (historyPost == null) {
            // 新增
            post = WordPressUtil.createPost(httpClient, authHeaders, postUrl, uuid, title, content, categoryIds, tagIds);
        } else {
            // 更新
            Long postId = historyPost.getId();
            String updateUrl = postUrl + "/" + postId;
            post = WordPressUtil.updatePost(httpClient, authHeaders, updateUrl, postId, uuid, title, content, categoryIds, tagIds);
        }

        if (post == null) {
            // 失败退出
            System.exit(1);
        }

        // 标准输出： 第一行：wordpress中post的id
        System.out.println(post.getId());
        // 标准输出： 第二行：文章的全局uuid
        System.out.println(post.getAcf().getMdUuid());
    }


}
