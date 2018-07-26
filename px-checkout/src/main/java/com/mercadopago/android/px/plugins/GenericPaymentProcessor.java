package com.mercadopago.android.px.plugins;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.core.MercadoPagoServicesAdapter;
import com.mercadopago.android.px.exceptions.MercadoPagoError;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentBody;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.mvp.TaggedCallback;
import com.mercadopago.android.px.plugins.model.GenericPayment;
import com.mercadopago.android.px.plugins.model.PluginPayment;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.util.ApiUtil;

public class GenericPaymentProcessor extends PaymentProcessor {

    private MercadoPagoServicesAdapter mercadoPagoServiceAdapter;

    /* default */ GenericPaymentProcessor(final Parcel in) {

        //Do nothing
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        //Do nothing
    }

    public static final Creator<GenericPaymentProcessor> CREATOR = new Creator<GenericPaymentProcessor>() {
        @Override
        public GenericPaymentProcessor createFromParcel(final Parcel in) {
            return new GenericPaymentProcessor(in);
        }

        @Override
        public GenericPaymentProcessor[] newArray(final int size) {
            return new GenericPaymentProcessor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void initPayment(@NonNull final Context appContext, @NonNull final Props props,
        @NonNull final OnPaymentListener paymentListener) {
        final Session session = Session.getSession(appContext);
        final PaymentSettingRepository paymentSettings = session.getConfigurationModule().getPaymentSettings();
        final String publicKey = paymentSettings.getPublicKey();
        mercadoPagoServiceAdapter = session.getMercadoPagoServiceAdapter();
        //TODO idempotency key, binary mode, customer id?
        createPaymentInMercadoPago("123", props.checkoutPreference, props.paymentData,
            paymentSettings.isBinaryMode(), publicKey,
            new TaggedCallback<Payment>(ApiUtil.RequestOrigin.CREATE_PAYMENT) {
                @Override
                public void onSuccess(final Payment payment) {
                    paymentListener.onPaymentFinished(mapPayment(payment, props));
                }

                @Override
                public void onFailure(final MercadoPagoError error) {
                    paymentListener.onPaymentError(error);
                }
            });
    }

    @NonNull
    private PluginPayment mapPayment(final Payment payment, @NonNull final Props props) {
        return new GenericPayment(payment.getId(), payment.getStatus(),
            payment.getStatusDetail(), props.paymentData);
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
