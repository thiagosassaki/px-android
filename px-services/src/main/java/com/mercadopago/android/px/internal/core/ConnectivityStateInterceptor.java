package com.mercadopago.android.px.internal.core;

import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.internal.util.NoConnectivityException;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;

public class ConnectivityStateInterceptor implements Interceptor {

    @Nullable
    private final ConnectivityManager connectivityManager;

    public ConnectivityStateInterceptor(@Nullable final ConnectivityManager connectivityManager) {
        this.connectivityManager = connectivityManager;
    }

    @SuppressLint("MissingPermission")
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        if (connectivityManager != null && (connectivityManager.getActiveNetworkInfo() == null
            || !connectivityManager.getActiveNetworkInfo().isAvailable()
            || !connectivityManager.getActiveNetworkInfo().isConnected())) {
            throw new NoConnectivityException();
        } else {
            return chain.proceed(chain.request());
        }
    }
}
