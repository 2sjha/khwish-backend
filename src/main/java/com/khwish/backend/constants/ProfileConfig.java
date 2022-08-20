package com.khwish.backend.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProfileConfig {

    @Value("${spring.profiles.active:staging}")
    private String env;

    @Value("${instamojo.base.url:baseUrl}")
    private String instamojoBaseUrl;

    @Value("${instamojo.client.id:clientId}")
    private String instamojoClientId;

    @Value("${instamojo.client.secret:clientSecret}")
    private String instamojoClientSecret;

    @Value("${khwish.backend.base.url:baseUrl}")
    private String khwishBackendBaseUrl;

    @Value("${khwish.web.base.url:baseUrl}")
    private String khwishWebBaseUrl;

    @Value("${khwish.withdrawal.requests.slack.channel:slackChannel}")
    private String withdrawalRequestsSlackChannel;

    @Value("${khwish.withdrawal.requests.spreadsheet.id:spreadsheetId}")
    private String withdrawalRequestsSpreadsheetId;

    public String getEnv() {
        return env;
    }

    public String getInstamojoBaseUrl() {
        return instamojoBaseUrl;
    }

    public String getInstamojoClientId() {
        return instamojoClientId;
    }

    public String getInstamojoClientSecret() {
        return instamojoClientSecret;
    }

    public String getKhwishBackendBaseUrl() {
        return khwishBackendBaseUrl;
    }

    public String getKhwishWebBaseUrl() {
        return khwishWebBaseUrl;
    }

    public String getWithdrawalRequestsSlackChannel() {
        return withdrawalRequestsSlackChannel;
    }

    public String getWithdrawalRequestsSpreadsheetId() {
        return withdrawalRequestsSpreadsheetId;
    }
}
