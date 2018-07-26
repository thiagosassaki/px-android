package com.mercadopago.android.px.views;

import com.mercadopago.android.px.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.mvp.MvpView;
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
