package com.mercadopago.android.px.internal.datasource;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.mercadopago.android.px.core.PaymentProcessor;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentBody;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

public class MercadoPagoPaymentProcessor implements PaymentProcessor {

    private static final int TIMEOUT = 20000;

    @Override
    public int getPaymentTimeout() {
        return TIMEOUT;
    }

    @Override
    public boolean shouldShowFragmentOnPayment() {
        return false;
    }

    @Nullable
    @Override
    public Bundle getFragmentBundle(@NonNull final CheckoutData data, @NonNull final Context context) {
        return null;
    }

    @Nullable
    @Override
    public Fragment getFragment(@NonNull final CheckoutData data, @NonNull final Context context) {
        return null;
    }

    @Override
    public void startPayment(@NonNull final CheckoutData data,
        @NonNull final Context context,
        @NonNull final OnPaymentListener paymentListener) {

        final Session session = Session.getSession(context);
        final PaymentSettingRepository paymentSettings = session.getConfigurationModule().getPaymentSettings();
        final String publicKey = paymentSettings.getPublicKey();
        final MercadoPagoServicesAdapter mercadoPagoServiceAdapter = session.getMercadoPagoServiceAdapter();

        final PaymentBody paymentBody =
            new PaymentBody(paymentSettings.getTransactionId(), data.paymentData, data.checkoutPreference);
        paymentBody.setBinaryMode(data.checkoutPreference.isBinaryMode());
        paymentBody.setPublicKey(publicKey);

        //TODO idempotency key, customer id?

        mercadoPagoServiceAdapter.createPayment(paymentBody,
            new TaggedCallback<Payment>(ApiUtil.RequestOrigin.CREATE_PAYMENT) {
                @Override
                public void onSuccess(final Payment payment) {
                    paymentListener.onPaymentFinished(payment);
                }

                @Override
                public void onFailure(final MercadoPagoError error) {
                    paymentListener.onPaymentError(error);
                }
            });
    }
}
