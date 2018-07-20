package com.mercadopago.android.px.services.core;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.model.BankDeal;
import com.mercadopago.android.px.model.CardToken;
import com.mercadopago.android.px.model.Customer;
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
import com.mercadopago.android.px.model.requests.PayerIntent;
import com.mercadopago.android.px.model.requests.SecurityCodeIntent;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.services.BankDealService;
import com.mercadopago.android.px.services.CheckoutService;
import com.mercadopago.android.px.services.CustomService;
import com.mercadopago.android.px.services.DiscountService;
import com.mercadopago.android.px.services.GatewayService;
import com.mercadopago.android.px.services.IdentificationService;
import com.mercadopago.android.px.services.PaymentService;
import com.mercadopago.android.px.services.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.android.px.services.callbacks.Callback;
import com.mercadopago.android.px.services.constants.ProcessingModes;
import com.mercadopago.android.px.services.util.HttpClientUtil;
import com.mercadopago.android.px.services.util.JsonUtil;
import com.mercadopago.android.px.services.util.LocaleUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.mercadopago.android.px.services.core.Settings.PAYMENT_METHODS_OPTIONS_API_VERSION;
import static com.mercadopago.android.px.services.core.Settings.PAYMENT_RESULT_API_VERSION;

public class MercadoPagoServices {

    private static final String MP_API_BASE_URL = "https://api.mercadopago.com";

    private static final int DEFAULT_CONNECT_TIMEOUT = 10;
    private static final int DEFAULT_READ_TIMEOUT = 20;
    private static final int DEFAULT_WRITE_TIMEOUT = 20;

    private static final int DEFAULT_PAYMENT_CONNECT_TIMEOUT = 10;
    private static final int DEFAULT_PAYMENT_READ_TIMEOUT = 20;
    private static final int DEFAULT_PAYMENT_WRITE_TIMEOUT = 20;

    private final Context mContext;
    private final String mPublicKey;
    private final String mPrivateKey;
    private final String mProcessingMode;

    public MercadoPagoServices(@NonNull final Context mContext,
        @NonNull final String mPublicKey,
        @Nullable final String mPrivateKey) {
        this.mContext = mContext;
        this.mPublicKey = mPublicKey;
        this.mPrivateKey = mPrivateKey;
        mProcessingMode = ProcessingModes.AGGREGATOR;
    }

    public void getCheckoutPreference(final String checkoutPreferenceId, final Callback<CheckoutPreference> callback) {
        final CheckoutService service = getDefaultRetrofit(mContext).create(CheckoutService.class);
        service.getPreference(Settings.servicesVersion, checkoutPreferenceId, mPublicKey).enqueue(callback);
    }

    public void getInstructions(final Long paymentId, final String paymentTypeId,
        final Callback<Instructions> callback) {
        final CheckoutService service = getDefaultRetrofit(mContext).create(CheckoutService.class);
        service.getPaymentResult(Settings.servicesVersion,
            mContext.getResources().getConfiguration().locale.getLanguage(),
            paymentId,
            mPublicKey, mPrivateKey, paymentTypeId, PAYMENT_RESULT_API_VERSION)
            .enqueue(callback);
    }

    @SuppressWarnings("unused")
    public void getPaymentMethodSearch(final BigDecimal amount, final List<String> excludedPaymentTypes,
        final List<String> excludedPaymentMethods, final List<String> cardsWithEsc, final List<String> supportedPlugins,
        final Payer payer, final Site site, final Callback<PaymentMethodSearch> callback) {
        final PayerIntent payerIntent = new PayerIntent(payer);
        final CheckoutService service = getDefaultRetrofit(mContext).create(CheckoutService.class);

        final String separator = ",";
        final String excludedPaymentTypesAppended = getListAsString(excludedPaymentTypes, separator);
        final String excludedPaymentMethodsAppended = getListAsString(excludedPaymentMethods, separator);
        final String cardsWithEscAppended = getListAsString(cardsWithEsc, separator);
        final String supportedPluginsAppended = getListAsString(supportedPlugins, separator);

        service.getPaymentMethodSearch(Settings.servicesVersion,
            mContext.getResources().getConfiguration().locale.getLanguage(), this.mPublicKey, amount,
            excludedPaymentTypesAppended, excludedPaymentMethodsAppended, payerIntent, site.getId(),
            PAYMENT_METHODS_OPTIONS_API_VERSION, mProcessingMode, cardsWithEscAppended, supportedPluginsAppended).
            enqueue(callback);
    }

