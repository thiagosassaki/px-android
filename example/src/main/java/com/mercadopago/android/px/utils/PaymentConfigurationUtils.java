package com.mercadopago.android.px.utils;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.configuration.DiscountConfiguration;
import com.mercadopago.android.px.configuration.PaymentConfiguration;
import com.mercadopago.android.px.core.PaymentMethodPlugin;
import com.mercadopago.android.px.core.PaymentProcessor;
import com.mercadopago.android.px.internal.features.plugins.SamplePaymentMethodPlugin;
import com.mercadopago.android.px.internal.features.plugins.SamplePaymentProcessor;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.model.commission.ChargeRule;
import com.mercadopago.android.px.model.commission.PaymentMethodChargeRule;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

public final class PaymentConfigurationUtils {
    private PaymentConfigurationUtils() {
        //Do nothing
    }

    public static PaymentConfiguration create(
        @NonNull final PaymentProcessor paymentProcessor) {
        return new PaymentConfiguration.Builder(paymentProcessor).build();
    }

    public static PaymentConfiguration create() {
        return create(new SamplePaymentProcessor());
    }

    public static PaymentConfiguration create(
        @NonNull final PaymentProcessor paymentProcessor,
        @NonNull final PaymentMethodPlugin paymentMethodPlugin) {
        return new PaymentConfiguration.Builder(paymentProcessor)
            .addPaymentMethodPlugin(paymentMethodPlugin)
            .build();
    }

    public static PaymentConfiguration createWithPlugin(
        @NonNull final PaymentProcessor paymentProcessor) {
        return create(paymentProcessor, new SamplePaymentMethodPlugin());
    }

    @NonNull
    public static PaymentConfiguration createWithCharge(
        final String paymentMethodId) {
        return new PaymentConfiguration.Builder(
            new SamplePaymentProcessor(BusinessSamples.getBusinessRejected()))
            .addChargeRules(getCharge(paymentMethodId))
            .build();
    }

    @NonNull
    public static PaymentConfiguration createWithChargeAndDiscount(
        final String paymentMethodId) {
        return new PaymentConfiguration.Builder(
            new SamplePaymentProcessor(BusinessSamples.getBusinessRejected()))
            .addChargeRules(getCharge(paymentMethodId))
            .setDiscountConfiguration(
                DiscountConfiguration.withDiscount(new Discount
                        .Builder("12344", Sites.ARGENTINA.getCurrencyId(), BigDecimal.TEN)
                        .setAmountOff(BigDecimal.TEN)
                        .build(),
                    new Campaign.Builder("12344")
                        .setMaxCouponAmount(BigDecimal.TEN)
                        .build())
            ).build();
    }

    @NonNull
    private static Collection<ChargeRule> getCharge(final String paymentMethodId) {
        final Collection<ChargeRule> charges = new ArrayList<>();
        charges.add(new PaymentMethodChargeRule(paymentMethodId, new BigDecimal(100)));
        return charges;
    }
}
