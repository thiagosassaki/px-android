package com.mercadopago.android.px.testcheckout.assertions;

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;
import com.mercadopago.android.px.testcheckout.pages.CardPage;
import com.mercadopago.android.px.testcheckout.pages.CashPage;
import com.mercadopago.android.px.testcheckout.pages.CongratsPage;
import com.mercadopago.android.px.testcheckout.pages.CreditCardPage;
import com.mercadopago.android.px.testcheckout.pages.DebitCardPage;
import com.mercadopago.android.px.testcheckout.pages.DiscountCodeInputPage;
import com.mercadopago.android.px.testcheckout.pages.DiscountCongratsPage;
import com.mercadopago.android.px.testcheckout.pages.DiscountDetailPage;
import com.mercadopago.android.px.testcheckout.pages.ExpiryDatePage;
import com.mercadopago.android.px.testcheckout.pages.IdentificationPage;
import com.mercadopago.android.px.testcheckout.pages.InstallmentsPage;
import com.mercadopago.android.px.testcheckout.pages.IssuerPage;
import com.mercadopago.android.px.testcheckout.pages.NamePage;
import com.mercadopago.android.px.testcheckout.pages.NoCheckoutPage;
import com.mercadopago.android.px.testcheckout.pages.OneTapPage;
import com.mercadopago.android.px.testcheckout.pages.PaymentMethodPage;
import com.mercadopago.android.px.testcheckout.pages.ReviewAndConfirmPage;
import com.mercadopago.android.px.testcheckout.pages.ReviewPaymentMethodsPage;
import com.mercadopago.android.px.testcheckout.pages.SecurityCodePage;
import com.mercadopago.android.px.testcheckout.pages.SecurityCodeToCongratsPage;
import org.hamcrest.Matcher;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class DefaultValidator implements CheckoutValidator {

    @Override
    public void validate(@NonNull final IssuerPage issuerPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final NamePage namePage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final PaymentMethodPage paymentMethodPage) {
        validateAmountView();
    }

    @Override
    public void validate(@NonNull final ReviewAndConfirmPage reviewAndConfirmPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final SecurityCodePage securityCodePage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final NoCheckoutPage noCheckoutPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final CardPage cardPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final CashPage cashPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final CongratsPage congratsPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final CreditCardPage creditCardPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final DebitCardPage debitCardPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final ExpiryDatePage expiryDatePage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final IdentificationPage identificationPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final InstallmentsPage installmentsPage) {
        validateAmountView();
    }

    @Override
    public void validate(@NonNull final ReviewPaymentMethodsPage reviewPaymentMethodsPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final DiscountDetailPage discountDetailPage) {
        final Matcher<View> discountDetailLine = withId(com.mercadopago.android.px.R.id.px_discount_detail_line);
        final Matcher<View> discountSubDetails = withId(com.mercadopago.android.px.R.id.px_discount_sub_details);

        onView(discountDetailLine).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(discountSubDetails).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(discountSubDetails)
            .check(matches(withText(com.mercadopago.android.px.R.string.px_we_apply_the_best_available_discount)));
    }

    @Override
    public void validate(@NonNull final DiscountCodeInputPage discountCodeInputPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final DiscountCongratsPage discountCongratsPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final SecurityCodeToCongratsPage securityCodeToCongratsPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final OneTapPage oneTapPage) {
        //TODO implement default PX Validations
    }

    private void validateAmountView() {
        final Matcher<View> amountDescription = withId(com.mercadopago.android.px.R.id.amount_description);
        final Matcher<View> maxCouponAmount = withId(com.mercadopago.android.px.R.id.max_coupon_amount);
        final Matcher<View> amountBeforeDiscount =
            withId(com.mercadopago.android.px.R.id.amount_before_discount);
        final Matcher<View> finalAmount = withId(com.mercadopago.android.px.R.id.final_amount);
        final Matcher<View> arrow = withId(com.mercadopago.android.px.R.id.blue_arrow);

        onView(amountDescription).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(maxCouponAmount).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(amountBeforeDiscount).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(finalAmount).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(arrow).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        onView(amountDescription).check(matches(withText(
            getInstrumentation().getTargetContext().getString(com.mercadopago.android.px.R.string.px_total_to_pay))));
    }

}
