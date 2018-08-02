package com.mercadopago.android.px.services;

import com.mercadopago.android.px.model.Installment;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.services.adapters.MPCall;
import java.math.BigDecimal;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by mromar on 10/20/17.
 */

public interface PaymentService {

    @GET("/v1/payment_methods")
    MPCall<List<PaymentMethod>> getPaymentMethods(@Query("public_key") String publicKey,
        @Query("access_token") String privateKey);

    @GET("/{version}/checkout/payment_methods/installments")
    MPCall<List<Installment>> getInstallments(@Path(value = "version", encoded = true) String version,
        @Query("public_key") String publicKey, @Query("access_token") String privateKey, @Query("bin") String bin,
        @Query("amount") BigDecimal amount, @Query("issuer.id") Long issuerId,
        @Query("payment_method_id") String paymentMethodId,
        @Query("locale") String locale,
        @Query("processing_mode") String processingMode,
        @Query("differential_pricing_id") Integer differentialPricingId);

    @GET("/{version}/checkout/payment_methods/card_issuers")
    MPCall<List<Issuer>> getIssuers(@Path(value = "version", encoded = true) String version,
        @Query("public_key") String publicKey, @Query("access_token") String privateKey,
        @Query("payment_method_id") String paymentMethodId, @Query("bin") String bin,
        @Query("processing_mode") String processingMode);
}