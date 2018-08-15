package com.mercadopago.android.px.internal.features;

import android.content.Context;
import com.mercadopago.android.px.internal.callbacks.FailureRecovery;
import com.mercadopago.android.px.model.CardInfo;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentType;
import java.util.List;

/**
 * Created by vaserber on 10/25/16.
 */

public class PaymentTypesPresenter {

    private PaymentTypesActivityView mView;
    private Context mContext;
    private FailureRecovery mFailureRecovery;

    //Activity parameters
    private CardInfo mCardInfo;

    //Local vars
    private PaymentMethod mPaymentMethod;
    private List<PaymentMethod> mPaymentMethodList;
    private List<PaymentType> mPaymentTypeList;

    public void setView(PaymentTypesActivityView view) {
        mView = view;
    }

    private void setFailureRecovery(FailureRecovery failureRecovery) {
        mFailureRecovery = failureRecovery;
    }

    public void setCardInfo(CardInfo cardInfo) {
        mCardInfo = cardInfo;
    }

    public CardInfo getCardInfo() {
        return mCardInfo;
    }

    public void setPaymentMethodList(List<PaymentMethod> paymentMethodList) {
        mPaymentMethodList = paymentMethodList;
    }

    public void setPaymentTypesList(List<PaymentType> paymentTypeList) {
        mPaymentTypeList = paymentTypeList;
    }

    public boolean isCardInfoAvailable() {
        return mCardInfo != null;
    }

    public List<PaymentMethod> getPaymentMethodList() {
        return mPaymentMethodList;
    }

    public List<PaymentType> getPaymentTypeList() {
        return mPaymentTypeList;
    }

    public void initializePaymentMethod() {
        mPaymentMethod = mPaymentMethodList.get(0);
    }

    public PaymentMethod getPaymentMethod() {
        return mPaymentMethod;
    }

    public void validateActivityParameters() throws IllegalStateException {
        if (mPaymentMethodList == null || mPaymentMethodList.isEmpty()) {
            mView.onInvalidStart("payment method list is null or empty");
        } else if (mPaymentTypeList == null || mPaymentTypeList.isEmpty()) {
            mView.onInvalidStart("payment types list is null or empty");
        } else {
            mView.onValidStart();
        }
    }

    public void loadPaymentTypes() {
        mView.initializePaymentTypes(mPaymentTypeList);
    }

    public void recoverFromFailure() {
        if (mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }

    public void onItemSelected(int position) {
        mView.finishWithResult(mPaymentTypeList.get(position));
    }
}
