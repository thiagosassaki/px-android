package com.mercadopago.android.px.internal.callbacks;

import com.mercadopago.android.px.services.Callback;

public interface MPCall<T> {

    void enqueue(Callback<T> callback);

    void execute(Callback<T> callback);
}