    public void createToken(final SavedCardToken savedCardToken, final Callback<Token> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                savedCardToken.setDevice(mContext);
                GatewayService service = getGatewayRetrofit().create(GatewayService.class);
                service.getToken(mPublicKey, mPrivateKey, savedCardToken).enqueue(callback);
            }
        }).start();
    }

    public void createToken(final CardToken cardToken, final Callback<Token> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                cardToken.setDevice(mContext);
                GatewayService service = getGatewayRetrofit().create(GatewayService.class);
                service.getToken(mPublicKey, mPrivateKey, cardToken).enqueue(callback);
            }
        }).start();
    }

    public void createToken(final SavedESCCardToken savedESCCardToken, final Callback<Token> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                savedESCCardToken.setDevice(mContext);
                GatewayService service = getGatewayRetrofit().create(GatewayService.class);
                service.getToken(mPublicKey, mPrivateKey, savedESCCardToken).enqueue(callback);
            }
        }).start();
    }

    public void cloneToken(final String tokenId, final Callback<Token> callback) {
        GatewayService service = getGatewayRetrofit().create(GatewayService.class);
        service.getToken(tokenId, this.mPublicKey, mPrivateKey).enqueue(callback);
    }

    public void putSecurityCode(final String tokenId, final SecurityCodeIntent securityCodeIntent,
        final Callback<Token> callback) {
        GatewayService service = getGatewayRetrofit().create(GatewayService.class);
        service.getToken(tokenId, this.mPublicKey, mPrivateKey, securityCodeIntent).enqueue(callback);
    }

    public void getBankDeals(final Callback<List<BankDeal>> callback) {
        BankDealService service = getDefaultRetrofit(mContext).create(BankDealService.class);
        service.getBankDeals(this.mPublicKey, mPrivateKey, LocaleUtil.getLanguage(mContext))
            .enqueue(callback);
    }

    public void getIdentificationTypes(Callback<List<IdentificationType>> callback) {
        IdentificationService service = getDefaultRetrofit(mContext).create(IdentificationService.class);
        service.getIdentificationTypes(this.mPublicKey, this.mPrivateKey).enqueue(callback);
    }

    public void getInstallments(String bin, BigDecimal amount, Long issuerId, String paymentMethodId,
        Callback<List<Installment>> callback) {
        PaymentService service = getDefaultRetrofit(mContext).create(PaymentService.class);
        service.getInstallments(Settings.servicesVersion, this.mPublicKey, mPrivateKey, bin, amount, issuerId,
            paymentMethodId,
            LocaleUtil.getLanguage(mContext), mProcessingMode).enqueue(callback);
    }

    public void getIssuers(String paymentMethodId, String bin, final Callback<List<Issuer>> callback) {
        PaymentService service = getDefaultRetrofit(mContext).create(PaymentService.class);
        service
            .getIssuers(Settings.servicesVersion, this.mPublicKey, mPrivateKey, paymentMethodId, bin, mProcessingMode)
            .enqueue(callback);
    }

    public void getPaymentMethods(final Callback<List<PaymentMethod>> callback) {
        PaymentService service = getDefaultRetrofit(mContext).create(PaymentService.class);
        service.getPaymentMethods(this.mPublicKey, mPrivateKey).enqueue(callback);
    }

    public void getDirectDiscount(String amount, String payerEmail, final Callback<Discount> callback) {
        DiscountService service = getDefaultRetrofit(mContext).create(DiscountService.class);
        service.getDiscount(this.mPublicKey, amount, payerEmail).enqueue(callback);
    }

    public void getCodeDiscount(String amount, String payerEmail, String couponCode,
        final Callback<Discount> callback) {
        DiscountService service = getDefaultRetrofit(mContext).create(DiscountService.class);
        service.getDiscount(this.mPublicKey, amount, payerEmail, couponCode).enqueue(callback);
    }

    public void getCustomer(String url, String uri, Callback<Customer> callback) {
        CustomService customService = getCustomService(url);
        customService.getCustomer(uri, null).enqueue(callback);
    }

    public void getCustomer(String url, String uri, @NonNull Map<String, String> additionalInfo,
        Callback<Customer> callback) {
        CustomService customService = getCustomService(url);
        customService.getCustomer(uri, additionalInfo).enqueue(callback);
    }

    public void createPayment(String baseUrl, String uri, Map<String, Object> paymentData,
        @NonNull Map<String, String> query, Callback<Payment> callback) {
        CustomService customService =
            getCustomService(baseUrl, DEFAULT_PAYMENT_CONNECT_TIMEOUT, DEFAULT_PAYMENT_READ_TIMEOUT,
                DEFAULT_PAYMENT_WRITE_TIMEOUT);
        customService.createPayment(Settings.servicesVersion, ripFirstSlash(uri), paymentData, query).enqueue(callback);
    }

    public void createPayment(String transactionId, String baseUrl, String uri,
        Map<String, Object> paymentData, @NonNull Map<String, String> query, Callback<Payment> callback) {
        CustomService customService =
            getCustomService(baseUrl, DEFAULT_PAYMENT_CONNECT_TIMEOUT, DEFAULT_PAYMENT_READ_TIMEOUT,
                DEFAULT_PAYMENT_WRITE_TIMEOUT);
        customService.createPayment(transactionId, ripFirstSlash(uri), paymentData, query).enqueue(callback);
    }

    public static Retrofit getDefaultRetrofit(final Context context) {
        return getDefaultRetrofit(context, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_WRITE_TIMEOUT);
    }

    private static Retrofit getDefaultRetrofit(final Context context, int connectTimeout, int readTimeout,
        int writeTimeout) {
        return getRetrofit(context, MP_API_BASE_URL, connectTimeout, readTimeout, writeTimeout);
    }

    private Retrofit getGatewayRetrofit() {
        return getGatewayRetrofit(DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_WRITE_TIMEOUT);
    }

    private Retrofit getGatewayRetrofit(final int connectTimeout, final int readTimeout, final int writeTimeout) {
        return getRetrofit(mContext, MP_API_BASE_URL, connectTimeout, readTimeout, writeTimeout);
    }

    private CustomService getCustomService(String url) {
        return getCustomService(url, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT, DEFAULT_WRITE_TIMEOUT);
    }

    private CustomService getCustomService(final String baseUrl,
        final int connectTimeout,
        final int readTimeout,
        final int writeTimeout) {

        final Retrofit retrofit = getRetrofit(mContext, baseUrl, connectTimeout, readTimeout, writeTimeout);
        return retrofit.create(CustomService.class);
    }

    private static String ripFirstSlash(final String uri) {
        return uri.startsWith("/") ? uri.substring(1) : uri;
    }

    private static Retrofit getRetrofit(final Context mContext,
        final String baseUrl,
        final int connectTimeout,
        final int readTimeout,
        final int writeTimeout) {

        return new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(JsonUtil.getInstance().getGson()))
            .client(HttpClientUtil.getClient(mContext, connectTimeout, readTimeout, writeTimeout))
            .addCallAdapterFactory(new ErrorHandlingCallAdapter.ErrorHandlingCallAdapterFactory())
            .build();
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
