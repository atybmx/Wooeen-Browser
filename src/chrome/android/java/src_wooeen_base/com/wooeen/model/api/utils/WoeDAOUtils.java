package com.wooeen.model.api.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class WoeDAOUtils {

    // public static String BASE_URL = "http://192.168.0.113:8080/pinup-wooeen-app/";
    // public static String SCHEMA = "http";
    // public static String AUTHORITY = "192.168.0.113:8080";
    // public static String PATH = "pinup-wooeen-app/api/";


   public static String BASE_URL = "https://app.wooeen.com/";
   public static String SCHEMA = "https";
   public static String AUTHORITY = "api.wooeen.com";
   public static String PATH = "api/";

    public static String APP_ID = "bb63712ac7ac4bc8a6a746b0ace643a0";
    public static String APP_TOKEN = "Gpk8PYyA9oZQ07XHFbsudr";

    public static Gson getGson(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss Z")
                .registerTypeAdapter(Date.class, new DateSerializer())
                .create();

        return gson;
    }

    private static class DateSerializer implements JsonSerializer<Date> {

        @Override
        public JsonElement serialize(Date src, Type type,
                JsonSerializationContext jsonSerializationContext) {
            return src == null ? null : new JsonPrimitive(src.getTime());
        }
    }

}
