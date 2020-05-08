package com.whut.srms.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "srms.custominfoheader")
public class CustomInfoProperties {

    String ErrorHeader;
}
