package com.mercadopago.android.px.internal.util;

import android.content.Context;
import com.mercadopago.android.px.internal.adapters.ErrorHandlingCallAdapter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitUtil {

    private static final String MP_API_BASE_URL = "https://api.mercadopago.com";

    private static final int DEFAULT_CONNECT_TIMEOUT = 10;
    private static final int DEFAULT_READ_TIMEOUT = 20;
    private static final int DEFAULT_WRITE_TIMEOUT = 20;

    private RetrofitUtil() {
    }

    public static Retrofit getRetrofitClient(final Context context) {
        return getRetrofit(context, MP_API_BASE_URL, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT,
            DEFAULT_WRITE_TIMEOUT);
    }

    private static Retrofit getRetrofit(final Context mContext,
        final String baseUrl,
        final int connectTimeout,
        final int readTimeout,
        final int writeTimeout) {

        return new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(JsonUtil.getInstance().getGson()))
            .client(HttpClientUtil.getClient(mContext, connectTimeout, readTimeout, writeTimeout))
            .addCallAdapterFactory(new ErrorHandlingCallAdapter.ErrorHandlingCallAdapterFactory())
            .build();
    }
}
