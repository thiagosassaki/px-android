package com.mercadopago.android.px.internal.features;

import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.util.List;

/**
 * Created by mromar on 4/11/17.
 */

public interface CustomerCardsView extends MvpView {

    void showCards(List<Card> cards, String actionMessage, OnSelectedCallback<Card> onSelectedCallback);

    void showConfirmPrompt(Card card);

    void showProgress();

    void hideProgress();

    void showError(MercadoPagoError error, String requestOrigin);

    void finishWithCardResult(Card card);

    void finishWithOkResult();
}
