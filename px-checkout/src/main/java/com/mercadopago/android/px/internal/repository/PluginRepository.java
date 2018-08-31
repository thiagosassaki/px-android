package com.mercadopago.android.px.internal.repository;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.core.PaymentMethodPlugin;
import com.mercadopago.android.px.internal.datasource.PluginInitializationTask;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodInfo;
import java.util.Collection;

public interface PluginRepository {

    @NonNull
    PaymentMethodPlugin getPlugin(@NonNull String pluginId);

    @NonNull
    PaymentMethod getPluginAsPaymentMethod(@NonNull final String pluginId, @NonNull final String paymentType);

    @NonNull
    PaymentMethodInfo getPaymentMethodInfo(PaymentMethodPlugin plugin);

    @NonNull
    PaymentMethodInfo getPaymentMethodInfo(@NonNull final String pluginId);

    Collection<PaymentMethodPlugin> getEnabledPlugins();

    boolean hasEnabledPaymentMethodPlugin();

    int getPaymentMethodPluginCount();

    @NonNull
    PaymentMethodPlugin getFirstEnabledPlugin();

    PluginInitializationTask getInitTask();
}
