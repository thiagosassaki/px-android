package com.mercadopago.android.px.internal.features.paymentresult;

import com.mercadopago.android.px.internal.base.ResourcesProvider;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.model.Instructions;

public interface PaymentResultProvider extends ResourcesProvider {

    void getInstructionsAsync(Long paymentId, String paymentTypeId, final TaggedCallback<Instructions> taggedCallback);

    String getStandardErrorMessage();

    String getApprovedTitle();

    String getPendingTitle();

    String getRejectedOtherReasonTitle(final String paymentMethodName);

    String getRejectedInsufficientAmountTitle(final String paymentMethodName);

    String getRejectedDuplicatedPaymentTitle(final String paymentMethodName);

    String getRejectedCardDisabledTitle(final String paymentMethodName);

    String getRejectedBadFilledCardTitle(final String paymentMethodName);

    String getRejectedBadFilledCardTitle();

    String getRejectedHighRiskTitle();

    String getRejectedMaxAttemptsTitle();

    String getRejectedInsufficientDataTitle();

    String getRejectedBadFilledOther();

    String getRejectedCallForAuthorizeTitle();

    String getEmptyText();

    String getPendingLabel();

    String getRejectionLabel();

    String getCancelPayment();

    String getContinueShopping();

    String getExitButtonDefaultText();

    String getChangePaymentMethodLabel();

    String getRecoverPayment();

    String getCardEnabled();

    String getErrorTitle();

    String getPendingContingencyBodyErrorDescription();

    String getPendingReviewManualBodyErrorDescription();

    String getRejectedCallForAuthBodyErrorDescription();

    String getRejectedCardDisabledBodyErrorDescription(String paymentMethodName);

    String getRejectedInsufficientAmountBodyErrorDescription();

    String getRejectedInsufficientAmountBodyErrorSecondDescription();

    String getRejectedOtherReasonBodyErrorDescription();

    String getRejectedByBankBodyErrorDescription();

    String getRejectedInsufficientDataBodyErrorDescription();

    String getRejectedDuplicatedPaymentBodyErrorDescription();

    String getRejectedMaxAttemptsBodyErrorDescription();

    String getRejectedHighRiskBodyErrorDescription();

    String getRejectedCallForAuthBodyActionText(final String paymentMethodName);

    String getRejectedCallForAuthBodySecondaryTitle();

    String getReceiptDescription(final Long receiptId);
}
