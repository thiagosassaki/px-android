package com.mercadopago.android.px.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.CheckoutActivity;
import com.mercadopago.android.px.callbacks.CallbackHolder;
import com.mercadopago.android.px.hooks.CheckoutHooks;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.commission.ChargeRule;
import com.mercadopago.android.px.plugins.DataInitializationTask;
import com.mercadopago.android.px.plugins.PaymentMethodPlugin;
import com.mercadopago.android.px.plugins.PaymentProcessor;
import com.mercadopago.android.px.preferences.AdvancedConfiguration;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.preferences.PaymentResultScreenPreference;
import com.mercadopago.android.px.preferences.ServicePreference;
import com.mercadopago.android.px.review_and_confirm.models.ReviewAndConfirmPreferences;
import com.mercadopago.android.px.tracker.FlowHandler;
import com.mercadopago.android.px.uicontrollers.FontCache;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mercadopago.android.px.plugins.PaymentProcessor.PAYMENT_PROCESSOR_KEY;
import static com.mercadopago.android.px.util.TextUtils.isEmpty;

public class MercadoPagoCheckout implements Serializable {

    public static final int CHECKOUT_REQUEST_CODE = 5;
    public static final int PAYMENT_DATA_RESULT_CODE = 6;
    public static final int PAYMENT_RESULT_CODE = 7;
    public static final int TIMER_FINISHED_RESULT_CODE = 8;
    public static final int PAYMENT_METHOD_CHANGED_REQUESTED = 9;

    @NonNull
    private final String publicKey;

    @Nullable
    private final CheckoutPreference checkoutPreference;

    @NonNull
    private final AdvancedConfiguration advancedConfiguration;

    @Nullable
    private final PaymentResultScreenPreference paymentResultScreenPreference;

    @Nullable
    private final PaymentData paymentData;

    @Nullable
    private final PaymentResult paymentResult;

    @Nullable
    private final String preferenceId;

    @Nullable
    private final Discount discount;

    @Nullable
    private final Campaign campaign;

    private final boolean binaryMode;

    @Nullable
    private final String privateKey;

    @NonNull
    private final ArrayList<ChargeRule> charges;

    /* default */ boolean prefetch = false;

    /* default */ MercadoPagoCheckout(final Builder builder) {
        publicKey = builder.publicKey;
        checkoutPreference = builder.checkoutPreference;
        advancedConfiguration = builder.advancedConfiguration;
        paymentResultScreenPreference = builder.paymentResultScreenPreference;
        binaryMode = builder.binaryMode;
        discount = builder.discount;
        campaign = builder.campaign;
        charges = builder.charges;
        paymentResult = builder.paymentResult;
        paymentData = builder.paymentData;
        preferenceId = builder.preferenceId;
        privateKey = builder.privateKey;
        configureCheckoutStore(builder);
        configureFlowHandler();
    }

    /**
     * Deprecated - new implementation involves payment processor.
     * <p>
     * Starts checkout experience.
     * When the flows ends it returns a {@link PaymentData} object to finish the payment.
     * will be returned on {@link Activity#onActivityResult(int, int, Intent)} if success or
     * {@link com.mercadopago.android.px.exceptions.MercadoPagoError}
     * if something went wrong or canceled.
     *
     * @param context context needed to start checkout.
     */
    @Deprecated
    @SuppressWarnings("unused")
    public void startForPaymentData(@NonNull final Context context) {
        //TODO payment result code should not be hardcoded.
        startForResult(context, MercadoPagoCheckout.PAYMENT_DATA_RESULT_CODE);
    }

    /**
     * Starts checkout experience.
     * When the flows ends it returns a {@link PaymentResult} object that
     * will be returned on {@link Activity#onActivityResult(int, int, Intent)} if success or
     * {@link com.mercadopago.android.px.exceptions.MercadoPagoError}
     * <p>
     * will return on {@link Activity#onActivityResult(int, int, Intent)}
     *
     * @param context context needed to start checkout.
     */
    @SuppressWarnings("unused")
    public void startForPayment(@NonNull final Context context) {
        //TODO payment result code should not be hardcoded.
        startForResult(context, MercadoPagoCheckout.PAYMENT_RESULT_CODE);
    }

