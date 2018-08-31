package com.mercadopago.android.px.internal.features.guessing_card;

import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.controllers.PaymentMethodGuessingController;
import com.mercadopago.android.px.model.IdentificationType;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.CardTokenException;
import java.util.List;

public interface GuessingCardActivityView extends MvpView {
    void onValidStart();

    void initializeTimer();

    void showError(MercadoPagoError error, String requestOrigin);

    void setCardNumberListeners(PaymentMethodGuessingController controller);

    void showInputContainer();

    void showApiExceptionError(ApiException exception, String requestOrigin);

    void setCardNumberInputMaxLength(int length);

    void setSecurityCodeInputMaxLength(int length);

    void setSecurityCodeViewLocation(String location);

    void initializeIdentificationTypes(List<IdentificationType> identificationTypes);

    void setNextButtonListeners();

    void setBackButtonListeners();

    void setErrorContainerListener();

    void setContainerAnimationListeners();

    void setIdentificationTypeListeners();

    void setIdentificationNumberListeners();

    void hideSecurityCodeInput();

    void hideIdentificationInput();

    void showIdentificationInput();

    void showSecurityCodeInput();

    void setCardholderNameListeners();

    void setExpiryDateListeners();

    void setSecurityCodeListeners();

    void setIdentificationNumberRestrictions(String type);

    void hideBankDeals();

    void showBankDeals();

    void clearErrorView();

    void setErrorView(String message);

    void setErrorView(CardTokenException exception);

    void setErrorCardNumber();

    void setErrorCardholderName();

    void setErrorExpiryDate();

    void setErrorSecurityCode();

    void setErrorIdentificationNumber();

    void clearErrorIdentificationNumber();

    void initializeTitle();

    void setCardholderName(String cardholderName);

    void setIdentificationNumber(String identificationNumber);

    void setSoftInputMode();

    void finishCardFlow(PaymentMethod paymentMethod, Token token, List<Issuer> issuers);

    void finishCardFlow(PaymentMethod paymentMethod, Token token, Issuer issuer,
        List<PayerCost> payerCosts);

    void finishCardFlow(PaymentMethod paymentMethod, Token token, Issuer issuer,
        PayerCost payerCost);

    void hideProgress();

    void setExclusionWithOneElementInfoView(PaymentMethod supportedPaymentMethod, boolean withAnimation);

    void hideExclusionWithOneElementInfoView();

    void setInvalidCardOnePaymentMethodErrorView();

    void setInvalidCardMultipleErrorView();

    void resolvePaymentMethodSet(PaymentMethod paymentMethod);

    void clearSecurityCodeEditText();

    void checkClearCardView();

    void hideRedErrorContainerView(boolean withAnimation);

    void restoreBlackInfoContainerView();

    void clearCardNumberInputLength();

    void clearCardNumberEditTextMask();

    void askForPaymentType();

    void showFinishCardFlow();

    void setPaymentMethod(PaymentMethod paymentMethod);
}
