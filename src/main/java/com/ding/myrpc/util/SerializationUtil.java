package com.ding.myrpc.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class SerializationUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T> byte[] serialize(T t) {
        try {
            return objectMapper.writeValueAsBytes(t);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T deserialize(byte[] bytes, Class<T> cls) {
        try {
            return objectMapper.readValue(bytes, cls);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
