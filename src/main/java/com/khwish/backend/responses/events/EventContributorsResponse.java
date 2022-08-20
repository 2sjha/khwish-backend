package com.khwish.backend.responses.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.khwish.backend.responses.base.BaseResponse;

import java.util.ArrayList;

public class EventContributorsResponse extends BaseResponse {

    @JsonProperty("contributions")
    private ArrayList<ContributionResponse> contributions;

    public EventContributorsResponse(Boolean success, String message, int statusCode, ArrayList<ContributionResponse> contributions) {
        super(success, message, statusCode);
        this.contributions = contributions;
    }

    public static class ContributionResponse {

        @JsonProperty("contributor_name")
        private String contributorName;

        @JsonProperty("contribution_amount")
        private Double contributionAmount;

        @JsonProperty("goal_name")
        private String goalName;

        @JsonProperty("created_at")
        private Long createdAt;

        public ContributionResponse(String contributorName, Double contributionAmount, String goalName, Long createdAt) {
            this.contributorName = contributorName;
            this.contributionAmount = contributionAmount;
            this.goalName = goalName;
            this.createdAt = createdAt;
        }
    }
}