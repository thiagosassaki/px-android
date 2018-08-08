package com.mercadopago.android.px.plugins;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.plugins.model.BusinessPayment;
import com.mercadopago.android.px.plugins.model.GenericPayment;
import com.mercadopago.android.px.plugins.model.PluginPayment;
import com.mercadopago.android.px.preferences.CheckoutPreference;

public abstract class PaymentProcessor {

    public static final class CheckoutData {

        public final PaymentData paymentData;
        public final CheckoutPreference checkoutPreference;

        /* default */ CheckoutData(final PaymentData paymentData,
            final CheckoutPreference checkoutPreference) {
            this.paymentData = paymentData;
            this.checkoutPreference = checkoutPreference;
        }
    }

    public interface OnPaymentListener {

        /**
         * Most generic way to process a payment.
         *
         * @param payment plugin payment.
         */
        void onPaymentFinished(@NonNull final PluginPayment payment);

        void onPaymentFinished(@NonNull final GenericPayment genericPayment);

        void onPaymentFinished(@NonNull final BusinessPayment businessPayment);

        void cancelPayment();
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
    public abstract void startPayment(
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
    public int getPaymentTimeout() {
        return 0;
    }

    /**
     * method to know if the payment processor should pay through
     * a fragment or do it through background execution.
     * will be called on runtime.
     *
     * @return if it should show view
     */
    public abstract boolean shouldShowFragmentOnPayment();

    /**
     * This bundle will be attached to the fragment that you expose in
     * {@link #getFragment(PaymentProcessor.CheckoutData, Context)}
     *
     * @param data checkout data to the moment it's called.
     * @param context that you may need to fill information.
     * @return fragment.
     */
    @Nullable
    public abstract Bundle getFragmentBundle(@NonNull final PaymentProcessor.CheckoutData data,
        @NonNull final Context context);

    /**
     * Fragment that will appear if {@link #shouldShowFragmentOnPayment()} is true
     * when user clicks this payment method.
     *
     * @param data checkout data to the moment it's called.
     * @param context that you may need to fill information.
     * @return plugin fragment
     */
    @Nullable
    public abstract PaymentProcessorFragment getFragment(
        @NonNull final PaymentProcessor.CheckoutData data,
        @NonNull final Context context);
}
