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
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.commission.ChargeRule;
import com.mercadopago.android.px.plugins.DataInitializationTask;
import com.mercadopago.android.px.plugins.PaymentMethodPlugin;
import com.mercadopago.android.px.plugins.PaymentProcessor;
import com.mercadopago.android.px.preferences.AdvancedConfiguration;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.tracker.FlowHandler;
import com.mercadopago.android.px.uicontrollers.FontCache;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mercadopago.android.px.plugins.PaymentProcessor.PAYMENT_PROCESSOR_KEY;
import static com.mercadopago.android.px.util.TextUtils.isEmpty;

@SuppressWarnings("unused")
public class MercadoPagoCheckout {

    public static final int PAYMENT_RESULT_CODE = 7;
    public static final String EXTRA_PAYMENT_RESULT = "EXTRA_PAYMENT_RESULT";
    public static final String EXTRA_ERROR = "EXTRA_ERROR";

    @NonNull
    private final String publicKey;

    @Nullable
    private final CheckoutPreference checkoutPreference;

    @NonNull
    private final AdvancedConfiguration advancedConfiguration;

    @Nullable
    private final String preferenceId;

    @Nullable
    private final Discount discount;

    @Nullable
    private final Campaign campaign;

    @Nullable
    private final String privateKey;

    @NonNull
    private final ArrayList<ChargeRule> charges;

    /* default */ boolean prefetch = false;

