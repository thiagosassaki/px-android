package com.mercadopago.android.px.services;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.internal.constants.ProcessingModes;
import com.mercadopago.android.px.internal.core.Settings;
import com.mercadopago.android.px.internal.services.BankDealService;
import com.mercadopago.android.px.internal.services.CheckoutService;
import com.mercadopago.android.px.internal.services.DiscountService;
import com.mercadopago.android.px.internal.services.GatewayService;
import com.mercadopago.android.px.internal.services.IdentificationService;
import com.mercadopago.android.px.internal.services.PaymentService;
import com.mercadopago.android.px.internal.util.LocaleUtil;
import com.mercadopago.android.px.internal.util.RetrofitUtil;
import com.mercadopago.android.px.model.BankDeal;
import com.mercadopago.android.px.model.CardToken;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.IdentificationType;
import com.mercadopago.android.px.model.Installment;
import com.mercadopago.android.px.model.Instructions;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.Payer;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.SavedCardToken;
import com.mercadopago.android.px.model.SavedESCCardToken;
import com.mercadopago.android.px.model.Site;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.model.requests.GroupsIntent;
import com.mercadopago.android.px.model.requests.SecurityCodeIntent;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.mercadopago.android.px.internal.core.Settings.PAYMENT_METHODS_OPTIONS_API_VERSION;
import static com.mercadopago.android.px.internal.core.Settings.PAYMENT_RESULT_API_VERSION;

/**
 * MercadoPagoServices provides an interface to access to our main API methods.
 */
@SuppressWarnings("unused")
public class MercadoPagoServices {

    private static final int DEFAULT_PAYMENT_CONNECT_TIMEOUT = 10;
    private static final int DEFAULT_PAYMENT_READ_TIMEOUT = 20;
    private static final int DEFAULT_PAYMENT_WRITE_TIMEOUT = 20;

    private final Context context;
    private final String publicKey;
    private final String privateKey;
    private final String processingMode;

    /**
     * @param context context to obtain connection interceptor and cache.
     * @param publicKey merchant public key / collector public key {@see <a href="https://www.mercadopago.com/mla/account/credentials">credentials</a>}
     * @param privateKey user private key / access_token if you have it.
     */
    public MercadoPagoServices(@NonNull final Context context,
        @NonNull final String publicKey,
        @Nullable final String privateKey) {
        this.context = context;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        processingMode = ProcessingModes.AGGREGATOR;
    }

    public void getCheckoutPreference(final String checkoutPreferenceId, final Callback<CheckoutPreference> callback) {
        final CheckoutService service = RetrofitUtil.getRetrofitClient(context).create(CheckoutService.class);
        service.getPreference(Settings.servicesVersion, checkoutPreferenceId, publicKey).enqueue(callback);
    }

    public void getInstructions(final Long paymentId, final String paymentTypeId,
        final Callback<Instructions> callback) {
        final CheckoutService service = RetrofitUtil.getRetrofitClient(context).create(CheckoutService.class);
        service.getPaymentResult(Settings.servicesVersion,
            context.getResources().getConfiguration().locale.getLanguage(),
            paymentId,
            publicKey, privateKey, paymentTypeId, PAYMENT_RESULT_API_VERSION)
            .enqueue(callback);
    }

    public void getPaymentMethodSearch(final BigDecimal amount, final List<String> excludedPaymentTypes,
        final List<String> excludedPaymentMethods, final List<String> cardsWithEsc, final List<String> supportedPlugins,
        final Payer payer, final Site site, @Nullable final Integer differentialPricing,
        final Callback<PaymentMethodSearch> callback) {
        final GroupsIntent groupsIntent = new GroupsIntent(privateKey);
        final CheckoutService service = RetrofitUtil.getRetrofitClient(context).create(CheckoutService.class);

        final String separator = ",";
        final String excludedPaymentTypesAppended = getListAsString(excludedPaymentTypes, separator);
        final String excludedPaymentMethodsAppended = getListAsString(excludedPaymentMethods, separator);
        final String cardsWithEscAppended = getListAsString(cardsWithEsc, separator);
        final String supportedPluginsAppended = getListAsString(supportedPlugins, separator);

        service.getPaymentMethodSearch(Settings.servicesVersion,
            context.getResources().getConfiguration().locale.getLanguage(), this.publicKey, amount,
            excludedPaymentTypesAppended, excludedPaymentMethodsAppended, groupsIntent, site.getId(),
            PAYMENT_METHODS_OPTIONS_API_VERSION, processingMode, cardsWithEscAppended, supportedPluginsAppended,
            differentialPricing).
            enqueue(callback);
    }

