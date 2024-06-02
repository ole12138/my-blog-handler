
package com.jingmin.blog.upload.wordpress.model;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@SuppressWarnings("unused")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Acf {

    
    private String mdUuid;

    public String getMdUuid() {
        return mdUuid;
    }

    public void setMdUuid(String mdUuid) {
        this.mdUuid = mdUuid;
    }

}
