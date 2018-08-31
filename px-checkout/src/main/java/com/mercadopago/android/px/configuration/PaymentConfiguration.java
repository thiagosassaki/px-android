package com.mercadopago.android.px.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.core.PaymentMethodPlugin;
import com.mercadopago.android.px.core.PaymentProcessor;
import com.mercadopago.android.px.model.commission.ChargeRule;
import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("unused")
public class PaymentConfiguration {

    @NonNull private final PaymentProcessor paymentProcessor;
    @NonNull private final ArrayList<PaymentMethodPlugin> paymentMethodPluginList;
    @NonNull private final ArrayList<ChargeRule> charges;
    @Nullable private final DiscountConfiguration discountConfiguration;

    protected PaymentConfiguration(@NonNull final PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
        paymentMethodPluginList = new ArrayList<>();
        charges = new ArrayList<>();
        discountConfiguration = null;
    }

    /* default */ PaymentConfiguration(@NonNull final Builder builder) {
        paymentProcessor = builder.paymentProcessor;
        paymentMethodPluginList = builder.paymentMethodPluginList;
        charges = builder.charges;
        discountConfiguration = builder.discountConfiguration;
    }

    @NonNull
    public PaymentProcessor getPaymentProcessor() {
        return paymentProcessor;
    }

    @NonNull
    public Collection<PaymentMethodPlugin> getPaymentMethodPluginList() {
        return paymentMethodPluginList;
    }

    @NonNull
    public ArrayList<ChargeRule> getCharges() {
        return charges;
    }

    @Nullable
    public DiscountConfiguration getDiscountConfiguration() {
        return discountConfiguration;
    }

    public static final class Builder {

        /* default */ @NonNull final PaymentProcessor paymentProcessor;
        /* default */ @NonNull final ArrayList<PaymentMethodPlugin> paymentMethodPluginList;
        /* default */ @NonNull ArrayList<ChargeRule> charges;
        /* default */ @Nullable DiscountConfiguration discountConfiguration;

        /**
         * @param paymentProcessor your custom payment processor.
         */
        public Builder(@NonNull final PaymentProcessor paymentProcessor) {
            this.paymentProcessor = paymentProcessor;
            paymentMethodPluginList = new ArrayList<>();
            charges = new ArrayList<>();
        }

        /**
         * Add your own payment method option to pay.
         *
         * @param paymentMethodPlugin your payment method plugin.
         * @return builder
         */
        public Builder addPaymentMethodPlugin(@NonNull final PaymentMethodPlugin paymentMethodPlugin) {
            paymentMethodPluginList.add(paymentMethodPlugin);
            return this;
        }

        /**
         * Add extra charges that will apply to total amount.
         *
         * @param charges the list of charges that could apply.
         * @return builder to keep operating
         */
        public Builder addChargeRules(@NonNull final Collection<ChargeRule> charges) {
            this.charges.addAll(charges);
            return this;
        }

        /**
         * {@link DiscountConfiguration} is an object that represents
         * the discount to be applied or error information to present to the user.
         * <p>
         * it's mandatory to handle your discounts by hand if you set a payment processor.
         *
         * @param discountConfiguration your custom discount configuration
         * @return builder to keep operating
         */
        public Builder setDiscountConfiguration(@NonNull final DiscountConfiguration discountConfiguration) {
            this.discountConfiguration = discountConfiguration;
            return this;
        }

        @NonNull
        public PaymentConfiguration build() {
            return new PaymentConfiguration(this);
        }
    }
}
