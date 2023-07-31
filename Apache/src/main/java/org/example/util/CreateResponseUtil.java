package org.example.util;

import org.example.model.Response;

public class CreateResponseUtil {
    public static <T> Response<T> createResponse(T obj, String message, String code){
        Response<T> response = new Response<T>();
        response.setResponseObject(obj);
        response.setResponseCode(code);
        response.setResponseMessage(message);
        return response;
    }
}
