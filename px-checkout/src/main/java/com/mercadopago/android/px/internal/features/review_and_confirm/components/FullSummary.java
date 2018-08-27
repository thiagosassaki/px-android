package com.mercadopago.android.px.internal.features.review_and_confirm.components;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import com.mercadopago.android.px.configuration.ReviewAndConfirmConfiguration;
import com.mercadopago.android.px.internal.features.review_and_confirm.SummaryProvider;
import com.mercadopago.android.px.internal.features.review_and_confirm.props.AmountDescriptionProps;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.view.Component;
import com.mercadopago.android.px.internal.view.RendererFactory;
import com.mercadopago.android.px.internal.viewmodel.Summary;
import com.mercadopago.android.px.internal.viewmodel.SummaryDetail;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.mercadopago.android.px.internal.util.TextUtil.isNotEmpty;

public class FullSummary extends Component<SummaryComponent.SummaryProps, Void> {

    private final SummaryProvider provider;

    static {
        RendererFactory.register(FullSummary.class, FullSummaryRenderer.class);
    }

    FullSummary(@NonNull final SummaryComponent.SummaryProps props,
        @NonNull final SummaryProvider provider) {
        super(props);
        this.provider = provider;
    }

    public List<AmountDescription> getAmountDescriptionComponents() {
        final List<AmountDescription> amountDescriptionList = new ArrayList<>();

        for (final SummaryDetail summaryDetail : getSummary().getSummaryDetails()) {
            final AmountDescriptionProps amountDescriptionProps = new AmountDescriptionProps(
                summaryDetail.getTotalAmount(),
                summaryDetail.getTitle(),
                props.summaryModel.currencyId,
                summaryDetail.getTextColor(),
                summaryDetail.getSummaryItemType());

            amountDescriptionList.add(new AmountDescription(amountDescriptionProps));
        }

        return amountDescriptionList;
    }

    @VisibleForTesting
    Summary getSummary() {
        final ReviewAndConfirmConfiguration reviewAndConfirmConfiguration = props.reviewAndConfirmConfiguration;
        final Summary.Builder summaryBuilder = new Summary.Builder();

        if (isPrefAmountDifferent(props.summaryModel.getCharges()) &&
            reviewAndConfirmConfiguration.hasProductAmount()) {
            summaryBuilder.addSummaryProductDetail(reviewAndConfirmConfiguration.getProductAmount(), getItemTitle(),
                provider.getDefaultTextColor())

                .addSummaryShippingDetail(reviewAndConfirmConfiguration.getShippingAmount(),
                    provider.getSummaryShippingTitle(), provider.getDefaultTextColor())

                .addSummaryArrearsDetail(reviewAndConfirmConfiguration.getArrearsAmount(),
                    provider.getSummaryArrearTitle(),
                    provider.getDefaultTextColor())

                .addSummaryTaxesDetail(reviewAndConfirmConfiguration.getTaxesAmount(), provider.getSummaryTaxesTitle(),
                    provider.getDefaultTextColor())

                .addSummaryDiscountDetail(getDiscountAmount(), provider.getSummaryDiscountsTitle(),
                    provider.getDiscountTextColor())

                .setDisclaimerText(reviewAndConfirmConfiguration.getDisclaimerText())
                .setDisclaimerColor(provider.getDisclaimerTextColor());
        } else {
            summaryBuilder.addSummaryProductDetail(props.summaryModel.getItemsAmount(), getItemTitle(),
                provider.getDefaultTextColor());

            if (!TextUtil.isEmpty(reviewAndConfirmConfiguration.getDisclaimerText())) {
                summaryBuilder.setDisclaimerText(reviewAndConfirmConfiguration.getDisclaimerText())
                    .setDisclaimerColor(provider.getDisclaimerTextColor());
            }

            if (isValidAmount(props.summaryModel.getCouponAmount())) {
                summaryBuilder.addSummaryDiscountDetail(props.summaryModel.getCouponAmount(),
                    provider.getSummaryDiscountsTitle(),
                    provider.getDiscountTextColor());
            }
        }

        if (props.summaryModel.hasCharges()) {
            summaryBuilder.addSummaryChargeDetail(props.summaryModel.getCharges(), provider.getSummaryChargesTitle(),
                provider.getDefaultTextColor());
        }

        return summaryBuilder.build();
    }

    private String getItemTitle() {
        String title = props.summaryModel.title;

        if (isNotEmpty(props.reviewAndConfirmConfiguration.getProductTitle())) {
            title = props.reviewAndConfirmConfiguration.getProductTitle();
        }

        return title;
    }

    private BigDecimal getDiscountAmount() {
        final ReviewAndConfirmConfiguration reviewScreenPreference = props.reviewAndConfirmConfiguration;
        BigDecimal discountAmount = reviewScreenPreference.getDiscountAmount();

        if (isValidAmount(props.summaryModel.getCouponAmount())) {
            discountAmount = discountAmount.add(props.summaryModel.getCouponAmount());
        }

        return discountAmount;
    }

    private boolean isValidAmount(final BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) != 0;
    }

    private boolean isPrefAmountDifferent(final BigDecimal charges) {
        final ReviewAndConfirmConfiguration reviewScreenPreference = props.reviewAndConfirmConfiguration;
        final BigDecimal partialCharges = charges != null ? charges : BigDecimal.ZERO;
        final BigDecimal totalAmountPreference = reviewScreenPreference.getTotalAmount().add(partialCharges);
        return totalAmountPreference.compareTo(props.summaryModel.getAmountToPay()) == 0;
    }

    public DisclaimerComponent getDisclaimerComponent(String disclaimer) {
        DisclaimerComponent.Props props = new DisclaimerComponent.Props(disclaimer);
        return new DisclaimerComponent(props);
    }
}