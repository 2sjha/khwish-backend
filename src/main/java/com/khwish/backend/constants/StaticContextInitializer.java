package com.khwish.backend.constants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class StaticContextInitializer {

    @Autowired
    private ProfileConfig profileConfig;

    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void init() {
        Constants.setProfileConfig(profileConfig);
    }
}

