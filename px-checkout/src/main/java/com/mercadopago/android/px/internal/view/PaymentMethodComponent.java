package com.mercadopago.android.px.internal.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.model.PaymentMethod;

public class PaymentMethodComponent extends Component<PaymentMethodComponent.PaymentMethodProps, Void> {

    static {
        RendererFactory.register(PaymentMethodComponent.class, PaymentMethodRenderer.class);
    }

    public static class PaymentMethodProps {

        /* default */ final PaymentMethod paymentMethod;
        /* default */ final TotalAmount.TotalAmountProps totalAmountProps;
        @Nullable
        /* default */ final String lastFourDigits;
        @Nullable
        /* default */ final String disclaimer;

        public PaymentMethodProps(final PaymentMethod paymentMethod,
            @Nullable final String lastFourDigits,
            @Nullable final String disclaimer,
            final TotalAmount.TotalAmountProps totalAmountProps) {
            this.paymentMethod = paymentMethod;
            this.lastFourDigits = lastFourDigits;
            this.disclaimer = disclaimer;
            this.totalAmountProps = totalAmountProps;
        }
    }

    public PaymentMethodComponent(@NonNull final PaymentMethodProps props) {
        super(props);
    }
}
