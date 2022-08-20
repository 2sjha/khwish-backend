package com.khwish.backend.instamojo;

import com.khwish.backend.constants.Constants;
import com.khwish.backend.constants.ResponseStrings;
import com.khwish.backend.models.Goal;
import com.khwish.backend.repositories.GoalRepository;
import com.khwish.backend.requests.instamojo.CreatePaymentRequest;
import com.khwish.backend.responses.base.BaseResponse;
import com.khwish.backend.responses.web.PaymentLinkResponse;
import com.khwish.backend.restapi.RestApiManager;
import com.khwish.backend.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Optional;

@Service
public class InstamojoService {

    private boolean tokenExpired = true;
    private Long tokenExpiryTime = DateTimeUtil.getCurrentEpoch();
    private String accessToken;

    private final GoalRepository goalRepo;

    @Autowired
    public InstamojoService(GoalRepository goalRepo) {
        this.goalRepo = goalRepo;
    }

    public BaseResponse createPaymentRequest(CreatePaymentRequest createPaymentRequest) {

        accessToken = getAccessToken(false);
        if (accessToken == null) {
            return new BaseResponse(false, ResponseStrings.OOPS, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        if (createPaymentRequest == null || StringUtils.isEmpty(createPaymentRequest.getGoalId())
                || createPaymentRequest.getAmount() < InstamojoConstants.MIN_PAYMENT_AMOUNT) {
            return new BaseResponse(false, ResponseStrings.BAD_REQUEST, HttpServletResponse.SC_BAD_REQUEST);
        }

        Optional<Goal> foundGoal = goalRepo.findById(createPaymentRequest.getGoalId());
        if (foundGoal.isPresent()) {
            Goal goal = foundGoal.get();

            HashMap<String, String> bodyMap = new HashMap<>();
            bodyMap.put("amount", String.valueOf(createPaymentRequest.getAmount()));
            bodyMap.put("purpose", getPurposeFromGoal(goal));
            bodyMap.put("webhook", Constants.getProfileConfig().getKhwishBackendBaseUrl() + Constants.BACKEND_INSTAMOJO_WEBHOOK_PATH);
            bodyMap.put("redirect_url", Constants.getProfileConfig().getKhwishWebBaseUrl() + Constants.WEB_THANKS_URL + goal.getId());
            bodyMap.put("allow_repeated_payments", String.valueOf(false));

            PaymentResponse paymentResponse;
            try {
                paymentResponse = RestApiManager.postOAuthForm(Constants.getProfileConfig().getInstamojoBaseUrl()
                        + InstamojoConstants.PAYMENT_REQUEST_PATH, accessToken, bodyMap, PaymentResponse.class);
            } catch (HttpClientErrorException.Unauthorized unauthorized) {
                accessToken = getAccessToken(true);

                try {
                    paymentResponse = RestApiManager.postOAuthForm(Constants.getProfileConfig().getInstamojoBaseUrl()
                            + InstamojoConstants.PAYMENT_REQUEST_PATH, accessToken, bodyMap, PaymentResponse.class);
                } catch (HttpClientErrorException.Unauthorized unauthorized2) {
                    paymentResponse = null;
                }
            }

            if (paymentResponse != null) {
                return new PaymentLinkResponse(true, ResponseStrings.PAYMENT_REQUEST_CREATED,
                        HttpServletResponse.SC_OK, paymentResponse.getLongurl());
            } else {
                return new BaseResponse(false, ResponseStrings.OOPS, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            return new BaseResponse(false, ResponseStrings.BAD_REQUEST, HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private String getAccessToken(boolean enforced) {
        if (enforced || tokenExpired || DateTimeUtil.getCurrentEpoch() > tokenExpiryTime) {
            HashMap<String, String> bodyMap = new HashMap<>();
            bodyMap.put("grant_type", "client_credentials");
            bodyMap.put("client_id", Constants.getProfileConfig().getInstamojoClientId());
            bodyMap.put("client_secret", Constants.getProfileConfig().getInstamojoClientSecret());
            try {
                OAuthResponse oAuthResponse = RestApiManager.postForm(Constants.getProfileConfig().getInstamojoBaseUrl()
                        + InstamojoConstants.OAUTH_TOKEN_PATH, bodyMap, OAuthResponse.class);
                if (oAuthResponse != null) {
                    accessToken = oAuthResponse.getAccessToken();
                    tokenExpired = false;
                    tokenExpiryTime = DateTimeUtil.getCurrentEpoch() + oAuthResponse.getExpiresIn();

                    return accessToken;
                } else {
                    return null;
                }
            } catch (HttpClientErrorException.Unauthorized e) {
                accessToken = null;
                tokenExpired = true;
                return null;
            }
        } else {
            return accessToken;
        }
    }

    private String getPurposeFromGoal(Goal goal) {
        return goal.getName() + "(" + goal.getId() + ")";
    }
}
