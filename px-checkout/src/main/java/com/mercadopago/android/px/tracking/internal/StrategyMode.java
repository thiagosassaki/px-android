package com.mercadopago.android.px.tracking.internal;

public final class StrategyMode {

    //Strategies
    public static final String NOOP_STRATEGY = "noop_strategy";
    public static final String BATCH_STRATEGY = "batch_strategy";
    public static final String REALTIME_STRATEGY = "realtime_strategy";
    public static final String FORCED_STRATEGY = "forced_strategy";

    private StrategyMode() {
    }
}
