package com.mercadopago.android.px.internal.util.textformatter;

import android.text.Spannable;

abstract class ChainFormatter {

    protected abstract Spannable apply(CharSequence charSequence);
}
