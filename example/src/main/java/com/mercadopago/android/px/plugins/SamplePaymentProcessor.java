package com.mercadopago.android.px.plugins;

import android.content.Context;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.plugins.components.SamplePayment;

/**
 * Created by nfortuna on 12/13/17.
 */

public class SamplePaymentProcessor extends PaymentProcessor {

    @NonNull
    @Override
    public PluginComponent createPaymentComponent(@NonNull final PluginComponent.Props props,
        @NonNull final Context context) {

        return new SamplePayment(
            props.toBuilder()
                .setToolbarVisible(false)
                .build()
        );
    }
}