package com.mercadopago.android.px.utils;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.model.commission.ChargeRule;
import com.mercadopago.android.px.model.commission.PaymentMethodChargeRule;
import com.mercadopago.android.px.plugins.PaymentMethodPlugin;
import com.mercadopago.android.px.plugins.PaymentProcessor;
import com.mercadopago.android.px.plugins.SamplePaymentMethodPlugin;
import com.mercadopago.android.px.plugins.SamplePaymentProcessor;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.preferences.DiscountConfiguration;
import com.mercadopago.android.px.preferences.PaymentConfiguration;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

public final class PaymentConfigurationUtils {
    private PaymentConfigurationUtils() {
        //Do nothing
    }

    public static PaymentConfiguration create(@NonNull final CheckoutPreference checkoutPreference,
        @NonNull final PaymentProcessor paymentProcessor) {
        return new PaymentConfiguration.Builder(checkoutPreference, paymentProcessor).build();
    }

    public static PaymentConfiguration create(@NonNull final CheckoutPreference checkoutPreference) {
        return create(checkoutPreference, new SamplePaymentProcessor());
    }

    public static PaymentConfiguration create(@NonNull final CheckoutPreference checkoutPreference,
        @NonNull final PaymentProcessor paymentProcessor,
        @NonNull final PaymentMethodPlugin paymentMethodPlugin) {
        return new PaymentConfiguration.Builder(checkoutPreference, paymentProcessor)
            .addPaymentMethodPlugin(paymentMethodPlugin)
            .build();
    }

    public static PaymentConfiguration createWithPlugin(@NonNull final CheckoutPreference checkoutPreference,
        @NonNull final PaymentProcessor paymentProcessor) {
        return create(checkoutPreference, paymentProcessor, new SamplePaymentMethodPlugin());
    }

    @NonNull
    public static PaymentConfiguration createWithCharge(final String preferenceId,
        final String paymentMethodId) {
        return new PaymentConfiguration.Builder(preferenceId,
            new SamplePaymentProcessor(BusinessSamples.getBusinessRejected()))
            .addChargeRules(getCharge(paymentMethodId))
            .build();
    }

    @NonNull
    public static PaymentConfiguration createWithChargeAndDiscount(final String preferenceId,
        final String paymentMethodId) {
        return new PaymentConfiguration.Builder(preferenceId,
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
