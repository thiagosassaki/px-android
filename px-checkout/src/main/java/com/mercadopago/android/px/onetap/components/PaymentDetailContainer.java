package com.mercadopago.android.px.onetap.components;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.mercadopago.android.px.components.CompactComponent;
import com.mercadopago.android.px.components.DiscountDetailContainer;
import com.mercadopago.android.px.components.DiscountDetailContainer.Props.DialogTitleType;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.Discount;
import com.mercadopago.android.px.model.Item;

import java.util.List;

import javax.annotation.Nonnull;

public class PaymentDetailContainer extends CompactComponent<PaymentDetailContainer.Props, Void> {

    public static final class Props {
        /* default */ final DiscountRepository discountRepository;
        /* default */ final List<Item> items;

        public Props(final DiscountRepository discountRepository, final List<Item> items) {
            this.discountRepository = discountRepository;
            this.items = items;
        }
    }

    public PaymentDetailContainer(@NonNull final PaymentDetailContainer.Props props) {
        super(props);
    }

    @Override
    public View render(@Nonnull final ViewGroup parent) {
        addItemDetails(parent);
        addDiscount(parent);
        return null;
    }

    private void addItemDetails(@NonNull final ViewGroup parent) {
        for (final Item item : props.items) {
            parent.addView(new DetailItem(item).render(parent));
        }
    }

    private void addDiscount(@NonNull final ViewGroup parent) {
        final Discount discount = props.discountRepository.getDiscount();
        final Campaign campaign = props.discountRepository.getCampaign();
        if (props.discountRepository.hasValidDiscount()) {
            final DiscountDetailContainer discountDetailContainer = new DiscountDetailContainer(new DiscountDetailContainer.Props(DialogTitleType.SMALL, discount, campaign));
            discountDetailContainer.render(parent);
        }
    }
}