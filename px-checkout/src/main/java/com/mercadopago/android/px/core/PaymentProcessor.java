package com.mercadopago.android.px.core;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.GenericPayment;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import java.io.Serializable;

@SuppressWarnings("unused")
public interface PaymentProcessor extends Serializable {

    /* default */ final class CheckoutData {

        public final PaymentData paymentData;
        public final CheckoutPreference checkoutPreference;

        public CheckoutData(final PaymentData paymentData,
            final CheckoutPreference checkoutPreference) {
            this.paymentData = paymentData;
            this.checkoutPreference = checkoutPreference;
        }
    }

    interface OnPaymentListener {

        void onPaymentFinished(@NonNull final Payment payment);

        void onPaymentFinished(@NonNull final GenericPayment genericPayment);

        void onPaymentFinished(@NonNull final BusinessPayment businessPayment);

        void onPaymentError(@NonNull final MercadoPagoError error);

    }

    /**
     * Method that we will call if {@link #shouldShowFragmentOnPayment()} is false.
     * we will place a loading for you meanwhile we call this method.
     *
     * @param data checkout data to the moment it's called.
     * @param context that you may need to fill information.
     * @param paymentListener when you have processed your payment
     * you should call {@link OnPaymentListener}
     */
    void startPayment(
        @NonNull final PaymentProcessor.CheckoutData data,
        @NonNull final Context context,
        @NonNull final OnPaymentListener paymentListener);

    /**
     * If you how much time will take the request timeout
     * you can tell us to optimize the loading UI.
     * will only works if {@link #shouldShowFragmentOnPayment()} is false.
     *
     * @return time in milliseconds
     */
    int getPaymentTimeout();

    /**
     * method to know if the payment processor should pay through
     * a fragment or do it through background execution.
     * will be called on runtime.
     *
     * @return if it should show view
     */
    boolean shouldShowFragmentOnPayment();

    /**
     * This bundle will be attached to the fragment that you expose in
     * {@link #getFragment(PaymentProcessor.CheckoutData, Context)}
     *
     * @param data checkout data to the moment it's called.
     * @param context that you may need to fill information.
     * @return fragment.
     */
    @Nullable
    Bundle getFragmentBundle(@NonNull final PaymentProcessor.CheckoutData data,
        @NonNull final Context context);

    /**
     * Fragment that will appear if {@link #shouldShowFragmentOnPayment()} is true
     * when user clicks this payment method.
     * <p>
     * inside {@link android.support.v4.app.Fragment#onAttach(Context)}
     * context will be an instance of {@link OnPaymentListener}
     *
     * @param data checkout data to the moment it's called.
     * @param context that you may need to fill information.
     * @return fragment
     */
    @Nullable
    Fragment getFragment(
        @NonNull final PaymentProcessor.CheckoutData data,
        @NonNull final Context context);
}
