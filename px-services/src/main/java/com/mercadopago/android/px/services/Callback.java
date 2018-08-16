package com.mercadopago.android.px.services;

import com.mercadopago.android.px.model.exceptions.ApiException;

public abstract class Callback<T> {

    public int attempts = 0;

    /**
     * Called for [200, 300) responses.
     */
    public abstract void success(T t);

    /**
     * Called for all errors.
     */
    public abstract void failure(ApiException apiException);
}