    public void createToken(final SavedCardToken savedCardToken, final Callback<Token> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                savedCardToken.setDevice(context);
                GatewayService service = RetrofitUtil.getRetrofitClient(context).create(GatewayService.class);
                service.getToken(publicKey, privateKey, savedCardToken).enqueue(callback);
            }
        }).start();
    }

    public void createToken(final CardToken cardToken, final Callback<Token> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                cardToken.setDevice(context);
                GatewayService service = RetrofitUtil.getRetrofitClient(context).create(GatewayService.class);
                service.getToken(publicKey, privateKey, cardToken).enqueue(callback);
            }
        }).start();
    }

    public void createToken(final SavedESCCardToken savedESCCardToken, final Callback<Token> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                savedESCCardToken.setDevice(context);
                GatewayService service = RetrofitUtil.getRetrofitClient(context).create(GatewayService.class);
                service.getToken(publicKey, privateKey, savedESCCardToken).enqueue(callback);
            }
        }).start();
    }

    public void cloneToken(final String tokenId, final Callback<Token> callback) {
        final GatewayService service = RetrofitUtil.getRetrofitClient(context).create(GatewayService.class);
        service.getToken(tokenId, publicKey, privateKey).enqueue(callback);
    }

    public void putSecurityCode(final String tokenId, final SecurityCodeIntent securityCodeIntent,
        final Callback<Token> callback) {
        final GatewayService service = RetrofitUtil.getRetrofitClient(context).create(GatewayService.class);
        service.getToken(tokenId, publicKey, privateKey, securityCodeIntent).enqueue(callback);
    }

    public void getBankDeals(final Callback<List<BankDeal>> callback) {
        final BankDealService service = RetrofitUtil.getRetrofitClient(context).create(BankDealService.class);
        service.getBankDeals(publicKey, privateKey, LocaleUtil.getLanguage(context))
            .enqueue(callback);
    }

    public void getIdentificationTypes(final Callback<List<IdentificationType>> callback) {
        final IdentificationService service =
            RetrofitUtil.getRetrofitClient(context).create(IdentificationService.class);
        service.getIdentificationTypes(publicKey, privateKey).enqueue(callback);
    }

    public void getInstallments(final String bin,
        final BigDecimal amount,
        final Long issuerId,
        final String paymentMethodId,
        @Nullable final Integer differentialPricingId,
        final Callback<List<Installment>> callback) {
        final PaymentService service = RetrofitUtil.getRetrofitClient(context).create(PaymentService.class);
        service.getInstallments(Settings.servicesVersion, publicKey, privateKey, bin, amount, issuerId,
            paymentMethodId, LocaleUtil.getLanguage(context), processingMode, differentialPricingId).enqueue(callback);
    }

    public void getIssuers(final String paymentMethodId, final String bin, final Callback<List<Issuer>> callback) {
        final PaymentService service = RetrofitUtil.getRetrofitClient(context).create(PaymentService.class);
        service
            .getIssuers(Settings.servicesVersion, publicKey, privateKey, paymentMethodId, bin, processingMode)
            .enqueue(callback);
    }

    public void getPaymentMethods(final Callback<List<PaymentMethod>> callback) {
        final PaymentService service = RetrofitUtil.getRetrofitClient(context).create(PaymentService.class);
        service.getPaymentMethods(publicKey, privateKey).enqueue(callback);
    }

    public void getDirectDiscount(final String amount, final String payerEmail, final Callback<Discount> callback) {
        final DiscountService service = RetrofitUtil.getRetrofitClient(context).create(DiscountService.class);
        service.getDiscount(publicKey, amount, payerEmail).enqueue(callback);
    }

    public void getCodeDiscount(final String amount, final String payerEmail, final String couponCode,
        final Callback<Discount> callback) {
        final DiscountService service = RetrofitUtil.getRetrofitClient(context).create(DiscountService.class);
        service.getDiscount(publicKey, amount, payerEmail, couponCode).enqueue(callback);
    }

    public void createPayment(final String transactionId, final Map<String, Object> paymentData,
        @NonNull final Map<String, String> query,
        final Callback<Payment> callback) {
        final PaymentService customService = RetrofitUtil.getRetrofitClient(context).create(PaymentService.class);
        customService.createPayment(transactionId, paymentData, query).enqueue(callback);
    }

    private String getListAsString(final List<String> list, final String separator) {
        final StringBuilder stringBuilder = new StringBuilder();
        if (list != null) {
            for (final String typeId : list) {
                stringBuilder.append(typeId);
                if (!typeId.equals(list.get(list.size() - 1))) {
                    stringBuilder.append(separator);
                }
            }
        }
        return stringBuilder.toString();
    }
}
