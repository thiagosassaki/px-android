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
import com.mercadopago.android.px.model.IPayment;

import static com.mercadopago.android.px.utils.PaymentUtils.getBusinessPaymentApproved;

public class SamplePaymentProcessor implements PaymentProcessor {

    private static final int CONSTANT_DELAY_MILLIS = 2000;
    private final IPayment iPayment;
    private final Handler handler = new Handler();

    public SamplePaymentProcessor(final IPayment iPayment) {
        this.iPayment = iPayment;
    }

    public SamplePaymentProcessor() {
        iPayment = getBusinessPaymentApproved();
    }

    @Override
    public void startPayment(@NonNull final CheckoutData data, @NonNull final Context context,
        @NonNull final OnPaymentListener paymentListener) {
        //This will never be called because shouldShowFragmentOnPayment is hardcoded
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (iPayment instanceof BusinessPayment) {
                    paymentListener.onPaymentFinished((BusinessPayment) iPayment);
                } else if (iPayment instanceof GenericPayment) {
                    paymentListener.onPaymentFinished((GenericPayment) iPayment);
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
        // This is just a sample, you should't do this, you must process the payment inside the fragment.
        samplePaymentProcessorFragment.setPayment(iPayment);
        return samplePaymentProcessorFragment;
    }
}