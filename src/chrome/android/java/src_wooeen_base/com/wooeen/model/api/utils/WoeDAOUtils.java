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
import com.wooeen.utils.DatetimeUtils;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class WoeDAOUtils {

//    public static String BASE_URL = "http://192.168.0.108:8080/pinup-wooeen-app/";
//    public static String SCHEMA = "http";
//    public static String AUTHORITY = "192.168.0.111:8080";
//    public static String PATH = "pinup-wooeen-app/api/";


    public static String BASE_URL = "https://claro.wwwd.com.br/";
    public static String SCHEMA = "https";
    public static String AUTHORITY = "claro.wwwd.com.br";
    public static String PATH = "api/";

    public static String APP_ID = "bb63712ac7ac4bc8a6a746b0ace643a0";
    public static String APP_TOKEN = "Gpk8PYyA9oZQ07XHFbsudr";

    public static Gson getGson(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss Z")
                .registerTypeAdapter(Date.class, new DateSerializer())
                .registerTypeAdapter(Date.class, new DateDeserializer())
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

    private static class DateDeserializer implements JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            if(json == null || json.isJsonNull())
                return null;
            Date d = DatetimeUtils.stringToDate(json.getAsString(), "yyyy-MM-dd HH:mm:ss Z");
            if(d != null)
                return d;
            d = DatetimeUtils.stringToDate(json.getAsString(), "yyyy-MM-dd HH:mm:ss");
            if(d != null)
                return d;
            d = DatetimeUtils.stringToDate(json.getAsString(), "yyyy-MM-dd");
            if(d != null)
                return d;
            long l = json.getAsLong();
            if(l > 0)
                return new Date(l);

            return null;
        }
    }

}
