package com.mercadopago.android.px.model.exceptions;

import com.mercadopago.android.px.model.Bin;

public class BinException extends RuntimeException {

    public BinException(int binLength) {
        super("Invalid bin: " + Bin.BIN_LENGTH + " digits needed, " + binLength + " found");
    }
}
