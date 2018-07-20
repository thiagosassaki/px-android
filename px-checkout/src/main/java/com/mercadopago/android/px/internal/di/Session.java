package com.mercadopago.android.px.internal.di;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.internal.datasource.AmountService;
import com.mercadopago.android.px.internal.datasource.DiscountApiService;
import com.mercadopago.android.px.internal.datasource.DiscountServiceImp;
import com.mercadopago.android.px.internal.datasource.DiscountStorageService;
import com.mercadopago.android.px.internal.datasource.GroupsService;
import com.mercadopago.android.px.internal.datasource.InstallmentService;
import com.mercadopago.android.px.internal.datasource.cache.GroupsCache;
import com.mercadopago.android.px.internal.datasource.cache.GroupsCacheCoordinator;
import com.mercadopago.android.px.internal.datasource.cache.GroupsDiskCache;
import com.mercadopago.android.px.internal.datasource.cache.GroupsMemCache;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.GroupsRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.services.CheckoutService;
import com.mercadopago.android.px.services.util.LocaleUtil;
import com.mercadopago.android.px.util.MercadoPagoESCImpl;

public final class Session extends ApplicationModule
    implements AmountComponent {

    @SuppressLint("StaticFieldLeak") private static Session instance;

    // mem cache - lazy init.
    private ConfigurationModule configurationModule;
    private DiscountRepository discountRepository;
    private AmountRepository amountRepository;
    private GroupsRepository groupsRepository;
    private GroupsCache groupsCache;

    private Session(@NonNull final Context context) {
        super(context);
    }

    public static Session getSession(final Context context) {
        if (instance == null) {
            instance = new Session(context);
        }
        return instance;
    }

    /**
     * Initialize Session with MercadoPagoCheckout information.
     *
     * @param mercadoPagoCheckout
     */
    public void init(@NonNull final MercadoPagoCheckout mercadoPagoCheckout) {
        //TODO add session mapping object.
        // delete old data.
        clear();
        // Store persistent configuration
        final ConfigurationModule configurationModule = getConfigurationModule();
        final DiscountRepository discountRepository = getDiscountRepository();

        final PaymentSettingRepository configuration = configurationModule.getPaymentSettings();
        configuration.configure(mercadoPagoCheckout.getMerchantPublicKey());
        configuration.configure(mercadoPagoCheckout.getCharges());
        configuration.configure(mercadoPagoCheckout.getFlowPreference());
        configuration.configurePrivateKey(mercadoPagoCheckout.getPrivateKey());
        discountRepository
            .configureDiscountManually(mercadoPagoCheckout.getDiscount(), mercadoPagoCheckout.getCampaign());

        final CheckoutPreference checkoutPreference = mercadoPagoCheckout.getCheckoutPreference();
        if (checkoutPreference != null) {
            configuration.configure(checkoutPreference);
        } else {
            configuration.configurePreferenceId(mercadoPagoCheckout.getPreferenceId());
        }
        // end Store persistent configuration
    }

    private void clear() {
        getDiscountRepository().reset();
        getConfigurationModule().reset();
        getGroupsCache().evict();
    }

    public GroupsRepository getGroupsRepository() {
        if (groupsRepository == null) {
            final PaymentSettingRepository paymentSettings = getConfigurationModule().getPaymentSettings();
            groupsRepository = new GroupsService(getAmountRepository(),
                paymentSettings,
                new MercadoPagoESCImpl(getContext(), paymentSettings.getFlow().isESCEnabled()),
                getRetrofitClient().create(CheckoutService.class),
                LocaleUtil.getLanguage(getContext()),
                getGroupsCache());
        }
        return groupsRepository;
    }

    @Override
    public AmountRepository getAmountRepository() {
        if (amountRepository == null) {
            final ConfigurationModule configurationModule = getConfigurationModule();
            final PaymentSettingRepository configuration = configurationModule.getPaymentSettings();
            final UserSelectionRepository userSelectionRepository = configurationModule.getUserSelectionRepository();
            amountRepository = new AmountService(configuration,
                configurationModule.getChargeSolver(),
                new InstallmentService(userSelectionRepository),
                getDiscountRepository());
        }
        return amountRepository;
    }

    @NonNull
    public DiscountRepository getDiscountRepository() {
        if (discountRepository == null) {
            final ConfigurationModule configurationModule = getConfigurationModule();
            discountRepository =
                new DiscountServiceImp(new DiscountStorageService(getSharedPreferences(), getJsonUtil()),
                    new DiscountApiService(getRetrofitClient(),
                        configurationModule.getPaymentSettings()));
        }
        return discountRepository;
    }

    @NonNull
    public ConfigurationModule getConfigurationModule() {
        if (configurationModule == null) {
            configurationModule = new ConfigurationModule(getContext());
        }
        return configurationModule;
    }

    @NonNull
    private GroupsCache getGroupsCache() {
        if (groupsCache == null) {
            groupsCache =
                new GroupsCacheCoordinator(new GroupsDiskCache(getFileManager(), getJsonUtil(), getCacheDir()),
                    new GroupsMemCache());
        }
        return groupsCache;
    }
}
