package com.mercadopago.android.px.testcheckout.pages;

import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;
import com.mercadopago.android.px.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class OneTapPage extends PageObject<CheckoutValidator>{

    public OneTapPage() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public OneTapPage(final CheckoutValidator validator) {
    }

    public DiscountDetailPage pressOnDiscountDetail() {
        onView(withId(R.id.amount_with_discount)).perform(click());
        return new DiscountDetailPage(validator);
    }

    public SecurityCodeToCongratsPage pressConfirmButton() {
        onView(withId(R.id.px_button_primary)).perform(click());
        return new SecurityCodeToCongratsPage(validator);
    }

    @Override
    public OneTapPage validate(final CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }
}
