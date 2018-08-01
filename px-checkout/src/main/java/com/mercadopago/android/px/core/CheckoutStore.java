package com.mercadopago.android.px.core;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.hooks.CheckoutHooks;
import com.mercadopago.android.px.hooks.Hook;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.plugins.DataInitializationTask;
import com.mercadopago.android.px.plugins.PaymentMethodPlugin;
import com.mercadopago.android.px.plugins.PaymentProcessor;
import com.mercadopago.android.px.preferences.PaymentResultScreenPreference;
import com.mercadopago.android.px.review_and_confirm.models.ReviewAndConfirmPreferences;
import com.mercadopago.android.px.util.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

import static com.mercadopago.android.px.plugins.PaymentProcessor.PAYMENT_PROCESSOR_KEY;

public final class CheckoutStore {

    private static final CheckoutStore INSTANCE = new CheckoutStore();

    //Preferences
    private PaymentResultScreenPreference paymentResultScreenPreference;

    private ReviewAndConfirmPreferences reviewAndConfirmPreferences;

    //Config
    private DataInitializationTask dataInitializationTask;
    private List<PaymentMethodPlugin> paymentMethodPluginList = new ArrayList<>();
    private Map<String, PaymentProcessor> paymentPlugins = new HashMap<>();

    @Deprecated
    private CheckoutHooks checkoutHooks;

    //App state
    private Hook hook;
    private final Map<String, Object> data = new HashMap<>();

    //Payment
    private PaymentResult paymentResult;
    private PaymentData paymentData;
    private Payment payment;

    private CheckoutStore() {
    }

    public static CheckoutStore getInstance() {
        return INSTANCE;
    }

    public PaymentResultScreenPreference getPaymentResultScreenPreference() {
        return paymentResultScreenPreference;
    }

    @NonNull
    public ReviewAndConfirmPreferences getReviewAndConfirmPreferences() {
        return reviewAndConfirmPreferences;
    }

    public void setPaymentResultScreenPreference(final PaymentResultScreenPreference paymentResultScreenPreference) {
        this.paymentResultScreenPreference = paymentResultScreenPreference;
    }

    public DataInitializationTask getDataInitializationTask() {
        return dataInitializationTask;
    }

    public void setDataInitializationTask(final DataInitializationTask dataInitializationTask) {
        this.dataInitializationTask = dataInitializationTask;
    }

    @NonNull
    public List<PaymentMethodPlugin> getPaymentMethodPluginList() {
        return paymentMethodPluginList;
    }

    public PaymentMethodPlugin getPaymentMethodPluginById(@NonNull final String id) {
        for (final PaymentMethodPlugin plugin : paymentMethodPluginList) {
            if (plugin.getId().equalsIgnoreCase(id)) {
                return plugin;
            }
        }
        return null;
    }

    public void setPaymentMethodPluginList(@NonNull final List<PaymentMethodPlugin> paymentMethodPluginList) {
        this.paymentMethodPluginList = paymentMethodPluginList;
    }

    public boolean hasEnabledPaymentMethodPlugin() {
        boolean result = false;
        for (final PaymentMethodPlugin plugin : paymentMethodPluginList) {
            if (plugin.isEnabled(CheckoutStore.getInstance().getData())) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Nonnull
    public List<String> getEnabledPaymentMethodPluginsIds() {
        final List<String> pluginIds = new ArrayList<>();
        for (final PaymentMethodPlugin plugin : paymentMethodPluginList) {
            if (plugin.isEnabled(CheckoutStore.getInstance().getData())) {
                pluginIds.add(plugin.getId());
            }
        }
        return pluginIds;
    }

    public PaymentMethodPlugin getFirstEnabledPlugin() {
        for (final PaymentMethodPlugin plugin : paymentMethodPluginList) {
            if (plugin.isEnabled(CheckoutStore.getInstance().getData())) {
                return plugin;
            }
        }
        return null;
    }

    public int getPaymentMethodPluginCount() {
        int count = 0;
        for (final PaymentMethodPlugin plugin : paymentMethodPluginList) {
            if (plugin.isEnabled(CheckoutStore.getInstance().getData())) {
                count++;
            }
        }
        return count;
    }

    public void setPaymentPlugins(Map<String, PaymentProcessor> paymentPlugins) {
        this.paymentPlugins = paymentPlugins;
    }

    public Hook getHook() {
        return hook;
    }

    public void setHook(Hook hook) {
        this.hook = hook;
    }

    @Deprecated
    public CheckoutHooks getCheckoutHooks() {
        return checkoutHooks;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public PaymentProcessor doesPaymentProcessorSupportPaymentMethodSelected(
        @NonNull final String selectedPaymentMethodId) {
        PaymentProcessor paymentProcessor = null;
        if (!TextUtils.isEmpty(selectedPaymentMethodId)) {
            paymentProcessor = paymentPlugins.get(PAYMENT_PROCESSOR_KEY);
            if (paymentProcessor == null || !paymentProcessor.support(selectedPaymentMethodId, getData())) {
                paymentProcessor = paymentPlugins.get(selectedPaymentMethodId);
            }
        }
        return paymentProcessor;
    }

    public boolean hasPaymentProcessor() {
        return paymentPlugins.containsKey(PAYMENT_PROCESSOR_KEY);
    }

    public void addPaymentPlugins(@NonNull final PaymentProcessor paymentProcessor,
        @NonNull final String paymentMethod) {
        paymentPlugins.put(paymentMethod, paymentProcessor);
    }

    public PaymentData getPaymentData() {
        return paymentData;
    }

    public void setPaymentData(final PaymentData paymentData) {
        this.paymentData = paymentData;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public PaymentResult getPaymentResult() {
        return paymentResult;
    }

    public void setPaymentResult(PaymentResult paymentResult) {
        this.paymentResult = paymentResult;
    }

    public void setReviewAndConfirmPreferences(final ReviewAndConfirmPreferences reviewAndConfirmPreferences) {
        this.reviewAndConfirmPreferences = reviewAndConfirmPreferences;
    }

    public void reset() {
        paymentResult = null;
        paymentData = null;
        payment = null;
    }

    public void resetPlugins() {
        paymentPlugins = new HashMap<>();
        paymentMethodPluginList = new ArrayList<>();
    }
}