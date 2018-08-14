package com.mercadopago.android.px.testcheckout.flows;

import android.content.Context;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.model.Item;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.testcheckout.input.Card;
import com.mercadopago.android.px.testcheckout.pages.CongratsPage;
import com.mercadopago.android.px.testcheckout.pages.DebitCardPage;
import com.mercadopago.android.px.testcheckout.pages.PaymentMethodPage;
import com.mercadopago.android.px.testcheckout.pages.ReviewAndConfirmPage;
import com.mercadopago.android.px.testcheckout.pages.SecurityCodePage;
import java.math.BigDecimal;
import java.util.Collections;

public class SavedCardTestFlow extends TestFlow {

    private static final String PUBLIC_KEY = "APP_USR-648a260d-6fd9-4ad7-9284-90f22262c18d";
    private static final String ESC_NUMBER = "123";
    private String userWithCardAccessToken = "APP_USR-1505-080815-c6ea450de1bf828e39add499237d727f-312667294";
    private String cardId;
    private String paymentMethodId;
    private String paymentTypeId;

    public SavedCardTestFlow(@NonNull final String cardId, @NonNull final String paymentMethodId, @NonNull final Context context) {
        this.cardId = cardId;
        this.paymentMethodId = paymentMethodId;
        this.context = context;
        this.checkout = getMercadoPagoCheckout().build();
    }

    public SavedCardTestFlow(String paymentTypeId, @NonNull final Context context) {
        userWithCardAccessToken = null;
        this.paymentTypeId = paymentTypeId;
        this.context = context;
        this.checkout = getMercadoPagoCheckout().build();
    }

    //TODO: Should replace thread.sleep with IdlingResource
    //Thread.sleep is needed to wait lazy checkout to finish loading

    public CongratsPage runDefaultCardIdPaymentFlow() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startCheckout();
        new SecurityCodePage(null)
            .enterSecurityCode(ESC_NUMBER);
        return new ReviewAndConfirmPage().pressConfirmButton();
    }

    public CongratsPage runInvalidDefaultCardIdPaymentFlow() {
        PaymentMethodPage paymentMethodPage = new PaymentMethodPage(null);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startCheckout();
        paymentMethodPage.selectSavedDebitCard()
            .enterSecurityCode(ESC_NUMBER);
        return new ReviewAndConfirmPage().pressConfirmButton();
    }

    public CongratsPage runNewCardPaymentFlow(@NonNull final Card card) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startCheckout();
        return new DebitCardPage()
            .enterCreditCardNumber(card.cardNumber())
            .enterCardholderName(card.cardHolderName())
            .enterExpiryDate(card.expDate())
            .enterSecurityCode(card.escNumber())
            .enterIdentificationNumberToReviewAndConfirm(card.cardHolderIdentityNumber())
            .pressConfirmButton();
    }

    private MercadoPagoCheckout.Builder getMercadoPagoCheckout() {
        final CheckoutPreference checkoutPreference = getCheckoutPreference(getFakeItem());
        return new MercadoPagoCheckout.Builder(PUBLIC_KEY, checkoutPreference)
            .setPrivateKey(userWithCardAccessToken);
    }

    @NonNull
    private CheckoutPreference getCheckoutPreference(final Item item) {
        final CheckoutPreference checkoutPreference = new CheckoutPreference.Builder(
            Sites.ARGENTINA, "a@a.a", Collections.singletonList(item))
            .build();
        if(paymentTypeId != null){
        checkoutPreference.getPaymentPreference().setDefaultPaymentTypeId(paymentTypeId);
        checkoutPreference.getPaymentPreference().setDefaultCardId(cardId);
        } else {
            checkoutPreference.getPaymentPreference().setDefaultPaymentMethodId(paymentMethodId);
            checkoutPreference.getPaymentPreference().setDefaultCardId(cardId);
        }
        return checkoutPreference;
    }

    @NonNull
    private Item getFakeItem() {
        final Item item = new Item("sarasa", 1, new BigDecimal(100));
        item.setId("sarasa");
        item.setCurrencyId("ARS");
        return item;
    }
}