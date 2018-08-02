package com.mercadopago.android.px.views;

import com.mercadopago.android.px.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.mvp.MvpView;
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
