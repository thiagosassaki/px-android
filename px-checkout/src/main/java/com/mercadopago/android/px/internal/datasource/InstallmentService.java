package com.mercadopago.android.px.internal.datasource;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.repository.InstallmentRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.model.Installment;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import java.math.BigDecimal;
import java.util.List;

public class InstallmentService implements InstallmentRepository {

    @NonNull private final UserSelectionRepository userSelectionService;

    public InstallmentService(@NonNull final UserSelectionRepository userSelectionService) {
        this.userSelectionService = userSelectionService;
    }

    @NonNull
    @Override
    public BigDecimal getInstallmentAmount() {
        return userSelectionService.hasPayerCostSelected() ? userSelectionService.getPayerCost().getInstallmentAmount()
            : BigDecimal.ZERO;
    }

    @NonNull
    @Override
    public BigDecimal getInstallmentTotalAmount() {
        return userSelectionService.hasPayerCostSelected() ? userSelectionService.getPayerCost().getTotalAmount()
            : BigDecimal.ZERO;
    }

    @NonNull
    @Override
    public MPCall<List<Installment>> getInstallments() {
        return null;
    }
}
