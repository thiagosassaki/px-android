package com.mercadopago.android.px.internal.datasource.cache;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.services.Callback;
import com.mercadopago.android.px.model.exceptions.ApiException;

public class GroupsMemCache implements GroupsCache {

    private PaymentMethodSearch groups;

    @NonNull
    @Override
    public MPCall<PaymentMethodSearch> get() {
        return new MPCall<PaymentMethodSearch>() {
            @Override
            public void enqueue(final Callback<PaymentMethodSearch> callback) {
                resolve(callback);
            }

            @Override
            public void execute(final Callback<PaymentMethodSearch> callback) {
                resolve(callback);
            }
        };
    }

    /* default */ void resolve(final Callback<PaymentMethodSearch> callback) {
        if (isCached()) {
            callback.success(groups);
        } else {
            callback.failure(new ApiException());
        }
    }

    @Override
    public void put(@NonNull final PaymentMethodSearch groups) {
        this.groups = groups;
    }

    @Override
    public void evict() {
        groups = null;
    }

    @Override
    public boolean isCached() {
        return groups != null;
    }
}
