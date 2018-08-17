package com.mercadopago.android.px.internal.features.paymentresult.components;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.features.paymentresult.props.InstructionsReferencesProps;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.Component;
import com.mercadopago.android.px.model.InstructionReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaserber on 11/13/17.
 */

public class InstructionsReferences extends Component<InstructionsReferencesProps, Void> {

    public InstructionsReferences(@NonNull final InstructionsReferencesProps props,
        @NonNull final ActionDispatcher dispatcher) {
        super(props, dispatcher);
    }

    public List<InstructionReferenceComponent> getReferenceComponents() {
        List<InstructionReferenceComponent> componentList = new ArrayList<>();

        for (InstructionReference reference : props.references) {
            final InstructionReferenceComponent.Props referenceProps = new InstructionReferenceComponent.Props.Builder()
                .setReference(reference)
                .build();

            final InstructionReferenceComponent component =
                new InstructionReferenceComponent(referenceProps, getDispatcher());

            componentList.add(component);
        }

        return componentList;
    }
}
