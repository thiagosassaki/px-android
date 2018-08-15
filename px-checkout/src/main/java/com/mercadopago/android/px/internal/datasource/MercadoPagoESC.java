package com.mercadopago.android.px.internal.datasource;

import java.util.Set;

public interface MercadoPagoESC {

    String getESC(String cardId);

    boolean saveESC(String cardId, String value);

    void deleteESC(String cardId);

    void deleteAllESC();

    Set<String> getESCCardIds();

    boolean isESCEnabled();
}
