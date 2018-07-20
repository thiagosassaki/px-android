package com.mercadopago.android.px.review_and_confirm.components;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.mercadopago.android.px.components.Component;
import com.mercadopago.android.px.components.RendererFactory;
import com.mercadopago.android.px.model.Summary;
import com.mercadopago.android.px.model.SummaryDetail;
import com.mercadopago.android.px.review_and_confirm.SummaryProvider;
import com.mercadopago.android.px.review_and_confirm.models.ReviewAndConfirmPreferences;
import com.mercadopago.android.px.review_and_confirm.props.AmountDescriptionProps;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.mercadopago.android.px.util.TextUtils.isEmpty;
import static com.mercadopago.android.px.util.TextUtils.isNotEmpty;

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
        final ReviewAndConfirmPreferences reviewAndConfirmPreferences = props.reviewAndConfirmPreferences;
        final Summary.Builder summaryBuilder = new com.mercadopago.android.px.model.Summary.Builder();

        // TODO hotfix charges with preferences
        if (isPrefAmountDifferent(props.summaryModel.getCharges()) && reviewAndConfirmPreferences.hasProductAmount()) {
            summaryBuilder.addSummaryProductDetail(reviewAndConfirmPreferences.getProductAmount(), getItemTitle(),
                    provider.getDefaultTextColor())

                    .addSummaryShippingDetail(reviewAndConfirmPreferences.getShippingAmount(),
                            provider.getSummaryShippingTitle(), provider.getDefaultTextColor())

                    .addSummaryArrearsDetail(reviewAndConfirmPreferences.getArrearsAmount(),
                            provider.getSummaryArrearTitle(),
                            provider.getDefaultTextColor())

                    .addSummaryTaxesDetail(reviewAndConfirmPreferences.getTaxesAmount(), provider.getSummaryTaxesTitle(),
                            provider.getDefaultTextColor())

                    .addSummaryDiscountDetail(getDiscountAmount(), provider.getSummaryDiscountsTitle(),
                            provider.getDiscountTextColor())

                    .setDisclaimerText(reviewAndConfirmPreferences.getDisclaimerText())
                    .setDisclaimerColor(provider.getDisclaimerTextColor());
        } else {
            summaryBuilder.addSummaryProductDetail(props.summaryModel.getItemsAmount(), getItemTitle(),
                    provider.getDefaultTextColor());

            if (!isEmpty(reviewAndConfirmPreferences.getDisclaimerText())) {
                summaryBuilder.setDisclaimerText(reviewAndConfirmPreferences.getDisclaimerText())
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

        if (isNotEmpty(props.reviewAndConfirmPreferences.getProductTitle())) {
            title = props.reviewAndConfirmPreferences.getProductTitle();
        }

        return title;
    }

    private BigDecimal getDiscountAmount() {
        final ReviewAndConfirmPreferences reviewScreenPreference = props.reviewAndConfirmPreferences;
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
        final ReviewAndConfirmPreferences reviewScreenPreference = props.reviewAndConfirmPreferences;
        final BigDecimal partialCharges = charges != null ? charges : BigDecimal.ZERO;
        final BigDecimal totalAmountPreference = reviewScreenPreference.getTotalAmount().add(partialCharges);
        return totalAmountPreference.compareTo(props.summaryModel.getAmountToPay()) == 0;
    }

    public DisclaimerComponent getDisclaimerComponent(String disclaimer) {
        DisclaimerComponent.Props props = new DisclaimerComponent.Props(disclaimer);
        return new DisclaimerComponent(props);
    }
}