package com.mercadopago.android.px.internal.features.review_and_confirm;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.di.ConfigurationModule;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.ItemsModel;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.LineSeparatorType;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.PaymentModel;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.SummaryModel;
import com.mercadopago.android.px.internal.features.review_and_confirm.models.TermsAndConditionsModel;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.Issuer;
import com.mercadopago.android.px.model.Item;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.Site;
import com.mercadopago.android.px.model.Token;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import java.util.List;

public class ReviewAndConfirmBuilder {

    private Issuer issuer;
    private Discount discount;
    private Campaign campaign;
    private Token token;
    private Boolean hasExtraPaymentMethods;
    private String merchantPublicKey;
    private CheckoutPreference checkoutPreference;

    public ReviewAndConfirmBuilder setIssuer(final Issuer issuer) {
        this.issuer = issuer;
        return this;
    }

    public ReviewAndConfirmBuilder setMerchantPublicKey(final String merchantPublicKey) {
        this.merchantPublicKey = merchantPublicKey;
        return this;
    }

    public ReviewAndConfirmBuilder setToken(final Token token) {
        this.token = token;
        return this;
    }

    public ReviewAndConfirmBuilder setHasExtraPaymentMethods(final boolean hasExtraPaymentMethods) {
        this.hasExtraPaymentMethods = hasExtraPaymentMethods;
        return this;
    }

    public ReviewAndConfirmBuilder setDiscount(Discount discount, Campaign campaign) {
        this.discount = discount;
        this.campaign = campaign;
        return this;
    }

    private void validate(final Activity activity) {
        if (activity == null) {
            throw new IllegalStateException("activity can't be null");
        }
        if (checkoutPreference == null) {
            throw new IllegalStateException("Checkout preference can't be null");
        }
    }

    public void startForResult(@NonNull final Activity activity) {

        validate(activity);

        final Session session = Session.getSession(activity);
        final ConfigurationModule configurationModule = session.getConfigurationModule();
        final UserSelectionRepository userSelectionRepository =
            configurationModule.getUserSelectionRepository();

        final AmountRepository amountRepository = session.getAmountRepository();

        final PaymentMethod paymentMethod = userSelectionRepository.getPaymentMethod();

        final List<Item> items = checkoutPreference.getItems();

        final Site site = checkoutPreference.getSite();

        final String title = SummaryModel.resolveTitle(items,
            activity.getResources().getString(R.string.px_review_summary_product),
            activity.getResources().getString(R.string.px_review_summary_products));

        final boolean termsAndConditionsEnabled =
            TextUtil.isEmpty(configurationModule.getPaymentSettings().getPrivateKey());

        final TermsAndConditionsModel mercadoPagoTermsAndConditions =
            termsAndConditionsEnabled ? new TermsAndConditionsModel(site.getTermsAndConditionsUrl(),
                activity.getString(R.string.px_terms_and_conditions_message),
                activity.getString(R.string.px_terms_and_conditions_linked_message),
                merchantPublicKey,
                LineSeparatorType.TOP_LINE_SEPARATOR) : null;

        final TermsAndConditionsModel discountTermsAndConditions =
            campaign != null ? new TermsAndConditionsModel(campaign.getCampaignTermsUrl(),
                activity.getString(R.string.px_discount_terms_and_conditions_message),
                activity.getString(R.string.px_discount_terms_and_conditions_linked_message),
                merchantPublicKey,
                LineSeparatorType.BOTTOM_LINE_SEPARATOR) : null;

        final PaymentModel paymentModel = new PaymentModel(paymentMethod, token, issuer, hasExtraPaymentMethods);

        final SummaryModel summaryModel =
            new SummaryModel(amountRepository.getAmountToPay(), paymentMethod, site,
                userSelectionRepository.getPayerCost(), discount, title,
                checkoutPreference.getTotalAmount(),
                amountRepository.getAppliedCharges());

        final ItemsModel itemsModel = new ItemsModel(site.getCurrencyId(), items);

        ReviewAndConfirmActivity.start(activity,
            merchantPublicKey,
            mercadoPagoTermsAndConditions,
            paymentModel,
            summaryModel,
            itemsModel,
            discountTermsAndConditions);
    }

    public ReviewAndConfirmBuilder setPreference(@NonNull final CheckoutPreference checkoutPreference) {
        this.checkoutPreference = checkoutPreference;
        return this;
    }
}
