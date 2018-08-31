package com.mercadopago.android.px.internal.features.hooks.components;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.view.RendererFactory;
import com.mercadopago.android.px.internal.features.hooks.HookComponent;

public class PaymentMethodConfirm extends HookComponent<Void> {

    static {
        RendererFactory.register(PaymentMethodConfirm.class, PaymentMethodConfirmRenderer.class);
    }

    public PaymentMethodConfirm(@NonNull final Props props) {
        super(props);
    }
}
