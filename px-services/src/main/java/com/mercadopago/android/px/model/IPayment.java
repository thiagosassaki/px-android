package com.mercadopago.android.px.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.Serializable;

public interface IPayment extends Serializable {

    @Nullable
    Long getId();

    @Nullable
    String getStatementDescription();

    @NonNull
    String getPaymentStatus();

    @NonNull
    String getPaymentStatusDetail();
}
