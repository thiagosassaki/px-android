package com.mercadopago.android.px.internal.features.plugins;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.mercadopago.android.px.core.PaymentProcessor;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.GenericPayment;

import static com.mercadopago.android.px.utils.PaymentUtils.getBusinessPaymentApproved;

public class SamplePaymentProcessor implements PaymentProcessor {

    private static final int CONSTANT_DELAY_MILLIS = 2000;
    private final PluginPayment pluginPayment;
    private final Handler handler = new Handler();

    public SamplePaymentProcessor(final PluginPayment pluginPayment) {
        this.pluginPayment = pluginPayment;
    }

    public SamplePaymentProcessor() {
        pluginPayment = getBusinessPaymentApproved();
    }

    @Override
    public void startPayment(@NonNull final CheckoutData data, @NonNull final Context context,
        @NonNull final OnPaymentListener paymentListener) {
        //This will never be called because shouldShowFragmentOnPayment is hardcoded
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pluginPayment instanceof BusinessPayment) {
                    paymentListener.onPaymentFinished((BusinessPayment) pluginPayment);
                } else if (pluginPayment instanceof GenericPayment) {
                    paymentListener.onPaymentFinished((GenericPayment) pluginPayment);
                }
            }
        }, CONSTANT_DELAY_MILLIS);
    }

    @Override
    public boolean shouldShowFragmentOnPayment() {
        return true;
    }

    @Override
    public int getPaymentTimeout() {
        return CONSTANT_DELAY_MILLIS;
    }

    @Nullable
    @Override
    public Bundle getFragmentBundle(@NonNull final CheckoutData data, @NonNull final Context context) {
        return new Bundle();
    }

    @Nullable
    @Override
    public Fragment getFragment(@NonNull final CheckoutData data,
        @NonNull final Context context) {
        final SamplePaymentProcessorFragment samplePaymentProcessorFragment = new SamplePaymentProcessorFragment();
        //TODO warning, dont you really do this.
        samplePaymentProcessorFragment.setPayment(pluginPayment);
        return samplePaymentProcessorFragment;
    }
}