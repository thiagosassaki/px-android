package com.mercadopago.android.px.cardvault;

import com.mercadopago.android.px.mocks.IdentificationTypes;
import com.mercadopago.android.px.model.IdentificationType;
import com.mercadopago.android.px.internal.util.MPCardMaskUtil;
import java.util.Arrays;
import java.util.Collection;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class MasksTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "RUT", "123123123-V", "123.123.123-V" },
            { "RUT", "123123123-V123", "123.123.123-V123" },
            { "DNI", "35654343", "35.654.343" },
            { "DNI", "5654343", "5.654.343" },
            { "DNI", "000000000", "000.000.000" },
            { "CPF", "33538765618", "335.387.656-18" },
            { "CPF", "00000000000", "000.000.000-00" }
        });
    }

    private String idType;

    private String rawNumber;

    private String maskedNumber;

    public MasksTest(String idType, String rawNumber, String maskedNumber) {
        this.idType = idType;
        this.rawNumber = rawNumber;
        this.maskedNumber = maskedNumber;
    }

    @Test
    public void whenTextIdRequiredThenFormatWithDecimalSymbols() {
        IdentificationType identificationType = IdentificationTypes.getById(idType);
        String maskResult = MPCardMaskUtil.buildIdentificationNumberWithMask(rawNumber, identificationType);
        Assert.assertEquals(maskedNumber, maskResult);
    }
}
