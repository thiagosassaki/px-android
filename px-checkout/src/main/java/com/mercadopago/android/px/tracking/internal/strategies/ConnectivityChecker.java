package com.mercadopago.android.px.tracking.internal.strategies;

public interface ConnectivityChecker {
    boolean hasConnection();

    boolean hasWifiConnection();
}
