package com.mercadopago.android.px.internal.features.paymentresult;

import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.mocks.Instructions;
import com.mercadopago.android.px.model.Instruction;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionReferenceComponent;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsReferences;
import com.mercadopago.android.px.internal.features.paymentresult.props.InstructionsReferencesProps;
import java.util.List;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by vaserber on 23/11/2017.
 */

public class ReferencesTest {

    private ActionDispatcher dispatcher;

    @Before
    public void setup() {
        dispatcher = mock(ActionDispatcher.class);
    }

    @Test
    public void testCreateReferenceComponentsList() {
        final Instruction instruction = Instructions.getBanamexBankTransferInstruction();
        final InstructionsReferencesProps props = new InstructionsReferencesProps.Builder()
            .setReferences(instruction.getReferences())
            .build();
        final InstructionsReferences component = new InstructionsReferences(props, dispatcher);

        Assert.assertNotNull(instruction.getReferences());
        Assert.assertFalse(instruction.getReferences().isEmpty());

        final List<InstructionReferenceComponent> referenceComponentList = component.getReferenceComponents();

        Assert.assertNotNull(referenceComponentList);
        Assert.assertFalse(referenceComponentList.isEmpty());
        Assert.assertEquals(instruction.getReferences().size(), referenceComponentList.size());
    }

    @Test
    public void testReferenceComponentsListPropsAreValid() {
        final Instruction instruction = Instructions.getBanamexBankTransferInstruction();
        final InstructionsReferencesProps props = new InstructionsReferencesProps.Builder()
            .setReferences(instruction.getReferences())
            .build();
        final InstructionsReferences component = new InstructionsReferences(props, dispatcher);

        final List<InstructionReferenceComponent> referenceComponentList = component.getReferenceComponents();

        for (InstructionReferenceComponent instructionReferenceComponent : referenceComponentList) {
            Assert.assertNotNull(instructionReferenceComponent.props.reference);
        }
    }
}