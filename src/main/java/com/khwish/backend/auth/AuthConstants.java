package com.khwish.backend.auth;

import java.util.HashMap;

public class AuthConstants {

    public static final Long AUTH_VALIDITY_TIME = 15 * 24 * 60 * 60L; //15 days in seconds
    public static final int AUTH_TOKEN_LENGTH = 24;

    public static HashMap<String, String> CLIENT_ID_SERVICE_KEY_MAP = new HashMap<>();

    static {
        CLIENT_ID_SERVICE_KEY_MAP.put("gift-web", "####################");
    }
}