    private void configureFlowHandler() {
        //Create flow identifier only for new checkouts
        if (paymentResult == null && paymentData == null) {
            FlowHandler.getInstance().generateFlowId();
        }
    }

    private void configureCheckoutStore(final Builder builder) {
        final CheckoutStore store = CheckoutStore.getInstance();
        store.reset();
        store.setReviewAndConfirmPreferences(builder.reviewAndConfirmPreferences);
        store.setPaymentResultScreenPreference(paymentResultScreenPreference);
        store.setPaymentMethodPluginList(builder.paymentMethodPluginList);
        store.setPaymentPlugins(builder.paymentPlugins);
        store.setCheckoutHooks(builder.checkoutHooks);
        store.setDataInitializationTask(builder.dataInitializationTask);
        store.setCheckoutPreference(builder.checkoutPreference);
    }

    private void startForResult(@NonNull final Context context, final int resultCode) {
        CallbackHolder.getInstance().clean();
        startCheckoutActivity(context, resultCode);
    }

    private void startCheckoutActivity(@NonNull final Context context, final int resultCode) {
        startIntent(context, CheckoutActivity.getIntent(context, resultCode, this));
    }

    private void startIntent(@NonNull final Context context, @NonNull final Intent checkoutIntent) {

        if (!prefetch) {
            Session.getSession(context).init(this);
        }

        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(checkoutIntent, MercadoPagoCheckout.CHECKOUT_REQUEST_CODE);
        } else {
            context.startActivity(checkoutIntent);
        }
    }

    @Nullable
    public PaymentResultScreenPreference getPaymentResultScreenPreference() {
        return paymentResultScreenPreference;
    }

    @NonNull
    public AdvancedConfiguration getAdvancedConfiguration() {
        return advancedConfiguration;
    }

    public boolean isBinaryMode() {
        return binaryMode;
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

    @Nullable
    public PaymentData getPaymentData() {
        return paymentData;
    }

    @Nullable
    public PaymentResult getPaymentResult() {
        return paymentResult;
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

    public static class Builder {

        final String publicKey;

        final String preferenceId;

        final CheckoutPreference checkoutPreference;

        @NonNull final ArrayList<ChargeRule> charges = new ArrayList<>();

        final Map<String, PaymentProcessor> paymentPlugins = new HashMap<>();

        final List<PaymentMethodPlugin> paymentMethodPluginList = new ArrayList<>();

        Boolean binaryMode = false;

        @NonNull
        AdvancedConfiguration advancedConfiguration = new AdvancedConfiguration.Builder().build();

        @Nullable
        String privateKey;

        PaymentResultScreenPreference paymentResultScreenPreference;
        PaymentData paymentData;
        PaymentResult paymentResult;
        Discount discount;
        Campaign campaign;
        CheckoutHooks checkoutHooks;
        DataInitializationTask dataInitializationTask;
        String regularFontPath;
        String lightFontPath;
        String monoFontPath;
        ReviewAndConfirmPreferences reviewAndConfirmPreferences;

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
            //TODO 21/06/2017 - Hack for credits, should remove payer access token.
            privateKey = checkoutPreference.getPayer().getAccessToken();
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
         * @return builder
         */
        public Builder setPrivateKey(@NonNull final String privateKey) {
            //TODO 21/06/2017 - Hack for credits, should remove payer access token.
            this.privateKey = privateKey;
            if (checkoutPreference != null) {
                checkoutPreference.getPayer().setAccessToken(privateKey);
            }
            return this;
        }

        /**
         * Add extra charges that will apply to total amount.
         *
         * @param charge Extra charge that you could collect.
         */
        public Builder addChargeRule(@NonNull final ChargeRule charge) {
            charges.add(charge);
            return this;
        }

        /**
         * Add extra charges that will apply to total amount.
         *
         * @param charges the list of chargest that could apply.
         */
        public Builder addChargeRules(@NonNull final Collection<ChargeRule> charges) {
            this.charges.addAll(charges);
            return this;
        }

        public Builder setAdvancedConfiguration(@NonNull final AdvancedConfiguration advancedConfiguration) {
            this.advancedConfiguration = advancedConfiguration;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setPaymentResultScreenPreference(PaymentResultScreenPreference paymentResultScreenPreference) {
            this.paymentResultScreenPreference = paymentResultScreenPreference;
            return this;
        }

        @SuppressWarnings("unused")
        @Deprecated
        public Builder setServicePreference(@NonNull final ServicePreference servicePreference) {
            return this;
        }

        public Builder setPaymentData(PaymentData paymentData) {
            this.paymentData = paymentData;
            return this;
        }

        /**
         * If enableBinaryMode is called, processed payment can only be APPROVED or REJECTED.
         * <p>
         * Non compatible with PaymentProcessor.
         * <p>
         * Non compatible with off payments methods
         *
         * @return builder
         */
        @SuppressWarnings("unused")
        public Builder enableBinaryMode() {
            binaryMode = true;
            return this;
        }

        /**
         * Enable to preset configurations to customize visualization on
         * the Review and Confirm Screen see {@link ReviewAndConfirmPreferences.Builder}
         *
         * @param reviewAndConfirmPreferences the custom preference configuration
         * @return builder to keep operating
         */
        @SuppressWarnings("unused")
        public Builder setReviewAndConfirmPreferences(final ReviewAndConfirmPreferences reviewAndConfirmPreferences) {
            this.reviewAndConfirmPreferences = reviewAndConfirmPreferences;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setPaymentResult(final PaymentResult paymentResult) {
            this.paymentResult = paymentResult;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setCheckoutHooks(@NonNull final CheckoutHooks checkoutHooks) {
            this.checkoutHooks = checkoutHooks;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder addPaymentMethodPlugin(@NonNull final PaymentMethodPlugin paymentMethodPlugin,
            @NonNull final PaymentProcessor paymentProcessor) {
            paymentMethodPluginList.add(paymentMethodPlugin);
            paymentPlugins.put(paymentMethodPlugin.getId(), paymentProcessor);
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setPaymentProcessor(@NonNull final PaymentProcessor paymentProcessor) {
            paymentPlugins.put(PAYMENT_PROCESSOR_KEY, paymentProcessor);
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setDataInitializationTask(@NonNull final DataInitializationTask dataInitializationTask) {
            this.dataInitializationTask = dataInitializationTask;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setCustomLightFont(String lightFontPath, Context context) {
            this.lightFontPath = lightFontPath;
            if (lightFontPath != null) {
                setCustomFont(context, FontCache.CUSTOM_LIGHT_FONT, this.lightFontPath);
            }
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setCustomRegularFont(String regularFontPath, Context context) {
            this.regularFontPath = regularFontPath;
            if (regularFontPath != null) {
                setCustomFont(context, FontCache.CUSTOM_REGULAR_FONT, this.regularFontPath);
            }
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setCustomMonoFont(String monoFontPath, Context context) {
            this.monoFontPath = monoFontPath;
            if (monoFontPath != null) {
                setCustomFont(context, FontCache.CUSTOM_MONO_FONT, this.monoFontPath);
            }
            return this;
        }

        private void setCustomFont(Context context, String fontType, String fontPath) {
            Typeface typeFace;
            if (!FontCache.hasTypeface(fontType)) {
                typeFace = Typeface.createFromAsset(context.getAssets(), fontPath);
                FontCache.setTypeface(fontType, typeFace);
            }
        }

        private boolean hasPaymentDataDiscount() {
            return paymentData != null && paymentData.getDiscount() != null;
        }

        private boolean hasPaymentResultDiscount() {
            return paymentResult != null && paymentResult.getPaymentData() != null &&
                paymentResult.getPaymentData().getDiscount() != null;
        }

        private boolean hasTwoDiscountsSet() {
            return (hasPaymentDataDiscount() || hasPaymentResultDiscount()) && discount != null;
        }

        public MercadoPagoCheckout build() {
            if (hasTwoDiscountsSet()) {
                throw new IllegalStateException("payment data discount and discount set");
            }
            return new MercadoPagoCheckout(this);
        }
    }
}