package com.mercadopago.android.px.testcheckout.assertions;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.testcheckout.pages.InstallmentsPage;
import com.mercadopago.android.px.testcheckout.pages.OneTapPage;
import com.mercadopago.android.px.testcheckout.pages.PaymentMethodPage;
import com.mercadopago.android.px.testcheckout.pages.ReviewAndConfirmPage;
import javax.annotation.Nonnull;
import org.hamcrest.Matcher;
import com.mercadopago.android.px.R;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasTextColor;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public abstract class DiscountValidator extends DefaultValidator {

    @Nonnull protected final Campaign campaign;
    @Nonnull protected final Discount discount;

    public DiscountValidator(@Nonnull final Campaign campaign, @NonNull final Discount discount) {
        this.campaign = campaign;
        this.discount = discount;
    }

    @Override
    public void validate(@NonNull final PaymentMethodPage paymentMethodPage) {
        validateDiscountRow();
    }

    @Override
    public void validate(@NonNull final InstallmentsPage installmentsPage) {
        validateDiscountRow();
    }

    @Override
    public void validate(@NonNull final ReviewAndConfirmPage reviewAndConfirmPage) {
        final Matcher<View> discountDescription = withText(R.string.px_review_summary_discount);
        onView(discountDescription).check(matches(hasTextColor(R.color.px_discount_description)));
    }

    @Override
    public void validate(@NonNull final OneTapPage oneTapPage) {
        final Matcher<View> amountWithDiscount = withId(com.mercadopago.android.px.R.id.amount_with_discount);
        final Matcher<View> discountMessage = withId(com.mercadopago.android.px.R.id.discount_message);
        final Matcher<View> discountMaxLabel = withId(com.mercadopago.android.px.R.id.discount_max_label);

        onView(amountWithDiscount).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(discountMessage).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(discountMaxLabel).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(discountMessage).check(matches(withText(getAmountDescription())));
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

        onView(amountDescription).check(matches(withText(getAmountDescription())));
    }

    private String getAmountDescription() {
        final String amountDescriptionMessage;

        if (discount.hasPercentOff()) {
            amountDescriptionMessage = getInstrumentation().getTargetContext()
                .getString(com.mercadopago.android.px.R.string.px_discount_percent_off_percent,
                    discount.getPercentOff());
        } else {
            amountDescriptionMessage = getInstrumentation().getTargetContext()
                .getString(com.mercadopago.android.px.R.string.px_discount_amount_off, discount.getAmountOff());
        }

        return amountDescriptionMessage;
    }
}
