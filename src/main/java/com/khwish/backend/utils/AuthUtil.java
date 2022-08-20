package com.khwish.backend.utils;

import com.khwish.backend.auth.AuthConstants;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class AuthUtil {

    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe

    public static String generateNewToken() {
        byte[] randomBytes = new byte[AuthConstants.AUTH_TOKEN_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}