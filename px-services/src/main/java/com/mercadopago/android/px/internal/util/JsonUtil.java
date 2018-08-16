package com.mercadopago.android.px.internal.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;

public final class JsonUtil {

    private static JsonUtil mInstance = null;
    private final Gson mGson;

    private JsonUtil() {
        mGson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).serializeNulls()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
    }

    public static JsonUtil getInstance() {
        if (mInstance == null) {
            mInstance = new JsonUtil();
        }
        return mInstance;
    }

    public <T> T fromJson(final String json, final Class<T> classOfT) {

        return mGson.fromJson(json, classOfT);
    }


    public <T> T fromJson(final String json, final Type classOfT) {
        return mGson.fromJson(json, classOfT);
    }

    public String toJson(final Object src) {

        return mGson.toJson(src);
    }

    public Gson getGson() {

        return mGson;
    }

}
