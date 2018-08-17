package com.mercadopago.android.px.utils;

import android.support.v4.util.Pair;
import com.mercadopago.android.px.configuration.PaymentConfiguration;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.internal.features.plugins.SamplePaymentProcessor;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.commission.ChargeRule;
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import static com.mercadopago.android.px.utils.PaymentConfigurationUtils.createWithCharge;
import static com.mercadopago.android.px.utils.PaymentConfigurationUtils.createWithChargeAndDiscount;

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
        options.add(new Pair<>("Extra charges/Discount - AccountMoney - Business", chargeAndDiscount("account_money")));
        options.add(new Pair<>("Extra charges/Discount - Visa - Business", chargeAndDiscount("visa")));
        options.add(new Pair<>("Extra charges/Discount - RapiPago - Business", chargeAndDiscount("rapipago")));
        options.add(new Pair<>("Extra charges/Discount - AccountMoney - Business", chargeAndDiscount("account_money")));
    }

    private static MercadoPagoCheckout.Builder chargeType(final String type) {
        final Collection<ChargeRule> charges = new ArrayList<>();
        charges.add(new PaymentTypeChargeRule(type, BigDecimal.TEN));
        final BusinessPayment payment = BusinessSamples.getBusinessRejected();

        return new MercadoPagoCheckout.Builder(PK, PREF,
            new PaymentConfiguration.Builder(new SamplePaymentProcessor(payment))
                .addChargeRules(charges)
                .build());
    }

    private static MercadoPagoCheckout.Builder charge(final String paymentMethodId) {
        return new MercadoPagoCheckout.Builder(PK, PREF, createWithCharge(paymentMethodId));
    }

    private static MercadoPagoCheckout.Builder chargeAndDiscount(final String paymentMethodId) {
        return new MercadoPagoCheckout.Builder(PK, PREF, createWithChargeAndDiscount(paymentMethodId));
    }
}
