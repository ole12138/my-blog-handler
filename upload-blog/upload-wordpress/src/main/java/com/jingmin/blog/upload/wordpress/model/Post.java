
package com.jingmin.blog.upload.wordpress.model;

import java.util.List;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WordPress 创建post的接口响应
 * <pre>
 *     {
 *   "id":31,
 *   "date":"2024-06-02T00:56:12",
 *   "date_gmt":"2024-06-01T16:56:12",
 *   "guid":{
 *     "rendered":"https:\/\/wordpress.ole12138.cn\/archives\/31",
 *     "raw":"https:\/\/wordpress.ole12138.cn\/archives\/31"
 *   },
 *   "modified":"2024-06-02T00:56:12",
 *   "modified_gmt":"2024-06-01T16:56:12",
 *   "password":"",
 *   "slug":"testabc-4",
 *   "status":"publish",
 *   "type":"post",
 *   "link":"https:\/\/wordpress.ole12138.cn\/archives\/31",
 *   "title":{
 *     "raw":"testabc",
 *     "rendered":"testabc"
 *   },
 *   "content":{
 *     "raw":"testxxxxxx",
 *     "rendered":"<p>testxxxxxx<\/p>\n",
 *     "protected":false,
 *     "block_version":0
 *   },
 *   "excerpt":{
 *     "raw":"",
 *     "rendered":"<p>testxxxxxx<\/p>\n",
 *     "protected":false
 *   },
 *   "author":1,
 *   "featured_media":0,
 *   "comment_status":"open",
 *   "ping_status":"open",
 *   "sticky":false,
 *   "template":"",
 *   "format":"standard",
 *   "meta":{
 *     "_acf_changed":false,
 *     "footnotes":""
 *   },
 *   "categories":[
 *     1
 *   ],
 *   "tags":[
 *
 *   ],
 *   "permalink_template":"https:\/\/wordpress.ole12138.cn\/archives\/31",
 *   "generated_slug":"testabc-4",
 *   "acf":{
 *     "md-uuid":"c0e2cd4a-887e-41eb-8b3e-0113a69b7d1d"
 *   },
 *   "_links":{
 *     "self":[
 *       {
 *         "href":"https:\/\/wordpress.ole12138.cn\/wp-json\/wp\/v2\/posts\/31"
 *       }
 *     ],
 *     "collection":[
 *       {
 *         "href":"https:\/\/wordpress.ole12138.cn\/wp-json\/wp\/v2\/posts"
 *       }
 *     ],
 *     "about":[
 *       {
 *         "href":"https:\/\/wordpress.ole12138.cn\/wp-json\/wp\/v2\/types\/post"
 *       }
 *     ],
 *     "author":[
 *       {
 *         "embeddable":true,
 *         "href":"https:\/\/wordpress.ole12138.cn\/wp-json\/wp\/v2\/users\/1"
 *       }
 *     ],
 *     "replies":[
 *       {
 *         "embeddable":true,
 *         "href":"https:\/\/wordpress.ole12138.cn\/wp-json\/wp\/v2\/comments?post=31"
 *       }
 *     ],
 *     "version-history":[
 *       {
 *         "count":0,
 *         "href":"https:\/\/wordpress.ole12138.cn\/wp-json\/wp\/v2\/posts\/31\/revisions"
 *       }
 *     ],
 *     "wp:attachment":[
 *       {
 *         "href":"https:\/\/wordpress.ole12138.cn\/wp-json\/wp\/v2\/media?parent=31"
 *       }
 *     ],
 *     "wp:term":[
 *       {
 *         "taxonomy":"category",
 *         "embeddable":true,
 *         "href":"https:\/\/wordpress.ole12138.cn\/wp-json\/wp\/v2\/categories?post=31"
 *       },
 *       {
 *         "taxonomy":"post_tag",
 *         "embeddable":true,
 *         "href":"https:\/\/wordpress.ole12138.cn\/wp-json\/wp\/v2\/tags?post=31"
 *       }
 *     ],
 *     "wp:action-publish":[
 *       {
 *         "href":"https:\/\/wordpress.ole12138.cn\/wp-json\/wp\/v2\/posts\/31"
 *       }
 *     ],
 *     "wp:action-unfiltered-html":[
 *       {
 *         "href":"https:\/\/wordpress.ole12138.cn\/wp-json\/wp\/v2\/posts\/31"
 *       }
 *     ],
 *     "wp:action-sticky":[
 *       {
 *         "href":"https:\/\/wordpress.ole12138.cn\/wp-json\/wp\/v2\/posts\/31"
 *       }
 *     ],
 *     "wp:action-assign-author":[
 *       {
 *         "href":"https:\/\/wordpress.ole12138.cn\/wp-json\/wp\/v2\/posts\/31"
 *       }
 *     ],
 *     "wp:action-create-categories":[
 *       {
 *         "href":"https:\/\/wordpress.ole12138.cn\/wp-json\/wp\/v2\/posts\/31"
 *       }
 *     ],
 *     "wp:action-assign-categories":[
 *       {
 *         "href":"https:\/\/wordpress.ole12138.cn\/wp-json\/wp\/v2\/posts\/31"
 *       }
 *     ],
 *     "wp:action-create-tags":[
 *       {
 *         "href":"https:\/\/wordpress.ole12138.cn\/wp-json\/wp\/v2\/posts\/31"
 *       }
 *     ],
 *     "wp:action-assign-tags":[
 *       {
 *         "href":"https:\/\/wordpress.ole12138.cn\/wp-json\/wp\/v2\/posts\/31"
 *       }
 *     ],
 *     "curies":[
 *       {
 *         "name":"wp",
 *         "href":"https:\/\/api.w.org\/{rel}",
 *         "templated":true
 *       }
 *     ]
 *   }
 * }
 * </pre>
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuppressWarnings("unused")
public class Post {

    
    private com.jingmin.blog.upload.wordpress.model._links _links;
    
