package com.mercadopago.android.px.testcheckout.assertions;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.testcheckout.pages.DiscountDetailPage;
import com.mercadopago.android.px.testcheckout.pages.PaymentMethodPage;
import javax.annotation.Nonnull;
import org.hamcrest.Matcher;
import com.mercadopago.android.px.R;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class OneShotDiscountValidator extends DefaultValidator {

    @Nonnull private final Campaign campaign;

    public OneShotDiscountValidator(@Nonnull final Campaign campaign) {
        this.campaign = campaign;
    }

    @Override
    public void validate(@NonNull final DiscountDetailPage discountDetailPage) {
        super.validate(discountDetailPage);
        final Matcher<View> detail = withId(com.mercadopago.android.px.R.id.detail);

        onView(detail).check(matches(withText(com.mercadopago.android.px.R.string.px_one_shot_discount_detail)));

        final Matcher<View> subtitle = withId(com.mercadopago.android.px.R.id.subtitle);
        final String maxCouponAmount = "$ " + campaign.getMaxCouponAmount();
        final String maxCouponAmountSubtitle =
            getInstrumentation().getTargetContext().getString(R.string.px_max_coupon_amount, maxCouponAmount);
        onView(subtitle).check(matches(withText(maxCouponAmountSubtitle)));
    }

    @Override
    public void validate(@NonNull final PaymentMethodPage paymentMethodPage) {
        super.validate(paymentMethodPage);

        //TODO VALIDAR QUE SE VEA TOPE DE DESCUENTO
        final Matcher<View> amountDescription = withId(com.mercadopago.android.px.R.id.amount_description);
        final Matcher<View> maxCouponAmount = withId(com.mercadopago.android.px.R.id.max_coupon_amount);
        final Matcher<View> amountBeforeDiscount = withId(com.mercadopago.android.px.R.id.amount_before_discount);
        final Matcher<View> finalAmount = withId(com.mercadopago.android.px.R.id.final_amount);

        onView(amountDescription).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(maxCouponAmount).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(amountBeforeDiscount).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(finalAmount).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }
}
