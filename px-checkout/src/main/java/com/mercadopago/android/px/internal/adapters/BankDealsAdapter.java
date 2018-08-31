package com.mercadopago.android.px.internal.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.view.MPTextView;
import com.mercadopago.android.px.model.BankDeal;
import com.squareup.picasso.Callback;
import java.util.List;

public class BankDealsAdapter extends RecyclerView.Adapter<BankDealsAdapter.ViewHolder> {

    private final List<BankDeal> mData;
    private OnSelectedCallback<BankDeal> listener;

    public BankDealsAdapter(final List<BankDeal> data, @NonNull final OnSelectedCallback<BankDeal> listener) {
        mData = data;
        this.listener = listener;
    }

    @Override
    public BankDealsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.px_row_bank_deals, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.populate(mData.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        /* default */ final MPTextView bankDescView;
        /* default */ final ImageView bankImageView;
        /* default */ final MPTextView installmentsView;
        /* default */ final MPTextView logoName;

        public ViewHolder(View v) {

            super(v);
            bankDescView = v.findViewById(R.id.mpsdkBankDesc);
            bankImageView = v.findViewById(R.id.mpsdkBankImg);
            installmentsView = v.findViewById(R.id.mpsdkInstallments);
            logoName = v.findViewById(R.id.logo_name);
        }

        /* default */ void populate(final BankDeal bankDeal,
            final OnSelectedCallback<BankDeal> bankDealOnSelectedCallback) {
            final String prettyExpirationDate = bankDeal.getPrettyExpirationDate();
            bankDescView.setText(
                bankDescView.getContext().getString(R.string.bank_deal_details_date_format, prettyExpirationDate));
            final String issuerName = bankDeal.getIssuer() != null ? bankDeal.getIssuer().getName() : "";
            logoName.setText(issuerName);
            logoName.setVisibility(View.VISIBLE);

            loadBankLogo(bankDeal);
            // Set installments
            installmentsView.setText(Html.fromHtml(getRecommendedMessage(bankDeal)));
            // Set view tag item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    bankDealOnSelectedCallback.onSelected(bankDeal);
                }
            });
        }

        private void loadBankLogo(final BankDeal bankDeal) {
            bankImageView.setVisibility(View.GONE);

            if (bankDeal.hasPictureUrl()) {
                ViewUtils.loadOrCallError(bankDeal.getPicture().getUrl(), bankImageView, new Callback.EmptyCallback() {
                    @Override
                    public void onSuccess() {
                        bankImageView.setVisibility(View.VISIBLE);
                        logoName.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        logoName.setVisibility(View.VISIBLE);
                        bankImageView.setVisibility(View.GONE);
                    }
                });
            }
        }

        private String getRecommendedMessage(BankDeal bankDeal) {
            return TextUtil.isEmpty(bankDeal.getRecommendedMessage()) ? "" : bankDeal.getRecommendedMessage();
        }
    }
}
