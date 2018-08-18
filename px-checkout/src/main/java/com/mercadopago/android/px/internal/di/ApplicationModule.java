package com.mercadopago.android.px.internal.di;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.datasource.cache.FileManager;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.internal.util.RetrofitUtil;
import java.io.File;
import retrofit2.Retrofit;

class ApplicationModule implements PreferenceComponent {

    @NonNull
    private final Context context;

    /* default */ ApplicationModule(@NonNull final Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    public Context getContext() {
        return context;
    }

    @Override
    public SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences("com.mercadopago.checkout.store", Context.MODE_PRIVATE);
    }

    /* default */ JsonUtil getJsonUtil() {
        return JsonUtil.getInstance();
    }

    /* default */ FileManager getFileManager() {
        return new FileManager();
    }

    /* default */ File getCacheDir() {
        return context.getCacheDir();
    }

    public Retrofit getRetrofitClient() {
        return RetrofitUtil.getRetrofitClient(context);
    }
}
