package com.mercadopago.android.px.internal.features.providers;

import com.mercadopago.android.px.internal.base.ResourcesProvider;
import com.mercadopago.android.px.internal.callbacks.TaggedCallback;
import com.mercadopago.android.px.model.IdentificationType;
import java.util.List;

/**
 * Created by mromar on 22/09/17.
 */

public interface PayerInformationProvider extends ResourcesProvider {

    void getIdentificationTypesAsync(final TaggedCallback<List<IdentificationType>> taggedCallback);

    String getInvalidIdentificationNumberErrorMessage();

    String getInvalidIdentificationNameErrorMessage();

    String getInvalidIdentificationLastNameErrorMessage();

    String getInvalidIdentificationBusinessNameErrorMessage();

    String getMissingPublicKeyErrorMessage();

    String getMissingIdentificationTypesErrorMessage();
}
