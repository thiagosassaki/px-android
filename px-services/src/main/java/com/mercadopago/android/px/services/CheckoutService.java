package com.mercadopago.android.px.services;

import com.mercadopago.android.px.model.Instructions;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.requests.PayerIntent;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.services.adapters.MPCall;
import java.math.BigDecimal;
import java.util.Map;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CheckoutService {

    @POST("/{version}/px_mobile_api/payment_methods")
    MPCall<PaymentMethodSearch> getPaymentMethodSearch(@Path(value = "version", encoded = true) String version,
        @Header("Accept-Language") String locale, @Query("public_key") String publicKey,
        @Query("amount") BigDecimal amount,
        @Query("excluded_payment_types") String excludedPaymentTypes,
        @Query("excluded_payment_methods") String excludedPaymentMethods,
        @Body PayerIntent payerIntent,
        @Query("site_id") String siteId,
        @Query("api_version") String apiVersion,
        @Query("processing_mode") String processingMode,
        @Query("cards_esc") String cardsWithEsc,
        @Query("support_plugins") String supportedPlugins,
        @Query("differential_pricing_id") Integer differentialPricingId);

    @POST("/{version}/checkout/payments")
    MPCall<Payment> createPayment(@Path(value = "version", encoded = true) String version,
        @Header("X-Idempotency-Key") String transactionId, @Body Map<String, Object> body);

    @GET("/{version}/checkout/payments/{payment_id}/results")
    MPCall<Instructions> getPaymentResult(@Path(value = "version", encoded = true) String version,
        @Header("Accept-Language") String locale, @Path(value = "payment_id", encoded = true) Long paymentId,
        @Query("public_key") String mKey, @Query("access_token") String privateKey,
        @Query("payment_type") String paymentTypeId, @Query("api_version") String apiVersion);

    @GET("/{version}/checkout/preferences/{preference_id}")
    MPCall<CheckoutPreference> getPreference(@Path(value = "version", encoded = true) String version,
        @Path(value = "preference_id", encoded = true) String checkoutPreferenceId,
        @Query("public_key") String publicKey);
}

