package com.khwish.backend.notifications;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.khwish.backend.constants.Constants;
import com.khwish.backend.models.UserBankDetails;
import com.khwish.backend.restapi.RestApiManager;

import java.io.Serializable;

public class SlackMessage {

    private static final String WEBHOOK_URL = "############################################";
    private static final String WITHDRAWAL_REQUEST_USERNAME = "Withdrawal Request";
    private static final String WITHDRAWAL_REQUEST_EMOJI = ":dollar:";

    public static void sendWithdrawalRequestMessage(String withdrawalMsg) {
        sendSlackMessage(Constants.getProfileConfig().getWithdrawalRequestsSlackChannel(), WITHDRAWAL_REQUEST_USERNAME, withdrawalMsg,
                WITHDRAWAL_REQUEST_EMOJI);
    }

    private static void sendSlackMessage(String channel, String userName, String text, String iconEmoji) {
        SlackMessageRequest slackMsg = new SlackMessageRequest(channel, userName, text, iconEmoji);
        RestApiManager.post(WEBHOOK_URL, slackMsg, String.class);
    }

    private static class SlackMessageRequest implements Serializable {

        @JsonProperty("channel")
        private String channel;

        @JsonProperty("username")
        private String userName;

        @JsonProperty("text")
        private String text;

        @JsonProperty("icon_emoji")
        private String iconEmoji;

        public SlackMessageRequest(String channel, String userName, String text, String iconEmoji) {
            this.channel = channel;
            this.userName = userName;
            this.text = text;
            this.iconEmoji = iconEmoji;
        }
    }
}
