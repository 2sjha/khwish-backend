package com.khwish.backend.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AuthEndpoints {

    //all endpoints which do not require user-id & auth-token authentication.
    public static ArrayList<String> AUTH_DISABLED_ENDPOINTS = new ArrayList<>();

    //all endpoints which require client-id & service-key authentication.
    public static ArrayList<String> CISK_AUTH_ENDPOINTS = new ArrayList<>();

    static List<Pattern> AUTH_DISABLED_ENDPOINT_PATTERNS = new ArrayList<>();
    static List<Pattern> CISK_AUTH_ENDPOINT_PATTERNS = new ArrayList<>();

    static {
        AUTH_DISABLED_ENDPOINT_PATTERNS.add(Pattern.compile("/user/login.*"));
        AUTH_DISABLED_ENDPOINT_PATTERNS.add(Pattern.compile("/"));
        AUTH_DISABLED_ENDPOINT_PATTERNS.add(Pattern.compile("/error"));
        AUTH_DISABLED_ENDPOINT_PATTERNS.add(Pattern.compile("/gift.*"));

        CISK_AUTH_ENDPOINT_PATTERNS.add(Pattern.compile("/web.*"));
    }
}