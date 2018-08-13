package com.mercadopago.android.px.testcheckout.pages;

import android.view.View;
import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;
import javax.annotation.Nonnull;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class SecurityCodeToCongratsPage extends PageObject<CheckoutValidator> {

    public SecurityCodeToCongratsPage() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public SecurityCodeToCongratsPage(@Nonnull final CheckoutValidator validator) {
        super(validator);
    }

    public CongratsPage enterSecurityCode(final String escNumber) {
        Matcher<View> cardSecurityCodeEditTextMatcher = withId(com.mercadopago.android.px.R.id.mpsdkCardSecurityCode);
        Matcher<View> cardNextButtonTextMatcher = withId(com.mercadopago.android.px.R.id.mpsdkNextButtonText);
        onView(cardSecurityCodeEditTextMatcher).perform(typeText(escNumber));
        onView(cardNextButtonTextMatcher).perform(click());
        return new CongratsPage(validator);
    }

    @Override
    public SecurityCodeToCongratsPage validate(final CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }
}
