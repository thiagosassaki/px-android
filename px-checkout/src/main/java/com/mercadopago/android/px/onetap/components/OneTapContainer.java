package com.mercadopago.android.px.onetap.components;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.android.px.R;
import com.mercadopago.android.px.components.Action;
import com.mercadopago.android.px.components.Button;
import com.mercadopago.android.px.components.ButtonPrimary;
import com.mercadopago.android.px.components.CompactComponent;
import com.mercadopago.android.px.components.TermsAndConditionsComponent;
import com.mercadopago.android.px.internal.di.ConfigurationModule;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.Item;
import com.mercadopago.android.px.onetap.OneTap;
import com.mercadopago.android.px.review_and_confirm.models.LineSeparatorType;
import com.mercadopago.android.px.review_and_confirm.models.TermsAndConditionsModel;
import com.mercadopago.android.px.viewmodel.OneTapModel;
import com.mercadopago.android.px.util.ViewUtils;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OneTapContainer extends CompactComponent<OneTapModel, OneTap.Actions> {

    public OneTapContainer(final OneTapModel oneTapModel, final OneTap.Actions callBack) {
        super(oneTapModel, callBack);
    }

    @Override
    public View render(@Nonnull final ViewGroup parent) {
        final Session session = Session.getSession(parent.getContext());
        final ConfigurationModule configurationModule = session.getConfigurationModule();
        final PaymentSettingRepository configuration = configurationModule.getPaymentSettings();
        final DiscountRepository discountRepository = session.getDiscountRepository();
        final Discount discount = discountRepository.getDiscount();
        final Campaign campaign = discountRepository.getCampaign();

        addItem(parent, configuration.getCheckoutPreference().getItems());
        addAmount(parent, configuration, discountRepository);
        addPaymentMethod(parent, configuration, discountRepository);
        addTermsAndConditions(parent, campaign);
        addConfirmButton(parent, discount);
        return parent;
    }

    private void addItem(final ViewGroup parent, final List<Item> items) {
        final String defaultMultipleTitle = parent.getContext().getString(R.string.px_review_summary_products);
        final int icon =
                props.getCollectorIcon() == null ? R.drawable.px_review_item_default : props.getCollectorIcon();
        final String itemsTitle = com.mercadopago.android.px.model.Item
                .getItemsTitle(items, defaultMultipleTitle);
        final View render = new CollapsedItem(new CollapsedItem.Props(icon, itemsTitle)).render(parent);
        parent.addView(render);
    }

    private void addAmount(final ViewGroup parent, final PaymentSettingRepository configuration, final DiscountRepository discountRepository) {
        final Amount.Props props = Amount.Props.from(this.props, configuration, discountRepository);
        final View view = new Amount(props, getActions()).render(parent);
        parent.addView(view);
    }

    private void addPaymentMethod(final ViewGroup parent,
                                  final PaymentSettingRepository configuration,
                                  final DiscountRepository discountRepository) {
        final View view =
                new PaymentMethod(PaymentMethod.Props.createFrom(props, configuration, discountRepository),
                        getActions()).render(parent);
        parent.addView(view);
    }

    private void addTermsAndConditions(final ViewGroup parent, @Nullable final Campaign campaign) {
        if (campaign != null) {
            final Context context = parent.getContext();
            TermsAndConditionsModel model = new TermsAndConditionsModel(campaign.getCampaignTermsUrl(),
                    context.getString(R.string.px_discount_terms_and_conditions_message),
                    context.getString(R.string.px_discount_terms_and_conditions_linked_message),
                    props.getPublicKey(),
                    LineSeparatorType.NONE);
            final View view = new TermsAndConditionsComponent(model)
                    .render(parent);
            parent.addView(view);
        }
    }

    private void addConfirmButton(final @Nonnull ViewGroup parent, @Nullable final Discount discount) {
        final String confirm = parent.getContext().getString(R.string.px_confirm);
        final Button.Actions actions = new Button.Actions() {
            @Override
            public void onClick(final Action action) {
                getActions().confirmPayment();
            }
        };

        final Button button = new ButtonPrimary(new Button.Props(confirm), actions);
        final View view = button.render(parent);
        final int resMargin = discount != null ? R.dimen.px_zero_height : R.dimen.px_m_margin;
        ViewUtils.setMarginTopInView(view, parent.getContext().getResources().getDimensionPixelSize(resMargin));
        parent.addView(view);
    }
}
