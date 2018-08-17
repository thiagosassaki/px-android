package com.mercadopago.android.px.testcheckout.pages;

import android.support.test.espresso.contrib.RecyclerViewActions;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class PaymentMethodPage extends PageObject<CheckoutValidator> {

    protected PaymentMethodPage() {
    }

    public PaymentMethodPage(final CheckoutValidator validator) {
        super(validator);
    }

    public SecurityCodePage selectSavedDebitCard() {
        onView(withId(R.id.mpsdkGroupsList))
            .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        return new SecurityCodePage(validator);
    }

    public CardPage selectCard() {
        onView(withId(R.id.mpsdkGroupsList))
            .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        return new CardPage(validator);
    }

    public CashPage selectCash() {
        onView(withId(R.id.mpsdkGroupsList))
            .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        return new CashPage(validator);
    }

    public DiscountDetailPage pressOnDiscountDetail() {
        onView(withId(R.id.amount_view)).perform(click());
        return new DiscountDetailPage(validator);
    }

    public DiscountCodeInputPage pressOnDiscountCodeInput() {
        onView(withId(R.id.amount_view)).perform(click());
        return new DiscountCodeInputPage(validator);
    }

    @Override
    public PaymentMethodPage validate(CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }
}
