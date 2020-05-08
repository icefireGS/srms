package com.whut.srms.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "srms.urlheader")
public class UrlProperties {
    private String tracker1;
    private String tracker2;
    private String tracker1_group_name;
    private String tracker2_group_name;
}
