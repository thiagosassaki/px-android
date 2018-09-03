package com.mercadopago.android.px.testcheckout.assertions;

import android.support.annotation.NonNull;
import android.view.View;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.testcheckout.pages.DiscountDetailPage;
import javax.annotation.Nonnull;
import org.hamcrest.Matcher;
import com.mercadopago.android.px.R;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class OneShotDiscountValidator extends DiscountValidator {

    public OneShotDiscountValidator(@Nonnull final Campaign campaign, @NonNull final Discount discount) {
        super(campaign, discount);
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
}
