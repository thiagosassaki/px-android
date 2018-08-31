package com.mercadopago.android.px.internal.features.paymentresult.components;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.internal.features.paymentresult.PaymentResultDecorator;
import com.mercadopago.android.px.internal.features.paymentresult.PaymentResultProvider;
import com.mercadopago.android.px.internal.features.paymentresult.formatter.HeaderTitleFormatter;
import com.mercadopago.android.px.internal.features.paymentresult.model.Badge;
import com.mercadopago.android.px.internal.features.paymentresult.props.HeaderProps;
import com.mercadopago.android.px.internal.features.paymentresult.props.PaymentResultBodyProps;
import com.mercadopago.android.px.internal.features.paymentresult.props.PaymentResultProps;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Component;
import com.mercadopago.android.px.internal.view.LoadingComponent;
import com.mercadopago.android.px.internal.view.RendererFactory;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentMethods;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.PaymentTypes;

public class PaymentResultContainer extends Component<PaymentResultProps, Void> {

    static {
        RendererFactory.register(PaymentResultContainer.class, PaymentResultRenderer.class);
    }

    private static final int DEFAULT_STATUS_BAR_COLOR = R.color.px_blue_status_bar;
    private static final int GREEN_STATUS_BAR_COLOR = R.color.px_green_status_bar;
    private static final int RED_STATUS_BAR_COLOR = R.color.px_red_status_bar;
    private static final int ORANGE_STATUS_BAR_COLOR = R.color.px_orange_status_bar;

    public static final int DEFAULT_ICON_IMAGE = R.drawable.px_icon_default;
    public static final int ITEM_ICON_IMAGE = R.drawable.px_icon_product;
    public static final int CARD_ICON_IMAGE = R.drawable.px_icon_card;
    public static final int BOLETO_ICON_IMAGE = R.drawable.px_icon_boleto;

    //armar componente Badge que va como hijo
    public static final int DEFAULT_BADGE_IMAGE = 0;
    public static final int CHECK_BADGE_IMAGE = R.drawable.px_badge_check;
    public static final int PENDING_BADGE_GREEN_IMAGE = R.drawable.px_badge_pending;
    public static final int PENDING_BADGE_ORANGE_IMAGE = R.drawable.px_badge_pending_orange;
    public static final int ERROR_BADGE_IMAGE = R.drawable.px_badge_error;
    public static final int WARNING_BADGE_IMAGE = R.drawable.px_badge_warning;

    public PaymentResultProvider paymentResultProvider;

    public PaymentResultContainer(@NonNull final ActionDispatcher dispatcher,
        @NonNull final PaymentResultProps props,
        @NonNull final PaymentResultProvider paymentResultProvider) {
        super(props, dispatcher);
        this.paymentResultProvider = paymentResultProvider;
    }

    public boolean isLoading() {
        return props.loading;
    }

    public LoadingComponent getLoadingComponent() {
        return new LoadingComponent();
    }

    public Header getHeaderComponent() {

        final HeaderProps headerProps = new HeaderProps.Builder()
            .setHeight(getHeaderMode())
            .setBackground(getBackground(props.paymentResult))
            .setStatusBarColor(getStatusBarColor(props.paymentResult))
            .setIconImage(getIconImage(props))
            .setIconUrl(getIconUrl(props))
            .setBadgeImage(getBadgeImage(props))
            .setTitle(getTitle(props))
            .setLabel(getLabel(props))
            .build();

        return new Header(headerProps, getDispatcher());
    }

