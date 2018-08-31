package com.mercadopago.android.px.internal.view;

import android.support.annotation.NonNull;

public class Receipt extends Component<Receipt.ReceiptProps, Void> {

    static {
        RendererFactory.register(Receipt.class, ReceiptRenderer.class);
    }

    public static class ReceiptProps {

        public final String receiptId;

        public ReceiptProps(@NonNull final String receiptId) {
            this.receiptId = receiptId;
        }
    }

    public Receipt(@NonNull ReceiptProps props) {
        super(props);
    }
}
