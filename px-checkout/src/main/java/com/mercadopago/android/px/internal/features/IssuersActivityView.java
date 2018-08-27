package com.mercadopago.android.px.internal.features;

import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.util.List;

/**
 * Created by vaserber on 10/11/16.
 */

public interface IssuersActivityView extends MvpView {

    void showIssuers(List<Issuer> issuersList, OnSelectedCallback<Integer> onSelectedCallback);

    void showHeader();

    void showLoadingView();

    void stopLoadingView();

    void showError(MercadoPagoError error, String requestOrigin);

    void finishWithResult();
}
