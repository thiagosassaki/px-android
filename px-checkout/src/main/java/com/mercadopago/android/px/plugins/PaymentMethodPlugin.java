package com.mercadopago.android.px.plugins;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.plugins.model.PaymentMethodInfo;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import java.io.Serializable;

import static com.mercadopago.android.px.plugins.PaymentMethodPlugin.PluginPosition.TOP;

public abstract class PaymentMethodPlugin implements Serializable {

    public enum PluginPosition {
        TOP, BOTTOM
    }

    public static class CheckoutData {
        public final PaymentData paymentData;
        public final CheckoutPreference checkoutPreference;

        /* default */ CheckoutData(@NonNull final PaymentData paymentData,
            @NonNull final CheckoutPreference checkoutPreference) {
            this.paymentData = paymentData;
            this.checkoutPreference = checkoutPreference;
        }
    }

    public PaymentMethodPlugin() {
        //Constructor needed for serialization.
    }

    /**
     * This method returns {@link PluginPosition#TOP} or
     * {@link PluginPosition#BOTTOM} it represents
     * where the plugin will be placed in payment method selection.
     *
     * @return the position where the plugin will be placed
     */
    public PluginPosition getPluginPosition() {
        return TOP;
    }

    /**
     * Returns the plugin id
     *
     * @return id
     */
    @NonNull
    public abstract String getId();

    /**
     * method to know if the plugin should show a view on selection.
     * will be called on runtime.
     *
     * @return if it should show view
     */
    public abstract boolean shouldShowFragmentOnSelection();

    /**
     * This method returns the minimum amount of information required
     * by the payment method to be shown in payment method selection.
     *
     * @param context provided to construct the PaymentMethodInfo if it's needed.
     * @return PaymentMethodInfo {@link PaymentMethodInfo}
     */
    @NonNull
    public abstract PaymentMethodInfo getPaymentMethodInfo(@NonNull final Context context);

    /**
     * This bundle will be attached to the fragment that you expose in
     * {@link #getFragment(CheckoutData, Context)}
     *
     * @param data checkout data to the moment it's called.
     * @param context that you may need to fill information.
     * @return fragment.
     */
    @Nullable
    public abstract Bundle getFragmentBundle(@NonNull final CheckoutData data,
        @NonNull final Context context);

    @Nullable
    public abstract PluginFragment getFragment(
        @NonNull final CheckoutData data,
        @NonNull final Context context);

    /**
     * method to know if the plugin is available.
     * will be called in runtime.
     *
     * @return if the plugin is available to use.
     */
    public abstract boolean isEnabled();
}