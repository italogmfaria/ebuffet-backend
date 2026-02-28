package com.ebuffet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SingleBuffetProperties {

    @Value("${ebuffet.buffet-id}")
    private Long buffetId;

    public Long getBuffetId() {
        return buffetId;
    }
}
