package com.mercadopago.android.px.internal.services;

import com.mercadopago.android.px.model.Customer;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import java.util.Map;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface CustomService {

    @POST("/{uri}")
    MPCall<CheckoutPreference> createPreference(@Path(value = "uri", encoded = true) String uri,
        @Body Map<String, Object> additionalInfo);

    @POST("/{uri}")
    MPCall<Payment> createPayment(@Header("X-Idempotency-Key") String transactionId,
        @Path(value = "uri", encoded = true) String uri,
        @Body Map<String, Object> additionalInfo,
        @QueryMap Map<String, String> query);

    @POST("/{uri}")
    MPCall<Payment> createPayment(@Path(value = "uri", encoded = true) String uri,
        @Body Map<String, Object> additionalInfo,
        @QueryMap Map<String, String> query);

    @GET("/{uri}")
    MPCall<Customer> getCustomer(@Path(value = "uri", encoded = true) String uri,
        @QueryMap(encoded = true) Map<String, String> additionalInfo);

    @GET("/{uri}")
    MPCall<Discount> getDirectDiscount(@Path(value = "uri", encoded = true) String uri,
        @Query("transaction_amount") String transactionAmount,
        @Query("email") String payerEmail,
        @QueryMap(encoded = true) Map<String, String> discountAdditionalInfo);

    @GET("/{uri}")
    MPCall<Discount> getCodeDiscount(@Path(value = "uri", encoded = true) String uri,
        @Query("transaction_amount") String transactionAmount,
        @Query("email") String payerEmail,
        @Query("coupon_code") String couponCode,
        @QueryMap(encoded = true) Map<String, String> discountAdditionalInfo);
}
