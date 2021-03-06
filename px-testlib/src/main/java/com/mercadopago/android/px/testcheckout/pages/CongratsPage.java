package com.mercadopago.android.px.testcheckout.pages;

import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

public class CongratsPage extends PageObject<CheckoutValidator> {

    public CongratsPage() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public CongratsPage(CheckoutValidator validator) {
        super(validator);
    }

    @Override
    public CongratsPage validate(CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }
}
