package com.mercadopago.android.px.codediscount;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.mvp.MvpPresenter;
import com.mercadopago.android.px.services.callbacks.Callback;
import com.mercadopago.android.px.services.exceptions.ApiException;

class CodeDiscountPresenter extends MvpPresenter<CodeDiscountView, DiscountRepository> {

    @NonNull
    /* default */ final DiscountRepository discountRepository;

    @NonNull
    protected final AmountRepository amountRepository;

    public CodeDiscountPresenter(@NonNull final DiscountRepository discountRepository,
                                 @NonNull final AmountRepository amountRepository) {
        this.discountRepository = discountRepository;
        this.amountRepository = amountRepository;
    }

    public void getDiscountForCode(final String input) {
        discountRepository.getCodeDiscount(amountRepository.getItemsAmount(), input)
                .enqueue(new Callback<Discount>() {
                    @Override
                    public void success(final Discount discount) {
                        if (isViewAttached()) {
                            final Campaign campaign = getCampaign(discount.getId());
                            discountRepository.saveDiscountCode(input);
                            discountRepository.configureDiscountManually(discount, campaign);
                            getView().processSuccess(discount);
                        }
                    }

                    @Override
                    public void failure(final ApiException apiException) {
                        if (isViewAttached()) {
                            getView().processCodeError();
                        }
                    }
                });
    }

    @Nullable
    private Campaign getCampaign(final String discountId) {
        return discountRepository.getCampaign(discountId);
    }
}

