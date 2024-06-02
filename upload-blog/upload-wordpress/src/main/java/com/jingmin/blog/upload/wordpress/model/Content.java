
package com.jingmin.blog.upload.wordpress.model;






@SuppressWarnings("unused")
public class Content {

    
    private Long blockVersion;
    // 
    // private Boolean protected;
    
    private String raw;
    
    private String rendered;

    public Long getBlockVersion() {
        return blockVersion;
    }

    public void setBlockVersion(Long blockVersion) {
        this.blockVersion = blockVersion;
    }

    // public Boolean getProtected() {
    //     return protected;
    // }
    //
    // public void setProtected(Boolean protected) {
    //     this.protected = protected;
    // }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getRendered() {
        return rendered;
    }

    public void setRendered(String rendered) {
        this.rendered = rendered;
    }

}
