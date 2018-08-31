package com.mercadopago.android.px.internal.features.paymentresult.components;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.features.paymentresult.props.InstructionsSecondaryInfoProps;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Component;

/**
 * Created by vaserber on 11/14/17.
 */

public class InstructionsSecondaryInfo extends Component<InstructionsSecondaryInfoProps, Void> {

    public InstructionsSecondaryInfo(@NonNull final InstructionsSecondaryInfoProps props,
        @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }
}
