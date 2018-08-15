package com.mercadopago.android.px.internal.features.paymentresult.components;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.features.paymentresult.props.InstructionsSubtitleProps;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Component;

/**
 * Created by vaserber on 11/13/17.
 */

public class InstructionsSubtitle extends Component<InstructionsSubtitleProps, Void> {

    public InstructionsSubtitle(@NonNull final InstructionsSubtitleProps props,
        @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }
}
