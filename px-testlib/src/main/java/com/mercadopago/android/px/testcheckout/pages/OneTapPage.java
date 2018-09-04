package com.mercadopago.android.px.testcheckout.pages;

import com.mercadopago.android.px.R;
import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class OneTapPage extends PageObject<CheckoutValidator> {

    protected OneTapPage() {

    }

    public OneTapPage(final CheckoutValidator validator) {
        super(validator);
    }

    @Override
    public PageObject validate(final CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }

    public CongratsPage pressConfirmButton() {
        onView(withId(R.id.px_confirm_button)).perform(click());
        return new CongratsPage(validator);
    }

    public PaymentMethodPage changePaymentMethod() {
        onView(withId(R.id.main_payment_method_container)).perform(click());
        return new PaymentMethodPage(validator);
    }
}