    private Acf acf;
    
    private Long author;
    
    private List<Long> categories;
    
    private String commentStatus;
    
    private Content content;
    
    private String date;
    
    private String dateGmt;
    
    private Excerpt excerpt;
    
    private Long featuredMedia;
    
    private String format;
    
    private String generatedSlug;
    
    private Guid guid;
    
    private Long id;
    
    private String link;
    
    private Meta meta;
    
    private String modified;
    
    private String modifiedGmt;
    
    private String password;
    
    private String permalinkTemplate;
    
    private String pingStatus;
    
    private String slug;
    
    private String status;
    
    private Boolean sticky;
    
    private List<Object> tags;
    
    private String template;
    
    private Title title;
    
    private String type;

    public com.jingmin.blog.upload.wordpress.model._links get_links() {
        return _links;
    }

    public void set_links(com.jingmin.blog.upload.wordpress.model._links _links) {
        this._links = _links;
    }

    public Acf getAcf() {
        return acf;
    }

    public void setAcf(Acf acf) {
        this.acf = acf;
    }

    public Long getAuthor() {
        return author;
    }

    public void setAuthor(Long author) {
        this.author = author;
    }

    public List<Long> getCategories() {
        return categories;
    }

    public void setCategories(List<Long> categories) {
        this.categories = categories;
    }

    public String getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateGmt() {
        return dateGmt;
    }

    public void setDateGmt(String dateGmt) {
        this.dateGmt = dateGmt;
    }

    public Excerpt getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(Excerpt excerpt) {
        this.excerpt = excerpt;
    }

    public Long getFeaturedMedia() {
        return featuredMedia;
    }

    public void setFeaturedMedia(Long featuredMedia) {
        this.featuredMedia = featuredMedia;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getGeneratedSlug() {
        return generatedSlug;
    }

    public void setGeneratedSlug(String generatedSlug) {
        this.generatedSlug = generatedSlug;
    }

    public Guid getGuid() {
        return guid;
    }

    public void setGuid(Guid guid) {
        this.guid = guid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getModifiedGmt() {
        return modifiedGmt;
    }

    public void setModifiedGmt(String modifiedGmt) {
        this.modifiedGmt = modifiedGmt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPermalinkTemplate() {
        return permalinkTemplate;
    }

    public void setPermalinkTemplate(String permalinkTemplate) {
        this.permalinkTemplate = permalinkTemplate;
    }

    public String getPingStatus() {
        return pingStatus;
    }

    public void setPingStatus(String pingStatus) {
        this.pingStatus = pingStatus;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getSticky() {
        return sticky;
    }

    public void setSticky(Boolean sticky) {
        this.sticky = sticky;
    }

    public List<Object> getTags() {
        return tags;
    }

    public void setTags(List<Object> tags) {
        this.tags = tags;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
