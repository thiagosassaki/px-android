package com.mercadopago.android.px.internal.datasource;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mercadopago.android.px.core.CheckoutStore;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.services.adapters.MPCall;
import com.mercadopago.android.px.services.callbacks.Callback;
import com.mercadopago.android.px.services.exceptions.ApiException;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

public class DiscountServiceImp implements DiscountRepository {

    @NonNull /* default */ final DiscountStorageService discountStorageService;
    @NonNull /* default */ final DiscountApiService discountApiService;

    /* default */ volatile boolean fetched;

    public DiscountServiceImp(@NonNull final DiscountStorageService discountStorageService,
        @NonNull final DiscountApiService discountApiService) {
        this.discountStorageService = discountStorageService;
        this.discountApiService = discountApiService;
        fetched = false;
    }

    @Override
    public void configureMerchantDiscountManually(@Nullable final Discount discount, @Nullable final Campaign campaign,
        final boolean notAvailableDiscount) {
        final CheckoutStore store = CheckoutStore.getInstance();
        //TODO remove when discount signature change.
        if (store.hasPaymentProcessor() || !store.getPaymentMethodPluginList().isEmpty()) {
            discountStorageService.configureDiscountManually(discount, campaign, notAvailableDiscount);
        }
    }

    @Override
    public void configureDiscountManually(@Nullable final Discount discount, @Nullable final Campaign campaign) {
        discountStorageService.configureDiscountManually(discount, campaign, false);
    }

    @Override
    public void reset() {
        fetched = false;
        discountStorageService.reset();
    }

    @NonNull
    @Override
    public MPCall<Boolean> configureDiscountAutomatically(final BigDecimal amountToPay) {
        return new AutomaticDiscountCall(amountToPay);
    }

    @NonNull
    @Override
    public MPCall<Discount> getCodeDiscount(@NonNull final BigDecimal amount, @NonNull final String inputCode) {
        return discountApiService.getCodeDiscount(amount, inputCode);
    }

    @Nullable
    @Override
    public Discount getDiscount() {
        return discountStorageService.getDiscount();
    }

    @Nullable
    @Override
    public String getDiscountCode() {
        return discountStorageService.getDiscountCode();
    }

    @Nullable
    @Override
    public Campaign getCampaign() {
        return discountStorageService.getCampaign();
    }

    @Nullable
    @Override
    public Campaign getCampaign(final String discountId) {
        Campaign discountCampaign = null;

        for (final Campaign campaign : discountStorageService.getCampaigns()) {
            if (campaign.getId().equals(discountId)) {
                discountCampaign = campaign;
            }
        }

        return discountCampaign;
    }

    public boolean isNotAvailableDiscount() {
        return discountStorageService.isNotAvailableDiscount();
    }

    @Override
    public void saveDiscountCode(@NonNull final String code) {
        discountStorageService.saveDiscountCode(code);
    }

    @Override
    public boolean hasCodeCampaign() {
        return discountStorageService.hasCodeCampaign();
    }

    @Override
    public boolean hasValidDiscount() {
        return getDiscount() != null && getCampaign() != null;
    }

    private class AutomaticDiscountCall implements MPCall<Boolean> {

        /* default */ final BigDecimal amountToPay;

        /* default */ Campaign directCampaign;

        /* default */ AutomaticDiscountCall(final BigDecimal amountToPay) {
            this.amountToPay = amountToPay;
        }

        @Override
        public void enqueue(final Callback<Boolean> callback) {
            resolveCampaigns(callback, new Callable() {
                @Nullable
                @Override
                public Object call() {
                    discountApiService.getCampaigns().enqueue(campaignCache(callback, new Callable() {
                        @Nullable
                        @Override
                        public Object call() {
                            discountApiService.getDiscount(amountToPay).enqueue(directDiscountCallBack(callback));
                            return null;
                        }
                    }));
                    return null;
                }
            });
        }

        @Override
        public void execute(final Callback<Boolean> callback) {
            resolveCampaigns(callback, new Callable() {
                @Nullable
                @Override
                public Object call() {
                    discountApiService.getCampaigns().execute(campaignCache(callback, new Callable() {
                        @Nullable
                        @Override
                        public Object call() {
                            discountApiService.getDiscount(amountToPay).execute(directDiscountCallBack(callback));
                            return null;
                        }
                    }));
                    return null;
                }
            });
        }

        private void resolveCampaigns(final Callback<Boolean> callback, @NonNull final Callable campaignsCall) {
            if (shouldGetDiscount()) {
                fetched = true;
                try {
                    getFromNetwork(callback, campaignsCall);
                } catch (final Exception e) {
                    //Do nothing
                }
            } else {
                callback.success(false);
            }
        }

        private boolean shouldGetDiscount() {
            //TODO remove when discount signature change.
            final CheckoutStore store = CheckoutStore.getInstance();
            return !fetched && (store.getPaymentMethodPluginList().isEmpty() && !store.hasPaymentProcessor());
        }

        private void getFromNetwork(final Callback<Boolean> callback, @NonNull final Callable campaignsCall)
            throws Exception {
            final List<Campaign> storage = discountStorageService.getCampaigns();
            if (storage.isEmpty()) {
                campaignsCall.call();
            } else {
                callback.success(true);
            }
        }

        /* default */ Callback<List<Campaign>> campaignCache(final Callback<Boolean> callback,
            final Callable discountCall) {
            return new Callback<List<Campaign>>() {
                @Override
                public void success(final List<Campaign> campaigns) {
                    successCampaigns(campaigns);
                }

                @Override
                public void failure(final ApiException apiException) {
                    callback.success(false);
                }

                private void successCampaigns(final List<Campaign> campaigns) {
                    if (empty(campaigns)) {
                        callback.failure(new ApiException());
                    } else {
                        analyze(campaigns);
                    }
                }

                private boolean empty(final Collection<Campaign> campaigns) {
                    return campaigns == null || campaigns.isEmpty();
                }

                private void analyze(@NonNull final List<Campaign> campaigns) {
                    if (!hasDirectDiscount(campaigns)) {
                        discountStorageService.saveCampaigns(campaigns);
                        callback.success(false);
                    }
                }

                private boolean hasDirectDiscount(final Iterable<Campaign> campaigns) {
                    // If there is campaign ...
                    for (final Campaign campaign : campaigns) {
                        if (campaign.isDirectDiscountCampaign()) {
                            directCampaign = campaign;
                            try {
                                discountCall.call();
                            } catch (final Exception e) {
                                // do nothing.
                            }
                            return true;
                        }
                    }
                    return false;
                }
            };
        }

        /* default */ Callback<Discount> directDiscountCallBack(@NonNull final Callback<Boolean> callback) {
            return new Callback<Discount>() {
                @Override
                public void success(final Discount discount) {
                    discountStorageService.configureDiscountManually(discount, directCampaign, DiscountServiceImp.this
                        .isNotAvailableDiscount());
                    callback.success(true);
                }

                @Override
                public void failure(final ApiException apiException) {
                    discountStorageService.reset();
                    callback.failure(apiException);
                }
            };
        }
    }
}
