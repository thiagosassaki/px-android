package com.mercadopago.android.px.utils;

import android.support.v4.util.Pair;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.internal.features.plugins.SamplePaymentProcessor;
import com.mercadopago.android.px.configuration.DiscountConfiguration;
import com.mercadopago.android.px.configuration.PaymentConfiguration;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.ExitAction;
import com.mercadopago.android.px.model.Payment;
import java.math.BigDecimal;
import java.util.Collection;

import static com.mercadopago.android.px.utils.PaymentUtils.getBusinessPaymentApproved;

final class DiscountSamples {

    private static final String PK_WITH_DIRECT_DISCOUNT = "APP_USR-b8925182-e1bf-4c0e-bc38-1d893a19ab45";
    private static final String PREF_WITH_DIRECT_DISCOUNT = "241261700-459d4126-903c-4bad-bc05-82e5f13fa7d3";
    private static final String PK_WITH_DIRECT_DISCOUNT_MAX_REDEEM_PER_USER_3 =
        "APP_USR-9c40068d-d3ca-4f24-93bb-0c1f28138204";
    private static final String PREF_WITH_USED_UP_DISCOUNT = "336429666-d11db9cd-61a0-49ef-a4a7-3b6f15c8cb93";

    private static final String PK_WITH_CODE_DISCOUNT = "APP_USR-2e257493-3b80-4b71-8547-c841d035e8f2";
    private static final String PREF_WITH_CODE_DISCOUNT = "241261708-cd353b1b-940f-493b-b960-10106a24203c";

    private static final String BUSINESS_PAYMENT_IMAGE_URL =
        "https://www.jqueryscript.net/images/Simplest-Responsive-jQuery-Image-Lightbox-Plugin-simple-lightbox.jpg";
    private static final String BUSINESS_PAYMENT_TITLE = "Title";
    private static final String BUSINESS_PAYMENT_BUTTON_NAME = "ButtonSecondaryName";

    private static final String MERCHANT_DISCOUNT_ID = "77";
    private static final String MERCHANT_DISCOUNT_CURRENCY = "ARS";

    private DiscountSamples() {
    }

    public static void addAll(final Collection<Pair<String, MercadoPagoCheckout.Builder>> options) {
        options.add(
            new Pair<>("Discount - Direct",
                new MercadoPagoCheckout.Builder(PK_WITH_DIRECT_DISCOUNT, PREF_WITH_DIRECT_DISCOUNT)));
        options.add(new Pair<>("Discount - Code",
            new MercadoPagoCheckout.Builder(PK_WITH_CODE_DISCOUNT, PREF_WITH_CODE_DISCOUNT)));
        options.add(new Pair<>("Discount - Not available",
            getMercadoPagoBuilderWithNotAvailableDiscount(PK_WITH_DIRECT_DISCOUNT_MAX_REDEEM_PER_USER_3,
                PREF_WITH_USED_UP_DISCOUNT)));
        options.add(new Pair<>("Discount - Always on merchant discount with percent off",
            getMercadoPagoBuilderWithAlwaysOnDiscountWithPercentOff(PK_WITH_CODE_DISCOUNT, PREF_WITH_CODE_DISCOUNT)));
        options.add(new Pair<>("Discount - Always on merchant discount with amount off",
            getMercadoPagoBuilderWithAlwaysOnDiscountWithAmountOff(PK_WITH_CODE_DISCOUNT, PREF_WITH_CODE_DISCOUNT)));
        options.add(new Pair<>("Discount - One shot merchant discount",
            getMercadoPagoBuilderWithOneShotDiscount(PK_WITH_CODE_DISCOUNT, PREF_WITH_CODE_DISCOUNT)));
    }

    private static MercadoPagoCheckout.Builder getMercadoPagoBuilderWithNotAvailableDiscount(final String publicKey,
        final String prefId) {

        final SamplePaymentProcessor mainPaymentProcessor = new SamplePaymentProcessor(getBusinessPaymentApproved());
        return new MercadoPagoCheckout.Builder(publicKey, prefId,
            new PaymentConfiguration.Builder(mainPaymentProcessor).setDiscountConfiguration(
                DiscountConfiguration.forNotAvailableDiscount()).build());
    }

    private static MercadoPagoCheckout.Builder getMercadoPagoBuilderWithAlwaysOnDiscountWithPercentOff(
        final String publicKey,
        final String prefId) {
        final Campaign campaign =
            new Campaign.Builder(MERCHANT_DISCOUNT_ID).setMaxCouponAmount(new BigDecimal(200)).setMaxRedeemPerUser(5)
                .build();

        final DiscountConfiguration discountConfiguration = DiscountConfiguration.withDiscount(getDiscount(), campaign);
        final PaymentConfiguration paymentConfiguration = new PaymentConfiguration.Builder(getMainPaymentProcessor())
            .setDiscountConfiguration(discountConfiguration)
            .build();

        return new MercadoPagoCheckout.Builder(publicKey, prefId, paymentConfiguration);
    }

    private static MercadoPagoCheckout.Builder getMercadoPagoBuilderWithAlwaysOnDiscountWithAmountOff(
        final String publicKey,
        final String prefId) {
        final Discount discount =
            new Discount.Builder(MERCHANT_DISCOUNT_ID, MERCHANT_DISCOUNT_CURRENCY, new BigDecimal(50))
                .setAmountOff(new BigDecimal(50)).build();
        final Campaign campaign =
            new Campaign.Builder(MERCHANT_DISCOUNT_ID).setMaxCouponAmount(new BigDecimal(200)).setMaxRedeemPerUser(5)
                .build();

        final DiscountConfiguration discountConfiguration = DiscountConfiguration.withDiscount(discount, campaign);
        final PaymentConfiguration paymentConfiguration = new PaymentConfiguration.Builder(getMainPaymentProcessor())
            .setDiscountConfiguration(discountConfiguration)
            .build();

        return new MercadoPagoCheckout.Builder(publicKey, prefId, paymentConfiguration);
    }

    private static MercadoPagoCheckout.Builder getMercadoPagoBuilderWithOneShotDiscount(final String publicKey,
        final String prefId) {
        final Campaign campaign =
            new Campaign.Builder(MERCHANT_DISCOUNT_ID).setMaxCouponAmount(new BigDecimal(200)).setMaxRedeemPerUser(1)
                .build();

        final DiscountConfiguration discountConfiguration = DiscountConfiguration.withDiscount(getDiscount(), campaign);
        final PaymentConfiguration paymentConfiguration = new PaymentConfiguration.Builder(getMainPaymentProcessor())
            .setDiscountConfiguration(discountConfiguration)
            .build();

        return new MercadoPagoCheckout.Builder(publicKey, prefId, paymentConfiguration);
    }

    private static SamplePaymentProcessor getMainPaymentProcessor() {
        final BusinessPayment businessPayment = new BusinessPayment.Builder(BusinessPayment.Decorator.APPROVED,
            Payment.StatusCodes.STATUS_APPROVED,
            Payment.StatusDetail.STATUS_DETAIL_ACCREDITED,
            BUSINESS_PAYMENT_IMAGE_URL,
            BUSINESS_PAYMENT_TITLE)
            .setPaymentMethodVisibility(true)
            .setSecondaryButton(new ExitAction(BUSINESS_PAYMENT_BUTTON_NAME, 34))
            .build();

        return new SamplePaymentProcessor(businessPayment);
    }

    private static Discount getDiscount() {
        return new Discount.Builder(MERCHANT_DISCOUNT_ID, MERCHANT_DISCOUNT_CURRENCY, new BigDecimal(50))
            .setPercentOff(BigDecimal.TEN).build();
    }
}
