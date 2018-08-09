package com.mercadopago.android.px.utils;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.model.commission.ChargeRule;
import com.mercadopago.android.px.model.commission.PaymentMethodChargeRule;
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule;
import com.mercadopago.android.px.plugins.SamplePaymentProcessor;
import com.mercadopago.android.px.preferences.DiscountConfiguration;
import com.mercadopago.android.px.preferences.PaymentConfiguration;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import static com.mercadopago.android.px.utils.BusinessSamples.startCompleteApprovedBusiness;

final class ChargesSamples {

    private static final String PK = "TEST-c6d9b1f9-71ff-4e05-9327-3c62468a23ee";
    private static final String PREF = "243962506-0bb62e22-5c7b-425e-a0a6-c22d0f4758a9";

    private ChargesSamples() {
    }

    public static void addAll(final Collection<Pair<String, MercadoPagoCheckout.Builder>> options) {
        options.add(new Pair<>("Extra charges - Master", charge("master")));
        options.add(new Pair<>("Extra charges - CreditCard", chargeType(PaymentTypes.CREDIT_CARD)));
        options.add(new Pair<>("Extra charges - Visa", charge("visa")));
        options.add(new Pair<>("Extra charges - RapiPago", charge("rapipago")));
        options.add(new Pair<>("Extra charges - Visa - Business", chargeWithBusiness("visa")));
        options.add(new Pair<>("Extra charges - RapiPago - Business", chargeWithBusiness("rapipago")));
        options.add(new Pair<>("Extra charges/Discount - AccountMoney - Business", chargeAndDiscount("account_money")));
        options.add(new Pair<>("Extra charges/Discount - Visa - Business", chargeAndDiscount("visa")));
        options.add(new Pair<>("Extra charges/Discount - RapiPago - Business", chargeAndDiscount("rapipago")));
        options.add(new Pair<>("Extra charges/Discount - AccountMoney - Business", chargeAndDiscount("account_money")));
    }

    private static MercadoPagoCheckout.Builder chargeType(final String type) {
        final Collection<ChargeRule> charges = new ArrayList<>();
        charges.add(new PaymentTypeChargeRule(type, BigDecimal.TEN));
        return new MercadoPagoCheckout.Builder(PK, PREF).setPaymentConfiguration(
            new PaymentConfiguration.Builder(new SamplePaymentProcessor(BusinessSamples.getBusinessRejected()))
                .addChargeRules(charges)
                .build());
    }

    private static MercadoPagoCheckout.Builder chargeWithBusiness(final String paymentMethodId) {
        return startCompleteApprovedBusiness().setPaymentConfiguration(
            getPaymentConfig(paymentMethodId).build());
    }

    @NonNull
    private static PaymentConfiguration.Builder getPaymentConfig(final String paymentMethodId) {
        return new PaymentConfiguration.Builder(new SamplePaymentProcessor(BusinessSamples.getBusinessRejected()))
            .addChargeRules(getCharge(paymentMethodId));
    }

    private static MercadoPagoCheckout.Builder charge(final String paymentMethodId) {
        return new MercadoPagoCheckout.Builder(PK, PREF).setPaymentConfiguration(
            getPaymentConfig(paymentMethodId).build());
    }

    private static MercadoPagoCheckout.Builder chargeAndDiscount(final String paymentMethodId) {
        return chargeWithBusiness(paymentMethodId)
            .setPaymentConfiguration(getPaymentConfig(paymentMethodId)
                .setDiscountConfiguration(
                    DiscountConfiguration.withDiscount(new Discount
                    .Builder("12344", Sites.ARGENTINA.getCurrencyId(), BigDecimal.TEN)
                    .setAmountOff(BigDecimal.TEN)
                    .build(),
                new Campaign.Builder("12344")
                    .setMaxCouponAmount(BigDecimal.TEN)
                    .build())
                ).build());
    }

    @NonNull
    private static Collection<ChargeRule> getCharge(final String paymentMethodId) {
        final Collection<ChargeRule> charges = new ArrayList<>();
        charges.add(new PaymentMethodChargeRule(paymentMethodId, new BigDecimal(100)));
        return charges;
    }
}
