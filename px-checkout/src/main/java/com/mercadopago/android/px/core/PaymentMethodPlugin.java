package com.mercadopago.android.px.core;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.PaymentMethodInfo;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import java.io.Serializable;

public interface PaymentMethodPlugin extends Serializable {

    enum PluginPosition {
        TOP, BOTTOM
    }

    /* default */ final class CheckoutData {
        @NonNull public final CheckoutPreference checkoutPreference;
        @Nullable public final Discount discount;
        @Nullable public final String privateKey;

        public CheckoutData(@NonNull final CheckoutPreference checkoutPreference,
            @Nullable final Discount discount,
            @Nullable final String privateKey) {
            this.checkoutPreference = checkoutPreference;
            this.discount = discount;
            this.privateKey = privateKey;
        }
    }

    interface OnPaymentMethodListener {
        void next();

        void back();
    }

    /**
     * Init method to be called in a background thread to init this plugin.
     */
    void init(@NonNull final CheckoutData checkoutData);

    /**
     * This method returns {@link PluginPosition#TOP} or
     * {@link PluginPosition#BOTTOM} it represents
     * where the plugin will be placed in payment method selection.
     *
     * @return the position where the plugin will be placed
     */
    PluginPosition getPluginPosition();

    /**
     * Returns the plugin id
     *
     * @return id
     */
    @NonNull
    String getId();

    /**
     * method to know if the plugin should show a view on selection.
     * will be called on runtime.
     *
     * @return if it should show view
     */
    boolean shouldShowFragmentOnSelection();

    /**
     * This method returns the minimum amount of information required
     * by the payment method to be shown in payment method selection.
     *
     * @param context provided to construct the PaymentMethodInfo if it's needed.
     * @return PaymentMethodInfo {@link PaymentMethodInfo}
     */
    @NonNull
    PaymentMethodInfo getPaymentMethodInfo(@NonNull final Context context);

    /**
     * This bundle will be attached to the fragment that you expose in
     * {@link #getFragment(CheckoutData, Context)}
     *
     * @param data checkout data to the moment it's called.
     * @param context that you may need to fill information.
     * @return fragment.
     */
    @Nullable
    Bundle getFragmentBundle(@NonNull final CheckoutData data,
        @NonNull final Context context);

    /**
     * Fragment that will appear if {@link #shouldShowFragmentOnSelection()} is true
     * when user clicks this payment method.
     *
     *  inside {@link android.support.v4.app.Fragment#onAttach(Context)}
     * context will be an instance of {@link PaymentMethodPlugin.OnPaymentMethodListener}
     *
     * @param data checkout data to the moment it's called.
     * @param context that you may need to fill information.
     * @return plugin fragment
     */
    @Nullable
    Fragment getFragment(
        @NonNull final CheckoutData data,
        @NonNull final Context context);

    /**
     * method to know if the plugin is available.
     * will be called in runtime.
     *
     * @return if the plugin is available to use.
     */
    boolean isEnabled();
}