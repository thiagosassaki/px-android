package com.mercadopago.android.px.internal.services;

import com.mercadopago.android.px.model.Customer;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mromar on 10/20/17.
 */

public interface CustomerService {

    @GET("/customers")
    MPCall<Customer> getCustomer(@Query("preference_id") String preferenceId);
}