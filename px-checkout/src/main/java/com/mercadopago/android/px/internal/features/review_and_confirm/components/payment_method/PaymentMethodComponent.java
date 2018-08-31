package com.mercadopago.android.px.internal.features.review_and_confirm.components.payment_method;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.PaymentModel;
import com.mercadopago.android.px.internal.view.Button;
import com.mercadopago.android.px.internal.view.ButtonLink;
import com.mercadopago.android.px.internal.view.CompactComponent;
import com.mercadopago.android.px.model.Action;
import com.mercadopago.android.px.model.PaymentTypes;

public class PaymentMethodComponent extends CompactComponent<PaymentModel, PaymentMethodComponent.Actions> {

    public interface Actions {
        void onPaymentMethodChangeClicked();
    }

    public PaymentMethodComponent(PaymentModel props, Actions actions) {
        super(props, actions);
    }

    @VisibleForTesting()
    CompactComponent resolveComponent() {
        if (PaymentTypes.isCardPaymentType(props.getPaymentType())) {
            return new MethodCard(MethodCard.Props.createFrom(props));
        } else if (PaymentTypes.isPlugin(props.getPaymentType())) {
            return new MethodPlugin(MethodPlugin.Props.createFrom(props));
        } else {
            return new MethodOff(MethodOff.Props.createFrom(props));
        }
    }

    @Override
    public View render(@NonNull final ViewGroup parent) {

        ViewGroup paymentMethodView = (ViewGroup) resolveComponent().render(parent);

        if (shouldShowPaymentMethodButton()) {
            String changeLabel = parent.getContext().getString(R.string.px_change_payment);
            ButtonLink buttonLink = new ButtonLink(new Button.Props(changeLabel, null), new Button.Actions() {
                @Override
                public void onClick(final Action action) {
                    if (getActions() != null) {
                        getActions().onPaymentMethodChangeClicked();
                    }
                }

                @Override
                public void onClick(final int yButtonPosition, final int buttonHeight) {
                    //Do nothing
                }
            });

            compose(paymentMethodView, buttonLink.render(paymentMethodView));
        }

        return paymentMethodView;
    }

    @VisibleForTesting
    boolean shouldShowPaymentMethodButton() {
        return props.hasMoreThanOnePaymentMethod() || PaymentTypes.isCardPaymentType(props.getPaymentType());
    }
}
