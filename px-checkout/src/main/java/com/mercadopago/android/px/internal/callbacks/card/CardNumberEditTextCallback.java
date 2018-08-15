package com.mercadopago.android.px.internal.callbacks.card;

/**
 * Created by vaserber on 10/13/16.
 */

public interface CardNumberEditTextCallback {
    void checkOpenKeyboard();

    void appendSpace(CharSequence s);

    void deleteChar(CharSequence s);

    void saveCardNumber(CharSequence s);

    void changeErrorView();

    void toggleLineColorOnError(boolean toggle);
}
