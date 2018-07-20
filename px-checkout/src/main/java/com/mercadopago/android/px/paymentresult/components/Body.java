package com.mercadopago.android.px.paymentresult.components;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.components.ActionDispatcher;
import com.mercadopago.android.px.components.Component;
import com.mercadopago.android.px.components.PaymentMethodComponent;
import com.mercadopago.android.px.components.Receipt;
import com.mercadopago.android.px.components.TotalAmount;
import com.mercadopago.android.px.core.CheckoutStore;
import com.mercadopago.android.px.model.ExternalFragment;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.paymentresult.PaymentResultProvider;
import com.mercadopago.android.px.paymentresult.props.BodyErrorProps;
import com.mercadopago.android.px.paymentresult.props.InstructionsProps;
import com.mercadopago.android.px.paymentresult.props.PaymentResultBodyProps;

public class Body extends Component<PaymentResultBodyProps, Void> {

    public PaymentResultProvider paymentResultProvider;

    public Body(@NonNull final PaymentResultBodyProps props,
        @NonNull final ActionDispatcher dispatcher,
        @NonNull final PaymentResultProvider paymentResultProvider) {
        super(props, dispatcher);
        this.paymentResultProvider = paymentResultProvider;
    }

    public boolean hasInstructions() {
        return props.instruction != null;
    }

    public Instructions getInstructionsComponent() {
        final InstructionsProps instructionsProps = new InstructionsProps.Builder()
            .setInstruction(props.instruction)
            .setProcessingMode(props.processingMode)
            .build();
        return new Instructions(instructionsProps, getDispatcher());
    }

    public boolean hasPaymentMethodDescription() {
        return props.paymentData != null && isStatusApproved() &&
            isPaymentTypeOn(props.paymentData.getPaymentMethod());
    }

    private boolean isPaymentTypeOn(final com.mercadopago.android.px.model.PaymentMethod paymentMethod) {
        return isCardType(paymentMethod)
            || isAccountMoney(paymentMethod)
            || isPluginType(paymentMethod);
    }

    private boolean isCardType(final com.mercadopago.android.px.model.PaymentMethod paymentMethod) {
        return paymentMethod != null && paymentMethod.getPaymentTypeId() != null &&
            paymentMethod.getPaymentTypeId().equals(PaymentTypes.CREDIT_CARD) ||
            paymentMethod.getPaymentTypeId().equals(PaymentTypes.DEBIT_CARD) ||
            paymentMethod.getPaymentTypeId().equals(PaymentTypes.PREPAID_CARD);
    }

    private boolean isAccountMoney(final com.mercadopago.android.px.model.PaymentMethod paymentMethod) {
        return paymentMethod != null && paymentMethod.getPaymentTypeId() != null
            && paymentMethod.getPaymentTypeId().equals(PaymentTypes.ACCOUNT_MONEY);
    }

    private boolean isPluginType(final com.mercadopago.android.px.model.PaymentMethod paymentMethod) {
        return PaymentTypes.PLUGIN.equalsIgnoreCase(paymentMethod.getPaymentTypeId());
    }

    public PaymentMethodComponent getPaymentMethodComponent() {
        final TotalAmount.TotalAmountProps totalAmountProps = new TotalAmount.TotalAmountProps(props.currencyId,
            props.amount,
            props.paymentData.getPayerCost(),
            props.paymentData.getDiscount());

        final PaymentMethodComponent.PaymentMethodProps paymentMethodProps =
            new PaymentMethodComponent.PaymentMethodProps(props.paymentData.getPaymentMethod(),
                props.paymentData.getToken() != null ? props.paymentData.getToken().getLastFourDigits() : null,
                props.disclaimer,
                totalAmountProps);

        return new PaymentMethodComponent(paymentMethodProps);
    }

    public boolean hasBodyError() {
        return props.status != null && props.statusDetail != null &&
            (isPendingWithBody() || isRejectedWithBody());
    }

    private boolean isPendingWithBody() {
        return (props.status.equals(Payment.StatusCodes.STATUS_PENDING) ||
            props.status.equals(Payment.StatusCodes.STATUS_IN_PROCESS)) &&
            (props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_CONTINGENCY) ||
                props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_REVIEW_MANUAL));
    }

    private boolean isRejectedWithBody() {
        return (props.status.equals(Payment.StatusCodes.STATUS_REJECTED) &&
            (props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_OTHER_REASON) ||
                props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_BY_BANK) ||
                props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_INSUFFICIENT_DATA) ||
                props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT) ||
                props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS) ||
                props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_HIGH_RISK) ||
                props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE) ||
                props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED) ||
                props.statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT)));
    }

    private boolean isStatusApproved() {
        return Payment.StatusCodes.STATUS_APPROVED.equals(props.status);
    }

    public BodyError getBodyErrorComponent() {
        final BodyErrorProps bodyErrorProps = new BodyErrorProps.Builder()
            .setStatus(props.status)
            .setStatusDetail(props.statusDetail)
            .setPaymentMethodName(props.paymentData.getPaymentMethod().getName())
            .build();
        return new BodyError(bodyErrorProps, getDispatcher(), paymentResultProvider);
    }

    public boolean hasReceipt() {
        final com.mercadopago.android.px.model.PaymentMethod paymentMethod = props.paymentData.getPaymentMethod();
        return props.paymentId != null && props.isReceiptEnabled() && props.paymentData != null
            && isStatusApproved() && isPaymentTypeOn(paymentMethod);
    }

    public Receipt getReceiptComponent() {
        return new Receipt(new Receipt.ReceiptProps(String.valueOf(props.paymentId)));
    }

    //TODO remove when preferences is removed from singleton checkout store
    public boolean hasTopCustomComponent() {
        return CheckoutStore.getInstance().getPaymentResultScreenPreference().hasTopFragment();
    }

    //TODO remove when preferences is removed from singleton checkout store
    public boolean hasBottomCustomComponent() {
        return CheckoutStore.getInstance().getPaymentResultScreenPreference().hasBottomFragment();
    }

    //TODO remove when preferences is removed from singleton checkout store
    public ExternalFragment topFragment() {
        return CheckoutStore.getInstance().getPaymentResultScreenPreference().getTopFragment();
    }

    //TODO remove when preferences is removed from singleton checkout store
    public ExternalFragment bottomFragment() {
        return CheckoutStore.getInstance().getPaymentResultScreenPreference().getBottomFragment();
    }
}
