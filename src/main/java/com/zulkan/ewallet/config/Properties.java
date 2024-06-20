package com.zulkan.ewallet.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("config")
@Getter
@Setter
public class Properties {
    private String transferTopic;
    private String balanceTopupTopic;

}
