package com.mercadopago.android.px.callbacks;

import android.support.annotation.NonNull;

import com.mercadopago.android.px.model.Discount;

public interface OnCodeDiscountCallback {
    void onSuccess(@NonNull final Discount discount);

    void onFailure();
}
