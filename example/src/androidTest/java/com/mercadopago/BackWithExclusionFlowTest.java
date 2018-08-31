package com.mercadopago;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.testcheckout.assertions.DefaultValidator;
import com.mercadopago.android.px.testcheckout.idleresources.CheckoutResource;
import com.mercadopago.android.px.testcheckout.input.Card;
import com.mercadopago.android.px.testcheckout.input.FakeCard;
import com.mercadopago.android.px.testcheckout.pages.CreditCardPage;
import com.mercadopago.android.px.testcheckout.pages.ExpiryDatePage;
import com.mercadopago.android.px.testcheckout.pages.NamePage;
import com.mercadopago.android.px.testcheckout.pages.NoCheckoutPage;
import com.mercadopago.android.px.testcheckout.pages.SecurityCodePage;
import com.mercadopago.android.testlib.HttpResource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BackWithExclusionFlowTest {

    @Rule
    public HttpResource httpResource = new CheckoutResource();

    @Rule
    public ActivityTestRule<CheckoutExampleActivity> activityRule =
        new ActivityTestRule<>(CheckoutExampleActivity.class);

    @Before
    public void setUp() {
        MercadoPagoCheckout.Builder builder =
            new MercadoPagoCheckout.Builder("TEST-e4bdd1cf-bcb2-43f7-b565-ed4c9ea25be7",
                "243966003-bb8f7422-39c1-4337-81dd-60a88eb787df");
        builder.build().startPayment(activityRule.getActivity(), 1);
    }

    @Test
    public void whenDriveToIdentificationAndPressBackGoOutOfCheckout() {
        Card card = new FakeCard(FakeCard.CardState.APRO, "5323793735506106");
        NoCheckoutPage noCheckoutPage = new CreditCardPage(new DefaultValidator())
            .enterCreditCardNumber(card.cardNumber())
            .enterCardholderName(card.cardHolderName())
            .enterExpiryDate(card.expDate())
            .enterSecurityCode(card.escNumber())
            .pressBackWithExclusions();
        assertNotNull(noCheckoutPage);
    }

    @Test
    public void whenDriveToSecurityCodeAndPressBackGoOutOfCheckout() {
        Card card = new FakeCard(FakeCard.CardState.APRO, "5323793735506106");
        NoCheckoutPage noCheckoutPage = new CreditCardPage(new DefaultValidator())
            .enterCreditCardNumber(card.cardNumber())
            .enterCardholderName(card.cardHolderName())
            .enterExpiryDate(card.expDate())
            .pressBackWithExclusion();
        assertNotNull(noCheckoutPage);
    }

    @Test
    public void whenDriveToExpiryDateAndPressBackGoOutOfCheckout() {
        Card card = new FakeCard(FakeCard.CardState.APRO, "5323793735506106");
        NoCheckoutPage noCheckoutPage = new CreditCardPage(new DefaultValidator())
            .enterCreditCardNumber(card.cardNumber())
            .enterCardholderName(card.cardHolderName())
            .pressBackWithExclusion();
        assertNotNull(noCheckoutPage);
    }

    @Test
    public void whenDriveToNamePageAndPressBackGoOutOfCheckout() {
        Card card = new FakeCard(FakeCard.CardState.APRO, "5323793735506106");
        NoCheckoutPage noCheckoutPage = new CreditCardPage(new DefaultValidator())
            .enterCreditCardNumber(card.cardNumber())
            .pressBackWithExclusion();
        assertNotNull(noCheckoutPage);
    }

    @Test
    public void whenDriveToCreditCardPageAndPressBackGoOutOfCheckout() {
        NoCheckoutPage noCheckoutPage = new CreditCardPage(new DefaultValidator())
            .pressBackWithExclusion();
        assertNotNull(noCheckoutPage);
    }

    @Test
    public void whenDriveToIdentificationAndPressPreviousGoOutOfCheckout() {
        Card card = new FakeCard(FakeCard.CardState.APRO, "5323793735506106");
        SecurityCodePage securityCodePage = new CreditCardPage(new DefaultValidator())
            .enterCreditCardNumber(card.cardNumber())
            .enterCardholderName(card.cardHolderName())
            .enterExpiryDate(card.expDate())
            .enterSecurityCode(card.escNumber())
            .pressPrevious();
        assertNotNull(securityCodePage);
    }

    @Test
    public void whenDriveToSecurityCodeAndPressPreviousGoOutOfCheckout() {
        Card card = new FakeCard(FakeCard.CardState.APRO, "5323793735506106");
        ExpiryDatePage expiryDatePage = new CreditCardPage(new DefaultValidator())
            .enterCreditCardNumber(card.cardNumber())
            .enterCardholderName(card.cardHolderName())
            .enterExpiryDate(card.expDate())
            .pressPrevious();
        assertNotNull(expiryDatePage);
    }

    @Test
    public void whenDriveToExpiryDateAndPressPreviousGoOutOfCheckout() {
        Card card = new FakeCard(FakeCard.CardState.APRO, "5323793735506106");
        NamePage namePage = new CreditCardPage(new DefaultValidator())
            .enterCreditCardNumber(card.cardNumber())
            .enterCardholderName(card.cardHolderName())
            .pressPrevious();
        assertNotNull(namePage);
    }

    @Test
    public void whenDriveToNamePageAndPressPreviousGoOutOfCheckout() {
        Card card = new FakeCard(FakeCard.CardState.APRO, "5323793735506106");
        CreditCardPage creditCardPage = new CreditCardPage(new DefaultValidator())
            .enterCreditCardNumber(card.cardNumber())
            .pressPrevious();
        assertNotNull(creditCardPage);
    }

    @Test
    public void whenDriveToReviewAndConfirmAndPressBackGoOutOfCheckout() {
        Card card = new FakeCard(FakeCard.CardState.APRO, "5323793735506106");
        NoCheckoutPage noCheckoutPage = new CreditCardPage(new DefaultValidator())
            .enterCreditCardNumber(card.cardNumber())
            .enterCardholderName(card.cardHolderName())
            .enterExpiryDate(card.expDate())
            .enterSecurityCode(card.escNumber())
            .enterIdentificationNumberToIssuer(card.cardHolderIdentityNumber())
            .enterBankOptionToInstallments(0)
            .selectInstallments(0)
            .pressBackWithExclusion();
        assertNotNull(noCheckoutPage);
    }
}
