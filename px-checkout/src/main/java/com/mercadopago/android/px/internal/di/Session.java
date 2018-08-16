package com.mercadopago.android.px.internal.di;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.configuration.PaymentConfiguration;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.internal.datasource.MercadoPagoServicesAdapter;
import com.mercadopago.android.px.internal.datasource.AmountService;
import com.mercadopago.android.px.internal.datasource.DiscountApiService;
import com.mercadopago.android.px.internal.datasource.DiscountServiceImp;
import com.mercadopago.android.px.internal.datasource.DiscountStorageService;
import com.mercadopago.android.px.internal.datasource.GroupsService;
import com.mercadopago.android.px.internal.datasource.InstallmentService;
import com.mercadopago.android.px.internal.datasource.MercadoPagoESCImpl;
import com.mercadopago.android.px.internal.datasource.PluginService;
import com.mercadopago.android.px.internal.datasource.cache.GroupsCache;
import com.mercadopago.android.px.internal.datasource.cache.GroupsCacheCoordinator;
import com.mercadopago.android.px.internal.datasource.cache.GroupsDiskCache;
import com.mercadopago.android.px.internal.datasource.cache.GroupsMemCache;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.GroupsRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.PluginRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.services.CheckoutService;
import com.mercadopago.android.px.internal.util.LocaleUtil;
import com.mercadopago.android.px.internal.util.TextUtil;

public final class Session extends ApplicationModule
    implements AmountComponent {

    /**
     * This singleton instance is safe because session will work with
     * application context. Application context it's never leaking.
     */
    @SuppressLint("StaticFieldLeak") private static Session instance;

    // mem cache - lazy init.
    private ConfigurationModule configurationModule;
    private DiscountRepository discountRepository;
    private AmountRepository amountRepository;
    private GroupsRepository groupsRepository;
    private GroupsCache groupsCache;
    private PluginService pluginRepository;

    private Session(@NonNull final Context context) {
        super(context.getApplicationContext());
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
        // Store persistent paymentSetting
        final ConfigurationModule configurationModule = getConfigurationModule();
        final DiscountRepository discountRepository = getDiscountRepository();

        final PaymentConfiguration paymentConfiguration = mercadoPagoCheckout.getPaymentConfiguration();

        final PaymentSettingRepository paymentSetting = configurationModule.getPaymentSettings();
        paymentSetting.configure(mercadoPagoCheckout.getPublicKey());

        paymentSetting.configure(mercadoPagoCheckout.getAdvancedConfiguration());
        paymentSetting.configurePrivateKey(mercadoPagoCheckout.getPrivateKey());
        paymentSetting.configure(paymentConfiguration);

        discountRepository.configureMerchantDiscountManually(paymentConfiguration);
        resolvePreference(mercadoPagoCheckout, paymentSetting);
        // end Store persistent paymentSetting
    }

    private void resolvePreference(@NonNull final MercadoPagoCheckout mercadoPagoCheckout,
        final PaymentSettingRepository paymentSetting) {
        final String preferenceId = mercadoPagoCheckout.getPreferenceId();

        if (TextUtil.isEmpty(preferenceId)) {

            if (TextUtil.isEmpty(mercadoPagoCheckout
                .getPaymentConfiguration()
                .getPreferenceId())) {

                //Pref abierta
                paymentSetting.configure(mercadoPagoCheckout.
                    getPaymentConfiguration()
                    .getCheckoutPreference());
            } else {
                paymentSetting.configurePreferenceId(mercadoPagoCheckout
                    .getPaymentConfiguration()
                    .getPreferenceId());
            }
        } else {
            //Pref cerrada.
            paymentSetting.configurePreferenceId(preferenceId);
        }
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
                getMercadoPagoESC(),
                getRetrofitClient().create(CheckoutService.class),
                LocaleUtil.getLanguage(getContext()),
                getGroupsCache());
        }
        return groupsRepository;
    }

    @NonNull
    public MercadoPagoESCImpl getMercadoPagoESC() {
        final PaymentSettingRepository paymentSettings = getConfigurationModule().getPaymentSettings();
        return new MercadoPagoESCImpl(getContext(), paymentSettings.getAdvancedConfiguration().isEscEnabled());
    }

    @NonNull
    public MercadoPagoServicesAdapter getMercadoPagoServiceAdapter() {
        final PaymentSettingRepository paymentSettings = getConfigurationModule().getPaymentSettings();
        return new MercadoPagoServicesAdapter(getContext(), paymentSettings.getPublicKey(),
            paymentSettings.getPrivateKey());
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
            final PaymentSettingRepository paymentSettings = configurationModule.getPaymentSettings();
            discountRepository =
                new DiscountServiceImp(new DiscountStorageService(getSharedPreferences(), getJsonUtil()),
                    new DiscountApiService(getRetrofitClient(),
                        paymentSettings),
                    paymentSettings);
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

    @NonNull
    public PluginRepository getPluginRepository() {
        if (pluginRepository == null) {
            pluginRepository = new PluginService(getContext(), getConfigurationModule().getPaymentSettings());
        }
        return pluginRepository;
    }
}
