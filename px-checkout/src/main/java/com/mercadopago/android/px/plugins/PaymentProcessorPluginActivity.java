package com.mercadopago.android.px.plugins;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.core.CheckoutStore;
import com.mercadopago.android.px.internal.di.ConfigurationModule;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.PaymentTypes;
import com.mercadopago.android.px.plugins.model.BusinessPayment;
import com.mercadopago.android.px.plugins.model.GenericPayment;
import com.mercadopago.android.px.plugins.model.PluginPayment;
import com.mercadopago.android.px.plugins.model.Processor;
import com.mercadopago.android.px.preferences.CheckoutPreference;

public final class PaymentProcessorPluginActivity extends AppCompatActivity
    implements PaymentProcessor.OnPaymentListener, Processor {

    private static final String EXTRA_BUSINESS_PAYMENT = "extra_business_payment";
    private static final String PROCESSOR_FRAGMENT = "PROCESSOR_FRAGMENT";

    public static Intent getIntent(@NonNull final Context context) {
        return new Intent(context, PaymentProcessorPluginActivity.class);
    }

    public static boolean isBusiness(@Nullable final Intent intent) {
        return intent != null && intent.getExtras() != null && intent.getExtras().containsKey(EXTRA_BUSINESS_PAYMENT);
    }

    @Nullable
    public static BusinessPayment getBusinessPayment(final Intent intent) {
        return (BusinessPayment) intent.getExtras().get(EXTRA_BUSINESS_PAYMENT);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.px_activity_payment_processor);

        final CheckoutStore store = CheckoutStore.getInstance();
        final Session session = Session.getSession(getApplicationContext());
        final ConfigurationModule configurationModule = session.getConfigurationModule();
        final PaymentProcessor paymentProcessor =
            configurationModule.getPaymentSettings()
                .getPaymentConfiguration()
                .getPaymentProcessor();

        final PaymentData paymentData = store.getPaymentData();
        final CheckoutPreference checkoutPreference = configurationModule.getPaymentSettings().getCheckoutPreference();
        final PaymentProcessor.CheckoutData checkoutData =
            new PaymentProcessor.CheckoutData(paymentData, checkoutPreference);

        final PaymentProcessorFragment fragment = paymentProcessor.getFragment(checkoutData, this);
        final Bundle fragmentBundle = paymentProcessor.getFragmentBundle(checkoutData, this);

        if (fragmentBundle != null) {
            fragment.setArguments(fragmentBundle);
        }

        getSupportFragmentManager().beginTransaction()
            .replace(R.id.main_container, fragment, PROCESSOR_FRAGMENT)
            .commit();
    }

    private PaymentResult toPaymentResult(@NonNull final GenericPayment genericPayment) {

        final Payment payment = new Payment();
        payment.setId(genericPayment.paymentId);
        payment.setPaymentMethodId(genericPayment.paymentData.getPaymentMethod().getId());
        payment.setPaymentTypeId(PaymentTypes.PLUGIN);
        payment.setStatus(genericPayment.status);
        payment.setStatusDetail(genericPayment.statusDetail);

        return new PaymentResult.Builder()
            .setPaymentData(genericPayment.paymentData)
            .setPaymentId(payment.getId())
            .setPaymentStatus(payment.getStatus())
            .setPaymentStatusDetail(payment.getStatusDetail())
            .build();
    }

    @Override
    public void process(final BusinessPayment businessPayment) {
        final Intent intent = new Intent();
        intent.putExtra(EXTRA_BUSINESS_PAYMENT, businessPayment);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void process(final GenericPayment genericPayment) {
        final PaymentResult paymentResult = toPaymentResult(genericPayment);
        CheckoutStore.getInstance().setPaymentResult(paymentResult);
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onPaymentFinished(@NonNull final PluginPayment payment) {
        payment.process(this);
    }

    @Override
    public void onPaymentFinished(@NonNull final GenericPayment genericPayment) {
        process(genericPayment);
    }

    @Override
    public void onPaymentFinished(@NonNull final BusinessPayment businessPayment) {
        process(businessPayment);
    }

    @Override
    public void cancelPayment() {
        setResult(RESULT_CANCELED);
        finish();
    }
}