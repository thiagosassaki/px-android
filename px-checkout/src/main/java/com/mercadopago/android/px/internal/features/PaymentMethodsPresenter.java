package com.mercadopago.android.px.internal.features;

import com.mercadopago.android.px.internal.base.MvpPresenter;
import com.mercadopago.android.px.internal.callbacks.FailureRecovery;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.internal.features.providers.PaymentMethodsProvider;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.preferences.PaymentPreference;
import java.util.ArrayList;
import java.util.List;

public class PaymentMethodsPresenter extends MvpPresenter<PaymentMethodsView, PaymentMethodsProvider> {

    private boolean showBankDeals;
    private PaymentPreference paymentPreference;
    private List<String> supportedPaymentTypes;
    private FailureRecovery failureRecovery;

    public void setShowBankDeals(boolean showBankDeals) {
        this.showBankDeals = showBankDeals;
    }

    public void setPaymentPreference(PaymentPreference paymentPreference) {
        this.paymentPreference = paymentPreference;
    }

    public void setSupportedPaymentTypes(List<String> supportedPaymentTypes) {
        this.supportedPaymentTypes = supportedPaymentTypes;
    }

    public void start() {
        definePaymentMethodsExclusions();
        retrievePaymentMethods();
        if (showBankDeals && isViewAttached()) {
            getView().showBankDeals();
        }
    }

    private void retrievePaymentMethods() {
        getView().showProgress();
        getResourcesProvider()
            .getPaymentMethods(new TaggedCallback<List<PaymentMethod>>(ApiUtil.RequestOrigin.GET_PAYMENT_METHODS) {
                @Override
                public void onSuccess(List<PaymentMethod> paymentMethods) {
                    if (isViewAttached()) {
                        getView().showPaymentMethods(getSupportedPaymentMethods(paymentMethods));
                        getView().hideProgress();
                    }
                }

                @Override
                public void onFailure(MercadoPagoError exception) {
                    if (isViewAttached()) {
                        setFailureRecovery(new FailureRecovery() {
                            @Override
                            public void recover() {
                                retrievePaymentMethods();
                            }
                        });
                        getView().showError(exception);
                        getView().hideProgress();
                    }
                }
            });
    }

    private void definePaymentMethodsExclusions() {
        //Give priority to PaymentPreference over supported payment types
        if (!isPaymentPreferenceSet() && supportedPaymentTypesSet()) {
            List<String> excludedPaymentTypes = new ArrayList<>();
            for (String type : PaymentTypes.getAllPaymentTypes()) {
                if (!supportedPaymentTypes.contains(type)) {
                    excludedPaymentTypes.add(type);
                }
            }
            paymentPreference = new PaymentPreference();
            paymentPreference.setExcludedPaymentTypeIds(excludedPaymentTypes);
        }
    }

    private boolean supportedPaymentTypesSet() {
        return supportedPaymentTypes != null;
    }

    private boolean isPaymentPreferenceSet() {
        return paymentPreference != null;
    }

    private List<PaymentMethod> getSupportedPaymentMethods(List<PaymentMethod> paymentMethods) {

        List<PaymentMethod> supportedPaymentMethods;
        if (paymentPreference == null) {
            supportedPaymentMethods = paymentMethods;
        } else {
            supportedPaymentMethods = paymentPreference.getSupportedPaymentMethods(paymentMethods);
            supportedPaymentMethods =
                getPaymentMethodsOfType(paymentPreference.getDefaultPaymentTypeId(), supportedPaymentMethods);
        }
        return supportedPaymentMethods;
    }

    private List<PaymentMethod> getPaymentMethodsOfType(String paymentTypeId, List<PaymentMethod> paymentMethodList) {

        List<PaymentMethod> validPaymentMethods = new ArrayList<>();
        if (paymentMethodList != null && paymentTypeId != null && !paymentTypeId.isEmpty()) {
            for (PaymentMethod currentPaymentMethod : paymentMethodList) {
                if (currentPaymentMethod.getPaymentTypeId().equals(paymentTypeId)) {
                    validPaymentMethods.add(currentPaymentMethod);
                }
            }
        } else {
            validPaymentMethods = paymentMethodList;
        }
        return validPaymentMethods;
    }

    public void setFailureRecovery(FailureRecovery failureRecovery) {
        this.failureRecovery = failureRecovery;
    }

    public void recoverFromFailure() {
        if (failureRecovery != null) {
            failureRecovery.recover();
        }
    }
}
