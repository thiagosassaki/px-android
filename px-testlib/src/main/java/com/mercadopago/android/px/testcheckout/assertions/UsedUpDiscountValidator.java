package com.mercadopago.android.px.testcheckout.assertions;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;
import com.mercadopago.android.px.testcheckout.pages.DiscountDetailPage;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class UsedUpDiscountValidator extends DiscountValidator {

    @Override
    public void validate(@NonNull final DiscountDetailPage discountDetailPage) {
        // We don't want DefaultValidator's behaviour. We are not calling super's discount detail validations intentionally.
        final Matcher<View> discountDetailLine = withId(com.mercadopago.android.px.R.id.px_discount_detail_line);
        final Matcher<View> discountSubDetails = withId(com.mercadopago.android.px.R.id.px_discount_sub_details);

        onView(discountDetailLine).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(discountSubDetails).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }
}
