package com.mercadopago.android.px.internal.util.textformatter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.view.View;
import android.widget.TextView;

public abstract class Style extends ChainFormatter {

    @NonNull final AmountFormatter amountFormatter;

    Style(@NonNull final AmountFormatter amountFormatter) {
        this.amountFormatter = amountFormatter;
    }

    public TextFormatter into(@NonNull final TextView textView) {
        textView.setVisibility(View.VISIBLE);
        return new TextFormatter(textView, this);
    }

    public abstract Spannable apply(final int holder, final Context context);
}
