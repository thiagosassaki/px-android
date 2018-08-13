package com.mercadopago.android.px.testcheckout.assertions;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.testcheckout.pages.CardPage;
import com.mercadopago.android.px.testcheckout.pages.CashPage;
import com.mercadopago.android.px.testcheckout.pages.CongratsPage;
import com.mercadopago.android.px.testcheckout.pages.CreditCardPage;
import com.mercadopago.android.px.testcheckout.pages.DebitCardPage;
import com.mercadopago.android.px.testcheckout.pages.DiscountCodeInputPage;
import com.mercadopago.android.px.testcheckout.pages.DiscountCongratsPage;
import com.mercadopago.android.px.testcheckout.pages.DiscountDetailPage;
import com.mercadopago.android.px.testcheckout.pages.ExpiryDatePage;
import com.mercadopago.android.px.testcheckout.pages.IdentificationPage;
import com.mercadopago.android.px.testcheckout.pages.InstallmentsPage;
import com.mercadopago.android.px.testcheckout.pages.IssuerPage;
import com.mercadopago.android.px.testcheckout.pages.NamePage;
import com.mercadopago.android.px.testcheckout.pages.NoCheckoutPage;
import com.mercadopago.android.px.testcheckout.pages.PaymentMethodPage;
import com.mercadopago.android.px.testcheckout.pages.ReviewAndConfirmPage;
import com.mercadopago.android.px.testcheckout.pages.ReviewPaymentMethodsPage;
import com.mercadopago.android.px.testcheckout.pages.SecurityCodePage;

public class DefaultValidator implements CheckoutValidator {
    @Override
    public void validate(@NonNull final IssuerPage issuerPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final NamePage namePage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final PaymentMethodPage paymentMethodPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final ReviewAndConfirmPage reviewAndConfirmPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final SecurityCodePage securityCodePage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final NoCheckoutPage noCheckoutPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final CardPage cardPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final CashPage cashPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final CongratsPage congratsPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final CreditCardPage creditCardPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final DebitCardPage debitCardPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final ExpiryDatePage expiryDatePage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final IdentificationPage identificationPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final InstallmentsPage installmentsPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final ReviewPaymentMethodsPage reviewPaymentMethodsPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final DiscountDetailPage discountDetailPage) {


    }

    @Override
    public void validate(@NonNull final DiscountCodeInputPage discountCodeInputPage) {
        //TODO implement default PX Validations
    }

    @Override
    public void validate(@NonNull final DiscountCongratsPage discountCongratsPage) {
        //TODO implement default PX Validations
    }
}
