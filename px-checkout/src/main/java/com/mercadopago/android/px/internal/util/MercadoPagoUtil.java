package com.mercadopago.android.px.internal.util;

import android.content.Context;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.model.Bin;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.exceptions.BinException;
import java.util.ArrayList;
import java.util.List;

public class MercadoPagoUtil {

    private static final String SDK_PREFIX = "px_";

    public static int getPaymentMethodIcon(Context context, String paymentMethodId) {

        return getPaymentMethodPicture(context, SDK_PREFIX, paymentMethodId);
    }

    private static int getPaymentMethodPicture(Context context, String type, String paymentMethodId) {

        int resource;
        paymentMethodId = type + paymentMethodId;
        try {
            resource = context.getResources().getIdentifier(paymentMethodId, "drawable", context.getPackageName());
        } catch (Exception e) {
            try {
                resource =
                    context.getResources().getIdentifier(SDK_PREFIX + "bank", "drawable", context.getPackageName());
            } catch (Exception ex) {
                resource = 0;
            }
        }
        return resource;
    }

    public static int getPaymentMethodSearchItemIcon(Context context, String itemId) {
        int resource;
        if (itemId != null && context != null) {
            try {
                resource =
                    context.getResources().getIdentifier(SDK_PREFIX + itemId, "drawable", context.getPackageName());
            } catch (Exception e) {
                resource = 0;
            }
        } else {
            resource = 0;
        }
        return resource;
    }

    public static boolean isCard(String paymentTypeId) {

        return (paymentTypeId != null) && (paymentTypeId.equals("credit_card") || paymentTypeId.equals("debit_card") ||
            paymentTypeId.equals("prepaid_card"));
    }

    public static String getAccreditationTimeMessage(Context context, int milliseconds) {

        String accreditationMessage;

        if (milliseconds == 0) {
            accreditationMessage = context.getString(R.string.px_instant_accreditation_time);
        } else {
            StringBuilder accreditationTimeMessageBuilder = new StringBuilder();
            if (milliseconds > 1440 && milliseconds < 2880) {

                accreditationTimeMessageBuilder.append(context.getString(R.string.px_accreditation_time));
                accreditationTimeMessageBuilder.append(" 1 ");
                accreditationTimeMessageBuilder.append(context.getString(R.string.px_working_day));
            } else if (milliseconds < 1440) {

                accreditationTimeMessageBuilder.append(context.getString(R.string.px_accreditation_time));
                accreditationTimeMessageBuilder.append(" ");
                accreditationTimeMessageBuilder.append(milliseconds / 60);
                accreditationTimeMessageBuilder.append(" ");
                accreditationTimeMessageBuilder.append(context.getString(R.string.px_hour));
            } else {

                accreditationTimeMessageBuilder.append(context.getString(R.string.px_accreditation_time));
                accreditationTimeMessageBuilder.append(" ");
                accreditationTimeMessageBuilder.append(milliseconds / (60 * 24));
                accreditationTimeMessageBuilder.append(" ");
                accreditationTimeMessageBuilder.append(context.getString(R.string.px_working_days));
            }
            accreditationMessage = accreditationTimeMessageBuilder.toString();
        }
        return accreditationMessage;
    }

    public static List<PaymentMethod> getValidPaymentMethodsForBin(String bin, List<PaymentMethod> paymentMethods) {
        if (bin.length() == Bin.BIN_LENGTH) {
            List<PaymentMethod> validPaymentMethods = new ArrayList<>();
            for (PaymentMethod pm : paymentMethods) {
                if (pm.isValidForBin(bin)) {
                    validPaymentMethods.add(pm);
                }
            }
            return validPaymentMethods;
        }

        throw new BinException(bin.length());
    }
}
