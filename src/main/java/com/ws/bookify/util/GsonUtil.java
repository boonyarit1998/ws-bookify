package com.ws.bookify.util;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public final class GsonUtil {

    private static Gson gson;

    @Autowired
    public GsonUtil(Gson gson) {
        GsonUtil.gson = gson;
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static String toJson(List<?> objList) {
        return gson.toJson(objList);
    }

}
