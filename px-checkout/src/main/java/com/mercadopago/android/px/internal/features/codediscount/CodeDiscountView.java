package com.mercadopago.android.px.internal.features.codediscount;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.model.Discount;

public interface CodeDiscountView extends MvpView {

    void processSuccess(@NonNull final Discount discount);

    void processCodeError();
}
