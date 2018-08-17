package com.mercadopago.android.px.utils;

import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.services.Callback;

public class StubSuccessMpCall<T> implements MPCall<T> {

    private final T value;

    public StubSuccessMpCall(T value) {
        this.value = value;
    }

    @Override
    public void enqueue(final Callback<T> callback) {
        callback.success(value);
    }

    @Override
    public void execute(final Callback<T> callback) {
        callback.success(value);
    }
}
