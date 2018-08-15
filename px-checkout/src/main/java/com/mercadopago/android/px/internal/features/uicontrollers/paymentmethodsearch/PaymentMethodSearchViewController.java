package com.mercadopago.android.px.internal.features.uicontrollers.paymentmethodsearch;

import android.view.View;
import com.mercadopago.android.px.internal.features.uicontrollers.CustomViewController;

public interface PaymentMethodSearchViewController extends CustomViewController {
    void draw();

    void setOnClickListener(View.OnClickListener listener);
}
