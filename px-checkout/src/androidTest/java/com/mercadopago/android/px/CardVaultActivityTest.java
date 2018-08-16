package com.mercadopago.android.px;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.Intents;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.mercadopago.android.px.internal.di.ConfigurationModule;
import com.mercadopago.android.px.internal.features.cardvault.CardVaultActivity;
import com.mercadopago.android.px.internal.features.ErrorActivity;
import com.mercadopago.android.px.internal.features.guessing_card.GuessingCardActivity;
import com.mercadopago.android.px.internal.features.InstallmentsActivity;
import com.mercadopago.android.px.internal.features.IssuersActivity;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.Installment;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.Item;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.configuration.AdvancedConfiguration;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.services.util.FakeAPI;
import com.mercadopago.android.px.test.StaticMock;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.Intents.times;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static junit.framework.Assert.assertTrue;

/**
 * Created by vaserber on 4/24/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class CardVaultActivityTest {

    @Rule
    public ActivityTestRule<CardVaultActivity> mTestRule = new ActivityTestRule<>(CardVaultActivity.class, true, false);

    private Intent validStartIntent;
    private FakeAPI mFakeAPI;

    @Before
    public void setupStartIntent() {

        final Item item = new Item("sarasa", 1, new BigDecimal(100));
        item.setId("someId");
        final PaymentSettingRepository configuration =
            new ConfigurationModule(InstrumentationRegistry.getContext()).getPaymentSettings();
        configuration.configure(new AdvancedConfiguration.Builder().build());
        configuration.configure(new CheckoutPreference.Builder(Sites.ARGENTINA, "a@a.a",
            Collections.singletonList(item)).build());

        validStartIntent = new Intent();
        validStartIntent.putExtra("merchantPublicKey", "1234");
    }

    @Before
    public void startFakeAPI() {
        mFakeAPI = new FakeAPI();
        mFakeAPI.start();
    }

    @Before
    public void initIntentsRecording() {
        Intents.init();
    }

    @After
    public void stopFakeAPI() {
        mFakeAPI.stop();
    }

    @After
    public void releaseIntents() {
        Intents.release();
    }

    @Test
    public void ifInstallmentsForCardisEmptyhenShowErrorActivity() {
        List<Installment> installmentsList = new ArrayList<>();
        mFakeAPI.addResponseToQueue(installmentsList, 200, "");

        validStartIntent.putExtra("card", JsonUtil.getInstance().toJson(StaticMock.getCard()));
        mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifInstallmentsForCardAPICallFailsShowErrorActivity() {
        mFakeAPI.addResponseToQueue("", 401, "");

        validStartIntent.putExtra("card", JsonUtil.getInstance().toJson(StaticMock.getCard()));
        mTestRule.launchActivity(validStartIntent);

        intended(hasComponent(ErrorActivity.class.getName()));
    }

    @Test
    public void ifNoCardSetThenStartCardFlow() {
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Token mockedToken = StaticMock.getToken();
        String issuers = StaticMock.getIssuersJson();
        String payerCosts = StaticMock.getPayerCostsJson();
        Discount mockedDiscount = null;

        //Guessing response
        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("token", JsonUtil.getInstance().toJson(mockedToken));
        guessingResultIntent.putExtra("discount", JsonUtil.getInstance().toJson(mockedDiscount));
        guessingResultIntent.putExtra("issuers", issuers);
        guessingResultIntent.putExtra("payerCosts", payerCosts);

        Instrumentation.ActivityResult result =
            new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        intended((hasComponent(GuessingCardActivity.class.getName())), times(1));
    }

    @Test
    public void ifIssuerNotResolvedInCardFlowThenStartIssuerActivity() {
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Token mockedToken = StaticMock.getToken();
        String issuers = StaticMock.getIssuersJson();
        String payerCosts = StaticMock.getPayerCostsJson();
        Discount mockedDiscount = null;

        //Guessing response
        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("token", JsonUtil.getInstance().toJson(mockedToken));
        guessingResultIntent.putExtra("discount", JsonUtil.getInstance().toJson(mockedDiscount));
        guessingResultIntent.putExtra("issuers", issuers);
        guessingResultIntent.putExtra("payerCosts", payerCosts);

        Instrumentation.ActivityResult result =
            new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        intended((hasComponent(IssuersActivity.class.getName())), times(1));
    }

    @Test
    public void ifIssuerResolvedBuPayerCostNotResolvedInCardFlowThenStartInstallmentsctivity() {
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Token mockedToken = StaticMock.getToken();
        String payerCosts = StaticMock.getPayerCostsJson();
        Issuer mockedIssuer = StaticMock.getIssuer();
        Discount mockedDiscount = null;

        //Guessing response
        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("token", JsonUtil.getInstance().toJson(mockedToken));
        guessingResultIntent.putExtra("discount", JsonUtil.getInstance().toJson(mockedDiscount));
        guessingResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(mockedIssuer));
        guessingResultIntent.putExtra("payerCosts", payerCosts);

        Instrumentation.ActivityResult result =
            new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        mTestRule.launchActivity(validStartIntent);

        intended((hasComponent(InstallmentsActivity.class.getName())), times(1));
    }

    @Ignore
    @Test
    public void ifInstallmentsEnabledForSavedCardThenStartInstallmentsActivity() {
        String installmentsJson = StaticMock.getInstallmentsJson();
        mFakeAPI.addResponseToQueue(installmentsJson, 200, "");

        validStartIntent.putExtra("card", JsonUtil.getInstance().toJson(StaticMock.getCard()));
        mTestRule.launchActivity(validStartIntent);

        intended((hasComponent(InstallmentsActivity.class.getName())), times(1));
    }

    @Test
    public void ifCardDataIsAskedInNewCardFlowThenFinishWithResult() {
        PaymentMethod paymentMethod = StaticMock.getPaymentMethodOn();
        Token mockedToken = StaticMock.getToken();
        String payerCosts = StaticMock.getPayerCostsJson();
        String issuers = StaticMock.getIssuersJson();
        Issuer mockedIssuer = StaticMock.getIssuer();
        PayerCost mockedPayerCost = StaticMock.getPayerCostWithInterests();
        Discount mockedDiscount = null;

        //Guessing response
        Intent guessingResultIntent = new Intent();
        guessingResultIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        guessingResultIntent.putExtra("token", JsonUtil.getInstance().toJson(mockedToken));
        guessingResultIntent.putExtra("discount", JsonUtil.getInstance().toJson(mockedDiscount));
        guessingResultIntent.putExtra("issuers", issuers);
        guessingResultIntent.putExtra("payerCosts", payerCosts);

        Instrumentation.ActivityResult result =
            new Instrumentation.ActivityResult(Activity.RESULT_OK, guessingResultIntent);
        intending(hasComponent(GuessingCardActivity.class.getName())).respondWith(result);

        //Issuer response
        Intent issuerResultIntent = new Intent();
        issuerResultIntent.putExtra("issuer", JsonUtil.getInstance().toJson(mockedIssuer));

        Instrumentation.ActivityResult issuerResult =
            new Instrumentation.ActivityResult(Activity.RESULT_OK, issuerResultIntent);
        intending(hasComponent(IssuersActivity.class.getName())).respondWith(issuerResult);

        //Installments response
        Intent installmentsResultIntent = new Intent();
        installmentsResultIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(mockedPayerCost));

        Instrumentation.ActivityResult installmentsResult =
            new Instrumentation.ActivityResult(Activity.RESULT_OK, installmentsResultIntent);
        intending(hasComponent(InstallmentsActivity.class.getName())).respondWith(installmentsResult);

        CardVaultActivity cardVaultActivity = mTestRule.launchActivity(validStartIntent);

        assertTrue(cardVaultActivity.isFinishing());
    }
}
