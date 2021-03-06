package com.mercadopago.android.px.internal.features.paymentresult;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.internal.base.MvpPresenter;
import com.mercadopago.android.px.internal.callbacks.FailureRecovery;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.internal.constants.ProcessingModes;
import com.mercadopago.android.px.internal.features.review_and_confirm.components.actions.ChangePaymentMethodAction;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.view.ActionsListener;
import com.mercadopago.android.px.internal.view.LinkAction;
import com.mercadopago.android.px.internal.view.NextAction;
import com.mercadopago.android.px.internal.view.RecoverPaymentAction;
import com.mercadopago.android.px.internal.view.ResultCodeAction;
import com.mercadopago.android.px.model.Action;
import com.mercadopago.android.px.model.Instruction;
import com.mercadopago.android.px.model.Instructions;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.ScreenViewEvent;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.tracking.internal.utils.TrackingUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PaymentResultPresenter extends MvpPresenter<PaymentResultPropsView, PaymentResultProvider>
    implements ActionsListener {
    private PaymentResult paymentResult;
    private BigDecimal amount;

    private final PaymentResultNavigator navigator;
    private final PaymentSettingRepository paymentSettings;
    private FailureRecovery failureRecovery;
    private boolean initialized = false;

    public PaymentResultPresenter(@NonNull final PaymentResultNavigator navigator,
        final PaymentSettingRepository paymentSettings) {
        this.navigator = navigator;
        this.paymentSettings = paymentSettings;
    }

    public void initialize() {
        if (!isInitialized()) {
            try {
                validateParameters();
                onValidStart();
                initialized = true;
            } catch (final IllegalStateException exception) {
                navigator.showError(new MercadoPagoError(exception.getMessage(), false), "");
            }
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    private void validateParameters() {
        if (!isPaymentResultValid()) {
            throw new IllegalStateException("payment result is invalid");
        } else if (!isPaymentMethodValid()) {
            throw new IllegalStateException("payment data is invalid");
        } else if (isPaymentMethodOff()) {
            if (!isPaymentIdValid()) {
                throw new IllegalStateException("payment id is invalid");
            }
        }
    }

    protected void onValidStart() {
        initializeTracking();
        boolean showLoading = false;
        if (hasToAskForInstructions()) {
            showLoading = true;
        }
        getView().setPropPaymentResult(paymentSettings.getCheckoutPreference().getSite().getCurrencyId(), paymentResult,
            showLoading);
        checkGetInstructions();
    }

    private void initializeTracking() {
        ScreenViewEvent.Builder builder = new ScreenViewEvent.Builder()
            .setScreenId(getScreenId())
            .setScreenName(getScreenName())
            .addProperty(TrackingUtil.PROPERTY_PAYMENT_IS_EXPRESS, TrackingUtil.IS_EXPRESS_DEFAULT_VALUE)
            .addProperty(TrackingUtil.PROPERTY_PAYMENT_TYPE_ID,
                paymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId())
            .addProperty(TrackingUtil.PROPERTY_PAYMENT_METHOD_ID,
                paymentResult.getPaymentData().getPaymentMethod().getId())
            .addProperty(TrackingUtil.PROPERTY_PAYMENT_STATUS, paymentResult.getPaymentStatus())
            .addProperty(TrackingUtil.PROPERTY_PAYMENT_STATUS_DETAIL, paymentResult.getPaymentStatusDetail())
            .addProperty(TrackingUtil.PROPERTY_PAYMENT_ID, String.valueOf(paymentResult.getPaymentId()));
        if (paymentResult.getPaymentData().getIssuer() != null &&
            paymentResult.getPaymentData().getIssuer().getId() != null) {
            builder.addProperty(TrackingUtil.PROPERTY_ISSUER_ID,
                String.valueOf(paymentResult.getPaymentData().getIssuer().getId()));
        }
        if (navigator != null) {
            navigator.trackScreen(builder.build());
        }
    }

    private String getScreenId() {
        String screenId = "";
        if (isApproved()) {
            screenId = TrackingUtil.SCREEN_ID_PAYMENT_RESULT_APPROVED;
        } else if (paymentResult.isRejected()) {
            screenId = TrackingUtil.SCREEN_ID_PAYMENT_RESULT_REJECTED;
        } else if (paymentResult.isInstructions()) {
            screenId = TrackingUtil.SCREEN_ID_PAYMENT_RESULT_INSTRUCTIONS;
        } else if (paymentResult.isPending()) {
            screenId = TrackingUtil.SCREEN_ID_PAYMENT_RESULT_PENDING;
        }
        return screenId;
    }

    private String getScreenName() {
        String screenName = "";
        if (paymentResult.isRejected() || paymentResult.isPending() || isApproved()) {
            screenName = TrackingUtil.SCREEN_NAME_PAYMENT_RESULT;
        } else if (paymentResult.isCallForAuthorize()) {
            screenName = TrackingUtil.SCREEN_NAME_PAYMENT_RESULT_CALL_FOR_AUTH;
        } else if (paymentResult.isInstructions()) {
            screenName = TrackingUtil.SCREEN_NAME_PAYMENT_RESULT_INSTRUCTIONS;
        }
        return screenName;
    }

    private boolean isApproved() {
        return paymentResult.getPaymentStatus().equals(Payment.StatusCodes.STATUS_APPROVED);
    }

    private boolean isPaymentResultValid() {
        return paymentResult != null && paymentResult.getPaymentStatus() != null &&
            paymentResult.getPaymentStatusDetail() != null;
    }

    private boolean isPaymentMethodValid() {
        return paymentResult != null && paymentResult.getPaymentData() != null &&
            paymentResult.getPaymentData().getPaymentMethod() != null &&
            paymentResult.getPaymentData().getPaymentMethod().getId() != null &&
            !paymentResult.getPaymentData().getPaymentMethod().getId().isEmpty() &&
            paymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId() != null &&
            !paymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId().isEmpty() &&
            paymentResult.getPaymentData().getPaymentMethod().getName() != null &&
            !paymentResult.getPaymentData().getPaymentMethod().getName().isEmpty();
    }

    private boolean isPaymentIdValid() {
        return paymentResult.getPaymentId() != null;
    }

    private boolean isPaymentMethodOff() {
        final String paymentStatus = paymentResult.getPaymentStatus();
        final String paymentStatusDetail = paymentResult.getPaymentStatusDetail();
        return paymentStatus.equals(Payment.StatusCodes.STATUS_PENDING)
            && paymentStatusDetail.equals(Payment.StatusDetail.STATUS_DETAIL_PENDING_WAITING_PAYMENT);
    }

    public void setPaymentResult(final PaymentResult paymentResult) {
        this.paymentResult = paymentResult;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    private void checkGetInstructions() {
        if (hasToAskForInstructions()) {
            getInstructionsAsync(paymentResult.getPaymentId(),
                paymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId());
        } else {
            getView().notifyPropsChanged();
        }
    }

    private boolean hasToAskForInstructions() {
        return isPaymentMethodOff();
    }

    private void getInstructionsAsync(final Long paymentId, final String paymentTypeId) {
        getResourcesProvider().getInstructionsAsync(paymentId, paymentTypeId,
            new TaggedCallback<Instructions>(ApiUtil.RequestOrigin.GET_INSTRUCTIONS) {
                @Override
                public void onSuccess(Instructions instructions) {
                    final List<Instruction> instructionsList
                        = instructions.getInstructions() == null ? new ArrayList<Instruction>()
                        : instructions.getInstructions();
                    if (instructionsList.isEmpty()) {
                        navigator
                            .showError(new MercadoPagoError(getResourcesProvider().getStandardErrorMessage(), false),
                                ApiUtil.RequestOrigin.GET_INSTRUCTIONS);
                    } else {
                        resolveInstructions(instructionsList);
                    }
                }

                @Override
                public void onFailure(final MercadoPagoError error) {
                    //TODO revisar
                    if (navigator != null) {
                        navigator.showError(error, ApiUtil.RequestOrigin.GET_INSTRUCTIONS);
                        setFailureRecovery(new FailureRecovery() {
                            @Override
                            public void recover() {
                                getInstructionsAsync(paymentId, paymentTypeId);
                            }
                        });
                    }
                }
            });
    }

    public void recoverFromFailure() {
        if (failureRecovery != null) {
            failureRecovery.recover();
        }
    }

    private void setFailureRecovery(final FailureRecovery failureRecovery) {
        this.failureRecovery = failureRecovery;
    }

    private void resolveInstructions(final List<Instruction> instructionsList) {
        final Instruction instruction = getInstruction(instructionsList);
        if (instruction == null) {
            navigator.showError(new MercadoPagoError(getResourcesProvider().getStandardErrorMessage(), false),
                ApiUtil.RequestOrigin.GET_INSTRUCTIONS);
        } else {
            getView().setPropInstruction(instruction, ProcessingModes.AGGREGATOR, false);
            getView().notifyPropsChanged();
        }
    }

    private Instruction getInstruction(final List<Instruction> instructions) {
        final Instruction instruction;
        if (instructions.size() == 1) {
            instruction = instructions.get(0);
        } else {
            instruction = getInstructionForType(instructions,
                paymentResult.getPaymentData().getPaymentMethod().getPaymentTypeId());
        }
        return instruction;
    }

    private Instruction getInstructionForType(final List<Instruction> instructions, final String paymentTypeId) {
        Instruction instructionForType = null;
        for (Instruction instruction : instructions) {
            if (instruction.getType().equals(paymentTypeId)) {
                instructionForType = instruction;
                break;
            }
        }
        return instructionForType;
    }

    @Override
    public void onAction(@NonNull final Action action) {
        if (action instanceof NextAction) {
            navigator.finishWithResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        } else if (action instanceof ResultCodeAction) {
            navigator.finishWithResult(((ResultCodeAction) action).resultCode);
        } else if (action instanceof ChangePaymentMethodAction) {
            navigator.changePaymentMethod();
        } else if (action instanceof RecoverPaymentAction) {
            navigator.recoverPayment();
        } else if (action instanceof LinkAction) {
            navigator.openLink(((LinkAction) action).url);
        }
    }

    public PaymentResult getPaymentResult() {
        return paymentResult;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
