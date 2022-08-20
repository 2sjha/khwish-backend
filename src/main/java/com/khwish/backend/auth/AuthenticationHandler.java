package com.khwish.backend.auth;

import com.khwish.backend.constants.ResponseStrings;
import com.khwish.backend.models.AuthToken;
import com.khwish.backend.repositories.AuthTokenRepository;
import com.khwish.backend.responses.base.BaseResponse;
import com.khwish.backend.utils.DateTimeUtil;
import com.khwish.backend.utils.ParsingUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class AuthenticationHandler extends HandlerInterceptorAdapter {

    private final String classTag = this.getClass().getSimpleName();
    private Logger LOGGER = LogManager.getLogger(this.getClass());
    private AuthTokenRepository authRepo;

    @Autowired
    public AuthenticationHandler(AuthTokenRepository authRepo) {
        this.authRepo = authRepo;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        String requestUri = request.getRequestURI();
        /* Check if current request needs Client-Id & Service-Key Auth, by URI in CISK_ENABLED_ENDPOINTS
           then check if the current request needs User-id & Auth-token Auth, by URI not in AUTH_DISABLED_ENDPOINTS
         */
        if (AuthEndpoints.CISK_AUTH_ENDPOINTS.contains(requestUri)) {
            String clientId = request.getHeader("client-id");
            String serviceKey = request.getHeader("service-key");

            if (clientId == null || clientId.equals("") || serviceKey == null || serviceKey.equals("")) {
                LOGGER.log(Level.ERROR, "[" + classTag + "][preHandle] Empty headers.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String unauthorizedJson = ParsingUtil.toJson(new BaseResponse(false,
                        ResponseStrings.UNAUTHORIZED, HttpServletResponse.SC_UNAUTHORIZED));
                response.getWriter().write(unauthorizedJson);
                return false;
            }

            if (serviceKey.equals(AuthConstants.CLIENT_ID_SERVICE_KEY_MAP.get(clientId))) {
                return true;
            } else {
                LOGGER.log(Level.ERROR, "[" + classTag + "][preHandle] Wrong CISK Pair. clientId: "
                        + clientId + " serviceKey: " + serviceKey);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String unauthorizedJson = ParsingUtil.toJson(new BaseResponse(false,
                        ResponseStrings.UNAUTHORIZED, HttpServletResponse.SC_UNAUTHORIZED));
                response.getWriter().write(unauthorizedJson);

                return false;
            }
        } else if (!AuthEndpoints.AUTH_DISABLED_ENDPOINTS.contains(requestUri)) {
            String userIdStr = request.getHeader("user-id");
            String authTokenStr = request.getHeader("auth-token");
            String appVersion = request.getHeader("app-version");
            if (StringUtils.isEmpty(userIdStr) || StringUtils.isEmpty(authTokenStr) || StringUtils.isEmpty(appVersion)) {
                LOGGER.log(Level.ERROR, "[" + classTag + "][preHandle] Empty headers.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String unauthorizedJson = ParsingUtil.toJson(new BaseResponse(false,
                        ResponseStrings.UNAUTHORIZED, HttpServletResponse.SC_UNAUTHORIZED));
                response.getWriter().write(unauthorizedJson);

                return false;
            }

            UUID userId;
            try {
                userId = UUID.fromString(userIdStr);
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, "[" + classTag + "][preHandle] Error: " + e.toString());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String unauthorizedJson = ParsingUtil.toJson(new BaseResponse(false,
                        ResponseStrings.UNAUTHORIZED, HttpServletResponse.SC_UNAUTHORIZED));
                response.getWriter().write(unauthorizedJson);

                return false;
            }

            AuthToken authToken = authRepo.findByUserId(userId);
            if (authToken == null || !authToken.getToken().equals(authTokenStr)) {
                LOGGER.log(Level.ERROR, "[" + classTag + "][preHandle] Wrong authToken: "
                        + authTokenStr + " used for userId: " + userIdStr);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                String unauthorizedJson = ParsingUtil.toJson(new BaseResponse(false,
                        ResponseStrings.UNAUTHORIZED, HttpServletResponse.SC_UNAUTHORIZED));
                response.getWriter().write(unauthorizedJson);

                return false;
            } else if (authToken.getExpiresAt() < DateTimeUtil.getCurrentEpoch()) {
                // If Auth-token has expired, extend expiry for the token
                Long newExpiryTime = DateTimeUtil.getCurrentEpoch() + AuthConstants.AUTH_VALIDITY_TIME;
                LOGGER.log(Level.INFO, "[" + classTag + "][preHandle] AuthToken: " + authToken.getToken()
                        + " expired, setting new expiry: " + newExpiryTime);
                authToken.setExpiresAt(newExpiryTime);
                authRepo.save(authToken);

                request.setAttribute("user-id", userId);
                return true;
            } else if (authToken.getToken().equals(authTokenStr)
                    && authToken.getExpiresAt() > DateTimeUtil.getCurrentEpoch()) {
                request.setAttribute("user-id", userId);
                return true;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        long startTime = (Long) request.getAttribute("startTime");
        request.removeAttribute("startTime");

        long endTime = System.currentTimeMillis();

        if (!request.getRequestURI().equals("/")) {
            LOGGER.log(Level.INFO, "[" + classTag + "][postHandle] Request: " + request.getRequestURI() +
                    " Processing Time :: " + (endTime - startTime) + "ms");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception exceptionIfAny) {
    }
}