package com.mercadopago;

import android.app.Instrumentation;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.plugins.MainPaymentProcessor;
import com.mercadopago.android.px.plugins.SamplePaymentMethodPlugin;
import com.mercadopago.android.px.plugins.model.BusinessPayment;
import com.mercadopago.android.px.plugins.model.ExitAction;
import com.mercadopago.android.px.testcheckout.assertions.DefaultValidator;
import com.mercadopago.android.px.testcheckout.flows.DiscountTestFlow;
import com.mercadopago.android.px.testcheckout.idleresources.CheckoutResource;
import com.mercadopago.android.px.testcheckout.input.Country;
import com.mercadopago.android.px.testcheckout.input.FakeCard;
import com.mercadopago.android.px.testcheckout.input.Visa;
import com.mercadopago.android.px.testcheckout.pages.CongratsPage;
import com.mercadopago.android.px.testcheckout.pages.DiscountDetailPage;
import com.mercadopago.android.px.testcheckout.pages.PaymentMethodPage;
import com.mercadopago.android.testlib.HttpResource;
import com.mercadopago.example.R;
import java.math.BigDecimal;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DiscountTest {

    private static final String DIRECT_DISCOUNT_PUBLIC_KEY = "APP_USR-b8925182-e1bf-4c0e-bc38-1d893a19ab45";
    private static final String CODE_DISCOUNT_PUBLIC_KEY = "APP_USR-2e257493-3b80-4b71-8547-c841d035e8f2";

    private static final String DIRECT_DISCOUNT_PREFERENCE_ID = "241261700-459d4126-903c-4bad-bc05-82e5f13fa7d3";
    private static final String CODE_DISCOUNT_PREFERENCE_ID = "241261708-cd353b1b-940f-493b-b960-10106a24203c";

    private static final String MERCHANT_PUBLIC_KEY = "TEST-c6d9b1f9-71ff-4e05-9327-3c62468a23ee";
    private static final String PREFERENCE_ID = "243962506-0bb62e22-5c7b-425e-a0a6-c22d0f4758a9";

    private static final String BUSINESS_PAYMENT_IMAGE_URL =
        "https://www.jqueryscript.net/images/Simplest-Responsive-jQuery-Image-Lightbox-Plugin-simple-lightbox.jpg";
    private static final String BUSINESS_PAYMENT_TITLE = "Title";
    private static final String BUSINESS_PAYMENT_BUTTON_NAME = "ButtonSecondaryName";

    private static final String MERCHANT_DISCOUNT_ID = "77";
    private static final String MERCHANT_DISCOUNT_CURRENCY = "ARS";

    @Rule
    public HttpResource httpResource = new CheckoutResource();

    @Rule
    public ActivityTestRule<CheckoutExampleActivity> activityRule =
        new ActivityTestRule<>(CheckoutExampleActivity.class);

    private DiscountTestFlow discountTestFlow;
    private BusinessPayment businessPayment;
    private MainPaymentProcessor mainPaymentProcessor;
    private Discount discount;
    private Campaign campaign;
    private Visa card;

    @Before
    public void setUp() {
        final MercadoPagoCheckout.Builder builder =
            new MercadoPagoCheckout.Builder(DIRECT_DISCOUNT_PUBLIC_KEY, DIRECT_DISCOUNT_PREFERENCE_ID);
        discountTestFlow = new DiscountTestFlow(builder.build(), activityRule.getActivity());

        businessPayment = new BusinessPayment.Builder(BusinessPayment.Decorator.APPROVED,
            Payment.StatusCodes.STATUS_APPROVED,
            Payment.StatusDetail.STATUS_DETAIL_ACCREDITED,
            BUSINESS_PAYMENT_IMAGE_URL,
            BUSINESS_PAYMENT_TITLE)
            .setPaymentMethodVisibility(true)
            .setSecondaryButton(new ExitAction(BUSINESS_PAYMENT_BUTTON_NAME, 34))
            .build();

        mainPaymentProcessor = new MainPaymentProcessor(businessPayment);

        discount = new Discount.Builder(MERCHANT_DISCOUNT_ID, MERCHANT_DISCOUNT_CURRENCY, new BigDecimal(50))
            .setPercentOff(BigDecimal.TEN).build();
        campaign = new Campaign.Builder(MERCHANT_DISCOUNT_ID).setMaxCouponAmount(new BigDecimal(200)).setMaxRedeemPerUser(2).build();

        card = new Visa(FakeCard.CardState.APRO, Country.ARGENTINA);
    }

    @Test
    public void whenMerchantDiscountIsAlwaysOnAndHasPaymentProcessorThenShowMerchantDiscountAndGetCongrats() {
        final MercadoPagoCheckout.Builder builder =
            new MercadoPagoCheckout.Builder(MERCHANT_PUBLIC_KEY, PREFERENCE_ID)
                .setPaymentProcessor(mainPaymentProcessor)
                .addPaymentMethodPlugin(new SamplePaymentMethodPlugin(), mainPaymentProcessor)
                .setDiscount(discount, campaign);

        campaign = new Campaign.Builder(MERCHANT_DISCOUNT_ID).setMaxCouponAmount(new BigDecimal(200)).setMaxRedeemPerUser(2).build();

        discountTestFlow = new DiscountTestFlow(builder.build(), activityRule.getActivity());

        final CongratsPage congratsPage =
            discountTestFlow.runCreditCardPaymentFlowWithMerchantDiscountApplied(card, 1, new DefaultValidator() {
                @Override
                public void validate(@NonNull final DiscountDetailPage discountDetailPage) {
                    super.validate(discountDetailPage);
                    final Matcher<View> detail = withId(com.mercadopago.android.px.R.id.detail);

                    onView(detail).check(matches(withText(com.mercadopago.android.px.R.string.px_always_on_discount_detail)));

                    final Matcher<View> subtitle = withId(com.mercadopago.android.px.R.id.subtitle);
                    final String maxCouponAmount = "$ " + campaign.getMaxCouponAmount();
                    final String maxCouponAmountSubtitle = getInstrumentation().getTargetContext().getString(R.string.px_max_coupon_amount, maxCouponAmount);
                    onView(subtitle).check(matches(withText(maxCouponAmountSubtitle)));
                }

                @Override
                public void validate(@NonNull final PaymentMethodPage paymentMethodPage) {
                    super.validate(paymentMethodPage);

                    //TODO
                    final Matcher<View> amountDescription = withId(com.mercadopago.android.px.R.id.amount_description);
                    final Matcher<View> maxCouponAmount = withId(com.mercadopago.android.px.R.id.max_coupon_amount);
                    final Matcher<View> amountBeforeDiscount = withId(com.mercadopago.android.px.R.id.amount_before_discount);
                    final Matcher<View> finalAmount = withId(com.mercadopago.android.px.R.id.final_amount);

                    onView(amountDescription).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                    onView(maxCouponAmount).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                    onView(amountBeforeDiscount).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                    onView(finalAmount).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                }
            });
        assertNotNull(congratsPage);
    }

    @Test
    public void whenMerchantDiscountIsOneShotAndHasPaymentProcessorThenShowMerchantDiscountAndGetCongrats() {
        campaign = new Campaign.Builder(MERCHANT_DISCOUNT_ID).setMaxCouponAmount(new BigDecimal(200)).setMaxRedeemPerUser(1).build();

        final MercadoPagoCheckout.Builder builder =
            new MercadoPagoCheckout.Builder(MERCHANT_PUBLIC_KEY, PREFERENCE_ID)
                .setPaymentProcessor(mainPaymentProcessor)
                .addPaymentMethodPlugin(new SamplePaymentMethodPlugin(), mainPaymentProcessor)
                .setDiscount(discount, campaign);

        discountTestFlow = new DiscountTestFlow(builder.build(), activityRule.getActivity());

        final CongratsPage congratsPage =
            discountTestFlow.runCreditCardPaymentFlowWithMerchantDiscountApplied(card, 1, new DefaultValidator() {
                @Override
                public void validate(@NonNull final DiscountDetailPage discountDetailPage) {
                    super.validate(discountDetailPage);
                    final Matcher<View> detail = withId(com.mercadopago.android.px.R.id.detail);

                    onView(detail).check(matches(withText(com.mercadopago.android.px.R.string.px_one_shot_discount_detail)));

                    final Matcher<View> subtitle = withId(com.mercadopago.android.px.R.id.subtitle);
                    final String maxCouponAmount = "$ " + campaign.getMaxCouponAmount();
                    final String maxCouponAmountSubtitle = getInstrumentation().getTargetContext().getString(R.string.px_max_coupon_amount, maxCouponAmount);
                    onView(subtitle).check(matches(withText(maxCouponAmountSubtitle)));
                }

                @Override
                public void validate(@NonNull final PaymentMethodPage paymentMethodPage) {
                    super.validate(paymentMethodPage);

                    //TODO
                    final Matcher<View> amountDescription = withId(com.mercadopago.android.px.R.id.amount_description);
                    final Matcher<View> maxCouponAmount = withId(com.mercadopago.android.px.R.id.max_coupon_amount);
                    final Matcher<View> amountBeforeDiscount = withId(com.mercadopago.android.px.R.id.amount_before_discount);
                    final Matcher<View> finalAmount = withId(com.mercadopago.android.px.R.id.final_amount);

                    onView(amountDescription).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                    onView(maxCouponAmount).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                    onView(amountBeforeDiscount).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                    onView(finalAmount).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
                }
            });
        assertNotNull(congratsPage);
    }

    @Test
    public void whenMerchantDiscountIsAppliedAndHasNotPaymentProcessorThenNotShowDiscountAndGetCongrats() {
        final MercadoPagoCheckout.Builder builder =
            new MercadoPagoCheckout.Builder(MERCHANT_PUBLIC_KEY, PREFERENCE_ID)
                .setDiscount(discount, campaign);

        discountTestFlow = new DiscountTestFlow(builder.build(), activityRule.getActivity());

        final CongratsPage congratsPage =
            discountTestFlow.runCreditCardPaymentFlowWithoutPaymentProcessorWithMerchantDiscountApplied(card, 1, null);
        assertNotNull(congratsPage);
    }

    @Test
    public void whenMerchantDiscountWithMaxCouponAmountIsAppliedThenShowDiscountMaxCouponAmountAndGetCongrats() {
        final Campaign campaign = new Campaign.Builder("77").setMaxCouponAmount(new BigDecimal(200)).build();

        final MercadoPagoCheckout.Builder builder =
            new MercadoPagoCheckout.Builder(MERCHANT_PUBLIC_KEY, PREFERENCE_ID)
                .setPaymentProcessor(mainPaymentProcessor)
                .addPaymentMethodPlugin(new SamplePaymentMethodPlugin(), mainPaymentProcessor)
                .setDiscount(discount, campaign);

        discountTestFlow = new DiscountTestFlow(builder.build(), activityRule.getActivity());

        final CongratsPage congratsPage =
            discountTestFlow.runCreditCardPaymentFlowWithMerchantDiscountApplied(card, 1, null);
        assertNotNull(congratsPage);
    }

    @Test
    public void whenDirectDiscountIsAppliedAndPaidWithCreditCardThenShowDiscountAndGetCongrats() {
        final MercadoPagoCheckout.Builder builder =
            new MercadoPagoCheckout.Builder(DIRECT_DISCOUNT_PUBLIC_KEY, DIRECT_DISCOUNT_PREFERENCE_ID);
        discountTestFlow = new DiscountTestFlow(builder.build(), activityRule.getActivity());

        final Visa card = new Visa(FakeCard.CardState.APRO, Country.ARGENTINA);
        final CongratsPage congratsPage = discountTestFlow.runCreditCardPaymentFlowWithDiscountApplied(card, 1, null);
        assertNotNull(congratsPage);
    }

    @Test
    public void whenDirectDiscountIsAppliedAndPaidWithCashThenShowDiscountAndGetCongrats() {
        final MercadoPagoCheckout.Builder builder =
            new MercadoPagoCheckout.Builder(DIRECT_DISCOUNT_PUBLIC_KEY, DIRECT_DISCOUNT_PREFERENCE_ID);
        discountTestFlow = new DiscountTestFlow(builder.build(), activityRule.getActivity());

        final CongratsPage congratsPage = discountTestFlow.runCashPaymentFlowWithDiscountApplied(null);
        assertNotNull(congratsPage);
    }
}
