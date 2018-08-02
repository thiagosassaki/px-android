package com.mercadopago.android.px.providers;

import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.model.Installment;
import com.mercadopago.android.px.mvp.ResourcesProvider;
import com.mercadopago.android.px.mvp.TaggedCallback;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mromar on 4/28/17.
 */

public interface InstallmentsProvider extends ResourcesProvider {

    void getInstallments(final String bin,
        final BigDecimal amount,
        final Long issuerId,
        final String paymentMethodId,
        @Nullable final Integer differentialPricingId,
        final TaggedCallback<List<Installment>> taggedCallback);

    MercadoPagoError getNoInstallmentsFoundError();

    MercadoPagoError getMultipleInstallmentsFoundForAnIssuerError();

    MercadoPagoError getNoPayerCostFoundError();
}
