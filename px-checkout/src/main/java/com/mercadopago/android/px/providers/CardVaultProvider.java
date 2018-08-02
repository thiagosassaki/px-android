package com.mercadopago.android.px.providers;

import android.support.annotation.Nullable;
import com.mercadopago.android.px.model.Installment;
import com.mercadopago.android.px.model.SavedESCCardToken;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.mvp.ResourcesProvider;
import com.mercadopago.android.px.mvp.TaggedCallback;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by vaserber on 4/18/17.
 */

public interface CardVaultProvider extends ResourcesProvider {

    String getMissingPublicKeyErrorMessage();

    String getMissingSiteErrorMessage();

    String getMissingAmountErrorMessage();

    String getMissingPayerCostsErrorMessage();

    String getMissingInstallmentsForIssuerErrorMessage();

    String getMultipleInstallmentsForIssuerErrorMessage();

    void getInstallmentsAsync(final String bin, final Long issuerId, final String paymentMethodId,
        final BigDecimal amount,
        @Nullable final Integer differentialPricingId, final TaggedCallback<List<Installment>> taggedCallback);

    void createESCTokenAsync(final SavedESCCardToken escCardToken, final TaggedCallback<Token> taggedCallback);

    String findESCSaved(String cardId);

    void deleteESC(String cardId);
}
