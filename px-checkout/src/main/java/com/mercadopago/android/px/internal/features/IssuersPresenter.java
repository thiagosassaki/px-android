package com.mercadopago.android.px.internal.features;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.base.MvpPresenter;
import com.mercadopago.android.px.internal.callbacks.FailureRecovery;
import com.mercadopago.android.px.internal.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.internal.features.providers.IssuersProvider;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.model.CardInfo;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaserber on 10/11/16.
 */

public class IssuersPresenter extends MvpPresenter<IssuersActivityView, IssuersProvider> {

    @NonNull private final UserSelectionRepository userSelectionRepository;
    //Local vars
    private List<Issuer> mIssuers;
    private CardInfo mCardInfo;
    private FailureRecovery mFailureRecovery;

    //Card Info
    private String mBin = "";

    public IssuersPresenter(@NonNull final UserSelectionRepository userSelectionRepository) {
        this.userSelectionRepository = userSelectionRepository;
    }

    public void initialize() {
        if (wereIssuersSet()) {
            resolveIssuers(mIssuers);
        } else {
            getIssuersAsync();
        }
    }

    private boolean wereIssuersSet() {
        return mIssuers != null;
    }

    private void resolveIssuers(List<Issuer> issuers) {
        if (issuers == null) {
            issuers = new ArrayList<>();
        }

        mIssuers = issuers;

        if (mIssuers.isEmpty()) {
            getView().showError(getResourcesProvider().getEmptyIssuersError(), "");
        } else if (mIssuers.size() == 1) {
            final Issuer issuer = issuers.get(0);
            storeIssuerSelection(issuer);
            getView().finishWithResult();
        } else {
            getView().showHeader();
            getView().showIssuers(issuers, getDpadSelectionCallback());
        }
    }

    private void getIssuersAsync() {
        getView().showLoadingView();

        getResourcesProvider().getIssuers(userSelectionRepository.getPaymentMethod().getId(), mBin,
            new TaggedCallback<List<Issuer>>(ApiUtil.RequestOrigin.GET_ISSUERS) {
                @Override
                public void onSuccess(List<Issuer> issuers) {
                    getView().stopLoadingView();
                    resolveIssuers(issuers);
                }

                @Override
                public void onFailure(MercadoPagoError error) {
                    getView().stopLoadingView();

                    setFailureRecovery(new FailureRecovery() {
                        @Override
                        public void recover() {
                            getIssuersAsync();
                        }
                    });

                    getView().showError(error, ApiUtil.RequestOrigin.GET_ISSUERS);
                }
            });
    }

    private OnSelectedCallback<Integer> getDpadSelectionCallback() {
        return new OnSelectedCallback<Integer>() {
            @Override
            public void onSelected(Integer position) {
                onItemSelected(position);
            }
        };
    }

    public void onItemSelected(final int position) {
        final Issuer issuer = mIssuers.get(position);
        storeIssuerSelection(issuer);
        getView().finishWithResult();
    }

    private void storeIssuerSelection(@NonNull final Issuer issuer) {
        userSelectionRepository.select(issuer);
    }

    public void recoverFromFailure() {
        if (mFailureRecovery != null) {
            mFailureRecovery.recover();
        }
    }

    public PaymentMethod getPaymentMethod() {
        return userSelectionRepository.getPaymentMethod();
    }

    public void setIssuers(List<Issuer> issuers) {
        mIssuers = issuers;
    }

    public CardInfo getCardInfo() {
        return mCardInfo;
    }

    public void setFailureRecovery(FailureRecovery failureRecovery) {
        mFailureRecovery = failureRecovery;
    }

    public FailureRecovery getFailureRecovery() {
        return mFailureRecovery;
    }

    public void setCardInfo(CardInfo cardInfo) {
        mCardInfo = cardInfo;

        if (mCardInfo != null) {
            mBin = mCardInfo.getFirstSixDigits();
        }
    }

    public String getBin() {
        return mBin;
    }

    public boolean isRequiredCardDrawn() {
        return mCardInfo != null && userSelectionRepository.getPaymentMethod() != null;
    }
}
