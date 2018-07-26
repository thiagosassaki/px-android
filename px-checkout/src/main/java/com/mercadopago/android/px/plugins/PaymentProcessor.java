package com.mercadopago.android.px.plugins;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.preferences.CheckoutPreference;

public abstract class PaymentProcessor implements Parcelable {

    public static final String PAYMENT_PROCESSOR_KEY = "payment_processor";

    public boolean needsVisualPaymentProcessing() {
        return false;
    }

    public abstract void initPayment(@NonNull final Context appContext, @NonNull final PaymentProcessor.Props props,
        @NonNull final OnPaymentListener paymentListener);

    @NonNull
    public Fragment initVisualPayment(@NonNull final PaymentProcessor.Props props,
        @NonNull final OnPaymentListener paymentListener) {
        return new Fragment();
    }

    public static final class Props {

        public final PaymentData paymentData;
        public final CheckoutPreference checkoutPreference;

        public Props(@NonNull final PaymentData paymentData, @NonNull final CheckoutPreference checkoutPreference) {
            this.paymentData = paymentData;
            this.checkoutPreference = checkoutPreference;
        }
    }
}
