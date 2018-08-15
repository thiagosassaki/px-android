package com.mercadopago.android.px.internal.features.hooks.components;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.view.RendererFactory;
import com.mercadopago.android.px.internal.features.hooks.HookComponent;

public class PaymentTypeConfirm extends HookComponent<Void> {

    static {
        RendererFactory.register(PaymentTypeConfirm.class, PaymentTypeConfirmRenderer.class);
    }

    public PaymentTypeConfirm(@NonNull final Props props) {
        super(props);
    }
}
