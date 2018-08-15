package com.mercadopago.android.px.internal.features.paymentresult.components;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.features.paymentresult.props.InstructionsInfoProps;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Component;

/**
 * Created by vaserber on 11/13/17.
 */

public class InstructionsInfo extends Component<InstructionsInfoProps, Void> {

    public InstructionsInfo(@NonNull final InstructionsInfoProps props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }
}
