package com.mercadopago;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.testcheckout.flows.CreditCardTestFlow;
import com.mercadopago.android.px.testcheckout.flows.DebitCardTestFlow;
import com.mercadopago.android.px.testcheckout.flows.SavedCardTestFlow;
import com.mercadopago.android.px.testcheckout.idleresources.CheckoutResource;
import com.mercadopago.android.px.testcheckout.input.Amex;
import com.mercadopago.android.px.testcheckout.input.Country;
import com.mercadopago.android.px.testcheckout.input.FakeCard;
import com.mercadopago.android.px.testcheckout.input.Maestro;
import com.mercadopago.android.px.testcheckout.input.Master;
import com.mercadopago.android.px.testcheckout.input.Visa;
import com.mercadopago.android.px.testcheckout.input.VisaDebit;
import com.mercadopago.android.px.testcheckout.pages.CongratsPage;
import com.mercadopago.android.testlib.HttpResource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertNotNull;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NewCardTest {
    @Rule
    public HttpResource httpResource = new CheckoutResource();

    @Rule
    public ActivityTestRule<CheckoutExampleActivity> activityRule =
        new ActivityTestRule<>(CheckoutExampleActivity.class);

    private CreditCardTestFlow creditCardTestFlow;
    private DebitCardTestFlow debitCardTestFlow;

    @Before
    public void setUp() {
        MercadoPagoCheckout.Builder builder =
            new MercadoPagoCheckout.Builder("APP_USR-648a260d-6fd9-4ad7-9284-90f22262c18d",
                "243966003-d0be0be0-6fd8-4769-bf2f-7f2d979655f5");
        creditCardTestFlow = new CreditCardTestFlow(builder.build(), activityRule.getActivity());
        debitCardTestFlow = new DebitCardTestFlow(builder.build(), activityRule.getActivity());
    }

    @Test
    public void withValidVisaCreditCardNoInstallmentsFlowIsOk() {
        Visa card = new Visa(FakeCard.CardState.APRO, Country.ARGENTINA);
        CongratsPage congratsPage = creditCardTestFlow.runCreditCardPaymentFlowInstallmentsFirstOption(card);
        assertNotNull(congratsPage);
    }

    @Test
    public void withValidMasterCreditCardNoInstallmentsFlowIsOk() {
        Master card = new Master(FakeCard.CardState.APRO, Country.ARGENTINA);
        CongratsPage congratsPage = creditCardTestFlow.runCreditCardPaymentFlowInstallmentsFirstOption(card);
        assertNotNull(congratsPage);
    }

    @Test
    public void withValidAmericanExpressCreditCardNoInstallmentsFlowIsOk() {
        Amex card = new Amex(FakeCard.CardState.APRO, Country.ARGENTINA);
        CongratsPage congratsPage = creditCardTestFlow.runCreditCardPaymentFlowWithInstallments(card);
        assertNotNull(congratsPage);
    }

    @Test
    public void withValidVisaCreditCardInstallmentsFlowIsOk() {
        Visa card = new Visa(FakeCard.CardState.APRO, Country.ARGENTINA);
        CongratsPage congratsPage = creditCardTestFlow.runCreditCardPaymentFlowWithInstallments(card);
        assertNotNull(congratsPage);
    }

    @Test
    public void withValidMasterCreditCardInstallmentsFlowIsOk() {
        Master card = new Master(FakeCard.CardState.APRO, Country.ARGENTINA);
        CongratsPage congratsPage = creditCardTestFlow.runCreditCardPaymentFlowWithInstallments(card);
        assertNotNull(congratsPage);
    }

    @Test
    public void withValidAmericanExpressCreditCardInstallmentsFlowIsOk() {
        Amex card = new Amex(FakeCard.CardState.APRO, Country.ARGENTINA);
        CongratsPage congratsPage = creditCardTestFlow.runCreditCardPaymentFlowWithInstallments(card);
        assertNotNull(congratsPage);
    }

    @Test
    public void withValidMaestroDebitCardFlowIsOk() {
        Maestro card = new Maestro(FakeCard.CardState.APRO, Country.ARGENTINA);
        CongratsPage congratsPage = debitCardTestFlow.runDebitCardPaymentFlow(card);
        assertNotNull(congratsPage);
    }

    @Test
    public void withValidVisaDebitCardFlowIsOk() {
        VisaDebit card = new VisaDebit(FakeCard.CardState.APRO, Country.ARGENTINA);
        CongratsPage congratsPage = debitCardTestFlow.runDebitCardPaymentFlow(card);
        assertNotNull(congratsPage);
    }

    @Test
    public void withNewCardWithSavedCardFlowIsOk() {
        final Card card = new Card("123", "Test", "6042013076608231", "123456789", "1234");
        final SavedCardTestFlow savedCardTestFlow =
            new SavedCardTestFlow("debit_card", activityRule.getActivity());
        CongratsPage congratsPageSavedCard = savedCardTestFlow.runNewCardPaymentFlow(card);
        assertNotNull(congratsPageSavedCard);
    }
}
