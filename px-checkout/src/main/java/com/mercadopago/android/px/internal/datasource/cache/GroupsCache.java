package com.mercadopago.android.px.internal.datasource.cache;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.internal.callbacks.MPCall;

public interface GroupsCache {

    @NonNull
    MPCall<PaymentMethodSearch> get();

    void put(@NonNull final PaymentMethodSearch groups);

    void evict();

    boolean isCached();
}
