package com.mercadopago.android.px.testcheckout.pages;

import com.mercadopago.android.px.testcheckout.R;
import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class DiscountCongratsPage extends PageObject<CheckoutValidator> {

    public DiscountCongratsPage(final CheckoutValidator validator) {
        super(validator);
    }

    public PaymentMethodPage pressContinueToPaymentMethod() {
        onView(withId(R.id.button)).perform(click());
        return new PaymentMethodPage(validator);
    }

    @Override
    public DiscountCongratsPage validate(final CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }
}
