package com.jingmin.blog.upload.wordpress;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.jingmin.blog.upload.wordpress.model.*;
import com.jingmin.blog.upload.wordpress.util.CommandLineParser;
import com.jingmin.blog.upload.wordpress.util.HttpClientUtil;
import com.jingmin.blog.upload.wordpress.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.io.FileUtils;
import org.apache.hc.client5.http.classic.HttpClient;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

    private static String wordpressUrl = "https://wordpress.ole12138.cn/wp-json/wp/v2/posts";

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
        File file = new File(filePath);
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

        // 这里将文章所有的keyword 都建为 Category分类
        Map<String, Category> nameCategoryMap = getNameCategoryMap(categoryUrl);
        List<Long> categoryIds = new ArrayList<>();
        for(String keyword: keywords) {
            Category category = nameCategoryMap.get(keyword);
            if(category == null) {
                category = createCategory(categoryUrl, keyword);
                nameCategoryMap.put(keyword, category);
            }
            categoryIds.add(category.getId());
        }
        // 这里将文章所有的keyword 都建为 Tag标签
        Map<String, Tag> nameTagMap = getNameTagMap(tagUrl);
        List<Long> tagIds = new ArrayList<>();
        for(String keyword: keywords) {
            Tag tag = nameTagMap.get(keyword);
            if(tag == null) {
                tag = createTag(tagUrl, keyword);
                nameTagMap.put(keyword, tag);
            }
            tagIds.add(tag.getId());
        }
        //检查是否已经存在对应的文章
        Map<String, Post> uuidPostMap = getUuidPostMap(postUrl);
        Post historyPost = uuidPostMap.get(uuid);
        Post post = null;
        if (historyPost == null) {
            // 新增
            post = createPost(postUrl, uuid, title, content, categoryIds, tagIds);
        } else {
            // 更新
            Long postId = historyPost.getId();
            String updateUrl = postUrl + "/" + postId;
            post = updatePost(updateUrl, postId, uuid, title, content, categoryIds, tagIds);
        }

        if (post == null) {
            //失败退出
            System.exit(1);
        }

        // 标准输出： 第一行：wordpress中post的id
        System.out.println(post.getId());
        // 标准输出： 第二行：文章的全局uuid
        System.out.println(post.getAcf().getMdUuid());
    }

    private Category createCategory(String categoryUrl, String name) throws JsonProcessingException {
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .name(name)
                .build();
        String categoryJson = ObjectMapperUtil.SNAKE.writeValueAsString(categoryDTO);
        return HttpClientUtil.postJson(httpClient, categoryUrl, categoryJson, buildAuthHeaders(), Category.class);
    }

    private Tag createTag(String tagUrl, String name) throws JsonProcessingException {
        TagDTO tagDTO = TagDTO.builder()
                .name(name)
                .build();
        String tagJson = ObjectMapperUtil.SNAKE.writeValueAsString(tagDTO);
        return HttpClientUtil.postJson(httpClient, tagUrl, tagJson, buildAuthHeaders(), Tag.class);
    }

    /**
     * 新增post
     */
    private Post createPost(String postUrl, String uuid, String title, String content, List<Long> catogoryIds, List<Long> tagIds) throws JsonProcessingException {
        PostDTO postDTO = PostDTO.builder()
                .acf(Acf.builder().mdUuid(uuid).build())
                .title(title)
                .content(content)
                .status("publish")
                .categories(catogoryIds)
                .tags(tagIds)
                .build();
        String postJson = ObjectMapperUtil.SNAKE.writeValueAsString(postDTO);
        // System.out.println(postJson);

        return HttpClientUtil.postJson(httpClient, postUrl, postJson, buildAuthHeaders(), Post.class);
    }

    private Post updatePost(String postUrl,Long postId, String uuid, String title, String content, List<Long> catogoryIds, List<Long> tagIds) throws JsonProcessingException {
        PostDTO postDTO = PostDTO.builder()
                .id(postId)
                .acf(Acf.builder().mdUuid(uuid).build())
                .title(title)
                .content(content)
                .status("publish")
                .categories(catogoryIds)
                .tags(tagIds)
                .build();
        String postJson = ObjectMapperUtil.SNAKE.writeValueAsString(postDTO);
        // System.out.println(postJson);

        return HttpClientUtil.postJson(httpClient, postUrl, postJson, buildAuthHeaders(), Post.class);
    }
    private Map<String, String> buildAuthHeaders() {
        Map<String,String> headers = new HashMap<>();
        String userPassword = wpUser + ":" + wpPassword;
        String userPasswordBase64 = Base64.getEncoder().encodeToString(userPassword.getBytes(StandardCharsets.UTF_8));
        headers.put("Authorization", "Basic " + userPasswordBase64);
        return headers;
    }

    private Map<String, Tag> getNameTagMap(String tagUrl) {
        //先获取总页数
        int page = 1;
        String appendArgs = (!tagUrl.contains("?") ? "?" : ":") +
                "page=" + page +
                "&per_page=50";
        String url = tagUrl + appendArgs;
        Integer totalPages = Integer.parseInt(HttpClientUtil.getRespHeader(httpClient, url, null, "X-WP-TotalPages"));

        //依次获取每页数据
        List<Tag> tags = new ArrayList<>();
        for(page = 1; page <= totalPages; page++) {
            appendArgs = (!tagUrl.contains("?") ? "?" : ":") +
                    "page=" + page +
                    "&per_page=50";
            url = tagUrl + appendArgs;

            List<Tag> tempTags = HttpClientUtil.getJson(httpClient, url, null, buildAuthHeaders(), new TypeReference<List<Tag>>() {
            });
            if (tempTags != null && !tempTags.isEmpty()) {
                tags.addAll(tempTags);
            }
            // System.out.printf("Page: %d, pageSize: %d\n", page, tempPosts != null ? tempPosts.size():0);
        }
        return tags.stream()
                .filter(tag -> tag != null && tag.getName() != null)
                .collect(Collectors.toMap(tag -> tag.getName(), tag -> tag));
    }

    private Map<String, Category> getNameCategoryMap(String categoryUrl) {
        //先获取总页数
        int page = 1;
        String appendArgs = (!categoryUrl.contains("?") ? "?" : ":") +
                "page=" + page +
                "&per_page=50";
        String url = categoryUrl + appendArgs;
        Integer totalPages = Integer.parseInt(HttpClientUtil.getRespHeader(httpClient, url, null, "X-WP-TotalPages"));

        //依次获取每页数据
        List<Category> categories = new ArrayList<>();
        for(page = 1; page <= totalPages; page++) {
            appendArgs = (!categoryUrl.contains("?") ? "?" : ":") +
                    "page=" + page +
                    "&per_page=50";
            url = categoryUrl + appendArgs;

            List<Category> tempCategories = HttpClientUtil.getJson(httpClient, url, null, buildAuthHeaders(), new TypeReference<List<Category>>() {
            });
            if (tempCategories != null && !tempCategories.isEmpty()) {
                categories.addAll(tempCategories);
            }
            // System.out.printf("Page: %d, pageSize: %d\n", page, tempPosts != null ? tempPosts.size():0);
        }
        return categories.stream()
                .filter(category -> category != null && category.getName() != null)
                .collect(Collectors.toMap(category -> category.getName(), category -> category));
    }

    /**
     * 查询所有的post, 以map(uuid,post)的方式返回
     * 注意，这里过滤掉了不含uuid的post
     * <a href="https://developer.wordpress.org/rest-api/reference/posts/#list-posts"/>
     * <a href="https://developer.wordpress.org/rest-api/using-the-rest-api/pagination/"/>
     */
    private Map<String, Post> getUuidPostMap(String postUrl) {

        //先获取总页数
        int page = 1;
        // 为了减少io不必要的io, 这里限制只返回post的id和 md_uuid
        String appendArgs = (!postUrl.contains("?") ? "?" : ":") +
                "page=" + page +
                "&per_page=50" +
                "&_fields=id,acf.md_uuid";
        String url = postUrl + appendArgs;
        Integer totalPages = Integer.parseInt(HttpClientUtil.getRespHeader(httpClient, url, null, "X-WP-TotalPages"));

        //依次获取每页数据
        List<Post> posts = new ArrayList<>();
        for(page = 1; page <= totalPages; page++) {
            List<Post> tempPosts = null;
            // 为了减少io不必要的io, 这里限制只返回post的id和 md_uuid
            appendArgs = (!postUrl.contains("?") ? "?" : ":") +
                    "page=" + page +
                    "&per_page=50" +
                    "&_fields=id,acf.md_uuid";
            url = postUrl + appendArgs;
            tempPosts = HttpClientUtil.getJson(httpClient, url, null, buildAuthHeaders(), new TypeReference<List<Post>>() {
            });
            if (tempPosts != null && !tempPosts.isEmpty()) {
                posts.addAll(tempPosts);
            }
            // System.out.printf("Page: %d, pageSize: %d\n", page, tempPosts != null ? tempPosts.size():0);
        }

        return posts.stream()
                .filter(post->post.getAcf() != null
                        && post.getAcf().getMdUuid() != null
                        && !post.getAcf().getMdUuid().isEmpty()
                )
                .collect(Collectors.toMap((post -> post.getAcf().getMdUuid()), post -> post));
    }

}
