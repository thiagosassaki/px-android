package com.mercadopago.android.px.internal.features.paymentresult.components;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Component;
import com.mercadopago.android.px.model.InstructionReference;

/**
 * Created by vaserber on 11/13/17.
 */

public class InstructionReferenceComponent extends Component<InstructionReferenceComponent.Props, Void> {

    public InstructionReferenceComponent(@NonNull final Props props, @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public static class Props {

        public final InstructionReference reference;

        public Props(final Builder builder) {
            reference = builder.reference;
        }

        public Builder toBuilder() {
            return new Props.Builder()
                .setReference(reference);
        }

        public static class Builder {
            public InstructionReference reference;

            public Builder setReference(@NonNull InstructionReference reference) {
                this.reference = reference;
                return this;
            }

            public Props build() {
                return new Props(this);
            }
        }
    }
}
