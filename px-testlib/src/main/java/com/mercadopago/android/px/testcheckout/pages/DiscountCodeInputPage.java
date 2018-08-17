package com.mercadopago.android.px.testcheckout.pages;

import android.view.View;
import com.mercadopago.android.px.testcheckout.R;
import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class DiscountCodeInputPage extends PageObject<CheckoutValidator> {

    private static String DISCOUNT_CODE = "prueba";

    public DiscountCodeInputPage(final CheckoutValidator validator) {
        super(validator);
    }

    public DiscountCodeInputPage focusInputCode() {
        final Matcher<View> codeInput = withId(com.mercadopago.android.px.R.id.text_field);
        onView(codeInput).perform(click());
        return new DiscountCodeInputPage(validator);
    }

    public DiscountCongratsPage enterDiscountCode() {
        final Matcher<View> codeInput = withId(com.mercadopago.android.px.R.id.text_field);
        final Matcher<View> confirmButton = withId(com.mercadopago.android.px.R.id.confirm_button);

        onView(codeInput).perform(click());
        onView(codeInput).perform(typeText(DISCOUNT_CODE));
        onView(confirmButton).perform(click());

        return new DiscountCongratsPage(validator);
    }

    @Override
    public DiscountCodeInputPage validate(final CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }
}
