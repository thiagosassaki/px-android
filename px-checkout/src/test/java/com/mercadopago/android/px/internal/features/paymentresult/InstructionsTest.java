package com.mercadopago.android.px.internal.features.paymentresult;

import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.mocks.Instructions;
import com.mercadopago.android.px.model.Instruction;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsContent;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsSecondaryInfo;
import com.mercadopago.android.px.internal.features.paymentresult.components.InstructionsSubtitle;
import com.mercadopago.android.px.internal.features.paymentresult.props.InstructionsProps;
import com.mercadopago.android.px.internal.constants.ProcessingModes;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by vaserber on 22/11/2017.
 */

public class InstructionsTest {

    private ActionDispatcher dispatcher;

    @Before
    public void setup() {
        dispatcher = mock(ActionDispatcher.class);
    }

    @Test
    public void testInstructionHasSubtitle() {
        final Instruction instruction = Instructions.getRapipagoInstruction();
        final com.mercadopago.android.px.internal.features.paymentresult.components.Instructions component =
            getInstructionsComponent(instruction);

        Assert.assertNotNull(instruction.getSubtitle());
        Assert.assertFalse(instruction.getSubtitle().isEmpty());
        Assert.assertTrue(component.hasSubtitle());
        Assert.assertNotNull(component.getSubtitleComponent());
    }

    @Test
    public void testSubtitlePropsAreValid() {
        final Instruction instruction = Instructions.getRapipagoInstruction();
        final com.mercadopago.android.px.internal.features.paymentresult.components.Instructions component =
            getInstructionsComponent(instruction);

        final InstructionsSubtitle subtitle = component.getSubtitleComponent();
        Assert.assertNotNull(subtitle.props.subtitle);
        Assert.assertEquals(subtitle.props.subtitle, instruction.getSubtitle());
    }

    @Test
    public void testContentComponentIsValid() {
        final Instruction instruction = Instructions.getRapipagoInstruction();
        final com.mercadopago.android.px.internal.features.paymentresult.components.Instructions component =
            getInstructionsComponent(instruction);

        final InstructionsContent content = component.getContentComponent();
        Assert.assertNotNull(content);
        Assert.assertEquals(content.props.instruction, instruction);
    }

    @Test
    public void testInstructionHasSecondaryInfo() {
        final Instruction instruction = Instructions.getRapipagoInstruction();
        final com.mercadopago.android.px.internal.features.paymentresult.components.Instructions component =
            getInstructionsComponent(instruction);

        Assert.assertNotNull(instruction.getSecondaryInfo());
        Assert.assertFalse(instruction.getSecondaryInfo().isEmpty());
        Assert.assertTrue(component.hasSecondaryInfo());
        Assert.assertNotNull(component.getSecondaryInfoComponent());
    }

    @Test
    public void testSecondaryInfoPropsAreValid() {
        final Instruction instruction = Instructions.getRapipagoInstruction();
        final com.mercadopago.android.px.internal.features.paymentresult.components.Instructions component =
            getInstructionsComponent(instruction);

        final InstructionsSecondaryInfo secondaryInfo = component.getSecondaryInfoComponent();
        Assert.assertNotNull(secondaryInfo.props.secondaryInfo);
        Assert.assertFalse(secondaryInfo.props.secondaryInfo.isEmpty());
        Assert.assertEquals(secondaryInfo.props.secondaryInfo, instruction.getSecondaryInfo());
    }

    @Test
    public void testOnAggregatorThenShowEmailInSecondaryInfo() {
        final Instruction instruction = Instructions.getRapipagoInstruction();
        final com.mercadopago.android.px.internal.features.paymentresult.components.Instructions component =
            getInstructionsComponent(instruction);

        Assert.assertTrue(component.shouldShowEmailInSecondaryInfo());
    }

    @Test
    public void testOnGatewayThenDontShowEmailInSecondaryInfo() {
        final Instruction instruction = Instructions.getRapipagoInstruction();
        final InstructionsProps props = new InstructionsProps.Builder()
            .setProcessingMode(ProcessingModes.GATEWAY)
            .setInstruction(instruction)
            .build();

        final com.mercadopago.android.px.internal.features.paymentresult.components.Instructions component =
            new com.mercadopago.android.px.internal.features.paymentresult.components.Instructions(props, dispatcher);

        Assert.assertFalse(component.shouldShowEmailInSecondaryInfo());
    }

    private com.mercadopago.android.px.internal.features.paymentresult.components.Instructions getInstructionsComponent(
        Instruction instruction) {
        final InstructionsProps props = new InstructionsProps.Builder()
            .setProcessingMode(ProcessingModes.AGGREGATOR)
            .setInstruction(instruction)
            .build();
        final com.mercadopago.android.px.internal.features.paymentresult.components.Instructions component =
            new com.mercadopago.android.px.internal.features.paymentresult.components.Instructions(props, dispatcher);
        return component;
    }
}
