package com.mercadopago.android.px.internal.datasource;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.core.CheckoutStore;
import com.mercadopago.android.px.internal.datasource.cache.GroupsCache;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.GroupsRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.model.Site;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.model.requests.PayerIntent;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.services.CheckoutService;
import com.mercadopago.android.px.services.adapters.MPCall;
import com.mercadopago.android.px.services.callbacks.Callback;
import com.mercadopago.android.px.services.constants.ProcessingModes;
import com.mercadopago.android.px.services.core.Settings;
import com.mercadopago.android.px.services.exceptions.ApiException;
import com.mercadopago.android.px.util.MercadoPagoESC;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static com.mercadopago.android.px.services.core.Settings.PAYMENT_METHODS_OPTIONS_API_VERSION;

public class GroupsService implements GroupsRepository {

    private static final String SEPARATOR = ",";

    @NonNull private final AmountRepository amountRepository;
    @NonNull private final PaymentSettingRepository paymentSettingRepository;
    @NonNull private final MercadoPagoESC mercadoPagoESC;
    @NonNull private final CheckoutService checkoutService;
    @NonNull private final String language;
    @NonNull private final GroupsCache groupsCache;

    public GroupsService(@NonNull final AmountRepository amountRepository,
        @NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final MercadoPagoESC mercadoPagoESC,
        @NonNull final CheckoutService checkoutService,
        @NonNull final String language,
        @NonNull final GroupsCache groupsCache) {
        this.amountRepository = amountRepository;
        this.paymentSettingRepository = paymentSettingRepository;
        this.mercadoPagoESC = mercadoPagoESC;
        this.checkoutService = checkoutService;
        this.language = language;
        this.groupsCache = groupsCache;
    }

    @NonNull
    @Override
    public MPCall<PaymentMethodSearch> getGroups() {
        if (groupsCache.isCached()) {
            return groupsCache.get();
        } else {
            return newCall();
        }
    }

    @NonNull
    private MPCall<PaymentMethodSearch> newCall() {
        return new MPCall<PaymentMethodSearch>() {

            @Override
            public void enqueue(final Callback<PaymentMethodSearch> callback) {
                newRequest().enqueue(getInternalCallback(callback));
            }

            @Override
            public void execute(final Callback<PaymentMethodSearch> callback) {
                newRequest().execute(getInternalCallback(callback));
            }

            @NonNull /* default */ Callback<PaymentMethodSearch> getInternalCallback(
                final Callback<PaymentMethodSearch> callback) {
                return new Callback<PaymentMethodSearch>() {
                    @Override
                    public void success(final PaymentMethodSearch paymentMethodSearch) {
                        groupsCache.put(paymentMethodSearch);
                        callback.success(paymentMethodSearch);
                    }

                    @Override
                    public void failure(final ApiException apiException) {
                        callback.failure(apiException);
                    }
                };
            }
        };
    }

    @NonNull /* default */ MPCall<PaymentMethodSearch> newRequest() {
        //TODO add preference service.
        final CheckoutPreference checkoutPreference = paymentSettingRepository.getCheckoutPreference();
        final Collection<String> excludedPaymentTypesSet = new HashSet<>(checkoutPreference.getExcludedPaymentTypes());
        excludedPaymentTypesSet.addAll(getUnsupportedPaymentTypes(checkoutPreference.getSite()));
        final PayerIntent payerIntent = new PayerIntent(checkoutPreference.getPayer());

        final String excludedPaymentTypesAppended =
            getListAsString(new ArrayList<>(excludedPaymentTypesSet), SEPARATOR);
        final String excludedPaymentMethodsAppended =
            getListAsString(checkoutPreference.getExcludedPaymentMethods(), SEPARATOR);
        final String cardsWithEscAppended = getListAsString(new ArrayList<>(mercadoPagoESC.getESCCardIds()), SEPARATOR);
        final String supportedPluginsAppended =
            getListAsString(CheckoutStore.getInstance().getEnabledPaymentMethodPluginsIds(), SEPARATOR);

        final Integer differentialPricingId =
            checkoutPreference.getDifferentialPricing() != null ? checkoutPreference.getDifferentialPricing()
                .getId() : null;

        return checkoutService
            .getPaymentMethodSearch(Settings.servicesVersion, language, paymentSettingRepository.getPublicKey(),
                amountRepository.getAmountToPay(),
                excludedPaymentTypesAppended,
                excludedPaymentMethodsAppended,
                payerIntent,
                checkoutPreference.getSiteId(),
                PAYMENT_METHODS_OPTIONS_API_VERSION,
                ProcessingModes.AGGREGATOR,
                cardsWithEscAppended,
                supportedPluginsAppended,
                differentialPricingId);
    }

    private Collection<String> getUnsupportedPaymentTypes(@NonNull final Site site) {

        final Collection<String> unsupportedTypesForSite = new ArrayList<>();
        if (Sites.CHILE.getId().equals(site.getId())
            || Sites.VENEZUELA.getId().equals(site.getId())
            || Sites.COLOMBIA.getId().equals(site.getId())) {

            unsupportedTypesForSite.add(PaymentTypes.TICKET);
            unsupportedTypesForSite.add(PaymentTypes.ATM);
            unsupportedTypesForSite.add(PaymentTypes.BANK_TRANSFER);
        }
        return unsupportedTypesForSite;
    }

    private String getListAsString(@NonNull final List<String> list, final String separator) {
        final StringBuilder stringBuilder = new StringBuilder();

        for (final String typeId : list) {
            stringBuilder.append(typeId);
            if (!typeId.equals(list.get(list.size() - 1))) {
                stringBuilder.append(separator);
            }
        }

        return stringBuilder.toString();
    }
}
