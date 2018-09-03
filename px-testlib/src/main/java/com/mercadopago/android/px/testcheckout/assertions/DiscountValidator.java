package com.mercadopago.android.px.testcheckout.assertions;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;
import com.mercadopago.android.px.testcheckout.pages.InstallmentsPage;
import com.mercadopago.android.px.testcheckout.pages.OneTapPage;
import com.mercadopago.android.px.testcheckout.pages.PaymentMethodPage;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class DiscountValidator extends DefaultValidator {

    @Override
    public void validate(@NonNull final PaymentMethodPage paymentMethodPage) {
        super.validate(paymentMethodPage);
        validateDiscountRow();
    }

    @Override
    public void validate(@NonNull final InstallmentsPage installmentsPage) {
        super.validate(installmentsPage);
        validateDiscountRow();
    }

    @Override
    public void validate(@NonNull final OneTapPage oneTapPage) {
        super.validate(oneTapPage);
        final Matcher<View> amountWithDiscount = withId(com.mercadopago.android.px.R.id.amount_with_discount);
        final Matcher<View> discountMessage = withId(com.mercadopago.android.px.R.id.discount_message);
        final Matcher<View> discountMaxLabel = withId(com.mercadopago.android.px.R.id.discount_max_label);

        onView(amountWithDiscount).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(discountMessage).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(discountMaxLabel).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    private void validateDiscountRow() {
        final Matcher<View> amountDescription = withId(com.mercadopago.android.px.R.id.amount_description);
        final Matcher<View> maxCouponAmount = withId(com.mercadopago.android.px.R.id.max_coupon_amount);
        final Matcher<View> amountBeforeDiscount =
            withId(com.mercadopago.android.px.R.id.amount_before_discount);
        final Matcher<View> finalAmount = withId(com.mercadopago.android.px.R.id.final_amount);

        onView(amountDescription).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(maxCouponAmount).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(amountBeforeDiscount)
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(finalAmount).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }
}
