package com.khwish.backend.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ParsingUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJson(String jsonStr, Class<T> tClass) {
        try {
            return objectMapper.readValue(jsonStr, tClass);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}