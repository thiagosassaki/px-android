package com.mercadopago.android.px.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.CheckoutActivity;
import com.mercadopago.android.px.callbacks.CallbackHolder;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.preferences.AdvancedConfiguration;
import com.mercadopago.android.px.preferences.PaymentConfiguration;
import com.mercadopago.android.px.tracker.FlowHandler;
import com.mercadopago.android.px.uicontrollers.FontCache;

import static com.mercadopago.android.px.util.TextUtils.isEmpty;

@SuppressWarnings("unused")
public class MercadoPagoCheckout {

    public static final int PAYMENT_RESULT_CODE = 7;
    public static final String EXTRA_PAYMENT_RESULT = "EXTRA_PAYMENT_RESULT";
    public static final String EXTRA_ERROR = "EXTRA_ERROR";

    @NonNull
    private final String publicKey;

    @NonNull
    private final AdvancedConfiguration advancedConfiguration;

    @Nullable
    private final String preferenceId;

    @Nullable
    private final String privateKey;

    @Nullable
    private final PaymentConfiguration paymentConfiguration;

    /* default */ boolean prefetch = false;

    /* default */ MercadoPagoCheckout(final Builder builder) {
        publicKey = builder.publicKey;
        advancedConfiguration = builder.advancedConfiguration;
        preferenceId = builder.preferenceId;
        privateKey = builder.privateKey;
        paymentConfiguration = builder.paymentConfiguration;
        configureCheckoutStore(builder);
        FlowHandler.getInstance().generateFlowId();
        CallbackHolder.getInstance().clean();
    }

    /**
     * Starts checkout experience.
     * When the flows ends it returns a {@link PaymentResult} object that
     * will be returned on {@link Activity#onActivityResult(int, int, Intent)} if success or
     * {@link com.mercadopago.android.px.model.exceptions.MercadoPagoError}
     * <p>
     * will return on {@link Activity#onActivityResult(int, int, Intent)}
     *
     * @param context context needed to start checkout.
     * @param requestCode it's the number that identifies the checkout flow request for
     * {@link Activity#onActivityResult(int, int, Intent)}
     */
    public void startPayment(@NonNull final Context context, final int requestCode) {
        startIntent(context, CheckoutActivity.getIntent(context), requestCode);
    }

    private void configureCheckoutStore(final Builder builder) {
        final CheckoutStore store = CheckoutStore.getInstance();
        store.reset();
    }

    private void startIntent(@NonNull final Context context, @NonNull final Intent checkoutIntent,
        final int requestCode) {
        if (!prefetch) {
            Session.getSession(context).init(this);
        }

        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(checkoutIntent, requestCode);
        } else {
            context.startActivity(checkoutIntent);
        }
    }

    @NonNull
    public AdvancedConfiguration getAdvancedConfiguration() {
        return advancedConfiguration;
    }

    @NonNull
    public String getPublicKey() {
        return publicKey;
    }

    @Nullable
    public String getPreferenceId() {
        return preferenceId;
    }

    @NonNull
    public String getPrivateKey() {
        return isEmpty(privateKey) ? "" : privateKey;
    }

    @Nullable
    public PaymentConfiguration getPaymentConfiguration() {
        return paymentConfiguration;
    }

    @SuppressWarnings("unused")
    public static final class Builder {

        /* default */ @NonNull final String publicKey;

        /* default */ @Nullable final String preferenceId;

        /* default */ @NonNull AdvancedConfiguration advancedConfiguration =
            new AdvancedConfiguration.Builder().build();

        /* default */ @Nullable PaymentConfiguration paymentConfiguration;

        /* default */ @Nullable String privateKey;

        /* default */ @Deprecated String regularFontPath;

        /* default */ @Deprecated String lightFontPath;

        /* default */ @Deprecated String monoFontPath;

        /**
         * Checkout builder allow you to create a {@link MercadoPagoCheckout}
         *
         * @param publicKey merchant public key.
         * @param paymentConfiguration the payment configuration for this checkout.
         */
        public Builder(@NonNull final String publicKey, @NonNull final PaymentConfiguration paymentConfiguration) {
            preferenceId = null;
            this.publicKey = publicKey;
            this.paymentConfiguration = paymentConfiguration;
        }

        /**
         * Checkout builder allow you to create a {@link MercadoPagoCheckout}
         *
         * @param publicKey merchant public key.
         * @param preferenceId the preference id that represents the payment information.
         */
        public Builder(@NonNull final String publicKey, @NonNull final String preferenceId) {
            this.publicKey = publicKey;
            this.preferenceId = preferenceId;
        }

        /**
         * Private key provides save card capabilities and account money balance.
         *
         * @param privateKey the user private key
         * @return builder to keep operating
         */
        public Builder setPrivateKey(@NonNull final String privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        public Builder setAdvancedConfiguration(@NonNull final AdvancedConfiguration advancedConfiguration) {
            this.advancedConfiguration = advancedConfiguration;
            return this;
        }

        public MercadoPagoCheckout build() {
            return new MercadoPagoCheckout(this);
        }

        /**
         * //TODO we will add a new mechanism
         *
         * @deprecated we will not support this mechanism anymore.
         */
        @Deprecated
        public Builder setCustomLightFont(@NonNull final String lightFontPath, @NonNull final Context context) {
            this.lightFontPath = lightFontPath;
            setCustomFont(context, FontCache.CUSTOM_LIGHT_FONT, this.lightFontPath);
            return this;
        }

        /**
         * //TODO we will add a new mechanism
         *
         * @deprecated we will not support this mechanism anymore.
         */
        @Deprecated
        public Builder setCustomRegularFont(@NonNull final String regularFontPath, @NonNull final Context context) {
            this.regularFontPath = regularFontPath;
            setCustomFont(context, FontCache.CUSTOM_REGULAR_FONT, this.regularFontPath);
            return this;
        }

        /**
         * //TODO we will add a new mechanism
         *
         * @deprecated we will not support this mechanism anymore.
         */
        @Deprecated
        public Builder setCustomMonoFont(@NonNull final String monoFontPath, final Context context) {
            this.monoFontPath = monoFontPath;
            setCustomFont(context, FontCache.CUSTOM_MONO_FONT, this.monoFontPath);
            return this;
        }

        /**
         * //TODO we will add a new mechanism
         * @deprecated we will not support this mechanism anymore.
         */
        @Deprecated
        private void setCustomFont(@NonNull final Context context, final String fontType, final String fontPath) {
            final Typeface typeFace;
            if (!FontCache.hasTypeface(fontType)) {
                typeFace = Typeface.createFromAsset(context.getAssets(), fontPath);
                FontCache.setTypeface(fontType, typeFace);
            }
        }
    }
}