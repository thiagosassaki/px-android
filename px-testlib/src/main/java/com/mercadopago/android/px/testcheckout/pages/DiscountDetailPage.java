package com.mercadopago.android.px.testcheckout.pages;

import com.mercadopago.android.px.testcheckout.assertions.CheckoutValidator;
import com.mercadopago.android.testlib.pages.PageObject;

import static android.support.test.espresso.Espresso.pressBack;

public class DiscountDetailPage extends PageObject<CheckoutValidator> {

    public DiscountDetailPage() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public DiscountDetailPage(final CheckoutValidator validator) {
        super(validator);
    }

    public PaymentMethodPage pressCloseToPaymentMethod() {
        pressBack();
        return new PaymentMethodPage(validator);
    }

    public InstallmentsPage pressCloseToInstallments() {
        pressBack();
        return new InstallmentsPage(validator);
    }

    public OneTapPage pressCloseToOneTap() {
        pressBack();
        return new OneTapPage(validator);
    }

    @Override
    public DiscountDetailPage validate(final CheckoutValidator validator) {
        validator.validate(this);
        return this;
    }
}