    public boolean hasBodyComponent() {
        boolean hasBody = true;
        if (props.paymentResult != null) {
            String status = props.paymentResult.getPaymentStatus();
            String statusDetail = props.paymentResult.getPaymentStatusDetail();

            if (Payment.StatusCodes.STATUS_REJECTED.equals(status)
                && Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_PLUGIN_PM.equals(statusDetail)) {
                hasBody = false;
            } else if (status != null && statusDetail != null && status.equals(Payment.StatusCodes.STATUS_REJECTED) &&
                (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_CARD_NUMBER) ||
                    statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE) ||
                    statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE) ||
                    statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER))) {
                hasBody = false;
            }
        }
        return hasBody;
    }

    public Body getBodyComponent() {
        Body body = null;
        if (props.paymentResult != null) {
            //TODO fix amount.
            final PaymentResultBodyProps bodyProps =
                new PaymentResultBodyProps.Builder(props.getPaymentResultScreenPreference())
                .setStatus(props.paymentResult.getPaymentStatus())
                .setStatusDetail(props.paymentResult.getPaymentStatusDetail())
                .setPaymentData(props.paymentResult.getPaymentData())
                .setDisclaimer(props.paymentResult.getStatementDescription())
                .setPaymentId(props.paymentResult.getPaymentId())
                .setInstruction(props.instruction)
                .setCurrencyId(props.currencyId)
                .setAmount(props.paymentResult.getPaymentData().getTransactionAmount())
                .setProcessingMode(props.processingMode)
                .build();
            body = new Body(bodyProps, getDispatcher(), paymentResultProvider);
        }
        return body;
    }

    public FooterContainer getFooterContainer() {
        return new FooterContainer(new FooterContainer.Props(
            props.paymentResult, props.getPaymentResultScreenPreference()),
            getDispatcher(),
            paymentResultProvider
        );
    }

    private String getHeaderMode() {
        String headerMode;
        if (hasBodyComponent()) {
            headerMode = props.headerMode;
        } else {
            headerMode = HeaderProps.HEADER_MODE_STRETCH;
        }
        return headerMode;
    }

    @ColorRes
    private int getBackground(@NonNull final PaymentResult paymentResult) {
        if (PaymentResultDecorator.isSuccessBackground(paymentResult.getPaymentStatus(),
            paymentResult.getPaymentStatusDetail())) {
            return R.color.ui_components_success_color;
        } else if (PaymentResultDecorator.isErrorNonRecoverableBackground(paymentResult.getPaymentStatus(),
            paymentResult.getPaymentStatusDetail())) {
            return R.color.ui_components_error_color;
        } else if (PaymentResultDecorator.isPendingOrErrorRecoverableBackground(paymentResult.getPaymentStatus(),
            paymentResult.getPaymentStatusDetail())) {
            return R.color.ui_components_warning_color;
        } else {
            return R.color.px_colorPrimary;
        }
    }

    private int getStatusBarColor(@NonNull final PaymentResult paymentResult) {
        if (PaymentResultDecorator.isSuccessBackground(paymentResult.getPaymentStatus(),
            paymentResult.getPaymentStatusDetail())) {
            return GREEN_STATUS_BAR_COLOR;
        } else if (PaymentResultDecorator.isErrorNonRecoverableBackground(paymentResult.getPaymentStatus(),
            paymentResult.getPaymentStatusDetail())) {
            return RED_STATUS_BAR_COLOR;
        } else if (PaymentResultDecorator.isPendingOrErrorRecoverableBackground(paymentResult.getPaymentStatus(),
            paymentResult.getPaymentStatusDetail())) {
            return ORANGE_STATUS_BAR_COLOR;
        } else {
            return DEFAULT_STATUS_BAR_COLOR;
        }
    }

    @Nullable
    private String getIconUrl(@NonNull final PaymentResultProps props) {
        final PaymentResultScreenConfiguration paymentResultScreenConfiguration =
            props.getPaymentResultScreenPreference();
        final String paymentStatus = props.paymentResult.getPaymentStatus();
        final String paymentStatusDetail = props.paymentResult.getPaymentStatusDetail();
        return paymentResultScreenConfiguration.getPreferenceUrlIcon(paymentStatus, paymentStatusDetail);
    }

    private int getIconImage(@NonNull final PaymentResultProps props) {
        final PaymentResultScreenConfiguration paymentResultScreenConfiguration =
            props.getPaymentResultScreenPreference();
        final String paymentStatus = props.paymentResult.getPaymentStatus();
        final String paymentStatusDetail = props.paymentResult.getPaymentStatusDetail();

        if (paymentResultScreenConfiguration.hasCustomizedImageIcon(paymentStatus, paymentStatusDetail)) {
            return paymentResultScreenConfiguration.getPreferenceIcon(paymentStatus, paymentStatusDetail);
        } else if (isItemIconImage(props.paymentResult)) {
            return ITEM_ICON_IMAGE;
        } else if (isCardIconImage(props.paymentResult)) {
            return CARD_ICON_IMAGE;
        } else if (isBoletoIconImage(props.paymentResult)) {
            return BOLETO_ICON_IMAGE;
        } else {
            return DEFAULT_ICON_IMAGE;
        }
    }

    private boolean isItemIconImage(@NonNull final PaymentResult paymentResult) {
        final String status = paymentResult.getPaymentStatus();
        final String statusDetail = paymentResult.getPaymentStatusDetail();
        return status.equals(Payment.StatusCodes.STATUS_APPROVED) ||
            (status.equals(Payment.StatusCodes.STATUS_PENDING) &&
                statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT));
    }

    private boolean isCardIconImage(@NonNull final PaymentResult paymentResult) {
        if (isPaymentMethodIconImage(paymentResult)) {
            final String paymentTypeId = paymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId();
            return paymentTypeId.equals(PaymentTypes.PREPAID_CARD) || paymentTypeId.equals(PaymentTypes.DEBIT_CARD) ||
                paymentTypeId.equals(PaymentTypes.CREDIT_CARD);
        }
        return false;
    }

    private boolean isBoletoIconImage(@NonNull final PaymentResult paymentResult) {
        if (isPaymentMethodIconImage(paymentResult)) {
            final String paymentMethodId = paymentResult.getPaymentData().getPaymentMethod().getId();
            return paymentMethodId.equals(PaymentMethods.BRASIL.BOLBRADESCO);
        }
        return false;
    }

    private boolean isPaymentMethodIconImage(@NonNull final PaymentResult paymentResult) {
        final String status = paymentResult.getPaymentStatus();
        final String statusDetail = paymentResult.getPaymentStatusDetail();
        return ((status.equals(Payment.StatusCodes.STATUS_PENDING) &&
            !statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT)) ||
            status.equals(Payment.StatusCodes.STATUS_IN_PROCESS) ||
            status.equals(Payment.StatusCodes.STATUS_REJECTED));
    }

    private int getBadgeImage(@NonNull final PaymentResultProps props) {
        if (props.isPluginPaymentResult(props.paymentResult)) {
            if (props.paymentResult != null && props.paymentResult.isStatusApproved()) {
                return CHECK_BADGE_IMAGE;
            } else {
                return ERROR_BADGE_IMAGE;
            }
        } else if (props.hasCustomizedBadge()) {
            final String badge = props.getPreferenceBadge();
            if (badge.equals(Badge.CHECK_BADGE_IMAGE)) {
                return CHECK_BADGE_IMAGE;
            } else if (badge.equals(Badge.PENDING_BADGE_IMAGE)) {
                return PENDING_BADGE_GREEN_IMAGE;
            } else {
                return DEFAULT_BADGE_IMAGE;
            }
        } else if (props.paymentResult == null) {
            return DEFAULT_BADGE_IMAGE;
        } else if (PaymentResultDecorator.isCheckBagde(props.paymentResult.getPaymentStatus())) {
            return CHECK_BADGE_IMAGE;
        } else if (PaymentResultDecorator.isPendingSuccessBadge(props.paymentResult.getPaymentStatus(),
            props.paymentResult.getPaymentStatusDetail())) {
            return PENDING_BADGE_GREEN_IMAGE;
        } else if (PaymentResultDecorator.isPendingWarningBadge(props.paymentResult.getPaymentStatus(),
            props.paymentResult.getPaymentStatusDetail())) {
            return PENDING_BADGE_ORANGE_IMAGE;
        } else if (PaymentResultDecorator.isErrorRecoverableBadge(props.paymentResult.getPaymentStatus(),
            props.paymentResult.getPaymentStatusDetail())) {
            return WARNING_BADGE_IMAGE;
        } else if (PaymentResultDecorator.isErrorNonRecoverableBadge(props.paymentResult.getPaymentStatus(),
            props.paymentResult.getPaymentStatusDetail())) {
            return ERROR_BADGE_IMAGE;
        } else {
            return DEFAULT_BADGE_IMAGE;
        }
    }


    private CharSequence getTitle(@NonNull final PaymentResultProps props) {
        if (props.hasCustomizedTitle()) {
            return props.getPreferenceTitle();
        } else if (props.hasInstructions()) {
            return props.getInstructionsTitle();
        } else if (props.paymentResult == null) { // TODO REMOVE THIS, is only used in mocks
            return paymentResultProvider.getEmptyText();
        } else if (isPaymentMethodOff(props.paymentResult)) {
            return paymentResultProvider.getEmptyText();
        } else {
            final String paymentMethodName = props.paymentResult.getPaymentData().getPaymentMethod().getName();
            final String status = props.paymentResult.getPaymentStatus();
            final String statusDetail = props.paymentResult.getPaymentStatusDetail();

            if (status.equals(Payment.StatusCodes.STATUS_APPROVED)) {
                return paymentResultProvider.getApprovedTitle();
            } else if (status.equals(Payment.StatusCodes.STATUS_IN_PROCESS) ||
                status.equals(Payment.StatusCodes.STATUS_PENDING)) {
                return paymentResultProvider.getPendingTitle();
            } else if (status.equals(Payment.StatusCodes.STATUS_REJECTED)) {

                if (Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_OTHER_REASON.equals(statusDetail)
                    || Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_PLUGIN_PM.equals(statusDetail)) {
                    return paymentResultProvider.getRejectedOtherReasonTitle(paymentMethodName);
                } else if (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_INSUFFICIENT_AMOUNT)) {
                    return paymentResultProvider.getRejectedInsufficientAmountTitle(paymentMethodName);
                } else if (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_DUPLICATED_PAYMENT)) {
                    return paymentResultProvider.getRejectedDuplicatedPaymentTitle(paymentMethodName);
                } else if (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_CARD_DISABLED)) {
                    return paymentResultProvider.getRejectedCardDisabledTitle(paymentMethodName);
                } else if (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_HIGH_RISK)) {
                    return paymentResultProvider.getRejectedHighRiskTitle();
                } else if (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_MAX_ATTEMPTS)) {
                    return paymentResultProvider.getRejectedMaxAttemptsTitle();
                } else if (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_OTHER) ||
                    statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_CARD_NUMBER) ||
                    statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_SECURITY_CODE) ||
                    statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_CC_REJECTED_BAD_FILLED_DATE)) {
                    return paymentResultProvider.getRejectedBadFilledCardTitle(paymentMethodName);
                } else if (statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_BY_BANK)
                    || statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_REJECTED_REJECTED_INSUFFICIENT_DATA)) {
                    return paymentResultProvider.getRejectedInsufficientDataTitle();
                } else if (props.paymentResult.isCallForAuthorize()) {
                    return getCallForAuthFormattedTitle(props);
                } else {
                    return paymentResultProvider.getRejectedBadFilledOther();
                }
            }
        }

        return paymentResultProvider.getEmptyText();
    }

    private CharSequence getCallForAuthFormattedTitle(final @NonNull PaymentResultProps props) {
        String rejectedCallForAuthorizeTitle = paymentResultProvider.getRejectedCallForAuthorizeTitle();
        HeaderTitleFormatter headerTitleFormatter = new HeaderTitleFormatter(props.currencyId,
            props.paymentResult.getPaymentData().getTransactionAmount(),
            props.paymentResult.getPaymentData().getPaymentMethod().getName());
        return headerTitleFormatter.formatTextWithAmount(rejectedCallForAuthorizeTitle);
    }

    private String getLabel(@NonNull final PaymentResultProps props) {
        if (!props.isPluginPaymentResult(props.paymentResult) && props.hasCustomizedLabel()) {
            return props.getPreferenceLabel();
        } else if (props.paymentResult == null) {
            return paymentResultProvider.getEmptyText();
        } else {
            if (isLabelEmpty(props.paymentResult)) {
                return paymentResultProvider.getEmptyText();
            } else if (isLabelPending(props.paymentResult)) {
                return paymentResultProvider.getPendingLabel();
            } else if (isLabelError(props.paymentResult)) {
                return paymentResultProvider.getRejectionLabel();
            }
        }
        return paymentResultProvider.getEmptyText();
    }

    private boolean isLabelEmpty(@NonNull final PaymentResult paymentResult) {
        final String status = paymentResult.getPaymentStatus();
        final String statusDetail = paymentResult.getPaymentStatusDetail();
        return status.equals(Payment.StatusCodes.STATUS_APPROVED) ||
            status.equals(Payment.StatusCodes.STATUS_IN_PROCESS) ||
            (status.equals(Payment.StatusCodes.STATUS_PENDING)
                && !statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT));
    }

    private boolean isLabelPending(@NonNull final PaymentResult paymentResult) {
        final String status = paymentResult.getPaymentStatus();
        final String statusDetail = paymentResult.getPaymentStatusDetail();
        return status.equals(Payment.StatusCodes.STATUS_PENDING)
            && statusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT);
    }

    private boolean isLabelError(@NonNull final PaymentResult paymentResult) {
        return paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_REJECTED);
    }

    private boolean isPaymentMethodOff(@NonNull final PaymentResult paymentResult) {
        final String paymentTypeId = paymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId();
        return paymentTypeId.equals(PaymentTypes.TICKET) || paymentTypeId.equals(PaymentTypes.ATM) ||
            paymentTypeId.equals(PaymentTypes.BANK_TRANSFER);
    }
}