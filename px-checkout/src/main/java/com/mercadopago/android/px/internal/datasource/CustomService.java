package com.mercadopago.android.px.internal.datasource;

import com.mercadopago.android.px.model.Customer;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import java.util.Map;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
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

    @GET("/{uri}")
    MPCall<Customer> getCustomer(@Path(value = "uri", encoded = true) String uri,
        @QueryMap(encoded = true) Map<String, String> additionalInfo);
}
