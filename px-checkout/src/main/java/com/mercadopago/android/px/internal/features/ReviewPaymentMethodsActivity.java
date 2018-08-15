package com.mercadopago.android.px.internal.features;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.adapters.ReviewPaymentMethodsAdapter;
import com.mercadopago.android.px.internal.features.providers.ReviewPaymentMethodsProviderImpl;
import com.mercadopago.android.px.internal.util.ErrorUtil;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by vaserber on 8/17/17.
 */

public class ReviewPaymentMethodsActivity extends MercadoPagoBaseActivity implements ReviewPaymentMethodsView {

    //Controls
    protected ReviewPaymentMethodsPresenter mPresenter;
    //View controls
    protected RecyclerView mPaymentMethodsView;
    protected ReviewPaymentMethodsAdapter mAdapter;
    protected FrameLayout mTryOtherCardButton;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        createPresenter();
        getActivityParameters();
        configurePresenter();

        setContentView();
        initializeControls();
        setListeners();
        mPresenter.initialize();
    }

    protected void createPresenter() {
        mPresenter = new ReviewPaymentMethodsPresenter();
    }

    protected void getActivityParameters() {
        List<PaymentMethod> supportedPaymentMethods = null;
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<PaymentMethod>>() {
            }.getType();
            supportedPaymentMethods = gson.fromJson(getIntent().getStringExtra("paymentMethods"), listType);
        } catch (Exception ex) {
            showError(new MercadoPagoError(mPresenter.getResourcesProvider().getStandardErrorMessage(), false), "");
        }
        mPresenter.setSupportedPaymentMethods(supportedPaymentMethods);
    }

    @Override
    public void showError(final MercadoPagoError error, final String requestOrigin) {
        ErrorUtil.startErrorActivity(this, error);
    }

    private void configurePresenter() {
        mPresenter.attachView(this);
        mPresenter.attachResourcesProvider(new ReviewPaymentMethodsProviderImpl(this));
    }

    protected void setContentView() {
        setContentView(R.layout.px_activity_review_payment_methods);
    }

    protected void initializeControls() {
        mPaymentMethodsView = findViewById(R.id.mpsdkReviewPaymentMethodsView);
        mTryOtherCardButton = findViewById(R.id.tryOtherCardButton);
    }

    @Override
    public void initializeSupportedPaymentMethods(List<PaymentMethod> supportedPaymentMethods) {
        mAdapter = new ReviewPaymentMethodsAdapter(supportedPaymentMethods);
        mPaymentMethodsView.setAdapter(mAdapter);
        mPaymentMethodsView.setLayoutManager(new LinearLayoutManager(this));
    }

    protected void setListeners() {
        mTryOtherCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.px_no_change_animation, R.anim.px_slide_down_activity);
            }
        });
    }
}
