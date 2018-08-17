package com.mercadopago.android.px.testcheckout.pages;

import android.support.test.espresso.action.ViewActions;
import android.view.View;
import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class SecurityCodePage extends PageObject<CheckoutValidator> {

    public SecurityCodePage() {
    }

    public SecurityCodePage(final CheckoutValidator validator) {
        super(validator);
    }

    @Override
    public SecurityCodePage validate(CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }

    public IdentificationPage enterSecurityCode(final String escNumber) {
        Matcher<View> cardSecurityCodeEditTextMatcher = withId(com.mercadopago.android.px.R.id.mpsdkCardSecurityCode);
        Matcher<View> cardNextButtonTextMatcher = withId(com.mercadopago.android.px.R.id.mpsdkNextButtonText);
        onView(cardSecurityCodeEditTextMatcher).perform(typeText(escNumber));
        onView(cardNextButtonTextMatcher).perform(click());
        return new IdentificationPage(validator);
    }

    public NoCheckoutPage pressBackWithExclusion() {
        onView(isRoot()).perform(ViewActions.pressBack());
        return new NoCheckoutPage(validator);
    }

    public PaymentMethodPage pressBack() {
        onView(isRoot()).perform(ViewActions.pressBack());
        return new PaymentMethodPage(validator);
    }

    public ExpiryDatePage pressPrevious() {
        onView(withId(com.mercadopago.android.px.R.id.mpsdkBackButton)).perform(click());
        return new ExpiryDatePage(validator);
    }
}
