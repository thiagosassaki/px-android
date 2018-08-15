package com.mercadopago.android.px.internal.features.hooks.components;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.view.RendererFactory;
import com.mercadopago.android.px.internal.features.hooks.HookComponent;

public class PaymentConfirm extends HookComponent<Void> {

    static {
        RendererFactory.register(PaymentConfirm.class, PaymentConfirmRenderer.class);
    }

    public PaymentConfirm(@NonNull final Props props) {
        super(props);
    }
}
