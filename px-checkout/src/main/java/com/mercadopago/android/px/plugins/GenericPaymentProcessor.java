package com.mercadopago.android.px.plugins;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.mercadopago.android.px.core.MercadoPagoServicesAdapter;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentBody;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.mvp.TaggedCallback;
import com.mercadopago.android.px.plugins.model.GenericPayment;
import com.mercadopago.android.px.plugins.model.PluginPayment;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.util.ApiUtil;

public class GenericPaymentProcessor implements PaymentProcessor {

    //TODO remove - ADD paymentAPI service.
    private MercadoPagoServicesAdapter mercadoPagoServiceAdapter;

    @Override
    public void startPayment(@NonNull final CheckoutData data, @NonNull final Context context,
        @NonNull final OnPaymentListener paymentListener) {

        final Session session = Session.getSession(context);
        final PaymentSettingRepository paymentSettings = session.getConfigurationModule().getPaymentSettings();
        final String publicKey = paymentSettings.getPublicKey();
        mercadoPagoServiceAdapter = session.getMercadoPagoServiceAdapter();
        //TODO idempotency key, customer id?
        //TODO payer identification - in 40.0.0 it;s mutable for brasil.
        createPaymentInMercadoPago(paymentSettings.getTransactionId(), data.checkoutPreference, data.paymentData,
            paymentSettings.getAdvancedConfiguration().isBinaryMode(), publicKey,
            new TaggedCallback<Payment>(ApiUtil.RequestOrigin.CREATE_PAYMENT) {
                @Override
                public void onSuccess(final Payment payment) {
                    paymentListener.onPaymentFinished(mapPayment(payment, data));
                }

                @Override
                public void onFailure(final MercadoPagoError error) {
                    paymentListener.onPaymentError(error);
                }
            });
    }

    @Override
    public int getPaymentTimeout() {
        return 0;
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

    /* default */
    @NonNull
    PluginPayment mapPayment(final Payment payment, @NonNull final CheckoutData data) {
        return new GenericPayment(payment.getId(), payment.getStatus(),
            payment.getStatusDetail(), data.paymentData);
    }

    private void createPaymentInMercadoPago(@NonNull final String transactionId,
        @NonNull final CheckoutPreference checkoutPreference,
        @NonNull final PaymentData paymentData,
        final boolean binaryMode,
        final String publicKey,
        final TaggedCallback<Payment> taggedCallback) {
        final PaymentBody paymentBody = new PaymentBody(transactionId, paymentData, checkoutPreference);
        paymentBody.setBinaryMode(binaryMode);
        paymentBody.setPublicKey(publicKey);
        mercadoPagoServiceAdapter.createPayment(paymentBody, taggedCallback);
    }
}
