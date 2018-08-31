package com.mercadopago;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.testcheckout.assertions.DefaultValidator;
import com.mercadopago.android.px.testcheckout.idleresources.CheckoutResource;
import com.mercadopago.android.px.testcheckout.input.Card;
import com.mercadopago.android.px.testcheckout.input.FakeCard;
import com.mercadopago.android.px.testcheckout.pages.CongratsPage;
import com.mercadopago.android.px.testcheckout.pages.PaymentMethodPage;
import com.mercadopago.android.testlib.HttpResource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BackFlowTest {

    @Rule
    public HttpResource httpResource = new CheckoutResource();

    @Rule
    public ActivityTestRule<CheckoutExampleActivity> activityRule =
        new ActivityTestRule<>(CheckoutExampleActivity.class);

    @Before
    public void setUp() {
        MercadoPagoCheckout.Builder builder =
            new MercadoPagoCheckout.Builder("APP_USR-648a260d-6fd9-4ad7-9284-90f22262c18d",
                "243966003-d0be0be0-6fd8-4769-bf2f-7f2d979655f5");
        builder.build().startPayment(activityRule.getActivity(), 1);
    }

    @Test
    public void whenDriveToIdentificationAndPressBackGoToPaymentMethodSelection() {
        Card card = new FakeCard(FakeCard.CardState.APRO, "5323793735506106");
        PaymentMethodPage paymentMethodPage = new PaymentMethodPage(new DefaultValidator())
            .selectCard()
            .selectCreditCard()
            .enterCreditCardNumber(card.cardNumber())
            .enterCardholderName(card.cardHolderName())
            .enterExpiryDate(card.expDate())
            .enterSecurityCode(card.escNumber())
            .pressBack();
        assertNotNull(paymentMethodPage);
    }

    @Test
    public void whenDriveToSecurityCodeAndPressBackGoToPaymentMethodSelection() {
        Card card = new FakeCard(FakeCard.CardState.APRO, "5323793735506106");
        PaymentMethodPage paymentMethodPage = new PaymentMethodPage(new DefaultValidator())
            .selectCard()
            .selectCreditCard()
            .enterCreditCardNumber(card.cardNumber())
            .enterCardholderName(card.cardHolderName())
            .enterExpiryDate(card.expDate())
            .pressBack();
        assertNotNull(paymentMethodPage);
    }

    @Test
    public void whenDriveToExpiryDateAndPressBackGoToPaymentMethodSelection() {
        Card card = new FakeCard(FakeCard.CardState.APRO, "5323793735506106");
        PaymentMethodPage paymentMethodPage = new PaymentMethodPage(new DefaultValidator())
            .selectCard()
            .selectCreditCard()
            .enterCreditCardNumber(card.cardNumber())
            .enterCardholderName(card.cardHolderName())
            .pressBack();
        assertNotNull(paymentMethodPage);
    }

    @Test
    public void whenDriveToNamePageAndPressBackGoToPaymentMethodSelection() {
        Card card = new FakeCard(FakeCard.CardState.APRO, "5323793735506106");
        PaymentMethodPage paymentMethodPage = new PaymentMethodPage(new DefaultValidator())
            .selectCard()
            .selectCreditCard()
            .enterCreditCardNumber(card.cardNumber())
            .pressBack();
        assertNotNull(paymentMethodPage);
    }

    @Test
    public void whenDriveToCreditCardPageAndPressBackGoToPaymentMethodSelection() {
        PaymentMethodPage paymentMethodPage = new PaymentMethodPage(new DefaultValidator())
            .selectCard()
            .selectCreditCard()
            .pressBack();
        assertNotNull(paymentMethodPage);
    }

    @Test
    public void whenDriveToReviewAndConfirmAndPressBackGoToPaymentMethodSelection() {
        Card card = new FakeCard(FakeCard.CardState.APRO, "5323793735506106");
        PaymentMethodPage paymentMethodPage = new PaymentMethodPage(new DefaultValidator())
            .selectCard()
            .selectCreditCard()
            .enterCreditCardNumber(card.cardNumber())
            .enterCardholderName(card.cardHolderName())
            .enterExpiryDate(card.expDate())
            .enterSecurityCode(card.escNumber())
            .enterIdentificationNumberToIssuer(card.cardHolderIdentityNumber())
            .enterBankOptionToInstallments(0)
            .selectInstallments(0)
            .pressBack();
        assertNotNull(paymentMethodPage);
    }

    @Test
    public void whenDriveToBankSelectionAndPressBackGoToPaymentMethodSelection() {
        Card card = new FakeCard(FakeCard.CardState.APRO, "5323793735506106");
        PaymentMethodPage paymentMethodPage = new PaymentMethodPage(new DefaultValidator())
            .selectCard()
            .selectCreditCard()
            .enterCreditCardNumber(card.cardNumber())
            .enterCardholderName(card.cardHolderName())
            .enterExpiryDate(card.expDate())
            .enterSecurityCode(card.escNumber())
            .enterIdentificationNumberToIssuer(card.cardHolderIdentityNumber())
            .pressBack();
        assertNotNull(paymentMethodPage);
    }

    @Test
    public void whenDriveToInstallmentsAndPressBackGoToPaymentMethodSelection() {
        Card card = new FakeCard(FakeCard.CardState.APRO, "5323793735506106");
        PaymentMethodPage paymentMethodPage = new PaymentMethodPage(new DefaultValidator())
            .selectCard()
            .selectCreditCard()
            .enterCreditCardNumber(card.cardNumber())
            .enterCardholderName(card.cardHolderName())
            .enterExpiryDate(card.expDate())
            .enterSecurityCode(card.escNumber())
            .enterIdentificationNumberToIssuer(card.cardHolderIdentityNumber())
            .enterBankOptionToInstallments(0)
            .pressBack();
        assertNotNull(paymentMethodPage);
    }

    @Test
    public void whenDriveToReviewAndConfirmAndPressBackAndThenFinishWithPayment() {
        Card card = new FakeCard(FakeCard.CardState.APRO, "5323793735506106");
        CongratsPage congratsPage = new PaymentMethodPage(new DefaultValidator())
            .selectCash()
            .selectMethod("Rapipago")
            .pressBack()
            .selectCard()
            .selectCreditCard()
            .enterCreditCardNumber(card.cardNumber())
            .enterCardholderName(card.cardHolderName())
            .enterExpiryDate(card.expDate())
            .enterSecurityCode(card.escNumber())
            .enterIdentificationNumberToIssuer(card.cardHolderIdentityNumber())
            .enterBankOptionToInstallments(0)
            .selectInstallments(0)
            .pressConfirmButton();
        assertNotNull(congratsPage);
    }
}
