package com.mercadopago.android.px.internal.features.business_result.components;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Component;
import com.mercadopago.android.px.internal.view.PaymentMethodComponent;
import com.mercadopago.android.px.internal.view.RendererFactory;
import com.mercadopago.android.px.model.BusinessPayment;

public class BusinessPaymentContainer extends Component<BusinessPaymentContainer.Props, Void> {

    static {
        RendererFactory.register(BusinessPaymentContainer.class, BusinessPaymentRenderer.class);
    }

    public static class Props {

        /* default */ final BusinessPayment payment;
        /* default */ final PaymentMethodComponent.PaymentMethodProps paymentMethod;

        public Props(@NonNull final BusinessPayment payment,
            @NonNull final PaymentMethodComponent.PaymentMethodProps paymentMethod) {
            this.payment = payment;
            this.paymentMethod = paymentMethod;
        }
    }

    public BusinessPaymentContainer(@NonNull final BusinessPaymentContainer.Props props,
        @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }
}
