package com.mercadopago.android.px.internal.features.paymentresult.components;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.internal.features.paymentresult.PaymentResultProvider;
import com.mercadopago.android.px.internal.features.review_and_confirm.components.actions.ChangePaymentMethodAction;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Button;
import com.mercadopago.android.px.internal.view.Component;
import com.mercadopago.android.px.internal.view.Footer;
import com.mercadopago.android.px.internal.view.NextAction;
import com.mercadopago.android.px.internal.view.RecoverPaymentAction;
import com.mercadopago.android.px.internal.view.RendererFactory;
import com.mercadopago.android.px.internal.view.ResultCodeAction;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentResult;

public class FooterContainer extends Component<FooterContainer.Props, Void> {

    static {
        RendererFactory.register(FooterContainer.class, FooterContainerRenderer.class);
    }

    public PaymentResultProvider resourcesProvider;

    public FooterContainer(@NonNull final Props props,
        @NonNull final ActionDispatcher dispatcher,
        @NonNull final PaymentResultProvider provider) {
        super(props, dispatcher);
        resourcesProvider = provider;
    }

    @VisibleForTesting
    Footer getFooter() {
        return new Footer(getFooterProps(), getDispatcher());
    }

    @VisibleForTesting
    Footer.Props getFooterProps() {

        final PaymentResultScreenConfiguration
            paymentResultScreenConfiguration = props.paymentResultScreenConfiguration;

        Button.Props buttonAction = null;
        Button.Props linkAction = null;

        if (props.paymentResult.isStatusApproved()) {

            if (!paymentResultScreenConfiguration.isCongratsSecondaryExitButtonEnabled() ||
                paymentResultScreenConfiguration.getSecondaryCongratsExitButtonTitle() == null
                || paymentResultScreenConfiguration.getSecondaryCongratsExitResultCode() == null) {
                buttonAction = null;
            } else {
                buttonAction = new Button.Props(
                    paymentResultScreenConfiguration.getSecondaryCongratsExitButtonTitle(),
                    new ResultCodeAction(paymentResultScreenConfiguration.getSecondaryCongratsExitResultCode())
                );
            }

            if (TextUtil.isEmpty(paymentResultScreenConfiguration.getExitButtonTitle())) {
                linkAction = new Button.Props(resourcesProvider.getContinueShopping(), new NextAction());
            } else {
                linkAction = new Button.Props(paymentResultScreenConfiguration.getExitButtonTitle(), new NextAction());
            }
        } else if (props.paymentResult.isStatusPending() || props.paymentResult.isStatusInProcess()) {

            if (!paymentResultScreenConfiguration.isPendingSecondaryExitButtonEnabled() ||
                paymentResultScreenConfiguration.getSecondaryPendingExitButtonTitle() == null
                || paymentResultScreenConfiguration.getSecondaryPendingExitResultCode() == null) {
                buttonAction = null;
            } else {
                buttonAction = new Button.Props(
                    paymentResultScreenConfiguration.getSecondaryPendingExitButtonTitle(),
                    new ResultCodeAction(paymentResultScreenConfiguration.getSecondaryPendingExitResultCode())
                );
            }

            if (TextUtil.isEmpty(paymentResultScreenConfiguration.getExitButtonTitle())) {
                linkAction = new Button.Props(resourcesProvider.getContinueShopping(), new NextAction());
            } else {
                linkAction = new Button.Props(paymentResultScreenConfiguration.getExitButtonTitle(), new NextAction());
            }
        } else if (props.paymentResult.isStatusRejected()) {

            if (Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CALL_FOR_AUTHORIZE
                .equals(props.paymentResult.getPaymentStatusDetail())) {

                buttonAction = new Button.Props(
                    resourcesProvider.getChangePaymentMethodLabel(),
                    new ChangePaymentMethodAction()
                );

                linkAction = new Button.Props(resourcesProvider.getCancelPayment(), new NextAction());
            } else if (Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED
                .equals(props.paymentResult.getPaymentStatusDetail())) {

                buttonAction = new Button.Props(
                    resourcesProvider.getCardEnabled(),
                    new RecoverPaymentAction()
                );

                linkAction = new Button.Props(resourcesProvider.getChangePaymentMethodLabel(),
                    new ChangePaymentMethodAction());
            } else if (Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT
                .equals(props.paymentResult.getPaymentStatusDetail())) {

                buttonAction = new Button.Props(
                    resourcesProvider.getChangePaymentMethodLabel(),
                    new ChangePaymentMethodAction()
                );

                linkAction = new Button.Props(resourcesProvider.getCancelPayment(), new NextAction());
            } else if (Payment.StatusDetail.isBadFilled(props.paymentResult.getPaymentStatusDetail())) {
                buttonAction = new Button.Props(
                    resourcesProvider.getRejectedBadFilledCardTitle(),
                    new RecoverPaymentAction()
                );

                linkAction = new Button.Props(resourcesProvider.getChangePaymentMethodLabel(),
                    new ChangePaymentMethodAction());
            } else if (Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT
                .equals(props.paymentResult.getPaymentStatusDetail())) {

                buttonAction = null;

                linkAction = new Button.Props(resourcesProvider.getContinueShopping(), new NextAction());
            } else {

                buttonAction = new Button.Props(
                    resourcesProvider.getChangePaymentMethodLabel(),
                    new ChangePaymentMethodAction()
                );

                linkAction = new Button.Props(resourcesProvider.getCancelPayment(), new NextAction());
            }

            // Remove the button by user preference
            if (!paymentResultScreenConfiguration.isRejectedSecondaryExitButtonEnabled()) {
                buttonAction = null;
            }
        }

        return new Footer.Props(
            buttonAction, linkAction
        );
    }

    public static class Props {

        public final PaymentResult paymentResult;
        public final PaymentResultScreenConfiguration paymentResultScreenConfiguration;

        public Props(PaymentResult paymentResult,
            final PaymentResultScreenConfiguration paymentResultScreenConfiguration) {
            this.paymentResult = paymentResult;
            this.paymentResultScreenConfiguration = paymentResultScreenConfiguration;
        }
    }
}