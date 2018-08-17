package com.mercadopago.android.px.internal.repository;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.Installment;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import java.math.BigDecimal;
import java.util.List;

public interface InstallmentRepository {

    @NonNull
    BigDecimal getInstallmentAmount();

    @NonNull
    BigDecimal getInstallmentTotalAmount();

    @NonNull
    MPCall<List<Installment>> getInstallments();
}
