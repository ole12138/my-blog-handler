package com.jingmin.blog.upload.wordpress.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.jingmin.blog.upload.wordpress.model.Acf;
import com.jingmin.blog.upload.wordpress.model.Category;
import com.jingmin.blog.upload.wordpress.model.Post;
import com.jingmin.blog.upload.wordpress.model.Tag;
import com.jingmin.blog.upload.wordpress.model.dto.CategoryDTO;
import com.jingmin.blog.upload.wordpress.model.dto.PostDTO;
import com.jingmin.blog.upload.wordpress.model.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.HttpClient;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WordPressUtil {
    /**
     * 创建分类
     * reference: <a href="https://developer.wordpress.org/rest-api/reference/categories/"/>
     */
    public static Category createCategory(HttpClient client, Map<String, String> authHeaders, String categoryUrl, String name, Long parentId) throws JsonProcessingException {
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .name(name)
                .parent(parentId == null ? null : parentId.intValue())
                .build();
        String categoryJson = ObjectMapperUtil.SNAKE.writeValueAsString(categoryDTO);
        return HttpClientUtil.postJson(client, categoryUrl, categoryJson, authHeaders, Category.class);
    }

    /**
     * 创建标签
     * reference: <a href="https://developer.wordpress.org/rest-api/reference/tags/"/>
     */
    public static Tag createTag(HttpClient client, Map<String, String> authHeaders, String tagUrl, String name) throws JsonProcessingException {
        TagDTO tagDTO = TagDTO.builder()
                .name(name)
                .build();
        String tagJson = ObjectMapperUtil.SNAKE.writeValueAsString(tagDTO);
        return HttpClientUtil.postJson(client, tagUrl, tagJson, authHeaders, Tag.class);
    }

    /**
     * 新增post
     * reference: <a href="https://developer.wordpress.org/rest-api/reference/posts/"/>
     */
    public static Post createPost(HttpClient client, Map<String, String> authHeaders, String postUrl, String uuid, String title, String content, List<Long> catogoryIds, List<Long> tagIds) throws JsonProcessingException {
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

        return HttpClientUtil.postJson(client, postUrl, postJson, authHeaders, Post.class);
    }

    /**
     * 更新post
     * reference: <a href="https://developer.wordpress.org/rest-api/reference/tags/"/>
     */
    public static Post updatePost(HttpClient client, Map<String, String> authHeaders, String postUrl, Long postId, String uuid, String title, String content, List<Long> catogoryIds, List<Long> tagIds) throws JsonProcessingException {
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

        return HttpClientUtil.postJson(client, postUrl, postJson, authHeaders, Post.class);
    }

    /**
     * 获取所有的tag, 以map(name, tag)的方式返回
     * 参考： <a href="https://developer.wordpress.org/rest-api/reference/tags/"/>
     */
    public static Map<String, Tag> getNameTagMap(HttpClient client, Map<String, String> authHeaders, String tagUrl) {
        // 先获取总页数
        int page = 1;
        String appendArgs = (!tagUrl.contains("?") ? "?" : ":") +
                "page=" + page +
                "&per_page=50";
        String url = tagUrl + appendArgs;
        Integer totalPages = Integer.parseInt(HttpClientUtil.getRespHeader(client, authHeaders, url, null, "X-WP-TotalPages"));

        // 依次获取每页数据
        List<Tag> tags = new ArrayList<>();
        for (page = 1; page <= totalPages; page++) {
            appendArgs = (!tagUrl.contains("?") ? "?" : ":") +
                    "page=" + page +
                    "&per_page=50";
            url = tagUrl + appendArgs;

            List<Tag> tempTags = HttpClientUtil.getJson(client, url, null, authHeaders, new TypeReference<List<Tag>>() {
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

    /**
     * 将传入的路径转为多层级目录
     * 比如传入 “aa/bb/cc”， 则将在wordpress中生成“aa”，“bb”，“cc” 三个目录， 且“aa”是“bb”的父级， “bb”是“cc”的父级
     */
    public static List<Category> generateCategoryOfPath(HttpClient client, Map<String, String> authHeaders, String categoryUrl, String categoryPath) throws JsonProcessingException {
        // 将传入的category按“/”分割
        String[] splits = Arrays.stream(Optional.ofNullable(categoryPath).orElse("").split("/"))
                .filter(StringUtils::isNotBlank)
                .toArray(String[]::new);
        if (splits.length == 0) {
            return Collections.emptyList();
        }

        Map<String, List<Category>> nameCategoryMap = getNameCategoryMultiMap(client, authHeaders, categoryUrl);

        // System.out.println("CateMap: " + nameCategoryMap.keySet());

        List<Category> cates = new ArrayList<>();
        Long pid = null;
        for (int i = 0; i < splits.length; i++) {
            String cateName = splits[i];
            // System.out.println("CateName: " + cateName);
            List<Category> namedCates = nameCategoryMap.getOrDefault(cateName, new ArrayList<>());
            // System.out.println("NamedCates: " + Arrays.toString(namedCates.toArray()));
            nameCategoryMap.put(cateName, namedCates);
            Category cate = null;

            BiPredicate<Category, Long> withParentId = (c, parentId) -> {
                boolean bothTopLevel = parentId == null && (c.getParent() == null || c.getParent() == 0);
                boolean sameParentId = parentId != null && c.getParent() != null && parentId.equals(c.getParent());
                return bothTopLevel || sameParentId;
            };
            final Long curPid = pid;
            cate = namedCates.stream().filter(c -> withParentId.test(c, curPid)).findFirst().orElse(null);
            if (cate == null) {
                cate = WordPressUtil.createCategory(client, authHeaders, categoryUrl, cateName, pid);
                namedCates.add(cate);
            }
            cates.add(cate);
            pid = cate.getId();
        }
        return cates;
    }

    /**
     * 查询所有的分类catogory, 以map(name, list(catogory))的方式返回、
     * 参考： <a href="https://developer.wordpress.org/rest-api/reference/categories/"/>
     */
    public static Map<String, List<Category>> getNameCategoryMultiMap(HttpClient client, Map<String, String> authHeaders, String categoryUrl) {
        // 先获取总页数
        int page = 1;
        String appendArgs = (!categoryUrl.contains("?") ? "?" : ":") +
                "page=" + page +
                "&per_page=50";
        String url = categoryUrl + appendArgs;
        Integer totalPages = Integer.parseInt(HttpClientUtil.getRespHeader(client, authHeaders, url, null, "X-WP-TotalPages"));

        // 依次获取每页数据
        Map<String, List<Category>> nameCategoryMultiMap = new HashMap<>();
        List<Category> categories = new ArrayList<>();
        for (page = 1; page <= totalPages; page++) {
            appendArgs = (!categoryUrl.contains("?") ? "?" : ":") +
                    "page=" + page +
                    "&per_page=50";
            url = categoryUrl + appendArgs;

            List<Category> tempCategories = HttpClientUtil.getJson(client, url, null, authHeaders, new TypeReference<List<Category>>() {
            });
            if (tempCategories != null && !tempCategories.isEmpty()) {
                for (Category category : tempCategories) {
                    String name = category.getName();
                    List<Category> namedCategories = nameCategoryMultiMap.get(name);
                    namedCategories = namedCategories == null ? new ArrayList<>() : namedCategories;
                    namedCategories.add(category);
                    nameCategoryMultiMap.put(category.getName(), namedCategories);
                }
            }
            // System.out.printf("Page: %d, pageSize: %d\n", page, tempPosts != null ? tempPosts.size():0);
        }
        return nameCategoryMultiMap;
    }

    /**
     * 查询所有的post, 以map(uuid,post)的方式返回
     * 注意，这里过滤掉了不含uuid的post
     * <a href="https://developer.wordpress.org/rest-api/reference/posts/#list-posts"/>
     * <a href="https://developer.wordpress.org/rest-api/using-the-rest-api/pagination/"/>
     */
    public static Map<String, Post> getUuidPostMap(HttpClient client, Map<String, String> authHeaders, String postUrl) {

        // 先获取总页数
        int page = 1;
        // 为了减少io不必要的io, 这里限制只返回post的id和 md_uuid
        String appendArgs = (!postUrl.contains("?") ? "?" : ":") +
                "page=" + page +
                "&per_page=50" +
                "&_fields=id,acf.md_uuid";
        String url = postUrl + appendArgs;
        Integer totalPages = Integer.parseInt(HttpClientUtil.getRespHeader(client, authHeaders, url, null, "X-WP-TotalPages"));

        // 依次获取每页数据
        List<Post> posts = new ArrayList<>();
        for (page = 1; page <= totalPages; page++) {
            List<Post> tempPosts = null;
            // 为了减少io不必要的io, 这里限制只返回post的id和 md_uuid
            appendArgs = (!postUrl.contains("?") ? "?" : ":") +
                    "page=" + page +
                    "&per_page=50" +
                    "&_fields=id,acf.md_uuid";
            url = postUrl + appendArgs;
            tempPosts = HttpClientUtil.getJson(client, url, null, authHeaders, new TypeReference<List<Post>>() {
            });
            if (tempPosts != null && !tempPosts.isEmpty()) {
                posts.addAll(tempPosts);
            }
            // System.out.printf("Page: %d, pageSize: %d\n", page, tempPosts != null ? tempPosts.size():0);
        }
        return posts.stream()
                .filter(post -> post.getAcf() != null
                        && post.getAcf().getMdUuid() != null
                        && !post.getAcf().getMdUuid().isEmpty()
                )
                .collect(Collectors.toMap((post -> post.getAcf().getMdUuid()), post -> post));
    }

}
