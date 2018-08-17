package com.mercadopago.android.px.internal.features.providers;

import com.mercadopago.android.px.internal.base.ResourcesProvider;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.model.Discount;

public interface DiscountsProvider extends ResourcesProvider {
    void getDirectDiscount(String transactionAmount, String payerEmail, TaggedCallback<Discount> taggedCallback);

    void getCodeDiscount(String transactionAmount, String payerEmail, String discountCode,
        TaggedCallback<Discount> taggedCallback);

    String getApiErrorMessage(String error);

    String getStandardErrorMessage();
}
