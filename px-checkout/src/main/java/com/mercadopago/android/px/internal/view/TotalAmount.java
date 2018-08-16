package com.mercadopago.android.px.internal.view;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.internal.util.CurrenciesUtil;
import java.math.BigDecimal;
import java.util.Locale;

public class TotalAmount extends Component<TotalAmount.TotalAmountProps, Void> {

    static {
        RendererFactory.register(TotalAmount.class, TotalAmountRenderer.class);
    }

    public static class TotalAmountProps {

        public final PayerCost payerCost;
        public final Discount discount;
        public final String currencyId;
        public final BigDecimal amount;

        public TotalAmountProps(final String currencyId,
            final BigDecimal amount,
            final PayerCost payerCost,
            final Discount discount) {
            this.payerCost = payerCost;
            this.discount = discount;
            this.currencyId = currencyId;
            this.amount = amount;
        }
    }

    public TotalAmount(@NonNull final TotalAmountProps props) {
        super(props);
    }

    public String getAmountTitle() {
        String amountTitle;

        if (hasPayerCostWithMultipleInstallments()) {
            final String installmentsAmount = CurrenciesUtil
                .getLocalizedAmountWithoutZeroDecimals(props.currencyId, props.payerCost.getInstallmentAmount());
            amountTitle = String.format(Locale.getDefault(),
                "%dx %s",
                props.payerCost.getInstallments(),
                installmentsAmount);
        } else {
            amountTitle = CurrenciesUtil.getLocalizedAmountWithoutZeroDecimals(props.currencyId, props.amount);
        }

        return amountTitle;
    }

    public String getAmountDetail() {
        String amountDetail = "";

        if (hasPayerCostWithMultipleInstallments()) {
            amountDetail = String.format(Locale.getDefault(), "(%s)", CurrenciesUtil
                .getLocalizedAmountWithoutZeroDecimals(props.currencyId, props.payerCost.getTotalAmount()));
        }

        return amountDetail;
    }

    public boolean hasPayerCostWithMultipleInstallments() {
        return props.payerCost != null && props.payerCost.hasMultipleInstallments();
    }
}
