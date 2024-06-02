
package com.jingmin.blog.upload.wordpress.model;





@SuppressWarnings("unused")
public class WpTerm {

    
    private Boolean embeddable;
    
    private String href;
    
    private String taxonomy;

    public Boolean getEmbeddable() {
        return embeddable;
    }

    public void setEmbeddable(Boolean embeddable) {
        this.embeddable = embeddable;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getTaxonomy() {
        return taxonomy;
    }

    public void setTaxonomy(String taxonomy) {
        this.taxonomy = taxonomy;
    }

}
