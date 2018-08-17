package com.mercadopago.android.px.internal.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.callbacks.OnSelectedCallback;
import com.mercadopago.android.px.internal.features.uicontrollers.payercosts.PayerCostRow;
import com.mercadopago.android.px.model.PayerCost;
import com.mercadopago.android.px.model.Site;
import java.util.ArrayList;
import java.util.List;

public class PayerCostsAdapter extends RecyclerView.Adapter<PayerCostsAdapter.ViewHolder> {

    private final Site mSite;
    private List<PayerCost> mInstallmentsList;
    private final OnSelectedCallback<Integer> mCallback;

    public PayerCostsAdapter(Site site, OnSelectedCallback<Integer> callback) {
        mSite = site;
        mInstallmentsList = new ArrayList<>();
        mCallback = callback;
    }

    public void addResults(List<PayerCost> list) {
        mInstallmentsList = list;
        notifyDataSetChanged();
    }

    public void clear() {
        mInstallmentsList.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View adapterView = inflater.inflate(R.layout.px_adapter_payer_cost, parent, false);
        return new ViewHolder(adapterView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PayerCost payerCost = mInstallmentsList.get(position);
        holder.mPayerCostRow.setSmallTextSize();
        holder.mPayerCostRow.drawPayerCost(payerCost.getInstallmentRate(),
            payerCost.getInstallments(),
            payerCost.getTotalAmount(),
            payerCost.getInstallmentAmount());
    }

    @Override
    public int getItemCount() {
        return mInstallmentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        /* default */ FrameLayout mPayerCostContainer;
        /* default */ PayerCostRow mPayerCostRow;

        ViewHolder(View itemView) {
            super(itemView);
            mPayerCostContainer = itemView.findViewById(R.id.mpsdkPayerCostAdapterContainer);
            mPayerCostRow = new PayerCostRow(itemView.getContext(), mSite);
            mPayerCostRow.inflateInParent(mPayerCostContainer, true);
            mPayerCostRow.initializeControls();

            itemView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event != null && event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                        mCallback.onSelected(getLayoutPosition());
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
