package com.mercadopago.android.px.internal.datasource;

import android.content.Context;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.PluginRepository;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.plugins.PaymentMethodPlugin;
import com.mercadopago.android.px.plugins.model.PaymentMethodInfo;
import com.mercadopago.android.px.preferences.PaymentConfiguration;
import java.util.ArrayList;
import java.util.Collection;

public class PluginService implements PluginRepository {

    @NonNull private final Context context;
    @NonNull private final PaymentSettingRepository paymentSettings;

    public PluginService(@NonNull final Context context,
        @NonNull final PaymentSettingRepository paymentSettings) {
        this.context = context;
        this.paymentSettings = paymentSettings;
    }

    @Override
    @NonNull
    public PaymentMethodPlugin getPlugin(@NonNull final String pluginId) throws IllegalStateException {

        for (final PaymentMethodPlugin plugin : all()) {
            if (plugin.getId().equalsIgnoreCase(pluginId)) {
                return plugin;
            }
        }

        throw new IllegalStateException("there is no plugin with id " + pluginId);
    }

    @NonNull
    private Iterable<PaymentMethodPlugin> all() {
        final PaymentConfiguration paymentConfiguration = paymentSettings.getPaymentConfiguration();
        return paymentConfiguration == null ? new ArrayList<PaymentMethodPlugin>() :
            paymentConfiguration.getPaymentMethodPluginList();
    }

    @Override
    @NonNull
    public PaymentMethod getPluginAsPaymentMethod(@NonNull final String pluginId, @NonNull final String paymentType) {
        final PaymentMethodPlugin plugin = getPlugin(pluginId);
        final PaymentMethodInfo paymentInfo = getPaymentMethodInfo(plugin);
        return new PaymentMethod(paymentInfo.getId(), paymentInfo.getName(), paymentType);
    }

    @Override
    @NonNull
    public PaymentMethodInfo getPaymentMethodInfo(final PaymentMethodPlugin plugin) {
        return plugin.getPaymentMethodInfo(context);
    }

    @NonNull
    @Override
    public PaymentMethodInfo getPaymentMethodInfo(@NonNull final String pluginId) {
        return getPaymentMethodInfo(getPlugin(pluginId));
    }

    @Override
    public Collection<PaymentMethodPlugin> getPaymentMethodPluginList() {
        final PaymentConfiguration paymentConfiguration = paymentSettings.getPaymentConfiguration();
        if (paymentConfiguration == null) {
            return new ArrayList<>();
        } else {
            return paymentSettings.getPaymentConfiguration().getPaymentMethodPluginList();
        }
    }

    @Override
    public int getPaymentMethodPluginCount() {
        int count = 0;
        for (final PaymentMethodPlugin plugin : all()) {
            if (plugin.isEnabled()) {
                count++;
            }
        }
        return count;
    }

    @Override
    @NonNull
    public PaymentMethodPlugin getFirstEnabledPlugin() {
        for (final PaymentMethodPlugin plugin : all()) {
            if (plugin.isEnabled()) {
                return plugin;
            }
        }
        throw new IllegalStateException("there is no plugin");
    }

    @Override
    public PluginInitializationTask getInitTask() {
        return new PluginInitializationTask(all());
    }

    @Override
    public boolean hasEnabledPaymentMethodPlugin() {
        boolean result = false;
        for (final PaymentMethodPlugin plugin : all()) {
            if (plugin.isEnabled()) {
                result = true;
                break;
            }
        }
        return result;
    }
}
