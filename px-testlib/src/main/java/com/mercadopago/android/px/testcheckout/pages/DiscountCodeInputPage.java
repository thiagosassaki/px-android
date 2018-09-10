package com.mercadopago.android.px.testcheckout.pages;

import android.support.annotation.NonNull;
import android.view.View;
import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;
import org.hamcrest.Matcher;
import com.mercadopago.android.px.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class DiscountCodeInputPage extends PageObject<CheckoutValidator> {

    public DiscountCodeInputPage(final CheckoutValidator validator) {
        super(validator);
    }

    public DiscountCodeInputPage focusInputCode() {
        final Matcher<View> codeInput = withId(R.id.text_field);
        onView(codeInput).perform(click());
        return new DiscountCodeInputPage(validator);
    }

    public DiscountCongratsPage enterDiscountCode(@NonNull final String discountCode) {
        final Matcher<View> codeInput = withId(R.id.ui_text_field_input);
        final Matcher<View> confirmButton = withId(R.id.confirm_button);

        onView(codeInput).perform(typeText(discountCode));

        onView(confirmButton).perform(click());

        return new DiscountCongratsPage(validator);
    }

    @Override
    public DiscountCodeInputPage validate(final CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }
}
