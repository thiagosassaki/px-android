package com.mercadopago.android.px.testcheckout.assertions;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.testcheckout.pages.CardPage;
import com.mercadopago.android.px.testcheckout.pages.CashPage;
import com.mercadopago.android.px.testcheckout.pages.CongratsPage;
import com.mercadopago.android.px.testcheckout.pages.CreditCardPage;
import com.mercadopago.android.px.testcheckout.pages.DebitCardPage;
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
import com.mercadopago.android.px.testcheckout.pages.DiscountDetailPage;
import com.mercadopago.android.px.testcheckout.pages.DiscountCodeInputPage;
import com.mercadopago.android.px.testcheckout.pages.DiscountCongratsPage;
import com.mercadopago.android.testlib.assertions.Validator;

public interface CheckoutValidator extends Validator {

    void validate(@NonNull final CardPage cardPage);

    void validate(@NonNull final CashPage cashPage);

    void validate(@NonNull final CongratsPage congratsPage);

    void validate(@NonNull final CreditCardPage creditCardPage);

    void validate(@NonNull final DebitCardPage debitCardPage);

    void validate(@NonNull final ExpiryDatePage expiryDatePage);

    void validate(@NonNull final IdentificationPage identificationPage);

    void validate(@NonNull final InstallmentsPage installmentsPage);

    void validate(@NonNull final IssuerPage issuerPage);

    void validate(@NonNull final NamePage namePage);

    void validate(@NonNull final PaymentMethodPage paymentMethodPage);

    void validate(@NonNull final ReviewAndConfirmPage reviewAndConfirmPage);

    void validate(@NonNull final SecurityCodePage securityCodePage);

    void validate(@NonNull final NoCheckoutPage noCheckoutPage);

    void validate(@NonNull final ReviewPaymentMethodsPage reviewPaymentMethodsPage);

    void validate(@NonNull final DiscountDetailPage discountDetailPage);

    void validate(@NonNull final DiscountCodeInputPage discountCodeInput);

    void validate(@NonNull final DiscountCongratsPage discountCongratsPage);
}
