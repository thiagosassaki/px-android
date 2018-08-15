package com.mercadopago.android.px.internal.callbacks;

/**
 * Created by mromar on 11/10/16.
 */

public interface TimerObserver {

    void onTimeChanged(String timeToShow);

    void onFinish();
}
