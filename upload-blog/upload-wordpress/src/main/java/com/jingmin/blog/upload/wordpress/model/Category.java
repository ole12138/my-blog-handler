
package com.jingmin.blog.upload.wordpress.model;

import lombok.ToString;

import java.util.List;



@ToString
@SuppressWarnings("unused")
public class Category {

    
    private com.jingmin.blog.upload.wordpress.model._links _links;
    
    private List<Object> acf;
    
    private Long count;
    
    private String description;
    
    private Long id;
    
    private String link;
    
    private List<Object> meta;
    
    private String name;
    
    private Long parent;
    
    private String slug;
    
    private String taxonomy;

    public com.jingmin.blog.upload.wordpress.model._links get_links() {
        return _links;
    }

    public void set_links(com.jingmin.blog.upload.wordpress.model._links _links) {
        this._links = _links;
    }

    public List<Object> getAcf() {
        return acf;
    }

    public void setAcf(List<Object> acf) {
        this.acf = acf;
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

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
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
