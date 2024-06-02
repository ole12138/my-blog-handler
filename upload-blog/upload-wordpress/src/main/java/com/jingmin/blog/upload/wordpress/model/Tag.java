
package com.jingmin.blog.upload.wordpress.model;

import java.util.List;




@SuppressWarnings("unused")
public class Tag {

    
    private com.jingmin.blog.upload.wordpress.model._links _links;
    
    private Long count;
    
    private String description;
    
    private Long id;
    
    private String link;
    
    private List<Object> meta;
    
    private String name;
    
    private String slug;
    
    private String taxonomy;

    public com.jingmin.blog.upload.wordpress.model._links get_links() {
        return _links;
    }

    public void set_links(com.jingmin.blog.upload.wordpress.model._links _links) {
        this._links = _links;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<Object> getMeta() {
        return meta;
    }

    public void setMeta(List<Object> meta) {
        this.meta = meta;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(String taxonomy) {
        this.taxonomy = taxonomy;
    }

}
