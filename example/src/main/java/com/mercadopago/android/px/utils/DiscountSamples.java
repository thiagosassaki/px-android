package com.mercadopago.android.px.utils;

import android.support.v4.util.Pair;

import com.mercadopago.android.px.core.MercadoPagoCheckout;

import com.mercadopago.android.px.plugins.MainPaymentProcessor;
import java.util.Collection;

import static com.mercadopago.android.px.utils.ExamplesUtils.getBusinessPaymentApproved;

final class DiscountSamples {

    private static final String PK_WITH_DIRECT_DISCOUNT = "APP_USR-b8925182-e1bf-4c0e-bc38-1d893a19ab45";
    private static final String PREF_WITH_DIRECT_DISCOUNT = "241261700-459d4126-903c-4bad-bc05-82e5f13fa7d3";
    private static final String PK_WITH_DIRECT_DISCOUNT_MAX_REDEEM_PER_USER_3 = "APP_USR-9c40068d-d3ca-4f24-93bb-0c1f28138204";
    private static final String PREF_WITH_USED_UP_DISCOUNT = "336429666-d11db9cd-61a0-49ef-a4a7-3b6f15c8cb93";

    private static final String PK_WITH_CODE_DISCOUNT = "APP_USR-2e257493-3b80-4b71-8547-c841d035e8f2";
    private static final String PREF_WITH_CODE_DISCOUNT = "241261708-cd353b1b-940f-493b-b960-10106a24203c";

    private DiscountSamples() {
    }

    public static void addAll(final Collection<Pair<String, MercadoPagoCheckout.Builder>> options) {
        options.add(
            new Pair<>("Direct discount", getMercadoPagoBuilder(PK_WITH_DIRECT_DISCOUNT, PREF_WITH_DIRECT_DISCOUNT)));
        options.add(new Pair<>("Code discount", getMercadoPagoBuilder(PK_WITH_CODE_DISCOUNT, PREF_WITH_CODE_DISCOUNT)));
        options.add(new Pair<>("Discount not available",
            getMercadoPagoBuilderWithNotAvailableDiscount(PK_WITH_DIRECT_DISCOUNT_MAX_REDEEM_PER_USER_3,
                PREF_WITH_USED_UP_DISCOUNT)));
    }

    private static MercadoPagoCheckout.Builder getMercadoPagoBuilderWithNotAvailableDiscount(final String publicKey,
        final String prefId) {

        final MainPaymentProcessor mainPaymentProcessor = new MainPaymentProcessor(getBusinessPaymentApproved());
        return getMercadoPagoBuilder(publicKey, prefId)
            .discountNotAvailable()
            .setPaymentProcessor(mainPaymentProcessor);
    }

    private static MercadoPagoCheckout.Builder getMercadoPagoBuilder(final String publicKey, final String prefId) {
        return new MercadoPagoCheckout.Builder(publicKey, prefId);
    }
}
