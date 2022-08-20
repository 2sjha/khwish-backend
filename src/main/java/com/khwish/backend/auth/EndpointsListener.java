package com.khwish.backend.auth;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class EndpointsListener implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(@Nonnull ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            ApplicationContext applicationContext = ((ContextRefreshedEvent) event).getApplicationContext();
            Map<RequestMappingInfo, HandlerMethod> reqHandlerMaps = applicationContext.getBean(RequestMappingHandlerMapping.class).getHandlerMethods();

            for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : reqHandlerMaps.entrySet()) {
                for (String reqPattern : entry.getKey().getPatternsCondition().getPatterns()) {
                    for (Pattern disabledPattern : AuthEndpoints.AUTH_DISABLED_ENDPOINT_PATTERNS) {
                        if (disabledPattern.matcher(reqPattern).matches()) {
                            AuthEndpoints.AUTH_DISABLED_ENDPOINTS.add(reqPattern);
                        }
                    }

                    for (Pattern ciskPattern : AuthEndpoints.CISK_AUTH_ENDPOINT_PATTERNS) {
                        if (ciskPattern.matcher(reqPattern).matches()) {
                            AuthEndpoints.CISK_AUTH_ENDPOINTS.add(reqPattern);
                        }
                    }
                }
            }
        }
    }
}