    /* default */ MercadoPagoCheckout(final Builder builder) {
        publicKey = builder.publicKey;
        checkoutPreference = builder.checkoutPreference;
        advancedConfiguration = builder.advancedConfiguration;
        discount = builder.discount;
        campaign = builder.campaign;
        charges = builder.charges;
        preferenceId = builder.preferenceId;
        privateKey = builder.privateKey;
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
     * @param requestCode it's the number that identifies the checkout flow request for {@link Activity#onActivityResult(int, int, Intent)}
     */
    public void startPayment(@NonNull final Context context, final int requestCode) {
        startIntent(context, CheckoutActivity.getIntent(context), requestCode);
    }

    private void configureCheckoutStore(final Builder builder) {
        final CheckoutStore store = CheckoutStore.getInstance();
        store.reset();
        store.setPaymentMethodPluginList(builder.paymentMethodPluginList);
        store.setPaymentPlugins(builder.paymentPlugins);
        store.setDataInitializationTask(builder.dataInitializationTask);
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

    @Nullable
    public Discount getDiscount() {
        return discount;
    }

    @Nullable
    public Campaign getCampaign() {
        return campaign;
    }

    @NonNull
    public List<ChargeRule> getCharges() {
        return charges;
    }

    @NonNull
    public String getMerchantPublicKey() {
        return publicKey;
    }

    @Nullable
    public String getPreferenceId() {
        return preferenceId;
    }

    @Nullable
    public CheckoutPreference getCheckoutPreference() {
        return checkoutPreference;
    }

    @NonNull
    public String getPrivateKey() {
        return isEmpty(privateKey) ? "" : privateKey;
    }

    @SuppressWarnings("unused")
    public static final class Builder {

        final String publicKey;

        final String preferenceId;

        final CheckoutPreference checkoutPreference;

        @NonNull final ArrayList<ChargeRule> charges = new ArrayList<>();

        final Map<String, PaymentProcessor> paymentPlugins = new HashMap<>();

        final List<PaymentMethodPlugin> paymentMethodPluginList = new ArrayList<>();

        @NonNull
        AdvancedConfiguration advancedConfiguration = new AdvancedConfiguration.Builder().build();

        @Nullable
        String privateKey;

        Discount discount;
        Campaign campaign;
        DataInitializationTask dataInitializationTask;
        String regularFontPath;
        String lightFontPath;
        String monoFontPath;

        /**
         * Checkout builder allow you to create a {@link MercadoPagoCheckout}
         *
         * @param publicKey merchant public key.
         * @param checkoutPreference the preference that represents the payment information.
         */
        public Builder(@NonNull final String publicKey, @NonNull final CheckoutPreference checkoutPreference) {
            preferenceId = null;
            this.publicKey = publicKey;
            this.checkoutPreference = checkoutPreference;
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
            checkoutPreference = null;
        }

        /**
         * Set Mercado Pago discount that will be applied to total amount.
         * When you set a discount with its campaign, we do not check in discount service.
         * You have to set a payment processor for discount be applied.
         *
         * @param discount Mercado Pago discount.
         * @param campaign Discount campaign with discount data.
         * @return builder to keep operating
         */
        public Builder setDiscount(@NonNull final Discount discount, @NonNull final Campaign campaign) {
            this.discount = discount;
            this.campaign = campaign;
            return this;
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

        /**
         * Add extra charges that will apply to total amount.
         *
         * @param charge Extra charge that you could collect.
         * @return builder to keep operating
         */
        public Builder addChargeRule(@NonNull final ChargeRule charge) {
            charges.add(charge);
            return this;
        }

        /**
         * Add extra charges that will apply to total amount.
         *
         * @param charges the list of chargest that could apply.
         * @return builder to keep operating
         */
        public Builder addChargeRules(@NonNull final Collection<ChargeRule> charges) {
            this.charges.addAll(charges);
            return this;
        }

        public Builder setAdvancedConfiguration(@NonNull final AdvancedConfiguration advancedConfiguration) {
            this.advancedConfiguration = advancedConfiguration;
            return this;
        }

        public Builder addPaymentMethodPlugin(@NonNull final PaymentMethodPlugin paymentMethodPlugin,
            @NonNull final PaymentProcessor paymentProcessor) {
            paymentMethodPluginList.add(paymentMethodPlugin);
            paymentPlugins.put(paymentMethodPlugin.getId(), paymentProcessor);
            return this;
        }

        public Builder setPaymentProcessor(@NonNull final PaymentProcessor paymentProcessor) {
            paymentPlugins.put(PAYMENT_PROCESSOR_KEY, paymentProcessor);
            return this;
        }

        public Builder setDataInitializationTask(@NonNull final DataInitializationTask dataInitializationTask) {
            this.dataInitializationTask = dataInitializationTask;
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
        public Builder setCustomLightFont(final String lightFontPath, final Context context) {
            this.lightFontPath = lightFontPath;
            if (lightFontPath != null) {
                setCustomFont(context, FontCache.CUSTOM_LIGHT_FONT, this.lightFontPath);
            }
            return this;
        }

        /**
         * //TODO we will add a new mechanism
         *
         * @deprecated we will not support this mechanism anymore.
         */
        @Deprecated
        public Builder setCustomRegularFont(final String regularFontPath, final Context context) {
            this.regularFontPath = regularFontPath;
            if (regularFontPath != null) {
                setCustomFont(context, FontCache.CUSTOM_REGULAR_FONT, this.regularFontPath);
            }
            return this;
        }

        /**
         * //TODO we will add a new mechanism
         *
         * @deprecated we will not support this mechanism anymore.
         */
        @Deprecated
        public Builder setCustomMonoFont(final String monoFontPath, final Context context) {
            this.monoFontPath = monoFontPath;
            if (monoFontPath != null) {
                setCustomFont(context, FontCache.CUSTOM_MONO_FONT, this.monoFontPath);
            }
            return this;
        }

        /**
         * //TODO we will add a new mechanism
         * @deprecated we will not support this mechanism anymore.
         */
        @Deprecated
        private void setCustomFont(final Context context, final String fontType, final String fontPath) {
            final Typeface typeFace;
            if (!FontCache.hasTypeface(fontType)) {
                typeFace = Typeface.createFromAsset(context.getAssets(), fontPath);
                FontCache.setTypeface(fontType, typeFace);
            }
        }
    }
}